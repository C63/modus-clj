-- :name create-task-list :< :!
insert into task_list (project_id, name, task_list_description)
values (:project-id, :name, :description)
returning task_list_id;

-- :name create-tasks :< :!
insert into task (task_list_id, name, task_description)
values (:task-list-id, :name, :description)
returning task_id;

-- :name add-account-to-task :!
insert into account_task (account_id, task_id)
  select
    :account-id,
    :task-id
  where not exists(select 1 from account_task
                    where account_id = :account-id
                      and task_id = :task-id);

-- :name remove-account-from-task :!
delete from account_task
where account_id = :account-id and task_id = :task-id;

-- :name add-account-to-task-list :!
insert into account_task_list (account_id, task_list_id)
  select
    :account-id,
    :task-list-id
  where not exists(select 1 from account_task_list
                    where account_id = :account-id
                      and task_list_id = :task-list-id);

-- :name remove-account-from-task-list :!
delete from account_task_list
where account_id = :account-id and task_list_id = :task-list-id