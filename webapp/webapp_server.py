import sys
import os
sys.path.append(os.path.join(os.path.dirname(__file__), ".."))

import flask
import json

from server import server
from server import db_handler

FILE_DIR = os.path.dirname(__file__)
EVENTS_DETAILS = os.path.join(FILE_DIR, "event-details.html")

BARS_COLORS = ["teal", "salmon", "peach", "lime"]

app = flask.Flask("mobile meetme")

@app.route('/')
def index():
    conn = db_handler.connect_db()
    db_handler.get_db_connection = lambda: conn
    #server.before_request()
    event_details = server.logic.get_event_details("0545920004", 1)
    print event_details

    params = dict()
    params["event_name"] = event_details["event_name"]
    params["time"] = "TBD"
    params["location"] = "TBD"

    params["polls"] = list()
    for poll in event_details["polls"]:
        new_poll = dict()
        new_poll["options"] = list()
        new_poll["name"] = poll["poll_name"]
        if len(poll["options"]) > 0:
            new_poll["bar_width"] = (100.0 / len(poll["options"])) - 1
        num_of_votes = float(sum([option["poll_option_count"] for option in poll["options"]]))
        counter = 0
        for option in poll["options"]:
            new_option = dict()
            new_option["numofvotes"] = option["poll_option_count"]
            new_option["value"] = option["poll_option_name"]
            new_option["color"] = BARS_COLORS[counter]
            counter += 1
            counter %= len(BARS_COLORS)
            new_option["height"] = (option["poll_option_count"] / num_of_votes) * 400
            new_poll["options"].append(new_option)
        params["polls"].append(new_poll)


    return flask.render_template_string(open(EVENTS_DETAILS).read(), **params)

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)
