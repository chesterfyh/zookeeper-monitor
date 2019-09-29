@echo off

set arg1=%1
set arg2=%2
@title %arg1% server


set SERVER_TYPE=%arg1%
set SERVER_NAME=ZooKeeper-Monitor
set MAIN_CLASS=com.flynn.zk.App

set ROOT_PATH=%cd%
rem set CONFIG_PATH=%ROOT_PATH%\config
set LIB_PATH=%ROOT_PATH%\lib
rem set JAVA_HOME=D:\software\Java\jdk1.8.0_211\
set CLASSPATH=%ROOT_PATH%\zookeeper-monitor-0.0.1-SNAPSHOT.jar;%LIB_PATH%\*;
rem java -cp
rem echo %ROOT_PATH%
rem echo %ROOT_PATH%
rem echo %CONFIG_PATH%
rem echo %LIB_PATH%
rem echo %SERVER_TYPE%
rem echo %SERVER_NAME%
rem echo %CLASSPATH%

rem %JAVA_HOME%\bin\java -server -cp %CLASSPATH% %MAIN_CLASS%
java -server %MAIN_CLASS%
rem pause