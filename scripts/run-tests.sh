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
    if [ "$module" == "extension/action-token-authenticator" ] \
        || [ "$module" == "extension/action-token-required-action" ] \
        || [ "$module" == "extension/event-listener-sysout" ] \
        || [ "$module" == "extension/event-store-mem" ] \
        || [ "$module" == "extension/extend-account-console" ]; then
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
  log_file=${module////_}.log
  if ! mvn clean install -Dnightly -f $module $args -B 2>&1 | tee test-logs/$log_file; then
    tests_with_errors+=("$module")
  fi
  printf "\n\n\n*****************************************\n"
  echo "Completed tests for $module QS"
  echo "*****************************************"
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

# TODO Update for Quarkus dist
#run_tests action-token-authenticator -Pwildfly-managed
#run_tests action-token-required-action -Pwildfly-managed

. scripts/export-keycloak-version.sh

if [ -n "$KEYCLOAK_VERSION" ]; then
  JS_VERSION_OPTION="-Dkeycloak.js.version=$KEYCLOAK_VERSION"
else
  JS_VERSION_OPTION=""
fi

# we need to run authz springboot tests first as they are the only ones relying on manual js-policies deployment
# other tests deploy (and the removes) the policies automatically which then later removes even the manually deployed ones

if [ "$1" = "jakarta" ]; then
  echo "Running tests with jakarta profile"
  run_tests extension/event-listener-sysout -Djakarta -Pkeycloak-remote
  run_tests extension/event-store-mem -Djakarta -Pkeycloak-remote
  run_tests extension/user-storage-simple -Djakarta -Pkeycloak-remote
  run_tests extension/user-storage-jpa -Djakarta -Pkeycloak-remote
  run_tests jakarta/app-authz-jakarta-servlet -Djakarta -Pwildfly-managed
  run_tests jakarta/app-jakarta-rs -Djakarta -Pwildfly-managed
else if [ "$1" = "nodejs" ]; then
  npm -C nodejs/service-nodejs install && npm -C nodejs/service-nodejs start >/dev/null& && npm -C nodejs/service-nodejs test
  npm -C nodejs/spa install && npm -C nodejs/spa test
else
  run_tests javaee/app-profile-saml-jee-jsp -Pwildfly-managed
fi

print_failed_tests
