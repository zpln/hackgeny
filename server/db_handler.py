import os
import flask
import sqlite3

DATABASE_PATH = "db.sqlite3"

def relative_to_absolute(path):
    return os.path.join(os.path.dirname(__file__), path)

def connect_db():
    return sqlite3.connect(relative_to_absolute(DATABASE_PATH))

def query_db(query, args=(), one_row_only=False):
    """
    Perform query on DB, and return rows selected
    :param query: The SQL query to perform
    :param args: Arguments that should be placed in query
    :param one_row_only: Used when only 1 row is returned, will return that row (or None if none returned)
    :return: Rows (or row) selected
    """
    cursor = flask.g.db.execute(query, args)
    rows = [dict((cursor.description[i][0], value) for i, value in enumerate(row)) for row in cursor.fetchall()]
    if one_row_only:
        return rows[0] if rows else None
    else:
        return rows

def insert_db(table_name, values):
    """
    Insert values to table
    :param table_name: Name of table
    :param values: List of values
    :return: ID of row inserted to
    """
    value_placeholders = []
    # Wrap strings with quotes
    for value in values.values():
        if type(value) is str:
            value_placeholders.append("'?'")
        else:
            value_placeholders.append("?")
    temp_col_name = values.keys()
    temp_col_name = str(temp_col_name)[1:-1]
    insert_str = "INSERT INTO {table_name} ({col_names}) VALUES ({args})".format(
        table_name=table_name, args=",".join(value_placeholders),
        col_names=temp_col_name
    )
    cur = flask.g.db.cursor()
    cur.execute(insert_str, values.values())
    flask.g.db.commit()
    # Return the ID of the entry inserted
    return cur.lastrowid
