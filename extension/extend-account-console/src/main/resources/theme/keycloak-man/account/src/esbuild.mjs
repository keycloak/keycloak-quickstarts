#!/usr/bin/env node
import * as esbuild from 'esbuild'

await esbuild.build({
  entryPoints: ['app/content/keycloak-man/KeycloakManLovesJsx.tsx'],
  bundle: true,
  format: "esm",
  packages: "external",
  loader: { '.tsx': 'tsx' },
  outdir: '../resources/content/keycloak-man',
})