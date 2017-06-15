#!/usr/bin/env bash
set -e
mkdir -p ~/.mitm-keystore/
cd ~/.mitm-keystore/
echo "Note, default keystore password is \"changeit\""
current_os="$(uname)"
if [[ "${current_os}" == "Darwin" ]]; then
  sudo keytool -import -alias "mitm-debugging-proxy" -keystore "$(/usr/libexec/java_home)/jre/lib/security/cacerts" -file "mitm-debugging-proxy.pem"
else
  sudo keytool -import -alias "mitm-debugging-proxy" -keystore "$(readlink -f /usr/bin/java | sed "s:bin/java::")lib/security/cacerts" -file "mitm-debugging-proxy.pem"
fi
