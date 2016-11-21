create type task_status as enum ('done','progressing','canceled','blocked');

alter table task add column status task_status not null  default 'progressing';

create table comment (
  comment_id bigserial primary key,
  task_id uuid references task(task_id),
  account_id bigint references account(account_id),
  content text,
  created_at timestamp with time zone not null default (current_timestamp),
  modified_at timestamp with time zone not null default (current_timestamp)
);

create trigger comment_modified_at_trigger
before update on comment
for each row
execute procedure update_modified_at();
