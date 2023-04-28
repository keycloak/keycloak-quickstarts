# Customizing Keycloak Themes

The Keycloak Admin UI, which is the interface for managing Keycloak, is built with [PatternFly](https://www.patternfly.org/v4/) - a set of UI design patterns and resources that can be used to create consistent, user-friendly interfaces for web applications.

## Creating Themes

With themes, it is possible to configure the Admin UI's look and feel. To ensure consistency and maintainability of the custom Keycloak theme, we advise using PatternFly best practices when customizing the Keycloak Admin UI. This includes following the recommended folder structure and using PatternFly CSS classes and guidelines when creating custom styles and overrides.

### Required Folder Structure

The recommended folder structure for a custom Keycloak theme is as follows:

├── mytheme/
│ ├── admin/
│ │ ├── resources/
│ │ | ├── css/
│ │ | | ├── styles.css
│ │ ├── theme.properties

In order to create a custom theme, there is a `theme.properties` file, along with a `resources` sub-folder that encompasses directories for CSS files, JavaScript files, and images. Within the `css` sub-folder, there is a `styles.css` file, which allows the addition of customized PatternFly styles. The `theme.properties` is a configuration file defines attributes such as the parent theme, and theme css, theme parent and title. The `theme.properties` file is used to register the theme with the Keycloak server and integrate it with the Keycloak user interface.

### Customizing with PatternFly Best Practices

Overrides to PatternFly variables should be made at the `:root` level for global variables or at the top-level component selector for component variables (for example, `.pf-c-\*`), as these overrides will cascade down to children elements accordingly. The examples are provided in the `styles.css` file.

If you utilize the provided stylesheet, upon logging in, the following will be visible to you.

![Keycloak Info Page](./img/keycloakInfo.png "Keycloak Info Page")

When you access the Clients section, this is the view that will be presented to you.

![Keycloak Clients Page](./img/keycloakClients.png "Keycloak Clients Page")

![Keycloak Create A New Client Page](./img/keycloakNewClient.png "Keycloak Create A New Client Page")

**Built-in themes should not be modified directly, instead a custom theme should be created.**

## Disclaimer

The way PatternFly variables are used in the Admin UI is subject to change, as we may update and modify the design system to better suit the needs of our users. Additionally, PatternFly, the design system on which our UI is based, may also make changes to their variables in future updates. As such, any information, analysis, or conclusions drawn from the current use of PatternFly variables in the Admin UI may not necessarily hold true or remain valid in the future. It is important to consider the potential for changes in the way we use PatternFly variables and any updates to the PatternFly design system, and to update any relevant information accordingly.
