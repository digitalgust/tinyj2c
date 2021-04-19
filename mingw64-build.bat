@echo off
@echo Requirment :  Jdk1.8  mingw32 cmake

set JAR=%JAVA_HOME%\bin\jar
set JAVAC=%JAVA_HOME%\bin\javac
set JAVA=%JAVA_HOME%\bin\java

set TARGETDIR=.
set LIBDIR=x86_64-w64-mingw32
set GCCHOME=D:\mingw-w64\x86_64-8.1.0-posix-sjlj-rt_v6-rev0
set GCCLIBDIR=%GCCHOME%\%LIBDIR%\lib

set path=%path%;%GCCHOME%\bin

setlocal enabledelayedexpansion

mkdir tools

@echo  * build convert tools
call :build_jar class2c.jar .\class2c\src\main\java .\tools\ 

@echo  * compile java source
%JAVA% -cp ./tools/class2c.jar com.ebsee.Main ./app/java/  ./app/out/classes/ ./app/out/c/ 

echo [INFO]build app.exe
call :jvm_compile app.exe app %TARGETDIR%

@echo  * execute 
.\app.exe test.Foo3

pause
goto :eof 

rem ==============================================================
:jvm_compile
    set SRCFILES=
    @for /f "delims=" %%i in ('@dir /S /B %2\*.c ^| @find /V "cmake-"') do (@set SRCFILES=!SRCFILES! %%i)
    rem echo SOURCE FILES: %SRCFILES%
    %GCCHOME%\bin\gcc -O3  -o %1 -I%2\vm -I%2\out\c  -L%GCCLIBDIR%  %SRCFILES%   -lpthread -lm 
goto :eof

:build_jar
    del /Q/S/F %3\%1
    md classes 
    dir /S /B %2\*.java > source.txt
    %JAVAC%  -encoding "utf-8"   -d classes @source.txt
    xcopy /E %2\resource\* classes\  
    %JAR% cf %1 -C classes .\
    del /Q/S source.txt
    rd /Q/S classes\
    copy /Y %1 %3\
    del /Q %1
goto :eof
