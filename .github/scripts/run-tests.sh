#!/bin/bash
set -o pipefail

mkdir test-logs

tests_with_errors=()

run_tests() {
  module=$1
  args="${*:2}"
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
    args="$args -s .github/maven-settings.xml"
  fi
  if [ -n "$CHROMEWEBDRIVER" ]; then
    args="$args -Dwebdriver.chrome.driver=$CHROMEWEBDRIVER/chromedriver"
  else
    args="$args -Dwebdriver.chrome.driver=/usr/local/bin/chromedriver"
  fi
  args="$args -D$module"
  log_file=${module////_}.log
  if ! mvn clean install -Dnightly $args -B 2>&1 | tee test-logs/$log_file; then
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

if [[ ( -n "$GITHUB_BASE_REF" &&  "$GITHUB_BASE_REF" == "latest" ) ]] || [[ ( -n "$QUICKSTART_BRANCH" && "$QUICKSTART_BRANCH" != "main" ) ]]; then
  export KEYCLOAK_VERSION=$(mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec)
else
  export KEYCLOAK_VERSION="999.0.0-SNAPSHOT"
fi

if [ -n "$KEYCLOAK_VERSION" ]; then
  JS_VERSION_OPTION="-Dkeycloak.js.version=$KEYCLOAK_VERSION"
else
  JS_VERSION_OPTION=""
fi

# we need to run authz springboot tests first as they are the only ones relying on manual js-policies deployment
# other tests deploy (and the removes) the policies automatically which then later removes even the manually deployed ones

if [ "$1" = "jakarta" ]; then
  echo "Running tests with jakarta profile"
  run_tests jakarta
elif [ "$1" = "extension" ]; then
  run_tests extension
elif [ "$1" = "nodejs" ]; then
  npm -C nodejs/resource-server install
  npm -C nodejs/resource-server ci
  npm -C nodejs/resource-server start&
  if ! npm -C nodejs/resource-server test 2>&1 | tee test-logs/nodejs_resource-server.log; then
    tests_with_errors+=("nodejs/resource-server")
  fi
elif [ "$1" = "js" ]; then
  npm -C js/spa install
  npm -C js/spa ci
  npx -C js/spa playwright install-deps chromium
  npx -C js/spa playwright install chromium
  if ! npm -C js/spa test 2>&1 | tee test-logs/js_spa.log; then
    tests_with_errors+=("js/spa")
  fi
elif [ "$1" = "spring" ]; then
  run_tests spring
fi

print_failed_tests
