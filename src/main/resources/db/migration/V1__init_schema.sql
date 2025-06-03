CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE tweets
(
    id         UUID PRIMARY KEY,
    user_id    UUID         NOT NULL,
    content    VARCHAR(280) NOT NULL,
    created_at TIMESTAMP    NOT NULL
);

CREATE INDEX idx_tweets_user_created_at ON tweets (user_id, created_at DESC);

CREATE TABLE follows
(
    follower_id UUID NOT NULL,
    followed_id UUID NOT NULL,
    PRIMARY KEY (follower_id, followed_id)
);

CREATE INDEX idx_follows_follower ON follows (follower_id);
