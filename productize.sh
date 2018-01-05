#!/bin/bash -e

# surrounding_tags will be master in the end
git config user.name "${GH_USER_NAME}"
git config user.email "{GH_USER_EMAIL}"


# Rename Keycloak to Red Hat SSO
find . -type f -name "*README*" -exec sed -i 's@<span>Keycloak</span>@Red Hat SSO@g' {} +
# Rename WildFly to JBoss EAP
find . -type f -name "*README*" -exec sed -i 's@<span>WildFly 10</span>@JBoss EAP@g' {} +
find . -type f -name "*README*" -exec sed -i 's@<span>WildFly</span>@JBoss EAP@g' {} +

# Rename env
find . -type f -name "*README*" -exec sed -i 's@<span>KEYCLOAK_HOME</span>@RHSSO_HOME@g' {} +
find . -type f -name "*README*" -exec sed -i 's@<span>WILDFLY_HOME</span>@EAP_HOME@g' {} +

# Rename commands
find . -type f -name "*README*" -exec sed -i 's@KEYCLOAK_HOME/bin@RHSSO_HOME/bin@g' {} +
find . -type f -name "*README*" -exec sed -i 's@KEYCLOAK_HOME\\bin@RHSSO_HOME\\bin@g' {} +
find . -type f -name "*README*" -exec sed -i 's@WILDFLY_HOME/bin@EAP_HOME/bin@g' {} +
find . -type f -name "*README*" -exec sed -i 's@WILDFLY_HOME\\bin@EAP_HOME\\bin@g' {} +

# Remove JBoss Repo
sed -i '/<repositories>/,/<\/repositories>/ d' pom.xml

# Add RHSSO Repo
sed -i '/<\/modules>/{ 
    a \    </modules>
    a \ 
    r scripts/ssorepo.txt
    d 
}' pom.xml

#update version to productized versions
find . -type f -name "*pom.xml*" -exec sed -i 's@SNAPSHOT</version>@redhat-2</version>@g' {} +

#rename names in POMs
find . -type f -name "*pom.xml*" -exec sed -i 's@<name>Keycloak Quickstart@<name>Red Hat SSO Quickstart@g' {} +


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
