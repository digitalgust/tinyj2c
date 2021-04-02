
echo "Requirement: jdk1.8 jar javac "

GCC=gcc
JAVAC=javac
JAR=jar
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_171.jdk/Contents/Home/
#JAVA_HOME=

function build_jar(){
    rm -rf $3/$1
    mkdir classes 
    find $2/java -name "*.java" >source.txt
    ${JAVAC}  -cp $4 -encoding "utf-8" -d classes @source.txt
    if [ -f "$2/resource/" ]
    then 
        cp -R $2/resource/* classes/
    fi
    ${JAR} cf $1 -C classes ./
    rm -rf source.txt
    rm -rf classes
    mkdir $3
    mv $1 $3/
}


echo "build tools/class2c.jar"
$(build_jar class2c.jar ./class2c/src/main "tools" "." ".")

if [ ! ${JAVA_HOME} ] ;then
    echo "JDK and JAVA_HOME env var set required"
else
    echo "JAVA_HOME=${JAVA_HOME}"
    ${JAVA_HOME}/bin/java -cp tools/class2c.jar com.ebsee.j2c.Main ./app/java/ ./app/out/classes/ ./app/out/c/
fi

CSRC="./app"
VMLIST=`find ${CSRC}/vm  -type f  -name "*.c" `
GENLIST=`find ${CSRC}/out/c  -type f  -name "*.c" `

${GCC} -O3  -o app.out -I${CSRC}/out/c -I${CSRC}/vm  -lpthread -lm   $VMLIST  ${GENLIST} 

echo "build completed : app.out"

