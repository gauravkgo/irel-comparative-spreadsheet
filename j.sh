
rm bin/*.class
cd ./src
javac "$1.java" -d ../bin
cd ../bin
java $1
cd ..

