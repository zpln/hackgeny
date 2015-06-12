import flask
import sqlite3

app = flask.Flask(__name__)
DATABASE_PATH = "db.sqlite3"

def connect_db():
    return sqlite3.connect(DATABASE_PATH)
    pass

def query_db(query, args=(), one=False):
    cur = flask.g.db.execute(query, args)
    rv = [dict((cur.description[idx][0], value)
               for idx, value in enumerate(row)) for row in cur.fetchall()]
    return (rv[0] if rv else None) if one else rv

@app.before_request
def before_request():
    flask.g.db = connect_db()
    pass

@app.after_request
def after_request(response):
    flask.g.db.close()
    return response
