# Project
import db_handler

# Python
import flask
from flask import request

app = flask.Flask(__name__)

@app.route('/get_events')
def get_events():
    events = db_handler.query_db("SELECT * FROM event")
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
