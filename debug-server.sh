#!/usr/bin/env bash
set -e
mvn spring-boot:run -Drun.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005"
