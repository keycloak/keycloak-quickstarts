#!/bin/bash -e

if [ $1 == "group1" ]; then
  for i in `mvn -q --also-make exec:exec -Dexec.executable="pwd" | awk -F '/' '{if (NR > 1) print $NF}'`;
  do
    # FIXME Workaround to skip Angular.js app on Travis CI while we figure out the best way to fix the issues with Selenium
    if [ "$i" = "app-angular2" ]; then
      continue
    fi
    mvn -B -s maven-settings.xml clean install -Pwildfly-managed -Denforcer.skip=true -f $i
  done
fi

if [ $1 == "group2" ]; then
  mvn -B -s maven-settings.xml test -Pwildfly-managed -f action-token-authenticator </dev/null
  mvn -B -s maven-settings.xml test -Pwildfly-managed -f action-token-required-action </dev/null
fi

if [ $1 == "group3" ]; then
  cd fuse && mvn -B -s ../maven-settings.xml clean install -Pfuse-server
fi

if [ $1 == "group4" ]; then
  cd app-authz-springboot && mvn -B -s ../maven-settings.xml clean test
  cd app-authz-rest-springboot && mvn -B -s ../maven-settings.xml clean test
  cd ../service-springboot-rest && mvn -B -s ../maven-settings.xml clean test
  mvn spring-boot:run&
  cd ../app-springboot
  mvn -B -s ../maven-settings.xml clean test
fi

if [ $1 == "group5" ]; then
  mvn -B -s maven-settings.xml test -Pkeycloak-remote -f user-storage-jpa
  mvn -B -s maven-settings.xml test -Pkeycloak-remote -f user-storage-simple
fi
if [ $1 == "group6" ] && [ $TRAVIS_PULL_REQUEST == "false" ]; then
 ./productize.sh
  exit 0
fi  
