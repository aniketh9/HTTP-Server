#!/bin/sh
CREATE=`which "$0" 2>/dev/null`
[ $? -gt 0 -a -f "$0" ] && CREATE="./$0"
java=java
if test -n "$JAVA_HOME"; then
    java="$JAVA_HOME/bin/java"
fi
exec "$java" $java_args -jar $CREATE "$@"
exit 1
