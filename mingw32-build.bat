@echo off
@echo Requirment :  Jdk1.8  mingw32 cmake

@echo  1. compile minijvm_rt source and minijvm_test source
java -cp ./class2c/dist/class2c.jar com.ebsee.j2c.Main ../miniJVM/minijvm/java/src/main/java/;../miniJVM/test/minijvm_test/src/main/java/  ./app/out/classes/ ./app/out/c/ 

@echo  2. generate Makefiles
cmake.exe -G "MinGW Makefiles" -B cmake-mingw

@echo  3.comile c source 
mingw32-make -C .\cmake-mingw

@echo  4. execute ccjvm.exe 
.\cmake-mingw\ccjvm.exe test.Foo3

pause
