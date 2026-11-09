/**
 * ES module loaded by the UiScriptProvider. Must export a default function that
 * receives { realm, i18n, adminClient } and returns an HTMLElement.
 */
export default function ({ realm, i18n, adminClient }) {
  const root = document.createElement("div");
  root.className = "pf-v5-c-content";
  root.setAttribute("data-testid", "hello-realm-script");

  const title = document.createElement("h1");
  title.textContent = i18n.t("helloRealmTitle", {
    defaultValue: `Hello ${realm}`,
    realm,
  });

  const info = document.createElement("p");
  info.setAttribute("data-testid", "hello-realm-info");
  info.textContent = `Using admin client at ${adminClient.baseUrl}`;

  const button = document.createElement("button");
  button.className = "pf-v5-c-button pf-m-primary";
  button.setAttribute("data-testid", "hello-realm-load");
  button.textContent = i18n.t("loadRealmDetails", {
    defaultValue: "Load realm details",
  });
  button.addEventListener("click", async () => {
    try {
      const representation = await adminClient.realms.findOne({ realm });
      info.textContent = `Display name: ${representation?.displayName || realm}`;
    } catch (error) {
      info.textContent = `Failed to load realm: ${error}`;
    }
  });

  root.append(title, info, button);
  return root;
}
