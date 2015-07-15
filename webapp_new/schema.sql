CREATE TABLE IF NOT EXISTS "users" (
    "primary" INTEGER PRIMARY KEY NOT NULL UNIQUE,
    "user_id" INTEGER NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS "fb_users" (
    "primary" INTEGER PRIMARY KEY NOT NULL UNIQUE,
    "fb_user_id" INTEGER NOT NULL UNIQUE,
    "user_id" INTEGER NOT NULL UNIQUE
);