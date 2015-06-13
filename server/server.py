# Project
import logic
import db_handler

# Python
import flask
import functools

app = flask.Flask(__name__)


def json():
    """
    Wraps functions and return their JSON serialized output
    """
    def decorated(func):
        @functools.wraps(func)
        def json_and_call(*args, **kwargs):
            return flask.jsonify(func(*args, **kwargs))
        return json_and_call
    return decorated


def check_required_parameters(parameters, post=False):
    """
    Checks that every parameter in parameters exists,
    if so - return a dictionary of the parameters
    if not - raise exception with the fitting message to the user
    :param parameters: List of parameters that will be checked
    :return: Parameters dictionary
    """
    parameter_values = {}
    for parameter in parameters:
        if not post:
            parameter_value = flask.request.args.get(parameter, None)
        else:
            parameter_value = flask.request.form.get(parameter, None)
        if parameter_value is None:
            error_message = "Required parameter {parameter} is not supplied".format(parameter=parameter)
            raise logic.APIException(error_message)
        else:
            parameter_values[parameter] = parameter_value
    return parameter_values


@app.route('/get_events')
@json()
def get_events():
    data = check_required_parameters(("user_id",))
    return {"events": logic.get_events(data["user_id"])}


@app.route('/get_event_details')
@json()
def get_event_details():
    data = check_required_parameters(("user_id", "event_id"))
    return logic.get_event_details(data["user_id"], int(data["event_id"]))


@app.route('/answer_poll')
@json()
def answer_poll():
    data = check_required_parameters(("user_id", "poll_option_id"))
    return logic.answer_poll(data["user_id"], int(data["poll_option_id"]))


@app.route('/create_event', methods=['POST'])
@json()
def create_event():
    data = check_required_parameters(("user_id", "event_name", "polls", "users"), True)
    return logic.create_event(data["user_id"], data["event_name"], data["polls"], data["users"])


@app.route('/add_poll_option')
@json()
def add_poll_option():
    data = check_required_parameters(("user_id", "poll_id", "poll_option_name"))
    return logic.add_poll_option(data["user_id"], int(data["poll_id"]), data["poll_option_name"])


@app.before_request
def before_request():
    flask.g.db = db_handler.connect_db()


@app.after_request
def after_request(response):
    flask.g.db.close()
    return response


@app.errorhandler(logic.APIException)
def handle_api_exception(error):
    response = error.to_json()
    response.status_code = error.status_code
    return response

if __name__ == '__main__':
    # TODO: Remove debug on production
    app.run(host="0.0.0.0", debug=True)
