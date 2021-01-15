#!/bin/bash
set -e

mvn clean test -Pkeycloak-remote -f user-storage-simple -B -s maven-settings.xml