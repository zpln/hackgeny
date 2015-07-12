from flask import Flask, url_for, redirect
import os

app = Flask(__name__)

STATIC_DIR = os.path.join(os.path.dirname(__file__), "static")


@app.route("/")
def index():
    return redirect(url_for("login"))


@app.route("/login")
def login():
    return open(os.path.join(STATIC_DIR, "login/login.html"), 'rb').read()


if __name__ == "__main__":
    app.run(debug=True)
