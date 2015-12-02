@echo off
:: NESTA VARI�VEL COLOQUE O CAMINHO DA PASTA BIN DO TEU JAVA, DEVE SER PARECIDO COM ESSE MEU
set JAVAPATH=C:\Program Files\Java\jdk1.8.0_40\bin

:: NESTA VARI�VEL COLOQUE O CAMINHO PRINCIPAL DO PROJETO
set PROJECTPATH=C:\workspace_compilador\compiler.git\Compiler

:: AQUI LIMPA OS ARQUIVOS GERADOS CASO J� TENHA COMPILADO ANTES
del /q/a/s/f "%PROJECTPATH%\lexer\*.class"
del /q/a/s/f "%PROJECTPATH%\symbols\*.class"
del /q/a/s/f "%PROJECTPATH%\inter\*.class"
del /q/a/s/f "%PROJECTPATH%\parser\*.class"
del /q/a/s/f "%PROJECTPATH%\main\*.class"

:: COMPILA O PROGRAMA
"%JAVAPATH%\javac.exe" -sourcepath %PROJECTPATH% "%PROJECTPATH%\main\Main.java"

:: LIMPA TELA E MOSTRA A SA�DA DO C�DIGO DE 3 ENDERE�OS
cls
"%JAVAPATH%\java.exe" -cp /%PROJECTPATH%\ main.Main < "%PROJECTPATH%\tests\prog666.t"

pause





