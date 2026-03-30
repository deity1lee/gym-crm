#!/bin/sh
# wait-for.sh

set -e

host="$1"
shift
cmd="$@"

until nc -z "$host" 3306; do
  echo "Waiting for MySQL at $host..."
  sleep 2
done

until nc -z activemq 61616; do
  echo "Waiting for ActiveMQ..."
  sleep 2
done

until nc -z discovery 8761; do
  echo "Waiting for Discovery Service..."
  sleep 2
done

exec $cmd