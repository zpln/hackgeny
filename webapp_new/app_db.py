import random
from flask import g
from db_utils import *

MAX_40_BIT = 2**40-1


def select_user_id_from_facebook_user_id(fb_user_id):
    user_id = query_db("SELECT user_id FROM fb_users WHERE fb_user_id = ?", [fb_user_id], True)

    if user_id is None:
        return None
    else:
        return user_id["user_id"]


def insert_new_user_id():
    new_id = random.randrange(MAX_40_BIT/2, MAX_40_BIT)
    insert_db("users", {"user_id": new_id})
    return new_id


def insert_fb_user(fb_user_id, user_id):
    insert_db("fb_users", {"fb_user_id": fb_user_id, "user_id": user_id})