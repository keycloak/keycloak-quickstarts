# Writing tests

We focus primarily on integration/functional level tests. Unit tests are avoided and only recommended for isolated
classes such as small utils. We do not use any mocking frameworks and we will not accept any contributions that adds a 
mocking framework.

When writing tests please follow the same approach as we have taken in the other tests. There are many ways to 
test software and we have chosen ours, so please appreciate that.

The main tests are provided in `src/test/java` for each Quickstart folder. Most of the integration tests are there.

Any test inside the quickstarts should be reasonable and straightforward to understand. But feedback for improvements are always welcome.

When developing your test depending on the feature or enhancement you are testing you may find it best to add to an
existing test, or to write a test from scratch. For the latter, we recommend finding another test that is close to what 
you need and use that as a basis.


# Running integration tests

By default, the integration tests for each quickstart, expect this initial admin user to have `admin` as username and `admin` as password. This is configurable in each `ArquillianTest` class.

```
static {
    try {
        importTestRealm("admin", "admin", "/quickstart-realm.json");
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

If you don't have access to admin's credentials, please import the `quickstart-realm.json` from `src/test/resources`.

To run integration tests for <span>WildFly</span> managed quickstarts use the following command:
```
mvn clean install -Pwildfly-managed -Denforcer.skip=true
```

If you want to run the tests for a quickstart that doesn't need <span>WildFly</span>, you have to use appropriate profile. See a particular quickstart's README for more details.
