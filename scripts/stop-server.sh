#!/bin/bash

. scripts/version.sh

${KEYCLOAK}/bin/jboss-cli.sh --connect controller=127.0.0.1:10090 command=:shutdown
