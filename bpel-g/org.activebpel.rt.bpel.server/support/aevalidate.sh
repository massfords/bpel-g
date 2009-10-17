#!/bin/sh
set -x

if [ -z "$CATALINA_HOME" ]
then
   echo CATALINA_HOME is not set.
   echo Set CATALINA_HOME or modify this file.
   exit
fi

if [ -z "$JAVA_HOME" ]
then
   echo JAVA_HOME is not set.
   echo Set JAVA_HOME or modify this batch file.
   exit
fi

CP_ROOT="$CATALINA_HOME/shared/lib"
if [ -z "$CP_ROOT/ae_rtbpelsvr.jar" ]
then
   echo $CP_ROOT/ae_rtbpelsvr.jar could not be found.
   exit
fi

MAIN_CP="$CP_ROOT/ae_rtbpelsvr.jar:$CP_ROOT/ae_rtbpel.jar:$CP_ROOT/ae_rt.jar:$CP_ROOT/xercesImpl.jar:$CP_ROOT/xml-apis.jar:$CP_ROOT/xmlParserAPIs.jar:$CP_ROOT/jaxrpc.jar:%CP_ROOT%/wsdl4j.jar:%CP_ROOT%/castor-0.9.5.3-xml.jar"
$JAVA_HOME/bin/java -classpath $MAIN_CP org.activebpel.rt.bpel.server.deploy.validate.main.AeMain $1




