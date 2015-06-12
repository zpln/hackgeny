# Project
import db_handler

# Python
import flask

app = flask.Flask(__name__)

def format_error(error_message):
    return flask.jsonify({"error": error_message})

@app.route('/get_events')
def get_events():
    user_id = flask.request.args.get("user_id", None)
    if user_id is None:
        return format_error("user_id not supplied")

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
