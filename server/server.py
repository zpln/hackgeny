# Project
import db_handler

# Python
import flask
from flask import request

app = flask.Flask(__name__)

def format_error(error_message):
    """
    Return JSON with supplied error message
    :param error_message: The string to return in JSON
    :return: JSON with error message inside it
    """
    return flask.jsonify({"error": error_message})


def check_required_parameters(parameters):
    """
    Checks that every parameter in parameters exists,
    if so - return a tuple with True, and a list of parameter values
    if not - return a tuple with False, and JSON with matching error
    :param parameters: List of parameters that will be checked
    :return: Tuple:
    (action_success, parameter_values/json_error)
    """
    parameter_values = []
    for parameter in parameters:
        parameter_value = flask.request.args.get(parameter, None)
        if parameter_value is None:
            error_message = "Required parameter {parameter} is not supplied".format(parameter=parameter)
            return False, flask.jsonify({"error": error_message})
        else:
            parameter_values.append(parameter_value)
    return True, parameter_values


@app.route('/get_events')
def get_events():
    required_exists, data = check_required_parameters(("user_id", ))
    if not required_exists:
        # If required items doesn't exist, return the JSON error message
        return data
    user_id, = data

    events = db_handler.query_db("""
    SELECT event.event_id, event.event_name, event_user.status
    FROM event
    INNER JOIN event_user
    INNER JOIN user
    WHERE event.event_id=event_user.event_id
    AND event_user.user_id = user.user_id
    AND user.phone = ?
    """, (user_id,))
    return flask.jsonify({"events": events})

@app.route('/Answear_poll')
def answear_polls():
    uid = flask.request.args.get("uid")
    opt_id = flask.request.args.get("opt_id")
    db_handler.instert_db("user_poll_options", (uid, opt_id))
    return "inserted data"



@app.before_request
def before_request():
    flask.g.db = db_handler.connect_db()

@app.after_request
def after_request(response):
    flask.g.db.close()
    return response

if __name__ == '__main__':
    # TODO: Remove debug on production
    app.run(debug=True)
