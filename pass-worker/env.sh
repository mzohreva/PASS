
JSVC=/usr/bin/jsvc
OPTIONS="-server"
PID=/tmp/pass-worker.pid
CLASSPATH=target/pass-worker-1.0-${classifier}.jar:target/lib/*
MAIN_CLASS=pass.worker.WorkerDaemon
ARGS="2 $(hostname)"
