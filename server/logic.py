import flask
import db_handler
import json

class APIException(Exception):
    status_code = 400

    def to_json(self):
        return flask.jsonify({"error": self.message})


def get_events(user_id):
    events = db_handler.query_db("""
    SELECT event.event_id, event.event_name, event_user.status
    FROM event
    INNER JOIN event_user
    INNER JOIN user
    WHERE event.event_id=event_user.event_id
    AND event_user.user_id = user.user_id
    AND user.phone = ?
    """, (user_id,))

    return events

def fill_poll_options_in_poll(poll, user_id):
    selected_poll_option = db_handler.query_db("""
    SELECT poll_option.poll_option_id FROM poll_option
    INNER JOIN user_poll_option
    INNER JOIN user
    WHERE poll_option.poll_id = ?
    AND poll_option.poll_option_id = user_poll_option.poll_option_id
    AND user_poll_option.user_id = user.user_id
    AND user.phone = ?
    """, (poll['poll_id'], user_id))
    if len(selected_poll_option) == 0:
        selected_poll_option = -1
    else:
        selected_poll_option = selected_poll_option[0]["poll_option_id"]

    poll_options = db_handler.query_db("""
    SELECT poll_option.poll_option_id, poll_option.poll_option_name,
    COUNT(poll_option.poll_option_id) AS poll_option_count
    FROM poll_option
    INNER JOIN user_poll_option
    WHERE poll_option.poll_id = ?
    AND user_poll_option.poll_option_id = poll_option.poll_option_id
    GROUP BY
    poll_option.poll_option_id
    """, (poll['poll_id'],))
    poll["options"] = poll_options
    poll["selected_poll_option"] = selected_poll_option

def get_event_details(event_id, user_id):
    event_details = db_handler.query_db("""
    SELECT event.event_id, event.event_name, event_user.status
    FROM event
    INNER JOIN event_user
    INNER JOIN user
    WHERE event.event_id = event_user.event_id
    AND event.event_id = ?
    AND event_user.user_id = user.user_id
    AND user.phone = ?
    """, (event_id, user_id), True)

    polls = db_handler.query_db("""
    SELECT poll.poll_id, poll.poll_name, poll.overridden_poll_option
    FROM poll
    WHERE poll.event_id = ?
    """, (event_id,))

    for poll in polls:
        fill_poll_options_in_poll(poll, user_id)

    event_details["polls"] = polls
    return event_details

def answer_polls(user_id, poll_option_id):
    result = db_handler.query_db("""
    SELECT COUNT(*) as count
    FROM poll_option
    INNER JOIN user_poll_option
    INNER JOIN user
    WHERE poll_option.poll_id IN (SELECT poll.poll_id
    FROM poll
    INNER JOIN poll_option
    WHERE poll.poll_id = poll_option.poll_id
    AND poll_option.poll_option_id = ?)
    AND user_poll_option.poll_option_id = poll_option.poll_option_id
    AND user_poll_option.user_id = user.user_id
    AND user.phone = ?
    """, (poll_option_id, user_id), True)

    # TODO: Support changing your mind
    if result["count"] > 0:
        raise APIException("Do not vote twice for the same poll")

    db_user_id = db_handler.query_db("""
    SELECT user_id FROM user WHERE phone = ?
    """, (user_id,), True)
    if db_user_id is None:
        raise APIException("User does not exist")

    db_handler.insert_db("user_poll_option", {"user_id": db_user_id["user_id"], "poll_option_id": poll_option_id})

    poll = db_handler.query_db("""
    SELECT poll.poll_id, poll.poll_name, poll.overridden_poll_option
    FROM poll
    INNER JOIN poll_option
    WHERE poll.poll_id = poll_option.poll_id
    AND poll_option.poll_option_id = ?
    """, (poll_option_id,), True)
    fill_poll_options_in_poll(poll, user_id)

    return poll


def create_event(uid, event_name, polls, users):
    """
    Create an event in the database, get
    event_name - a name chosen by the user for the event
    polls - the polls that the user created
    users - a list of user to create the event for them.
    :return:
    """
    event_id = db_handler.insert_db("event", {"event_name": event_name, "creator_id": uid})
    print event_id
    for poll in json.loads(polls):
        print "lolcake"
        poll_id = db_handler.insert_db("poll", {"poll_name": poll["pollname"], "event_id": event_id})
        for x in poll["optsion"]:
            print x
            db_handler.insert_db("poll_option", {"poll_option_name": x, "poll_id": poll_id})
    for user in json.loads(users):
        real_user = get_user_id(user)
        db_handler.insert_db("event_user", {"event_id": event_id, "user_id": real_user, "status": 0})
    return str(event_id)
