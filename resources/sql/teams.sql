-- :name create-team :< :!
insert into team (name, team_description)
values (:name, :description)
returning team_id;

-- :name add-account-to-team :!
insert into account_team (account_id, team_id)
  select
    :account-id,
    :team-id
  where not exists(select 1
                    from account_team
                    where account_id = :account-id and team_id = :team-id);

-- :name remove-account-from-team :!
delete from account_team
where account_id = :account-id and team_id = :team-id;

-- :name set-enabled :!
update team
set enabled = :enabled
where team_id = :team-id;

-- :name update-team-name :!
update team
set name = :name
where team_id = :team-id;