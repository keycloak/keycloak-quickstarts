# Custom Keycloak Admin UI

This is a template to build a custom Keycloak Account UI using the [Keycloak Admin UI](https://npmjs.com/package/@keycloak/keycloak-admin-ui) package.

## Getting started

To use HMR (Hot module replacement) in development, run the following command:

```bash
pnpm i
pnpm run dev
```
Then start the Keycloak server:

```bash
pnpm run start-keycloak
```

open the admin-console in your browser (http://localhost:8080/), you will see the custom UI.

## Build

To build the application for production, run the following command:

```bash
mvn install
```
This will create a "jar" file in the `target` directory that you can deploy to your Keycloak server by copying it to the `providers` directory.