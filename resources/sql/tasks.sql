-- :name create-task-list :!
insert into task_list (task_list_id, project_id, name, task_list_description)
values (:task-list-id, :project-id, :name, :description);

-- :name create-task :!
insert into task (task_id, task_list_id, name, task_description)
values (:task-id, :task-list-id, :name, :description);

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