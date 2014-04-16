#!/bin/sh
#
# Adapted from daffodil.sh
#

VERSION=0.5-0-SNAPSHOT
SCALA_VERSION=2.10
MAINCLASS=com.xmlcalabash.drivers.Main
BINDIR="$( dirname "$0" )"
CLASSPATH=$BINDIR/lib/'*':$BINDIR/lib_managed/'*':$BINDIR/target/scala-${SCALA_VERSION}/daffodil-calabash-extension_${SCALA_VERSION}-${VERSION}.jar

SOPTS="-J-Xms1024m -J-Xmx1024m -J-XX:MaxPermSize=256m -J-XX:ReservedCodeCacheSize=128m"

exec scala $SOPTS -cp "$CLASSPATH" "$MAINCLASS" -c $BINDIR/etc/calabash-config.xml "$@"
