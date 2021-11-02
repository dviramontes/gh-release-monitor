-- :name create-releases-table! :!
-- :doc creates releases table
create table if not exists releases
(
    id                         serial primary key,
    created_at                 timestamptz not null default now(),
    updated_at                 timestamptz not null default now(),
    deleted_at                 timestamptz,
    owner                      varchar(128) not null default '',
    repo                       varchar(128) not null default ''
);

-- :name drop-releases-table! :! :n
-- :doc drops releases table
drop table releases;