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

lein with-profile $lein_profile flyway migrate
lein run -m modus.main/-main