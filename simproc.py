#!/usr/bin/python
import sys
import random

registerCount = 666

def checkReg(reg):
    if reg == "":
        return False
    if reg[0]!='R':
        return False
    x = int(reg[1:])
    if x<0 or x>registerCount - 1:
        return False
    return True

def checkLabel(label,symb):
    if label == "" :
        return False
    if label in symb:
        return True
    return False

def checkImm(imm):
    if imm == "":
        return False
    try:
        int(imm)
        return True
    except:
        return False

def checkSyntaxe(inst,symb):
    opUAL = ["ADD","SUB","OR","AND","XOR","SL","SR","MUL","DIV","MOD"]
    opUALi = ["ADDi","SUBi","ORi","ANDi","XORi","SLi","SRi","MULi","DIVi","MODi"]
    opJMPC = ["JEQU","JNEQ","JSUP","JINF","JIEQ","JSEQ"]

    if inst[0] in opUAL:
        return checkReg(inst[1]) and checkReg(inst[2]) and checkReg(inst[3])

    if inst[0] in opUALi:
        return checkReg(inst[1]) and checkReg(inst[2]) and checkImm(inst[3])
 
    if inst[0] == "LD":
        return checkReg(inst[1]) and checkReg(inst[2])

    if inst[0] == "ST":
        return checkReg(inst[1]) and checkReg(inst[2])

    if inst[0] in opJMPC:
        return checkReg(inst[1]) and checkReg(inst[2]) and checkLabel(inst[3],symb)

    if inst[0] == "JMP":
        return checkLabel(inst[1],symb)

    if inst[0] == "CALL":
        return checkLabel(inst[1],symb)

    if inst[0] == "IN":
        return checkReg(inst[1])
    
    if inst[0] == "READ":
        return checkReg(inst[1])

    if inst[0] == "OUT":
        return checkReg(inst[1])

    if inst[0] == "PRINT":
        return checkReg(inst[1])
    if inst[0] == "RET":
        return True

    if inst[0] == "STOP":
        return True

    return 0

def readFile(ff):
    x = ff.read(1)
    if x == "":
        return 255
    else:
        return ord(x[0])

def readInt(ff):
    res = 0
    a = readFile(ff)
    while (48 <= a and a <= 57) :
        res = 10*res + a-48
        a = readFile(ff)
    return res


def writeFile(ff,c):
    ff.write(chr(c&0xFF))

def decodeREG(R):
    return int(R[1:])

def decodeIMM(I):
    return int(I)

# initialisation du processeur
# -------------------------------------------------

# 32 registres initialises aleatoirement

REG = []
for i in range(registerCount):
    REG.append(random.randint(0,1000))

# memoire de 65536 mots inialises aleatoirement

MEM = []
for i in range(65536):
    MEM.append(random.randint(0,1000))

# pile sauvegarde adresse de retour

PILE = []

# compteur ordinal initialise a 0

CO = 0
NbCycle = 0

# lecture des instructions

ff = open("prog.asm",'r') 
MEM_INST = ff.readlines()
ff.close();

# constitution de la table des symboles
PROG = []
SYMB = {}
NUML = []
n=0
for i in range(len(MEM_INST)):
    inst = MEM_INST[i].split()
    if len(inst)!=0:
        if inst[0][0]!='#':
            if inst[0][-1:] == ':':
                PROG.append(inst[1:])
                SYMB[inst[0][0:-1]] = n
            else:
                PROG.append(inst)
            NUML.append(i)
            n=n+1

# ouverture des fichiers d'E/S

fin = open("entrees.txt",'r') 
fout = open("sorties.txt",'w')

# EXECUTION
# -------------------------------------------------

while PROG[CO][0] != "STOP":

    if checkSyntaxe(PROG[CO],SYMB) == False:
        print("ERROR LINE",NUML[CO]+1)
        for x in PROG[CO]:
            print(x,)
        print()
        sys.exit(0)
        

    NbCycle = NbCycle + 1

    # UAL Registre

    if PROG[CO][0] == "ADD":
        REG[decodeREG(PROG[CO][1])] = REG[decodeREG(PROG[CO][2])] + REG[decodeREG(PROG[CO][3])]
        CO = CO + 1
    elif PROG[CO][0] == "SUB":
        REG[decodeREG(PROG[CO][1])] = REG[decodeREG(PROG[CO][2])] - REG[decodeREG(PROG[CO][3])]
        CO = CO + 1
    elif PROG[CO][0] == "MUL":
        REG[decodeREG(PROG[CO][1])] = REG[decodeREG(PROG[CO][2])] * REG[decodeREG(PROG[CO][3])]
        CO = CO + 1
    elif PROG[CO][0] == "DIV":
        REG[decodeREG(PROG[CO][1])] = REG[decodeREG(PROG[CO][2])] // REG[decodeREG(PROG[CO][3])]
        CO = CO + 1
    elif PROG[CO][0] == "MOD":
        REG[decodeREG(PROG[CO][1])] = REG[decodeREG(PROG[CO][2])] % REG[decodeREG(PROG[CO][3])]
        CO = CO + 1
    elif PROG[CO][0] == "AND":
        REG[decodeREG(PROG[CO][1])] = REG[decodeREG(PROG[CO][2])] & REG[decodeREG(PROG[CO][3])]
        CO = CO + 1
    elif PROG[CO][0] == "OR":
        REG[decodeREG(PROG[CO][1])] = REG[decodeREG(PROG[CO][2])] | REG[decodeREG(PROG[CO][3])]
        CO = CO + 1
    elif PROG[CO][0] == "XOR":
        REG[decodeREG(PROG[CO][1])] = REG[decodeREG(PROG[CO][2])] ^ REG[decodeREG(PROG[CO][3])]
        CO = CO + 1
    elif PROG[CO][0] == "SL":
        REG[decodeREG(PROG[CO][1])] = REG[decodeREG(PROG[CO][2])] << REG[decodeREG(PROG[CO][3])]
        CO = CO + 1
    elif PROG[CO][0] == "SR":
        REG[decodeREG(PROG[CO][1])] = REG[decodeREG(PROG[CO][2])] >> REG[decodeREG(PROG[CO][3])]
        CO = CO + 1

   # UAL immediat

    elif PROG[CO][0] == "ADDi":
        REG[decodeREG(PROG[CO][1])] = REG[decodeREG(PROG[CO][2])] + decodeIMM(PROG[CO][3])
        CO = CO + 1
    elif PROG[CO][0] == "SUBi":
        REG[decodeREG(PROG[CO][1])] = REG[decodeREG(PROG[CO][2])] - decodeIMM(PROG[CO][3])
        CO = CO + 1
    elif PROG[CO][0] == "MULi":
        REG[decodeREG(PROG[CO][1])] = REG[decodeREG(PROG[CO][2])] * decodeIMM(PROG[CO][3])
        CO = CO + 1
    elif PROG[CO][0] == "DIVi":
        REG[decodeREG(PROG[CO][1])] = REG[decodeREG(PROG[CO][2])] // decodeIMM(PROG[CO][3])
        CO = CO + 1
    elif PROG[CO][0] == "MODi":
        REG[decodeREG(PROG[CO][1])] = REG[decodeREG(PROG[CO][2])] % decodeIMM(PROG[CO][3])
        CO = CO + 1
    elif PROG[CO][0] == "ANDi":
        REG[decodeREG(PROG[CO][1])] = REG[decodeREG(PROG[CO][2])] & decodeIMM(PROG[CO][3])
        CO = CO + 1
    elif PROG[CO][0] == "ORi":
        REG[decodeREG(PROG[CO][1])] = REG[decodeREG(PROG[CO][2])] | decodeIMM(PROG[CO][3])
        CO = CO + 1
    elif PROG[CO][0] == "XORi":
        REG[decodeREG(PROG[CO][1])] = REG[decodeREG(PROG[CO][2])] ^ decodeIMM(PROG[CO][3])
        CO = CO + 1
    elif PROG[CO][0] == "SLi":
        REG[decodeREG(PROG[CO][1])] = REG[decodeREG(PROG[CO][2])] << decodeIMM(PROG[CO][3])
        CO = CO + 1
    elif PROG[CO][0] == "SRi":
        REG[decodeREG(PROG[CO][1])] = REG[decodeREG(PROG[CO][2])] >> decodeIMM(PROG[CO][3])
        CO = CO + 1

    # Memoire

    elif PROG[CO][0] == "LD": # LD R0 R1 <=> R0 := MEM[R1]
        if REG[decodeREG(PROG[CO][2])] > 65536:
            print('ERROR: memory address > 65536 -- CO =',CO)
            sys.exit(0)
        REG[decodeREG(PROG[CO][1])] = MEM[REG[decodeREG(PROG[CO][2])]]
        CO = CO + 1
    elif PROG[CO][0] == "ST": # ST R0 R1 <=> MEM[R1] = R0
        if REG[decodeREG(PROG[CO][2])] > 65536:
            print('ERROR: memory address > 65536 -- CO =',CO)
            sys.exit(0)
        MEM[REG[decodeREG(PROG[CO][2])]] = REG[decodeREG(PROG[CO][1])]
        CO = CO + 1

    # controle

    elif PROG[CO][0] == "JMP":
        CO = SYMB[PROG[CO][1]]
    elif PROG[CO][0] == "CALL":
        PILE.append(CO+1)
        CO = SYMB[PROG[CO][1]]
    elif PROG[CO][0] == "RET":
        CO = PILE.pop()
    elif PROG[CO][0] == "JEQU":
        if REG[decodeREG(PROG[CO][1])] == REG[decodeREG(PROG[CO][2])]:
            CO = SYMB[PROG[CO][3]]
        else:
            CO = CO + 1
    elif PROG[CO][0] == "JNEQ":
        if REG[decodeREG(PROG[CO][1])] != REG[decodeREG(PROG[CO][2])]:
            CO = SYMB[PROG[CO][3]]
        else:
            CO = CO + 1
    elif PROG[CO][0] == "JSUP":
        if REG[decodeREG(PROG[CO][1])] > REG[decodeREG(PROG[CO][2])]:
            CO = SYMB[PROG[CO][3]]
        else:
            CO = CO + 1
    elif PROG[CO][0] == "JINF":
        if REG[decodeREG(PROG[CO][1])] < REG[decodeREG(PROG[CO][2])]:
            CO = SYMB[PROG[CO][3]]
        else:
            CO = CO + 1
    elif PROG[CO][0] == "JIEQ":
        if REG[decodeREG(PROG[CO][1])] <= REG[decodeREG(PROG[CO][2])]:
            CO = SYMB[PROG[CO][3]]
        else:
            CO = CO + 1
    elif PROG[CO][0] == "JSEQ":
        if REG[decodeREG(PROG[CO][1])] >= REG[decodeREG(PROG[CO][2])]:
            CO = SYMB[PROG[CO][3]]
        else:
            CO = CO + 1
    # Entrees / Sorties simplifiees

    elif PROG[CO][0] == "IN":
        REG[decodeREG(PROG[CO][1])] = readFile(fin)
        CO = CO + 1
    elif PROG[CO][0] == "OUT":
        writeFile(fout,REG[decodeREG(PROG[CO][1])])
        CO = CO + 1
    elif PROG[CO][0] == "READ":
        REG[decodeREG(PROG[CO][1])] = readInt(fin)
        CO = CO + 1
    elif PROG[CO][0] == "PRINT":
        fout.write(str(REG[decodeREG(PROG[CO][1])]))
        CO = CO + 1
    # arret du programme

    elif PROG[CO][0] == "STOP":
        print('Execution :',NbCycle,'cycles')
        fin.close()
        fout.close()
        sys.exit(0)

    # erreur de syntaxe

    else:
        print('ERROR: OP Code', PROG[CO][0], 'unknown')
        fin.close()
        fout.close()
        sys.exit(0)






    
