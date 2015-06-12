import os
import flask
import sqlite3

DATABASE_PATH = "db.sqlite3"

def relative_to_absolute(path):
    return os.path.join(os.path.dirname(__file__), path)

def connect_db():
    return sqlite3.connect(relative_to_absolute(DATABASE_PATH))

def query_db(query, args=(), one=False):
    cur = flask.g.db.execute(query, args)
    rv = [dict((cur.description[idx][0], value)
               for idx, value in enumerate(row)) for row in cur.fetchall()]
    return (rv[0] if rv else None) if one else rv
