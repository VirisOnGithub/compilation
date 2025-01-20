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

public class AssemblerGenerator {
    private Program program;
    private ConflictGraph conflictGraph;
    private int dynamicArrayIndex;

    /**
     * Constructeur
     * @param program
     * @param allocator
     */
    public AssemblerGenerator(Program program, ConflictGraph conflictGraph) {
        this.program = program;
        this.conflictGraph = conflictGraph;
        this.dynamicArrayIndex = 57000 - conflictGraph.color();
    }

    private String getRegister(int register, StringBuilder result, int operationRegister) {
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
        if (register < 30) {
            return "R" + register;
        }
        else {
            return "R30";
        }
    }

    private void returnRegister(int register, StringBuilder result) {
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
                case "SR":
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
                        currentlyBuildInstruction += (op + " " + newDestReg + " " + newReg1 + " " + imm + "\n");
                        result.append(currentlyBuildInstruction);
                        this.returnRegister(destReg, result);
                    }
                    break;
/*
                case "JEQU":
                case "JINF":
                case "JSUP":
                case "JNEQ":
                case "JIEQ":
                case "JSEQ":
                    if (!(instruction instanceof CondJump)) {
                        throw new IllegalArgumentException("Invalid instruction type for conditional jump: " + instruction.getName());
                    }
                    CondJump condJump = (CondJump) instruction;
                    String reg1 = allocator.getRegister("R" + condJump.getSr1());
                    String reg2 = allocator.getRegister("R" + condJump.getSr2());

                    String label = condJump.getAddress();
                    if (reg1.startsWith("DYNAMIC")) {
                        result.append("LD R30, [SP + ").append(reg1.substring(8, reg1.length() - 1)).append("]\n");
                    } else {
                        result.append("LD R30, ").append(reg1).append("\n");
                    }

                    if (reg2.startsWith("DYNAMIC")) {
                        result.append("LD R31, [SP + ").append(reg2.substring(8, reg2.length() - 1)).append("]\n");
                    } else {
                        result.append("LD R31, ").append(reg2).append("\n");
                    }
                    result.append(parts[0]).append(" R30, R31, ").append(label).append("\n");
                    break;

                case "LD":
                    if (!(instruction instanceof Mem)) {
                        throw new IllegalArgumentException("Invalid instruction type for load: " + instruction.getName());
                    }
                    Mem mem = (Mem) instruction;
                    result.append("LD ").append(allocator.getRegister("R" + mem.getDest())).append(", ").append(allocator.getRegister("R" + mem.getAddress())).append("\n");
                    break;

                case "ST":
                    if (!(instruction instanceof Mem)) {
                        throw new IllegalArgumentException("Invalid instruction type for store: " + instruction.getName());
                    }
                    mem = (Mem) instruction;
                    result.append("ST ").append(allocator.getRegister("R" + mem.getAddress())).append(", ").append(allocator.getRegister("R" + mem.getDest())).append("\n");
                    break;

                case "CALL":
                case "JMP":
                    if (!(instruction instanceof JumpCall)) {
                        throw new IllegalArgumentException("Invalid instruction type for jump/call: " + instruction.getName());
                    }
                    JumpCall jumpCall = (JumpCall) instruction;
                    String jumpLabel = jumpCall.getAddress();
                    result.append(parts[0]).append(" ").append(jumpLabel).append("\n");
                    break;

                case "STOP":
                    result.append("STOP\n");
                    break;

                case "RET":
                    result.append("RET\n");
                    break;

                case "IN":
                case "OUT":
                case "READ":
                case "PRINT":
                    if (!(instruction instanceof IO)) {
                        throw new IllegalArgumentException("Invalid instruction type for print: " + instruction.getName());
                    }
                    IO io = (IO) instruction;
                    result.append(parts[0]).append(" ").append(allocator.getRegister("R" + io.getReg())).append("\n");
                    break;
*/
                default:
                    throw new IllegalArgumentException("Unknown instruction: " + parts[0]);
            }
        }

        return result.toString();
    }

    public static void main(String[] args) {
        Program program = new Program();

        for (int i = 0; i < 40; i++) {
            program.addInstruction(new UAL(UAL.Op.ADD, 70, i, i+1));
        }

/*         
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

        AssemblerGenerator generator = new AssemblerGenerator(program, conflictGraph);

        String assemblyCode = generator.generateAssembly();
        System.out.println(assemblyCode);
    }
}
