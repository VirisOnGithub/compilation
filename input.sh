if [ -e "$1" ]; then
    antlr4-parse grammarTCL.g4 main -gui "$1"
else
    antlr4-parse grammarTCL.g4 main -gui input
fi