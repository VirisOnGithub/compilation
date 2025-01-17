package src;

import src.Asm.Program;
import src.Asm.Instruction;
import src.Asm.CondJump;
import src.Asm.JumpCall;
import src.Asm.Ret;
import src.Asm.Stop;

public class AssemblerGenerator {
    private Program program;
    private RegisterAllocator allocator;

    public AssemblerGenerator(Program program, RegisterAllocator allocator) {
        this.program = program;
        this.allocator = allocator;
    }

    public String generateAssembly() {
        StringBuilder result = new StringBuilder();

        for (Instruction instruction : program.getInstructions()) {
            String[] parts = instruction.getName().split(" ");
            if (!instruction.getLabel().isEmpty()) {
                result.append(instruction.getLabel()).append(": ");
            }

            if (parts.length < 1) {
                throw new IllegalArgumentException("Invalid instruction format: " + instruction.getName());
            }

            switch (parts[0]) {
                case "XOR":
                case "OR":
                case "AND":
                case "NOT":
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
                    result.append("ST ").append(destReg).append(", R30\n");
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
                    result.append("MOV ").append(destReg).append(", R30\n");
                    break;

                case "MOV":
                    if (parts.length < 3) {
                        throw new IllegalArgumentException("Invalid instruction format: " + instruction.getName());
                    }
                    reg1 = allocator.getRegister(parts[2]);
                    destReg = allocator.getRegister(parts[1]);

                    if (reg1.startsWith("DYNAMIC")) {
                        result.append("LD R30, [SP + ").append(reg1.substring(8, reg1.length() - 1)).append("]\n");
                    } else {
                        result.append("LD R30, ").append(reg1).append("\n");
                    }

                    result.append("MOV ").append(destReg).append(", R30\n");
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
                case "JNEQ":
                case "JLT":
                case "JLE":
                case "JGT":
                case "JGE":
                case "JINF":
                case "JSUP":
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

	Instruction instr1 = new Instruction("L1", "XOR R1000 R1000 R1000") {};
	Instruction instr2 = new Instruction("L2", "SUBi R1000 R1000 1") {};
	Instruction instr3 = new Instruction("L3", "PRINT R1001") {};
	Instruction instr4 = new CondJump("L4", CondJump.Op.JEQU, 1000, 1001, "LABEL2") {};
	Instruction instr5 = new CondJump("L5", CondJump.Op.JINF, 1000, 1001, "L6") {};
	Instruction instr6 = new CondJump("L6", CondJump.Op.JSUP, 1000, 1001, "L7") {};
	Instruction instr7 = new JumpCall("L7", JumpCall.Op.CALL, "FUNC1") {};
	Instruction instr8 = new JumpCall("L8", JumpCall.Op.JMP, "END") {};
	Instruction instr9 = new Stop("L9") {};
	Instruction instr10 = new Instruction("LABEL1", "ADD R1002 R1003 R1004") {};
	Instruction instr11 = new Instruction("L11", "MUL R1005 R1006 R1007") {};
	Instruction instr12 = new JumpCall("L12", JumpCall.Op.JMP, "END") {};
	Instruction instr13 = new Instruction("LABEL2", "DIV R1008 R1009 R1010") {};
	Instruction instr14 = new Instruction("L14", "PRINT R1011") {};
	Instruction instr15 = new Ret("END") {};

	// Instructions utilisant plus de registres
	Instruction instr16 = new Instruction("L15", "ADD R1012 R1013 R1014") {};
	Instruction instr17 = new Instruction("L16", "MUL R1015 R1016 R1017") {};
	Instruction instr18 = new Instruction("L17", "SUB R1018 R1019 R1020") {};
	Instruction instr19 = new Instruction("L18", "DIV R1021 R1022 R1023") {};
	Instruction instr20 = new Instruction("L19", "AND R1024 R1025 R1026") {};
	Instruction instr21 = new Instruction("L20", "OR R1027 R1028 R1029") {};
	Instruction instr22 = new Instruction("L21", "ADDi R1030 R1031 5") {};
	Instruction instr23 = new Instruction("L22", "XOR R1032 R1033 R1034") {};
	Instruction instr24 = new Instruction("L23", "ADD R1035 R1036 R1037") {};
	Instruction instr25 = new Instruction("L24", "SUB R1038 R1039 R1040") {};
	Instruction instr26 = new Instruction("L25", "MUL R1041 R1042 R1043") {};
	Instruction instr27 = new Instruction("L26", "DIV R1044 R1045 R1046") {};
	Instruction instr28 = new Instruction("L27", "PRINT R1047") {};
	Instruction instr29 = new Instruction("L28", "ADD R1048 R1049 R1050") {};
	Instruction instr30 = new Instruction("L29", "SUB R1051 R1052 R1053") {};
	Instruction instr31 = new Instruction("L30", "MUL R1054 R1055 R1056") {};
	Instruction instr32 = new Instruction("L31", "DIV R1057 R1058 R1059") {};
	Instruction instr33 = new Instruction("L32", "PRINT R1060") {};

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
	program.addInstruction(instr17);
	program.addInstruction(instr18);
	program.addInstruction(instr19);
	program.addInstruction(instr20);
	program.addInstruction(instr21);
	program.addInstruction(instr22);
	program.addInstruction(instr23);
	program.addInstruction(instr24);
	program.addInstruction(instr25);
	program.addInstruction(instr26);
	program.addInstruction(instr27);
	program.addInstruction(instr28);
	program.addInstruction(instr29);
	program.addInstruction(instr30);
	program.addInstruction(instr31);
	program.addInstruction(instr32);
	program.addInstruction(instr33);

        ControlGraph controlGraph = new ControlGraph(program);
        ConflictGraph conflictGraph = new ConflictGraph(controlGraph, program);

        RegisterAllocator allocator = new RegisterAllocator(conflictGraph);
        AssemblerGenerator generator = new AssemblerGenerator(program, allocator);

        String assemblyCode = generator.generateAssembly();
        System.out.println(assemblyCode);
    }
}