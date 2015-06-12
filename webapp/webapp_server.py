import sys
import os
sys.path.append(os.path.join(os.path.dirname(__file__), ".."))

import flask
import json

from server import server

FILE_DIR = os.path.dirname(__file__)
EVENTS_DETAILS = os.path.join(FILE_DIR, "event-details.html")

app = flask.Flask("mobile meetme")

@app.route('/')
def index():
    return open(EVENTS_DETAILS).read()

@app.route('/tmp')
def tmp():
    event_details_json = """{
  "event_id": 1,
  "event_name": "Going out tonight",
  "polls": [
    {
      "options": [
        {
          "poll_option_count": 2,
          "poll_option_id": 1,
          "poll_option_name": "Panasi"
        },
        {
          "poll_option_count": 1,
          "poll_option_id": 2,
          "poll_option_name": "Hapak"
        }
      ],
      "overridden_poll_option": -1,
      "poll_id": 1,
      "poll_name": "Location",
      "selected_poll_option": 1
    }
  ],
  "status": 0
}"""
    event_details = json.loads(event_details_json)
    return server.get_events()

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)