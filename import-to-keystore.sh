#!/usr/bin/env bash
set -e
mkdir -p ~/.mitm-keystore/
cd ~/.mitm-keystore/
echo "Note, default keystore password is \"changeit\""
sudo keytool -import -alias "mitm-debugging-proxy" -keystore /Library/Java/JavaVirtualMachines/jdk1.8.0_131.jdk/Contents/Home/jre/lib/security/cacerts -file "mitm-debugging-proxy.pem"
