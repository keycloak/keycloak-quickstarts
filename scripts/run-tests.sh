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
  if [ -n "$PRODUCT" ] && [ "$PRODUCT" == "true" ]; then
    args="$args -s $PRODUCT_MVN_SETTINGS  -Dmaven.repo.local=$PRODUCT_MVN_REPO"
    if [ "$module" == "action-token-authenticator" ] \
        || [ "$module" == "action-token-required-action" ] \
        || [ "$module" == "app-authz-springboot-multitenancy" ] \
        || [ "$module" == "event-listener-sysout" ] \
        || [ "$module" == "event-store-mem" ] \
        || [ "$module" == "extend-account-console" ] \
        || [ "$module" == "app-springboot" ]; then
      return 0
    fi
  else
    args="$args -s maven-settings.xml"
  fi
  if [ -n "$CHROMEWEBDRIVER" ]; then
    args="$args -Dwebdriver.chrome.driver=$CHROMEWEBDRIVER/chromedriver"
  else
    args="$args -Dwebdriver.chrome.driver=/usr/local/bin/chromedriver"
  fi
  if ! mvn clean install -f $module $args -B 2>&1 | tee test-logs/$module.log; then
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

. scripts/export-keycloak-version.sh

if [ -n "$KEYCLOAK_VERSION" ]; then
  JS_VERSION_OPTION="-Dkeycloak.js.version=$KEYCLOAK_VERSION"
else
  JS_VERSION_OPTION=""
fi

run_tests app-angular2 -Pwildfly-managed "$JS_VERSION_OPTION"

# we need to run authz springboot tests first as they are the only ones relying on manual js-policies deployment
# other tests deploy (and the removes) the policies automatically which then later removes even the manually deployed ones
run_tests app-authz-rest-springboot -Pspring-boot
run_tests app-authz-spring-security -Pspring-boot
run_tests app-authz-springboot -Pspring-boot
run_tests app-authz-springboot-multitenancy -Pspring-boot
run_tests app-authz-jee-servlet -Pwildfly-managed
run_tests app-authz-jee-vanilla -Pwildfly-managed
run_tests app-authz-photoz -Pwildfly-managed
run_tests app-authz-rest-employee -Pwildfly-managed
run_tests app-authz-uma-photoz -Pwildfly-managed
run_tests app-jee-html5 -Pwildfly-managed
run_tests app-jee-jsp -Pwildfly-managed
run_tests app-profile-jee-html5 -Pwildfly-managed
run_tests app-profile-jee-jsp -Pwildfly-managed
run_tests app-profile-jee-vanilla -Pwildfly-managed
run_tests app-profile-saml-jee-jsp -Pwildfly-managed
run_tests event-listener-sysout -Pkeycloak-remote
run_tests event-store-mem -Pkeycloak-remote
run_tests extend-account-console -Pkeycloak-remote
run_tests fuse63 # no tests but at least let's try to compile it
run_tests fuse70 # no tests but at least let's try to compile it
run_tests service-jee-jaxrs -Pwildfly-managed
run_tests service-springboot-rest -Pspring-boot
run_tests user-storage-jpa -Pkeycloak-remote
run_tests user-storage-simple -Pkeycloak-remote

mvn -f service-springboot-rest spring-boot:run >/dev/null&
run_tests app-springboot -Pspring-boot

# service-nodejs tests
npm -C service-nodejs install
npm -C service-nodejs start >/dev/null&
# Wait for port 3000 to open for at most 30 seconds
{
    I=0
    while ! curl -sfN -o /dev/null http://localhost:3000/service/public && [[ $I -lt 60 ]]; do
         sleep 0.5
         echo -n .
         I=$[$I + 1]
    done
} 2>/dev/null
if ! npm -C service-nodejs test 2>&1 | tee test-logs/service-nodejs.log; then
  tests_with_errors+=("service-nodejs")
fi

print_failed_tests
