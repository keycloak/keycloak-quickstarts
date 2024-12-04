#!/bin/bash -e

# Sets new version of the project in all pom.xml files

NEW_VERSION=$1

mvn versions:set -DnewVersion=$NEW_VERSION -DgenerateBackupPoms=false -DgroupId=org.keycloak* -DartifactId=*
