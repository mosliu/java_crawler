#!/usr/bin/env bash
JAVA="java"
JAR1="java_crawler-1.0-SNAPSHOT.jar"
function runnohup() {
        nohup $JAVA  -Xmx4096m -Denv=pro -jar $JAR1  >> ./admin.out 2>&1 &
}
function run() {
        $JAVA  -Xmx4096m -Denv=pro -jar $JAR1
}


action=$1
[ -z $1 ] && action=run
case "$action" in
nohup)
        runnohup
        ;;
run)
        run
        ;;
*)
        echo "Arguments error! [${action} ]"
        echo "Usage: $(basename $0) {nohup|run}"
        ;;
esac
