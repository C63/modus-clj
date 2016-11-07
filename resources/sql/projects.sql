-- :name create-projects :!
insert into project (project_id, team_id, name, project_description)
values (:project-id, :team-id, :name, :description);

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

-- :name update-project :!
update project
set project_description = COALESCE(:description, project_description),
    name = COALESCE(:name, name)
where project_id = :project-id;

-- :name get-projects-by-account-id :? :*
select project.project_id, project.name, project.project_description
from project
  join account_project using (project_id)
where account_id = :account-id
  and project.enabled = true;

-- :name get-projects-by-team-id :? :*
select project.project_id, project.name, project.project_description
from project
where project.team_id = :team-id
  and project.enabled = true;

-- :name check-relationship-account-project :? :1
select count(account_id) as relationship_count
from account_project
  join account using (account_id)
where account_project.account_id = :account-id
  and account_project.project_id = :project-id
  and account.enabled = true ;
