#!/bin/bash

function waitForServer {
  dist=$1
  echo -n "Starting $dist"
  # Give the server some time to start up. Look for a well-known
  # bit of text in the log file. Try at most 50 times before giving up.
  C=50
  while :
  do
    grep "$dist .* (WildFly Core .*) started" keycloak.log
    if [ $? -eq 0 ]; then
      echo " server started."
      break
    elif [ $C -gt 0 ]; then
      echo -n "."
      C=$((C-1))
      sleep 1
    else
      echo " timeout!"
      exit 1
    fi
  done
}

if [ -n "$PRODUCT" ] && [ "$PRODUCT" == "true" ]; then
  "$PRODUCT_DIST/bin/standalone.sh" -Djava.net.preferIPv4Stack=true -Djboss.socket.binding.port-offset=100 > keycloak.log 2>&1 &
  waitForServer "Red Hat Single Sign-On"
else
  # GitHub Actions don't preserve file permissions when downloading artifacts
  chmod +x keycloak-dist/bin/standalone.sh

  # Start the server
  keycloak-dist/bin/standalone.sh -Djava.net.preferIPv4Stack=true -Djboss.socket.binding.port-offset=100 > keycloak.log 2>&1 &
  waitForServer "Keycloak"
fi
