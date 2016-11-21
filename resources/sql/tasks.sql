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
where account_id = :account-id and task_list_id = :task-list-id;

-- :name get-task-list-by-project-id :? :*
select task_list_id, name, task_list_description
from task_list
where project_id = :project-id
  and enabled = true ;

-- :name update-task-list :!
update task_list
set name = coalesce(:name, name),
    task_list_description = coalesce(:description, task_list_description)
where task_list_id  = :task-list-id;

-- :name get-task-list-by-id :? :1
select task_list_id, project_id, name, task_list_description
from task_list
where task_list_id = :task-list-id;

-- :name get-task-by-task-list-id :? :*
select task_id, task_list_id, name, task_description, status
from task
where task_list_id = :task-list-id
  and enabled = true;

-- :name update-task :!
update task
set name = coalesce(:name, name),
    task_description = coalesce(:description, task_description),
    task_list_id = coalesce(:task-list-id, task_list_id),
    status = coalesce(:status, status)
where task_id = :task-id;

-- :name get-task-by-id :? :1
select task_id, task_list_id, name, task_description, status
from task
where task_id = :task-id;

-- :name create-comment-for-task :!
insert into comment (task_id, account_id, content)
values (:task-id, :account-id, :content);

-- :name get-comment-by-task-id :? :*
select comment_id, account.name, content, comment.created_at, comment.modified_at
from comment join account using(account_id)
where task_id = :task-id
order by comment.created_at;