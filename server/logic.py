import json
import flask
import db_handler

class APIException(Exception):
    status_code = 400

    def to_json(self):
        return flask.jsonify({"error": self.message})


def get_user_id(phone):
    user_id = db_handler.query_db("""
    SELECT user_id FROM user WHERE phone = ?
    """, (phone,), True)
    if user_id is None:
        return db_handler.insert_db("user", {"phone": phone})
    else:
        return user_id["user_id"]


def get_events(phone):
    events = db_handler.query_db("""
    SELECT event.event_id, event.event_name, event_user.status
    FROM event
    INNER JOIN event_user
    WHERE event.event_id=event_user.event_id
    AND event_user.user_id = ?
    """, (get_user_id(phone),))

    return events

def fill_poll_options_in_poll(phone, poll):
    selected_poll_option = db_handler.query_db("""
    SELECT poll_option.poll_option_id FROM poll_option
    INNER JOIN user_poll_option
    WHERE poll_option.poll_id = ?
    AND poll_option.poll_option_id = user_poll_option.poll_option_id
    AND user_poll_option.user_id = ?
    """, (poll['poll_id'], get_user_id(phone)))
    if len(selected_poll_option) == 0:
        selected_poll_option = -1
    else:
        selected_poll_option = selected_poll_option[0]["poll_option_id"]

    poll_options = db_handler.query_db("""
    SELECT poll_option.poll_option_id, poll_option.poll_option_name
    FROM poll_option
    WHERE poll_option.poll_id = ?
    """, (poll['poll_id'],))

    for poll_option in poll_options:
        poll_option_count = db_handler.query_db("""
        SELECT COUNT(*) AS poll_option_count
        FROM user_poll_option
        WHERE user_poll_option.poll_option_id = ?
        """, (poll_option['poll_option_id'],), True)["poll_option_count"]
        poll_option["poll_option_count"] = poll_option_count

    poll["options"] = poll_options
    poll["selected_poll_option"] = selected_poll_option

def get_event_details(phone, event_id):
    event_details = db_handler.query_db("""
    SELECT event.event_id, event.event_name, user.phone AS creator_id, event_user.status
    FROM event
    INNER JOIN event_user
    INNER JOIN user
    WHERE event.event_id = event_user.event_id
    AND event.event_id = ?
    AND event_user.user_id = ?
    AND user.user_id = event.creator_id
    """, (event_id, get_user_id(phone)), True)

    if event_details is None:
        raise APIException("Event doesn't exist or you are not invited")

    polls = db_handler.query_db("""
    SELECT poll.poll_id, poll.poll_name, poll.overridden_poll_option
    FROM poll
    WHERE poll.event_id = ?
    """, (event_id,))

    for poll in polls:
        fill_poll_options_in_poll(phone, poll)

    event_details["polls"] = polls

    user_rows = db_handler.query_db("""
    SELECT user.phone
    FROM event_user
    INNER JOIN user
    WHERE event_user.event_id = ?
    AND event_user.user_id = user.user_id
    """, (event_id,))
    users = [user["phone"] for user in user_rows]
    event_details["users"] = users

    return event_details

def answer_poll(phone, poll_option_id):
    count = db_handler.query_db("""
    SELECT COUNT(*) as count
    FROM poll_option
    INNER JOIN user_poll_option
    WHERE poll_option.poll_id IN (SELECT poll.poll_id
    FROM poll
    INNER JOIN poll_option
    WHERE poll.poll_id = poll_option.poll_id
    AND poll_option.poll_option_id = ?)
    AND user_poll_option.poll_option_id = poll_option.poll_option_id
    AND user_poll_option.user_id = ?
    """, (poll_option_id, get_user_id(phone)), True)["count"]

    # TODO: Support changing your mind
    if count > 0:
        raise APIException("Do not vote twice for the same poll")

    db_handler.insert_db("user_poll_option", {"user_id": get_user_id(phone), "poll_option_id": poll_option_id})

    poll = db_handler.query_db("""
    SELECT poll.poll_id, poll.poll_name, poll.overridden_poll_option
    FROM poll
    INNER JOIN poll_option
    WHERE poll.poll_id = poll_option.poll_id
    AND poll_option.poll_option_id = ?
    """, (poll_option_id,), True)
    fill_poll_options_in_poll(phone, poll)

    return poll


class EventStatus(object):
    UNSET, YES, NO = range(3)

def create_event(phone, event_name, polls, users):
    """
    Create an event in the database, get
    event_name - a name chosen by the user for the event
    polls - the polls that the user created
    users - a list of user to create the event for them.
    :return:
    """
    user_id = get_user_id(phone)
    event_id = db_handler.insert_db("event", {"event_name": event_name, "creator_id": user_id})
    for poll in json.loads(polls):
        poll_id = db_handler.insert_db("poll", {"poll_name": poll["poll_name"], "event_id": event_id})
        for option_name in poll["option_names"]:
            db_handler.insert_db("poll_option", {"poll_option_name": option_name, "poll_id": poll_id})
    db_handler.insert_db("event_user", {"event_id": event_id, "user_id": user_id, "status": EventStatus.YES})

    for user in json.loads(users):
        real_user = get_user_id(user["phone"])
        db_handler.update_db("user", ["user_name"], (str(user["name"]),), str("user_id='" + str(real_user) + "'"))
        db_handler.insert_db("event_user", {"event_id": event_id, "user_id": real_user, "status": EventStatus.UNSET})
    return get_event_details(phone, event_id)



def add_poll_option(phone, poll_id, poll_option_name):
    count = db_handler.query_db("""
    SELECT COUNT(*) AS count
    FROM poll
    INNER JOIN event_user
    WHERE poll.poll_id = poll_id
    AND event_user.event_id = poll.event_id
    AND event_user.user_id = ?
    """, (get_user_id(phone),), True)["count"]
    if count == 0:
        raise APIException("Poll does not exist")

    parameters = {"poll_option_name": poll_option_name, "poll_id": poll_id}
    db_handler.insert_db("poll_option", parameters)
