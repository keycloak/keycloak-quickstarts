#!/bin/bash -e

if [ $1 == "group1" ]; then
  for i in `mvn -q --also-make exec:exec -Dexec.executable="pwd" | awk -F '/' '{if (NR > 1) print $NF}'`;
  do
    # FIXME Workaround to skip Angular.js app on Travis CI while we figure out the best way to fix the issues with Selenium
    if [ "$i" = "app-angular2" -o "$i" = "app-authz-uma-photoz" -o "$i" = "app-authz-photoz" -o "$i" = "photoz-html5-client" -o "$i" = "photoz-js-policies" -o "$i" = "photoz-restful-api" -o "$i" = "photoz-testsuite" -o "$i" = "app-profile-jee-html5" ]; then
      continue
    fi
    mvn -B -s maven-settings.xml clean install -Pwildfly-managed -Denforcer.skip=true -f $i
  done
fi

if [ $1 == "group2" ]; then
  mvn -B -s maven-settings.xml test -Pwildfly-managed -f action-token-authenticator/pom.xml </dev/null
  mvn -B -s maven-settings.xml test -Pwildfly-managed -f action-token-required-action/pom.xml </dev/null
fi

if [ $1 == "group3" ]; then
  mvn -f fuse63/pom.xml -B -s maven-settings.xml clean install
  mvn -f fuse70/pom.xml -B -s maven-settings.xml clean install
fi

if [ $1 == "group4" ]; then
  cd app-authz-springboot && mvn -B -s ../maven-settings.xml clean test -Pspring-boot -q
  cd ../service-springboot-rest && mvn -B -s ../maven-settings.xml clean test -Pspring-boot -q
  mvn spring-boot:run >/dev/null&
  cd ../app-springboot
  mvn -B -s ../maven-settings.xml clean test -Pspring-boot
fi

if [ $1 == "group5" ]; then
  mvn -B -s maven-settings.xml test -Pkeycloak-remote -f user-storage-jpa
  mvn -B -s maven-settings.xml test -Pkeycloak-remote -f user-storage-simple
fi
if [ $1 == "group6" ] && [ $TRAVIS_PULL_REQUEST == "false" ]; then
 ./productize.sh
  exit 0
fi  
if [ $1 == "group7" ]; then
  mvn -B -s maven-settings.xml test -Pkeycloak-remote -f event-listener-sysout
  mvn -B -s maven-settings.xml test -Pkeycloak-remote -f event-store-mem
fi
