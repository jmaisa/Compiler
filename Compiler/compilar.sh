#!/bin/bash

javac lexer/*.java
javac symbols/*.java
javac inter/*.java
javac parser/*.java
javac main/*.java


java main.Main <tests/prog666.t
