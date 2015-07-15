from flask import g


def query_db(query, args=(), one_row_only=False):
    """
    Perform query on DB, and return rows selected
    :param query: The SQL query to perform
    :param args: Arguments that should be placed in query
    :param one_row_only: Used when only 1 row is returned, will return that row (or None if none returned)
    :return: Rows (or row) selected
    """
    cursor = g.db.execute(query, args)
    rows = [dict((cursor.description[i][0], value) for i, value in enumerate(row)) for row in cursor.fetchall()]
    if one_row_only:
        return rows[0] if rows else None
    else:
        return rows


def insert_db(table_name, parameters):
    """
    Insert values to table
    :param table_name: Name of table
    :param parameters: Dictionary of name and value
    :return: ID of row inserted to
    """
    insert_str = "INSERT INTO {table_name} ({columns}) VALUES ({values})".format(
        table_name=table_name, columns=", ".join(parameters.keys()), values=", ".join('?' * len(parameters)),
    )
    cur = g.db.cursor()
    cur.execute(insert_str, parameters.values())
    g.db.commit()

    # Return the ID of the entry inserted
    return cur.lastrowid


def update_db(table_name, parameters, condition=None):
    set_strings = []
    for parameter_name in parameters.keys():
        set_strings.append("{parameter_name}=?".format(parameter_name=parameter_name))

    set_string = ", ".join(set_strings)

    update_str = "UPDATE {table_name} SET {set_string}".format(table_name=table_name, set_string=set_string)
    if condition is not None:
        update_str += " WHERE {condition}".format(condition=condition)
    cur = g.db.cursor()
    cur.execute(update_str, parameters.values())
    g.db.commit()
