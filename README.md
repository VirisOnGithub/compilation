# Compilation
## Run the project
- Write a valid TCL program in compilation/input.txt
- Run compilation/src/Main.java to compile the file into assembly code in compilation/prog.asm
- Run compilation/simproc.py to execute the assembly code. The result can be seen in compilation/sorties.txt

Note there is two integer at the start of compilation/src/Main.java that serves as options.
## Generate javadoc
- If you have IntelliJ, simply go to Tools>Generate Javadoc
- Else ???  
  (failed attempts at generating a command)
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
    - TyperVisitor, VarStack, and TyperError are used by group 1
    - CodeGenerator and VarStack are used by group 2
    - ControlGraph, ConflictGraph, and AssemblerGenerator are used by group 3
    - /Asm, /Graph, /Type acts as libraries needed for the project
## Git organisation
We used git all along the project for ease of use.  
We decided that each group would have its own branch : groupe1, groupe2 and groupe3.  
In the case of the second group, we further devided into a branch for each person,
as we were editing a single file (CodeGenerator) and we thought it would have caused a lot git merging issues.  
Finally, we merged everything back to the main branch at the end and ran the final tests.
## Design choices
We changed the grammar of the for structure, to make it more C-like. Before it would allow something like
```(int i = 10;, i < 10, i++;)``` and now it allows ```(int i = 10; i < 10; i++;)```.  
We don't authorize naming a function the same as a variable, i.e. `auto sum = sum(100)` will return an error.
## Students repartition
### Group 1: type analysis
- Lilas GRENIER
- Clément RENIERS
- Luka COUTANT
- Melvyn BAUVENT
### Group 2: linear code
- Louison PARANT
- Manu LUQUIN
- Simon PRIBYLSKI
- Julien CHATAIGNER
### Group 3: assembly code
- Amin MESSAOUDI
- Mohamed amine GUESMI
- Ahdi BEN MAAOUIA
- Ayoub NADIR