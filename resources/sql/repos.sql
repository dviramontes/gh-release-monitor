-- :name create-repos-table! :!
-- :doc creates repos table
create table if not exists repos
(
    id                         serial primary key,
    created_at                 timestamptz not null default now(),
    updated_at                 timestamptz not null default now(),
    deleted_at                 timestamptz,
    name                       varchar(128) not null default ''
);

-- :name drop-repos-table! :! :n
-- :doc drops repos table
drop table repos;