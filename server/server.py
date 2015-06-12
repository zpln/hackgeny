# Project
import db_handler

# Python
import flask

app = flask.Flask(__name__)

@app.route('/')
def hello_world():
    return flask.jsonify({"a": 1})

if __name__ == '__main__':
    # TODO: Remove debug on production
    app.run(debug=True)
