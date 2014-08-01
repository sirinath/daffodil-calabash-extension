@echo off
set VERSION=0.5.0
set SCALA_VERSION=2.10
set MAINCLASS=com.xmlcalabash.drivers.Main
set BINDIR=.
set CLASSPATH=%BINDIR%/lib/*;%BINDIR%/lib_managed/*;%BINDIR%/target/scala-%SCALA_VERSION%/daffodil-calabash-extension_%SCALA_VERSION%-%VERSION%.jar

set JOPTS=-Xms1024m -Xmx1024m -XX:MaxPermSize=256m -XX:ReservedCodeCacheSize=128m

rem @echo on
java %JOPTS% %MAINCLASS% -c %BINDIR%/etc/calabash-config.xml %*
