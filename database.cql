CREATE KEYSPACE scalac_twitter WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}  AND durable_writes = true;

CREATE TABLE scalac_twitter.tweets (
    username text,
    tmp bigint,
    tweet_id text,
    body text,
    tags set<text>,
    PRIMARY KEY (username, tmp)
) WITH CLUSTERING ORDER BY (tmp DESC)
    AND default_time_to_live = 0;

CREATE TABLE scalac_twitter.following (
    follower text,
    followee text,
    tmp bigint,
    PRIMARY KEY (follower, followee)
) WITH CLUSTERING ORDER BY (followee ASC)
    AND default_time_to_live = 0;

CREATE TABLE scalac_twitter.users (
    username text,
    password text,
    email text,
    signup_tmp bigint,
    id text,
    PRIMARY KEY (username, password)
) WITH CLUSTERING ORDER BY (password ASC)
    AND default_time_to_live = 0;