# surrounding_tags will be master in the end
git config user.name "${GH_USER_NAME}"
git config user.email "{GH_USER_EMAIL}"


# Rename Keycloak to Red Hat SSO
find . -type f -name "*README*" -exec sed -i 's@<span>Keycloak</span>@Red Hat SSO@g' {} +
# Rename WildFly to JBoss EAP
find . -type f -name "*README*" -exec sed -i 's@<span>WildFly</span>@JBoss EAP@g' {} +
find . -type f -name "*README*" -exec sed -i 's@<span>WildFly 10</span>@JBoss EAP@g' {} +

# Rename env
find . -type f -name "*README*" -exec sed -i 's@<span>KEYCLOAK_HOME</span>@RHSSO_HOME@g' {} +
find . -type f -name "*README*" -exec sed -i 's@<span>WILDFLY_HOME</span>@EAP_HOME@g' {} +

# Rename commands
find . -type f -name "*README*" -exec sed -i 's@KEYCLOAK_HOME/bin@RHSSO_HOME/bin@g' {} +
find . -type f -name "*README*" -exec sed -i 's@KEYCLOAK_HOME\\bin@RHSSO_HOME\\bin@g' {} +
find . -type f -name "*README*" -exec sed -i 's@WILDFLY_HOME/bin@EAP_HOME/bin@g' {} +
find . -type f -name "*README*" -exec sed -i 's@WILDFLY_HOME\\bin@EAP_HOME\\bin@g' {} +

# Add RHSSO Repo
sed -i '/<\/project>/{ 
    r scripts/ssorepo.txt
    a \</project>
    d 
}' pom.xml

#update version to SSHO 
./set-version.sh 7.2.0.DR4

#rename groupId in POMs
find . -type f -name "*pom.xml*" -exec sed -i 's@<groupId>org.keycloak.bom</groupId>@<groupId>com.redhat.bom.rh-sso</groupId>@g' {} +
find . -type f -name "*pom.xml*" -exec sed -i 's@<groupId>org.keycloak.quickstarts</groupId>@<groupId>com.redhat.rh-sso</groupId>@g' {} +
find . -type f -name "*pom.xml*" -exec sed -i 's@<groupId>org.keycloak.bom</groupId>@<groupId>com.redhat.bom.rh-sso</groupId>@g' {} +

find . -type f -name "*pom.xml*" -exec sed -i 's@<artifactId>keycloak-quickstart-parent</artifactId>@<artifactId>rh-sso-quickstart-parent</artifactId>@g' {} +
find . -type f -name "*pom.xml*" -exec sed -i 's@<name>Keycloak Quickstart@<name>Red Hat SSO Quickstart@g' {} +

find . -type f -name "*pom.xml*" -exec sed -i 's@<artifactId>keycloak-app-angular2</artifactId>@<artifactId>rh-sso-app-angular2</artifactId>@g' {} +
find . -type f -name "*pom.xml*" -exec sed -i 's@<artifactId>keycloak-app-authz-jee-servlet</artifactId>@<artifactId>rh-sso-app-authz-jee-servlet</artifactId>@g' {} +
find . -type f -name "*pom.xml*" -exec sed -i 's@<artifactId>keycloak-app-authz-jee-vanilla</artifactId>@<artifactId>rh-sso-app-authz-jee-vanilla</artifactId>@g' {} +
find . -type f -name "*pom.xml*" -exec sed -i 's@<artifactId>keycloak-app-jee-html5</artifactId>@<artifactId>rh-sso-app-jee-html5</artifactId>@g' {} +
find . -type f -name "*pom.xml*" -exec sed -i 's@<artifactId>keycloak-app-jee-jsp</artifactId>@<artifactId>rh-sso-app-jee-jsp</artifactId>@g' {} +
find . -type f -name "*pom.xml*" -exec sed -i 's@<artifactId>keycloak-app-profile-jee-html5</artifactId>@<artifactId>rh-sso-app-profile-jee-html5</artifactId>@g' {} +
find . -type f -name "*pom.xml*" -exec sed -i 's@<artifactId>keycloak-app-profile-jee-jsp</artifactId>@<artifactId>rh-sso-app-profile-jee-jsp</artifactId>@g' {} +
find . -type f -name "*pom.xml*" -exec sed -i 's@<artifactId>keycloak-app-profile-jee-vanilla</artifactId>@<artifactId>rh-sso-app-profile-jee-vanilla</artifactId>@g' {} +
find . -type f -name "*pom.xml*" -exec sed -i 's@<artifactId>keycloak-app-profile-jee-saml</artifactId>@<artifactId>rh-sso-app-profile-jee-saml</artifactId>@g' {} +
find . -type f -name "*pom.xml*" -exec sed -i 's@<artifactId>keycloak-app-profile-jee-saml</artifactId>@<artifactId>rh-sso-app-profile-jee-saml</artifactId>@g' {} +
find . -type f -name "*pom.xml*" -exec sed -i 's@<artifactId>keycloak-fuse-parent</artifactId>@<artifactId>rh-sso-fuse-parent</artifactId>@g' {} +
find . -type f -name "*pom.xml*" -exec sed -i 's@<artifactId>keycloak-fuse-app-war-jsp</artifactId>@<artifactId>rh-sso-fuse-app-war-jsp</artifactId>@g' {} +
find . -type f -name "*pom.xml*" -exec sed -i 's@<artifactId>keycloak-fuse-features</artifactId>@<artifactId>rh-sso-fuse-features</artifactId>@g' {} +
find . -type f -name "*pom.xml*" -exec sed -i 's@<artifactId>keycloak-fuse-server</artifactId>@<artifactId>rh-sso-fuse-server</artifactId>@g' {} +
find . -type f -name "*pom.xml*" -exec sed -i 's@<artifactId>keycloak-fuse-service-camel</artifactId>@<artifactId>rh-sso-fuse-service-camel</artifactId>@g' {} +
find . -type f -name "*pom.xml*" -exec sed -i 's@<artifactId>keycloak-service-cxf-jaxrs</artifactId>@<artifactId>rh-sso-service-cxf-jaxrs</artifactId>@g' {} +
find . -type f -name "*pom.xml*" -exec sed -i 's@<artifactId>keycloak-service-jee-jaxrs</artifactId>@<artifactId>rh-sso-service-jee-jaxrs</artifactId>@g' {} +
find . -type f -name "*pom.xml*" -exec sed -i 's@<artifactId>keycloak-user-storage-jpa</artifactId>@<artifactId>rh-sso-user-storage-jpa</artifactId>@g' {} +
find . -type f -name "*pom.xml*" -exec sed -i 's@<artifactId>keycloak-user-storage-properties</artifactId>@<artifactId>rh-sso-user-storage-properties</artifactId>@g' {} +


git checkout -b prod_staging
git checkout action-token-authenticator/pom.xml
git checkout action-token-required-action/pom.xml 
git checkout app-springboot/pom.xml
git checkout app-springboot/README.md
git rm -r action-token-authenticator
git rm -r action-token-required-action
git rm -r app-springboot 
git status

git commit . -m "rename pom and readme"
git push --force "https://${GH_TOKEN}@${GH_REF}" prod_staging:7.2.x-devel
