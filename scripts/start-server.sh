#!/bin/bash

. scripts/version.sh

set -v

function waitForServer {
  # Give the server some time to start up. Look for a well-known
  # bit of text in the log file. Try at most 50 times before giving up.
  C=50
  while [ $C -gt 0 ]
  do
    grep "Keycloak ${VERSION} (WildFly Core 2.0.10.Final) started" keycloak.log
    if [ $? -eq 0 ]; then
      echo "Server started."
      C=0
    else
      echo -n "."
      C=$(( $C - 1 ))
    fi
    sleep 1
  done
}

ARCHIVE="${KEYCLOAK}.tar.gz"
DIST="keycloak-server-dist"
URL="https://repo1.maven.org/maven2/org/keycloak/$DIST/${VERSION}/$DIST-${VERSION}.tar.gz"
# Download keycloak server if we don't already have it
if [ ! -e $KEYCLOAK ]
then
  curl -o $ARCHIVE $URL
  tar xzf $ARCHIVE
  rm -f $ARCHIVE
fi

$KEYCLOAK/bin/add-user-keycloak.sh -u admin -p admin
# Start the server
$KEYCLOAK/bin/standalone.sh -Djava.net.preferIPv4Stack=true \
                            -Djboss.socket.binding.port-offset=100 > keycloak.log 2>&1 &

waitForServer
