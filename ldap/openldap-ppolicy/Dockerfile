FROM debian:trixie

RUN apt-get update -qq \
    && echo "slapd slapd/internal/generated_adminpw password admin" | debconf-set-selections \
    && echo "slapd slapd/internal/adminpw password admin" | debconf-set-selections \
    && echo "slapd slapd/password1 password admin" | debconf-set-selections \
    && echo "slapd slapd/password2 password admin" | debconf-set-selections \
    && echo "slapd slapd/domain string example.org" | debconf-set-selections \
    && echo "slapd shared/organization string Example" | debconf-set-selections \
    && DEBIAN_FRONTEND=noninteractive apt-get install -y -qq slapd ldap-utils \
    && apt-get clean && rm -rf /var/lib/apt/lists/* \
    && mkdir -p /run/slapd && chown openldap:openldap /run/slapd

# Set a password for cn=admin,cn=config so cn=config can be managed via
# simple bind over ldap:// (not just SASL EXTERNAL via ldapi:// socket).
# Debian's slapd leaves cn=admin,cn=config without a password by default.
COPY set-config-pw.ldif /tmp/set-config-pw.ldif
RUN slapmodify -n0 -l /tmp/set-config-pw.ldif && rm /tmp/set-config-pw.ldif \
    && chown -R openldap:openldap /etc/ldap/slapd.d

EXPOSE 389

CMD ["slapd", "-d", "256", "-h", "ldap:/// ldapi:///", "-F", "/etc/ldap/slapd.d", "-u", "openldap", "-g", "openldap"]
