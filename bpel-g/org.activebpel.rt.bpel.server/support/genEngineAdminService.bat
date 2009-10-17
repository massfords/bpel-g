@echo off
set CP=\AEDevelopment\lib\ae_rtaxisbpel.jar
set CP=%CP%;\AEDevelopment\lib\ae_rt.jar
set CP=%CP%;\AEDevelopment\lib\ae_rtbpel.jar
set CP=%CP%;\AEDevelopment\lib\ae_rtbpelsvr.jar
set CP=%CP%;\AEDevelopment\lib\axis.jar
set CP=%CP%;\AEDevelopment\lib\commons-logging.jar
set CP=%CP%;\AEDevelopment\lib\commons-discovery.jar
set CP=%CP%;\AEDevelopment\lib\wsdl4j.jar
set CP=%CP%;\AEDevelopment\lib\jaxrpc.jar
set CP=%CP%;\AEDevelopment\lib\saaj.jar

set OUT=engineAdmin.wsdl
set LOC=http://127.0.0.1:8080/rdebug/BpelEngineAdmin
set NS="urn:AeEngineServices"

REM *** This command generates the WSDL file, but we tweak it to have proper param names ***
REM %JAVA_HOME%\bin\java -classpath %CP% org.apache.axis.wsdl.Java2WSDL -o%OUT% -l%LOC% -n%NS% org.activebpel.rt.axis.bpel.rdebug.server.IAeBpelAdmin

%JAVA_HOME%\bin\java -classpath %CP% org.apache.axis.wsdl.WSDL2Java %OUT%

%JAVA_HOME%\bin\java -classpath %CP% org.apache.axis.wsdl.WSDL2Java --server-side --skeletonDeploy true %OUT%
