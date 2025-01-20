On n'autorise pas de nommer une variable comme une fonction, par exemple `auto sum = sum(100)` renverra une erreur  

# Compilation
## Run the project
- Write a valid TCL program in compilation/input.txt
- Run compilation/src/Main.java to compile the file into assembly code in compilation/prog.asm
- Run compilation/simproc.py to execute the assembly code. The result can be seen in compilation/sorties.txt
## Generate javadoc
- If you have IntelliJ, simply go to Tools>Generate Javadoc
- Else ???  
```javadoc -d .\javadoc -private --class-path .\src\*.java .\src\Asm\*.java --module-source-path .\libraries\antlr-4.13.0-complete.jar .\src\* .\src\Asm\*```  
```javadoc -subpackages src --class-path .\src\*.java --module-source-path .\libraries\antlr-4.13.0-complete.jar .\src\*```  
- Open compilation/javadoc/index.html in your favorite browser
## See parse tree
- Write a valid TCL program in compilation/input.txt
- Run compilation/input.sh
## Project structure
- compilation/CodeGen: assembly code inserted in compilation/src/CodeGenerator, but written in assembly for better readability
- compilation/javadoc: the folder in which javadoc should be generated
- compilation/libraries: outside modules used for the project, here antlr4
- compilation/src: source code
## Students repartition
### Group 1: type analysis
- lilas
- clément
- luka
- melvyn
### Group 2: linear code
- louison
- manu
- simon
- julien
### Group 3: assembly code
- amin
- mohamed amine
- ahdi
- ayoub