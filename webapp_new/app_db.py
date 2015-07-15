import random
from flask import g

MAX_40_BIT = 2**40-1


def insert(func):
    """
    A decorator for a function that inserts something to the db, or alters it
    Simply makes sure to commit after the insert
    """
    def decorator(*args, **kwargs):
        result = func(*args, **kwargs)
        g.db.commit()
        return result
    return decorator


def select_user_id_from_facebook_user_id(fb_user_id):
    user_id = g.db.execute("SELECT user_id FROM fb_users WHERE fb_user_id=:fb_user_id", {"fb_user_id": fb_user_id})
    row = user_id.fetchone()

    if row is None:
        return None
    else:
        return row[0]


@insert
def insert_new_user_id():
    new_id = random.randrange(MAX_40_BIT/2, MAX_40_BIT)
    g.db.execute("INSERT INTO users (user_id) VALUES (:user_id)",
                 {"user_id": new_id})
    return new_id


@insert
def insert_fb_user(fb_user_id, user_id):
    g.db.execute("INSERT INTO fb_users (fb_user_id, user_id) VALUES (:fb_user_id, :user_id)",
                 {"fb_user_id": fb_user_id, "user_id": user_id})