-- :name create-projects :< :!
insert into project (project_id, team_id, name, project_description)
values (:project-id, :team-id, :name, :description)
returning project_id;

-- :name add-account-to-project :!
insert into account_project
  select
    :account-id,
    :project-id
  where not exists(select 1 from account_project
                    where account_id = :account-id
                      and project_id = :project-id);

-- :name remove-account-from-project :!
delete from account_project
where account_id = :account-id and project_id = :project-id;
