package src;

import src.Asm.Program;
import src.Asm.Instruction;
import src.Asm.CondJump;
import src.Asm.IO;
import src.Asm.JumpCall;
import src.Asm.Mem;
import src.Asm.Ret;
import src.Asm.Stop;
import src.Asm.UAL;
import src.Asm.UALi;
import src.grammarTCLParser.AdditionContext;

import javax.lang.model.type.ReferenceType;

public class AssemblerGenerator {
    private Program program;
    private ConflictGraph conflictGraph;
    private int dynamicArrayIndex;
    private int USE_CONFLICT_GRAPH;

    /**
     * Constructeur
     * @param program
     */
    public AssemblerGenerator(Program program, ConflictGraph conflictGraph, int USE_CONFLICT_GRAPH) {
        this.program = program;
        this.conflictGraph = conflictGraph;
        this.USE_CONFLICT_GRAPH = USE_CONFLICT_GRAPH;
        if(USE_CONFLICT_GRAPH == 1){
            this.dynamicArrayIndex = 65535 - conflictGraph.color();
        }
        else {
            this.dynamicArrayIndex = 57000;
        }
    }

    private int getActualRegister(int register) {
        if (this.USE_CONFLICT_GRAPH == 1) {
            return conflictGraph.getColor("R" + register);
        }
        else {
            return register;
        }
    }

    private String getRegister(int register, StringBuilder result, int operationRegister) {
        register = getActualRegister(register);
        if (register < 30) {
            return "R" + register;
        }
        else {
            int memLocation = (register - 30) + dynamicArrayIndex;
            result.append("XOR R" + operationRegister + " R" + operationRegister + " R" + operationRegister + "\n");
            result.append("ADDi R" + operationRegister + " R" + operationRegister + " " + memLocation + "\n");
            result.append("LD R" + operationRegister + " R" + operationRegister + "\n");
            return "R" + operationRegister;
        }
    }

    private String getRegisterNumber(int register) {
        register = getActualRegister(register);
        if (register < 30) {
            return "R" + register;
        }
        else {
            return "R30";
        }
    }

    private void returnRegister(int register, StringBuilder result) {
        register = getActualRegister(register);
        if (register < 30) return;
        int memLocation = (register - 30) + dynamicArrayIndex;
        result.append("XOR R31 R31 R31\n");
        result.append("ADDi R31 R31 " + memLocation + "\n");
        result.append("ST R30 R31\n");
    }

    /**
     * Génère le code assembleur à partir du programme et de l'allocation de registres
     * @return
     */
    public String generateAssembly() {
        if (program.getInstructions().isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        String currentlyBuildInstruction;

        for (Instruction instruction : program.getInstructions()) {
            String[] parts = instruction.getName().split(" ");
            currentlyBuildInstruction = "";
            if (!instruction.getLabel().isEmpty()) {
                currentlyBuildInstruction += (instruction.getLabel()) + (": ");
            }
            if (parts.length < 1) {
                throw new IllegalArgumentException("Format d'instruction invalide : " + instruction.getName());
            }
            String op = parts[0];

            switch (op) {
                case "XOR":
                case "OR":
                case "AND":
                case "SUB":
                case "ADD":
                case "MUL":
                case "DIV":
                case "MOD":
                case "SL":
                case "SR": {
                    if (!(instruction instanceof UAL) && !(instruction instanceof UALi)) {
                        throw new IllegalArgumentException("Type d'instruction invalide pour UAL/UALi: " + instruction.getName());
                    }
                    if (instruction instanceof UAL) {
                        UAL ual = (UAL) instruction;
                        int destReg = ual.getDest();
                        int reg1 = ual.getSr1();
                        int reg2 = ual.getSr2();
                        String newDestReg = this.getRegisterNumber(destReg);
                        String newReg1 = this.getRegister(reg1, result, 30);
                        String newReg2 = this.getRegister(reg2, result, 31);
                        currentlyBuildInstruction += (op + " " + newDestReg + " " + newReg1 + " " + newReg2 + "\n");
                        result.append(currentlyBuildInstruction);
                        this.returnRegister(destReg, result);
                    } else {
                        UALi uali = (UALi) instruction;
                        int destReg = uali.getDest();
                        int reg1 = uali.getSr();
                        int imm = uali.getImm();
                        String newDestReg = this.getRegisterNumber(destReg);
                        String newReg1 = this.getRegister(reg1, result, 30);
                        currentlyBuildInstruction += (op + "i " + newDestReg + " " + newReg1 + " " + imm + "\n");
                        result.append(currentlyBuildInstruction);
                        this.returnRegister(destReg, result);
                    }
                    break;
                }

                case "JEQU":
                case "JINF":
                case "JSUP":
                case "JNEQ":
                case "JIEQ":
                case "JSEQ": {
                    if (!(instruction instanceof CondJump)) {
                        throw new IllegalArgumentException("Invalid instruction type for conditional jump: " + instruction.getName());
                    }
                    CondJump condJump = (CondJump) instruction;
                    String address = condJump.getAddress();
                    int reg1 = condJump.getSr1();
                    int reg2 = condJump.getSr2();
                    String newReg1 = this.getRegister(reg1, result, 30);
                    String newReg2 = this.getRegister(reg2, result, 31);
                    currentlyBuildInstruction += (op + " " + newReg1 + " " + newReg2 + " " + address + "\n");
                    result.append(currentlyBuildInstruction);
                    break;
                }

                case "LD": {
                    if (!(instruction instanceof Mem)) {
                        throw new IllegalArgumentException("Invalid instruction type for load: " + instruction.getName());
                    }
                    Mem mem = (Mem) instruction;
                    int reg1 = mem.getDest();
                    int reg2 = mem.getAddress();
                    String newReg1 = this.getRegister(reg1, result, 30);
                    String newReg2 = this.getRegister(reg2, result, 31);
                    currentlyBuildInstruction += (op + " " + newReg1 + " " + newReg2 + "\n");
                    result.append(currentlyBuildInstruction);
                    this.returnRegister(reg1, result);
                    break;
                }

                case "ST": {
                    if (!(instruction instanceof Mem)) {
                        throw new IllegalArgumentException("Invalid instruction type for load: " + instruction.getName());
                    }
                    Mem mem = (Mem) instruction;
                    int reg1 = mem.getDest();
                    int reg2 = mem.getAddress();
                    String newReg1 = this.getRegister(reg1, result, 30);
                    String newReg2 = this.getRegister(reg2, result, 31);
                    currentlyBuildInstruction += (op + " " + newReg1 + " " + newReg2 + "\n");
                    result.append(currentlyBuildInstruction);
                    break;
                }

                case "CALL":
                case "JMP": {
                    if (!(instruction instanceof JumpCall)) {
                        throw new IllegalArgumentException("Invalid instruction type for jump/call: " + instruction.getName());
                    }
                    JumpCall jumpCall = (JumpCall) instruction;
                    String jumpLabel = jumpCall.getAddress();
                    currentlyBuildInstruction += (op + " " + jumpLabel + "\n");
                    result.append(currentlyBuildInstruction);
                    break;
                }

                case "STOP":
                case "RET": {
                    if (!(instruction instanceof Stop) && !(instruction instanceof Ret)) {
                        throw new IllegalArgumentException("Invalid instruction type for stop: " + instruction.getName());
                    }
                    currentlyBuildInstruction += (op + "\n");
                    result.append(currentlyBuildInstruction);
                    break;
                }

                case "IN":
                case "READ": {
                    if (!(instruction instanceof IO)) {
                        throw new IllegalArgumentException("Invalid instruction type for print: " + instruction.getName());
                    }
                    IO io = (IO) instruction;
                    int reg = io.getReg();
                    String newReg1 = this.getRegister(reg, result, 30);
                    currentlyBuildInstruction += (op + " " + newReg1 + "\n");
                    result.append(currentlyBuildInstruction);
                    this.returnRegister(reg, result);
                    break;
                }

                case "OUT":
                case "PRINT": {
                    if (!(instruction instanceof IO)) {
                        throw new IllegalArgumentException("Invalid instruction type for print: " + instruction.getName());
                    }
                    IO io = (IO) instruction;
                    int reg = io.getReg();
                    String newReg1 = this.getRegister(reg, result, 30);
                    currentlyBuildInstruction += (op + " " + newReg1 + "\n");
                    result.append(currentlyBuildInstruction);
                    break;
                }

                default:
                    throw new IllegalArgumentException("Unknown instruction: " + parts[0]);
            }
        }

        return result.toString();
    }

    public static void main(String[] args) {
        Program program = new Program();

        program.addInstruction(new UAL(UAL.Op.XOR, 0, 0, 0));
        program.addInstruction(new UALi(UALi.Op.ADD, 0, 0, 10));
        program.addInstruction(new UAL(UAL.Op.XOR, 1, 1, 1));
        program.addInstruction(new UALi(UALi.Op.ADD, 1, 1, 10));
        program.addInstruction(new UAL(UAL.Op.XOR, 2, 2, 2));
        program.addInstruction(new UALi(UALi.Op.ADD, 2, 2, 10));
        program.addInstruction(new UAL(UAL.Op.AND, 0, 1, 2));

        /*
        for (int i = 0; i < 40; i++) {
            program.addInstruction(new UAL(UAL.Op.ADD, 70, i, i+1));
        }

        Instruction instr0 = new IO(IO.Op.READ, 10000) {};
        program.addInstruction(instr0);
        
        // Instructions utilisant des registres uniques et augmentant progressivement le nombre
        for (int i = 0; i < 40; i++) {
            // Exemple d'opérations arithmétiques utilisant des registres R0 à R39
            Instruction instr = new UAL(UAL.Op.ADD, i, i, (i + 1) % 40) {};
            program.addInstruction(instr);
        }

        // Ajout d'opérations avec des registres existants
        Instruction instr40 = new UALi(UALi.Op.SUB, 39, 38, 10) {};
        Instruction instr41 = new CondJump(CondJump.Op.JEQU, 37, 36, "LABEL_EXTRA") {};
        Instruction instr42 = new JumpCall(JumpCall.Op.CALL, "FUNC_EXTRA") {};
        Instruction instr43 = new IO(IO.Op.PRINT, 35) {};
        Instruction instr44 = new Mem(Mem.Op.LD, 34, 33) {};
        Instruction instr45 = new Mem(Mem.Op.ST, 32, 31) {};
        Instruction instr46 = new JumpCall(JumpCall.Op.JMP, "STOP") {};
        Instruction instr47 = new UAL(UAL.Op.MUL, 10, 9, 8) {};
        Instruction instr48 = new Stop("STOP") {};

        // Ajout des instructions restantes au programme
        program.addInstruction(instr40);
        program.addInstruction(instr41);
        program.addInstruction(instr42);
        program.addInstruction(instr43);
        program.addInstruction(instr44);
        program.addInstruction(instr45);
        program.addInstruction(instr46);
        program.addInstruction(instr47);
        program.addInstruction(instr48);

        // Ajout d'un label et d'une instruction de retour pour valider les sauts
        Instruction instrLabel = new UAL("LABEL_EXTRA", UAL.Op.MUL, 10, 9, 8) {};
        Instruction instrReturn = new Ret("FUNC_EXTRA") {};
        program.addInstruction(instrLabel);
        program.addInstruction(instrReturn);
        */

        ControlGraph controlGraph = new ControlGraph(program);
        ConflictGraph conflictGraph = new ConflictGraph(controlGraph, program);

        AssemblerGenerator generator = new AssemblerGenerator(program, conflictGraph, 1);

        String assemblyCode = generator.generateAssembly();
        System.out.println(assemblyCode);
    }
}
