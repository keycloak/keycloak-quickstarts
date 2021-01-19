#!/bin/bash

mkdir test-logs

mvn clean test -Pwildfly-managed -f action-token-required-action -B &> test-logs/action-token-required-action.log
mvn clean test -Pwildfly-managed -f action-token-authenticator -B &> test-logs/action-token-authenticator.log
mvn clean test -Pwildfly-managed -f app-angular2 -B &> test-logs/app-angular2.log
mvn clean test -Pkeycloak-remote -f user-storage-simple -B &> test-logs/user-storage-simple.log
mvn clean test -Pkeycloak-remote -f user-storage-jpa -B &> test-logs/user-storage-jpa.log

testsWithErrors=()

for log in test-logs/*.log; do
  if grep -q "BUILD FAILURE" "$log"; then
    fileName=$(echo "$log" | sed -r "s/.+\/(.+)\..+/\1/")
    testsWithErrors+=("$fileName")
  fi
done

if [ ${#testsWithErrors[@]} -eq 0 ]; then
  echo "--------------------------------"
  echo "NO TEST ERRORS FOUND"
  echo "--------------------------------"
else
  echo "--------------------------------"
  echo "FAILED TESTS:"
  printf '%s\n' "${testsWithErrors[@]}"
  echo "--------------------------------"
  echo "Check individual logs for details"
  exit 1
fi