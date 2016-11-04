-- :name create-account :<! :1
insert into account (name, email, password_hash)
  select
    :name,
    :email,
    crypt(:password, gen_salt('bf'))
  where not exists(select 1
                   from account
                   where lower(email) = lower(:email))
returning account_id;

-- :name update-account-name :!
update account
set name = COALESCE(:name, name)
where account_id = :account-id;

-- :name update-account-email :!
update account
set email = :email
where account_id = :account-id and email != :email;

-- :name check-password :? :1
select count(account_id) as account_count
from account
where account_id = :account-id and password_hash = crypt(:password, password_hash) :: text;

-- :name change-password-hash :!
update account
set password_hash = crypt(:new - password, gen_salt('bf'))
where account_id = :account-id;

-- :name set-enabled :!
update account
set enabled = :enabled
where account_id = :account-id;

-- :name find-account-by-email :? :1
select account_id from account
where email = :email;

