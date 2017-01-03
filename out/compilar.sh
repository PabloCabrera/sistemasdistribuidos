#!/bin/sh
cd ..

mkdir -p out/tp1
mkdir -p out/tp2
mkdir -p out/misc

javac -d out tp1/*/*.java
javac -d out tp2/*/*.java
javac -d out misc/*.java

cd out
