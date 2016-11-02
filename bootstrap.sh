#!/usr/bin/env bash

profile=${1:-dev}

echo "profile: $profile"

case "$profile" in
  dev)
    database=modus
    host=localhost
    username=$(whoami)
    password=
    lein_profile=dev
    ;;
  *)
    usage
    ;;
esac

export PGPASSWORD=$password
echo "select pg_terminate_backend(pg_stat_activity.pid) from pg_stat_activity where pg_stat_activity.datname = '$database'" | psql --host $host --username $username postgres
dropdb --host $host --username $username $database
createdb --host $host --username $username $database
lein with-profile $lein_profile flyway migrate
