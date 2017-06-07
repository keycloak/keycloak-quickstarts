#!/bin/bash -e

if [ $1 == "group1" ]; then
  for i in `mvn -q --also-make exec:exec -Dexec.executable="pwd" | awk -F '/' '{if (NR > 1) print $NF}'`;
  do
    mvn -s maven-settings.xml clean install -Pwildfly-managed -Denforcer.skip=true -f $i
  done
fi

if [ $1 == "group2" ]; then
  mvn -B -s maven-settings.xml test -Pkeycloak-remote -f user-storage-jpa
  mvn -B -s maven-settings.xml test -Pkeycloak-remote -f user-storage-simple
fi

if [ $1 == "group3" ]; then
  cd fuse && mvn -B -s ../maven-settings.xml clean install -Pfuse-server
fi

