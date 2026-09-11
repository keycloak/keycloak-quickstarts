FROM fedora:42

RUN dnf install -y -q 389-ds-base && dnf clean all

RUN dscreate create-template /tmp/ds.inf \
    && sed -i \
        -e 's/;root_password = .*/root_password = admin1234/' \
        -e 's/;port = .*/port = 3389/' \
        -e 's/;suffix = .*/suffix = dc=example,dc=org/' \
        -e 's/;self_sign_cert = .*/self_sign_cert = False/' \
        -e 's/;create_suffix_entry = .*/create_suffix_entry = True/' \
        /tmp/ds.inf \
    && dscreate from-file /tmp/ds.inf \
    && rm /tmp/ds.inf \
    && dsctl localhost stop

EXPOSE 3389

CMD ["/usr/sbin/ns-slapd", "-D", "/etc/dirsrv/slapd-localhost", "-d", "0", "-i", "/run/dirsrv/slapd-localhost.pid"]
