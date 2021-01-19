#!/bin/bash
set -o pipefail

mkdir test-logs

tests_with_errors=()

run_tests() {
  module=$1
  args="${*:2}"
  echo "Args: $args"
  printf "\n\n\n*****************************************\n"
  echo "Running tests for $module QS"
  echo "*****************************************"
  if ! mvn clean test -f $module $args -B 2>&1 | tee test-logs/$module.log; then
    tests_with_errors+=("$module")
  fi
}

print_failed_tests() {
  printf "\n\n\n*****************************************\n\n\n"
  if [ ${#tests_with_errors[@]} -eq 0 ]; then
    echo "CONGRATS, NO TEST ERRORS FOUND"
  else
    echo "FAILED TESTS:"
    printf -- '- %s\n' "${tests_with_errors[@]}"
    echo "Check individual logs for details"
    exit 1
  fi
}

run_tests action-token-authenticator -Pwildfly-managed
run_tests action-token-required-action -Pwildfly-managed
run_tests app-angular2 -Pwildfly-managed
run_tests app-authz-jee-servlet -Pwildfly-managed
run_tests app-authz-jee-vanilla -Pwildfly-managed
run_tests app-authz-photoz -Pwildfly-managed
run_tests app-authz-rest-employee -Pwildfly-managed
run_tests app-authz-rest-springboot -Pwildfly-managed
run_tests app-authz-spring-security -Pwildfly-managed
run_tests app-authz-springboot -Pspring-boot
run_tests app-authz-uma-photoz -Pwildfly-managed
run_tests app-jee-html5 -Pwildfly-managed
run_tests app-jee-jsp -Pwildfly-managed
run_tests app-profile-jee-html5 -Pwildfly-managed
run_tests app-profile-jee-jsp -Pwildfly-managed
run_tests app-profile-jee-vanilla -Pwildfly-managed
run_tests app-profile-saml-jee-jsp -Pwildfly-managed
run_tests event-listener-sysout -Pkeycloak-remote
run_tests event-store-mem -Pkeycloak-remote
run_tests extend-account-console -Pwildfly-managed
run_tests fuse63 # no tests but at least let's try to compile it
run_tests fuse70 # no tests but at least let's try to compile it
run_tests service-jee-jaxrs -Pwildfly-managed
run_tests service-springboot-rest -Pspring-boot
run_tests user-storage-jpa -Pkeycloak-remote
run_tests user-storage-simple -Pkeycloak-remote

mvn -f service-springboot-rest spring-boot:run >/dev/null&
run_tests app-springboot -Pspring-boot

# service-nodejs tests
npm -C service-nodejs start >/dev/null&
if ! npm -C service-nodejs test 2>&1 | tee test-logs/service-nodejs.log; then
  tests_with_errors+=("service-nodejs")
fi

print_failed_tests