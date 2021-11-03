-- :name create-releases-table! :!
-- :doc creates releases table
create table if not exists releases
(
    id                         serial primary key,
    created_at                 timestamptz not null default now(),
    updated_at                 timestamptz not null default now(),
    deleted_at                 timestamptz,
    owner                      varchar(128) not null default '',
    repo                       varchar(128) not null default '',
    details                    jsonb not null default '{}',
    UNIQUE                     (owner, repo)
);

-- :name drop-releases-table! :! :n
-- :doc drops releases table
drop table releases;

-- :name create-release! :<!
-- :doc creates a new release record
insert into releases (
    owner,
    repo,
    details
) values (
    :owner,
    :repo,
    :details
) returning *;

-- :name get-releases :? :*
-- :doc retrieves all release records
select * from releases;

-- :name delete-release! :! :n
-- :doc deletes a release record given the id
delete from releases
where id = :id;

-- :name get-release-by-id :? :1
-- :doc retrieves a release record by id
select * from releases
where id = :id;