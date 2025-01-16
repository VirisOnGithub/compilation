package src;

import src.Asm.Program;
import src.Asm.Instruction;
import java.util.Map;

public class AssemblerGenerator {
    private Program program;
    private RegisterAllocator allocator;

    public AssemblerGenerator(Program program, RegisterAllocator allocator) {
        this.program = program;
        this.allocator = allocator;
    }

    public String generateCode() {
        StringBuilder code = new StringBuilder();
        for (Instruction instruction : program.getInstructions()) {
            code.append(generateInstructionCode(instruction)).append("\n");
        }
        return code.toString();
    }

    private String generateInstructionCode(Instruction instruction) {
        String[] parts = instruction.getName().split(" ");
        StringBuilder result = new StringBuilder();

        if (!instruction.getLabel().isEmpty()) {
            result.append(instruction.getLabel()).append(": ");
        }

        switch (parts[0]) {
            case "XOR":
            case "OR":
            case "AND":
            case "NOT":
            case "MOV":
            case "SUB":
            case "ADD":
            case "MUL":
            case "DIV":
                String reg1 = allocator.getRegister(parts[2]);
                String reg2 = allocator.getRegister(parts[3]);
                String destReg = allocator.getRegister(parts[1]);

                result.append("LD R30, ").append(reg1).append("\n");
                result.append("LD R31, ").append(reg2).append("\n");
                result.append(parts[0]).append(" R30, R30, R31\n");
                result.append("ST R30, ").append(destReg).append("\n");
                break;
            case "PRINT":
                result.append(parts[0]).append(" ")
                      .append(allocator.getRegister(parts[1]));
                break;
            case "JEQU":
            case "JINF":
            case "JSUP":
                result.append(parts[0]).append(" ")
                      .append(allocator.getRegister(parts[1])).append(", ")
                      .append(allocator.getRegister(parts[2])).append(", ")
                      .append(parts[3]);
                break;
            case "CALL":
            case "JMP":
                result.append(parts[0]).append(" ")
                      .append(parts[1]);
                break;
            case "RET":
            case "STOP":
                result.append(parts[0]);
                break;
            default:
                throw new IllegalArgumentException("Unknown instruction: " + parts[0]);
        }

        return result.toString();
    }

    public static void main(String[] args) {
        Program program = new Program();

        for (int i = 0; i < 40; i++) {
            program.addInstruction(new Instruction("L" + (i + 1), "ADD R" + (1000 + i) + " R" + (1000 + i) + " R" + (1000 + i)) {});
        }

        ControlGraph controlGraph = new ControlGraph(program);
        ConflictGraph conflictGraph = new ConflictGraph(controlGraph, program);
        RegisterAllocator allocator = new RegisterAllocator(conflictGraph);

        AssemblerGenerator generator = new AssemblerGenerator(program, allocator);
        String assemblyCode = generator.generateCode();
        System.out.println(assemblyCode);
    }
}