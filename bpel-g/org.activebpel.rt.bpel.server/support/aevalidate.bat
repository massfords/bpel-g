@echo off

if "%CATALINA_HOME%" == "" goto ABORT_TOMCAT

if "%JAVA_HOME%" == "" goto ABORT_JAVA

set CP_ROOT=%CATALINA_HOME%/shared/lib
if not exist "%CP_ROOT%/ae_rtbpelsvr.jar" goto ABORT_JAR

set MAIN_CP=%CP_ROOT%/ae_rtbpelsvr.jar;%CP_ROOT%/ae_rtbpel.jar;%CP_ROOT%/ae_rt.jar;%CP_ROOT%/xercesImpl.jar;%CP_ROOT%/xml-apis.jar;%CP_ROOT%/xmlParserAPIs.jar;%CP_ROOT%/jaxrpc.jar;%CP_ROOT%/wsdl4j.jar;%CP_ROOT%/castor-0.9.5.3-xml.jar

java -classpath "%MAIN_CP%" org.activebpel.rt.bpel.server.deploy.validate.main.AeMain %1
goto END


rem no bpel server jar
:ABORT_JAR
echo %CP_ROOT%/ae_rtbpelsvr.jar" could not be found.
goto END

rem no tomcat home
:ABORT_TOMCAT
echo CATALINA_HOME is not set.
echo Set CATALINA_HOME or modify this batch file.
goto END

rem no java home
:ABORT_JAVA
echo JAVA_HOME is not set.
echo Set JAVA_HOME or modify this batch file.
goto END

:END
