-- :name create-team :!
insert into team (team_id, name, team_description)
values (:team-id, :name, :description);

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

-- :name update-team :!
update team
set team_description = COALESCE(:description, team_description),
    name = COALESCE(:name, name)
where team_id = :team-id;

-- :name get-teams-by-account-id :? :*
select team_id, team.name, team_description
from team join account_team using (team_id)
          join account using (account_id)
where account_id = :account-id
  and team.enabled = true;

-- :name check-relationship-account-team :? :1
select count(account_id) as relationship_count
from account_team join account using (account_id)
where account_team.account_id = :account-id
  and account_team.team_id = :team-id
  and account.enabled = true ;
