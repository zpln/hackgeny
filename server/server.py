# Project
import db_handler

# Python
import flask

app = flask.Flask(__name__)


def format_success_or_error(message, is_error):
    """
    Return JSON with supplied error or success message
    :param message: The string to return in JSON
    :param is_error: If True: error will be returned. If not - success
    :return: JSON with error message inside it
    """
    if is_error:
        message_type = "error"
    else:
        message_type = "success"
    return flask.jsonify({message_type: message})


def check_required_parameters(parameters, post=False):
    """
    Checks that every parameter in parameters exists,
    if so - return a tuple with True, and a dictionary of parameter
    if not - return a tuple with False, and JSON with matching error
    :param parameters: List of parameters that will be checked
    :return: Tuple:
    (action_success, parameters/json_error)
    """
    parameter_values = {}
    for parameter in parameters:
        if (post == False):
            parameter_value = flask.request.args.get(parameter, None)
        else:
            parameter_value = flask.request.form.get(parameter, None)
        if parameter_value is None:
            error_message = "Required parameter {parameter} is not supplied".format(parameter=parameter)
            return False, flask.jsonify({"error": error_message})
        else:
            parameter_values[parameter] = parameter_value
    return True, parameter_values


@app.route('/get_events')
def get_events():
    required_exists, data = check_required_parameters(("user_id",))
    if not required_exists:
        # If required items doesn't exist, return the JSON error message
        return data

    events = db_handler.query_db("""
    SELECT event.event_id, event.event_name, event_user.status
    FROM event
    INNER JOIN event_user
    INNER JOIN user
    WHERE event.event_id=event_user.event_id
    AND event_user.user_id = user.user_id
    AND user.phone = ?
    """, (data["user_id"],))
    return flask.jsonify({"events": events})

@app.route('/get_event_details')
def get_event_details():
    required_exists, data = check_required_parameters(("user_id", "event_id"))
    if not required_exists:
        # If required items doesn't exist, return the JSON error message
        return data

    event_details = db_handler.query_db("""
    SELECT event.event_id, event.event_name, event_user.status
    FROM event
    INNER JOIN event_user
    INNER JOIN user
    WHERE event.event_id = event_user.event_id
    AND event.event_id = ?
    AND event_user.user_id = user.user_id
    AND user.phone = ?
    """, (data["event_id"], data["user_id"]), True)

    polls = db_handler.query_db("""
    SELECT poll.poll_id, poll.poll_name, poll.overridden_poll_option
    FROM poll
    WHERE poll.event_id = ?
    """, (data["event_id"],))

    for poll in polls:
        selected_poll_option = db_handler.query_db("""
        SELECT poll_option.poll_option_id FROM poll_option
        INNER JOIN user_poll_option
        INNER JOIN user
        WHERE poll_option.poll_id = ?
        AND poll_option.poll_option_id = user_poll_option.poll_option_id
        AND user_poll_option.user_id = user.user_id
        AND user.phone = ?
        """, (poll['poll_id'], data["user_id"]))
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
    event_details["polls"] = polls

    return flask.jsonify(event_details)


@app.route('/answer_poll')
def answer_polls():
    required_exists, data = check_required_parameters(("user_id", "poll_option_id"))
    if not required_exists:
        # If required items doesn't exist, return the JSON error message
        return data

    count = db_handler.query_db("""
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
    """, (data["poll_option_id"], data["user_id"]), True)["count"]

    # TODO: Support changing your mind
    if count > 0:
        return format_success_or_error("Do not vote twice for the same poll", True)

    user_id = db_handler.query_db("""
    SELECT user_id FROM user WHERE phone = ?
    """, (data["user_id"],), True)
    if user_id is None:
        return format_success_or_error("User does not exist", True)

    db_handler.insert_db("user_poll_option", (user_id["user_id"], data["poll_option_id"]))

    return flask.jsonify(format_success_or_error("User poll selection was successfully inserted", False))

@app.route('/create_event', methods=['POST'])
def create_event():
    """
    Creaete an event in the database, get
    event_name - a name chossen by the user for the event
    polls - the polls that the user created
    users - a list of user to create the event for them.
    :return:
    """

@app.before_request
def before_request():
    flask.g.db = db_handler.connect_db()


@app.after_request
def after_request(response):
    flask.g.db.close()
    return response


if __name__ == '__main__':
    # TODO: Remove debug on production
    app.run(debug=True, host="0.0.0.0")
