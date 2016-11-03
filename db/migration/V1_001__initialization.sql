create or replace function update_modified_at()
  returns trigger as $$
begin
  new.modified_at = current_timestamp;
  return new;
end;
$$ language 'plpgsql';

create extension if not exists pgcrypto;

-- alias: a
create table account (
  account_id bigserial primary key,
  name varchar(100) not null,
  email varchar(254),
  password_hash varchar(64) not null,
  enabled boolean not null default true,
  created_at timestamp with time zone not null default (current_timestamp),
  modified_at timestamp with time zone
);

create unique index account_email_idx
  on account (lower(email));

create trigger account_modified_at_trigger
before update on account
for each row
execute procedure update_modified_at();

-- alias: t
create table team (
  team_id uuid primary key,
  name varchar(100) not null,
  team_description varchar(254),
  enabled boolean not null default true,
  created_at timestamp with time zone not null default (current_timestamp),
  modified_at timestamp with time zone
);

create trigger team_modified_at_trigger
before update on team
for each row
execute procedure update_modified_at();

-- alias: p
create table project (
  project_id uuid primary key,
  team_id uuid references team (team_id) not null,
  name varchar(100) not null,
  project_description varchar(254),
  enabled boolean not null default true,
  created_at timestamp with time zone not null default (current_timestamp),
  modified_at timestamp with time zone
);

create trigger project_modified_at_trigger
before update on project
for each row
execute procedure update_modified_at();

-- alias: tsklst
create table task_list (
  task_list_id uuid primary key,
  project_id uuid references project (project_id) not null,
  name varchar(100) not null,
  task_list_description varchar(254),
  enabled boolean not null default true,
  created_at timestamp with time zone not null default (current_timestamp),
  modified_at timestamp with time zone
);

create trigger task_list_modified_at
before update on task_list
for each row
execute procedure update_modified_at();

-- alias: tsk
create table task (
  task_id uuid primary key,
  task_list_id uuid references task_list (task_list_id) not null,
  name varchar(100) not null,
  task_description varchar(254),
  enabled boolean not null default true,
  created_at timestamp with time zone not null default (current_timestamp),
  modified_at timestamp with time zone
);

create trigger task_modified_at
before update on task
for each row
execute procedure update_modified_at();

create table account_team (
  account_id bigint references account (account_id),
  team_id uuid references team (team_id),
  created_at timestamp with time zone not null default (current_timestamp)
);

create table account_project (
  account_id bigint references account (account_id),
  project_id uuid references project (project_id),
  created_at timestamp with time zone not null default (current_timestamp)
);

create table account_task_list (
  account_id bigint references account (account_id),
  task_list_id uuid references task_list (task_list_id),
  created_at timestamp with time zone not null default (current_timestamp)
);

create table account_task (
  account_id bigint references account (account_id),
  task_id uuid references task (task_id),
  created_at timestamp with time zone not null default (current_timestamp)
);