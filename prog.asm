XOR R0 R0 R0
ADDi R0 R0 0
XOR R1 R1 R1
ADDi R1 R1 4096
JMP main
*print_tab: RET
main: XOR R2 R2 R2
ADDi R2 R2 2
LD R3 R1
ADDi R4 R3 0
ST R2 R3
ADDi R3 R3 1
ADDi R1 R1 12
XOR R5 R5 R5
ADDi R5 R5 1
LD R6 R1
ADDi R7 R6 0
ST R5 R6
ADDi R6 R6 1
ADDi R1 R1 12
XOR R8 R8 R8
ADDi R8 R8 18
ST R8 R6
ADDi R6 R6 1
ADDi R9 R7 0
ST R9 R3
ADDi R3 R3 1
XOR R10 R10 R10
ADDi R10 R10 1
LD R11 R1
ADDi R12 R11 0
ST R10 R11
ADDi R11 R11 1
ADDi R1 R1 12
XOR R13 R13 R13
ADDi R13 R13 69
ST R13 R11
ADDi R11 R11 1
ADDi R14 R12 0
ST R14 R3
ADDi R3 R3 1
ADDi R15 R4 0
ADDi R16 R15 0
ST R2 R0
ADDi R0 R0 1
XOR R17 R17 R17
ADDi R17 R17 2
ST R17 R0
ADDi R0 R0 1
CALL *print_tab
XOR R18 R18 R18
ADDi R18 R18 0
JMP label0
label0: ADDi R18 R18 0
STOP
