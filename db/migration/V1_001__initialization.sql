create or replace function update_modified_at()
  returns trigger as $$
begin
  new.modified_at = current_timestamp;
  return new;
end;
$$ language 'plpgsql';

create extension if not exists pgcrypto;