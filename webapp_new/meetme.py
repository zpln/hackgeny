import flask
import os
from flask import Flask, url_for, redirect, g
import sqlite3
from contextlib import closing
import app_db

import settings

APP_NAME = __name__
STATIC_DIR = os.path.join(os.path.dirname(__file__), "static")

app = Flask(APP_NAME)
app.config.from_object(settings)


def connect_db():
    return sqlite3.connect(app.config['DATABASE'])


def init_db():
    with closing(connect_db()) as db:
        with app.open_resource('schema.sql', mode='r') as f:
            db.cursor().executescript(f.read())
        db.commit()


@app.before_request
def before_request():
    g.db = connect_db()


@app.teardown_request
def teardown_request(exception):
    db = getattr(g, 'db', None)
    if db is not None:
        db.close()


def json(func):
    def decorator(*args, **kwargs):
        result = func(*args, **kwargs)
        return flask.jsonify(result)
    return decorator


@app.route("/")
def index():
    return redirect(url_for("login"))


@app.route("/login")
def login():
    return open(os.path.join(STATIC_DIR, "login/login.html"), 'rb').read()


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


@app.route('/fb_login', methods=['POST'])
@json
def fb_login():
    fb_user_id = check_required_parameters({"fb_user_id"}, True)["fb_user_id"]

    user_id = app_db.select_user_id_from_facebook_user_id(fb_user_id)

    if user_id is None:
        user_id = app_db.insert_new_user_id()
        app_db.insert_fb_user(fb_user_id, user_id)

    return {"user_id": user_id}


if __name__ == "__main__":
    init_db()
    app.run()
