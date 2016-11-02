-- alias: a
create table account (
  account_id    bigserial primary key,
  name          varchar(100)             not null,
  email         varchar(254),
  password_hash varchar(64)              not null,
  enabled       boolean                  not null default true,
  created_at    timestamp with time zone not null default (current_timestamp),
  modified_at   timestamp with time zone
);

create unique index account_email_idx
  on account (lower(email));

create trigger account_modified_at_trigger
before update on account
for each row
execute procedure update_modified_at();