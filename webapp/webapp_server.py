import sys
import os
sys.path.append(os.path.join(os.path.dirname(__file__), ".."))

import flask

from server import server

FILE_DIR = os.path.dirname(__file__)
EVENTS_DETAILS = os.path.join(FILE_DIR, "event-details.html")

app = flask.Flask("mobile meetme")

@app.route('/index.html')
def index():
    print "@"*80
    print EVENTS_DETAILS
    return open(EVENTS_DETAILS).read()

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=80, debug=True)