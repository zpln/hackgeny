# Project
import db_handler

# Python
import flask

app = flask.Flask(__name__)

@app.route('/get_events')
def get_events():
    events = db_handler.query_db("SELECT * FROM event")
    return flask.jsonify({"events": events})

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
