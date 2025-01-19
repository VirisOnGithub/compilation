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

public class AssemblerGenerator {
    private Program program;
    private RegisterAllocator allocator;

    /**
     * Constructeur
     * @param program
     * @param allocator
     */
    public AssemblerGenerator(Program program, RegisterAllocator allocator) {
        this.program = program;
        this.allocator = allocator;
    }

    /**
     * Génère le code assembleur à partir du programme et de l'allocation de registres
     * @return
     */
    public String generateAssembly() {
        StringBuilder result = new StringBuilder();
        for (Instruction instruction : program.getInstructions()) {
            String[] parts = instruction.getName().split(" ");
            if (!instruction.getLabel().isEmpty()) {
                result.append(instruction.getLabel()).append(": ");
            }
            if (parts.length < 1) {
                throw new IllegalArgumentException("Format d'instruction invalide : " + instruction.getName());
            }
            switch (parts[0]) {
                case "XOR":
                case "OR":
                case "AND":
                case "NOT":
                case "NOR":
                case "NXOR":
                case "NAND":
                case "SUB":
                case "ADD":
                case "MUL":
                case "DIV":
                    if (parts.length < 4) {
                        throw new IllegalArgumentException("Invalid instruction format: " + instruction.getName());
                    }
                    String reg1 = allocator.getRegister(parts[2]);
                    String reg2 = allocator.getRegister(parts[3]);
                    String destReg = allocator.getRegister(parts[1]);
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
                    result.append(parts[0]).append(" R30, R30, R31\n");
                    result.append("ST ").append("R30, ").append(destReg).append("\n");
                    break;

                case "SUBi":
                case "ADDi":
                case "MULi":
                case "DIVi":
                    if (parts.length < 4) {
                        throw new IllegalArgumentException("Invalid instruction format: " + instruction.getName());
                    }
                    reg1 = allocator.getRegister(parts[2]);
                    String immediateValue = parts[3];
                    destReg = allocator.getRegister(parts[1]);
                    if (reg1.startsWith("DYNAMIC")) {
                        result.append("LD R30, [SP + ").append(reg1.substring(8, reg1.length() - 1)).append("]\n");
                    } else {
                        result.append("LD R30, ").append(reg1).append("\n");
                    }
                    result.append(parts[0]).append(" R30, R30, ").append(immediateValue).append("\n");
                    result.append("ST ").append("R30, ").append(destReg).append("\n");
                    break;

                case "PRINT":
                    if (parts.length < 2) {
                        throw new IllegalArgumentException("Invalid instruction format: " + instruction.getName());
                    }
                    String reg = allocator.getRegister(parts[1]);
                    if (reg.startsWith("DYNAMIC")) {
                        result.append("LD R30, [SP + ").append(reg.substring(8, reg.length() - 1)).append("]\n");
                        result.append("PRINT R30\n");
                    } else {
                        result.append("PRINT ").append(reg).append("\n");
                    }
                    break;

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
                    reg1 = allocator.getRegister("R" + condJump.getSr1());
                    reg2 = allocator.getRegister("R" + condJump.getSr2());
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
                    result.append("LD ").append("R").append(mem.getDest()).append(", ").append("R").append(mem.getAddress()).append("\n");
                    break;

                case "ST":
                    if (!(instruction instanceof Mem)) {
                        throw new IllegalArgumentException("Invalid instruction type for store: " + instruction.getName());
                    }
                    mem = (Mem) instruction;
                    result.append("ST ").append("R").append(mem.getDest()).append(", ").append("R").append(mem.getAddress()).append("\n");
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

                default:
                    throw new IllegalArgumentException("Unknown instruction: " + parts[0]);
            }
        }

        return result.toString();
    }

    public static void main(String[] args) {
        Program program = new Program();

        Instruction instr0 = new Mem(Mem.Op.ST, 0, 1) {};
        Instruction instr1 = new UAL(UAL.Op.XOR, 1000, 1000, 1000) {};
        Instruction instr2 = new UALi(UALi.Op.SUB, 1000, 1000, 1) {};
        Instruction instr3 = new IO(IO.Op.PRINT, 1001) {};
        Instruction instr4 = new CondJump(CondJump.Op.JEQU, 1000, 1001, "LABEL2") {};
        Instruction instr5 = new CondJump(CondJump.Op.JINF, 1000, 1001, "FUNC1") {};
        Instruction instr6 = new CondJump(CondJump.Op.JSUP, 1000, 1001, "LABEL1") {};
        Instruction instr7 = new JumpCall(JumpCall.Op.CALL, "FUNC1") {};
        Instruction instr8 = new JumpCall(JumpCall.Op.JMP, "END") {};
        Instruction instr9 = new Stop("STOP") {};
        Instruction instr10 = new UAL("LABEL1", UAL.Op.ADD, 1002, 1000, 1001) {};
        Instruction instr11 = new UAL("FUNC1", UAL.Op.MUL, 1003, 1002, 1000) {};
        Instruction instr12 = new JumpCall(JumpCall.Op.JMP, "END") {};
        Instruction instr13 = new UAL("LABEL2", UAL.Op.DIV, 1004, 1003, 1001) {};
        Instruction instr14 = new IO(IO.Op.PRINT, 1004) {};
        Instruction instr15 = new Ret("END") {};
        Instruction instr16 = new Mem(Mem.Op.LD, 1002, 1) {};

        program.addInstruction(instr0);
        program.addInstruction(instr1);
        program.addInstruction(instr2);
        program.addInstruction(instr3);
        program.addInstruction(instr4);
        program.addInstruction(instr5);
        program.addInstruction(instr6);
        program.addInstruction(instr7);
        program.addInstruction(instr8);
        program.addInstruction(instr9);
        program.addInstruction(instr10);
        program.addInstruction(instr11);
        program.addInstruction(instr12);
        program.addInstruction(instr13);
        program.addInstruction(instr14);
        program.addInstruction(instr15);
        program.addInstruction(instr16);

        ControlGraph controlGraph = new ControlGraph(program);
        ConflictGraph conflictGraph = new ConflictGraph(controlGraph, program);

        RegisterAllocator allocator = new RegisterAllocator(conflictGraph);
        AssemblerGenerator generator = new AssemblerGenerator(program, allocator);

        String assemblyCode = generator.generateAssembly();
        System.out.println(assemblyCode);
    }
}