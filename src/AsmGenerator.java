package src;

import src.Asm.Program;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import src.Asm.Instruction;
import src.Asm.SubInstruction;

public class AsmGenerator {
    private RegisterAllocator registerAllocator;

    public AsmGenerator(RegisterAllocator registerAllocator) {
        this.registerAllocator = registerAllocator;
    }

    public void generateAssembly(Program prog) {
        // Map labels to addresses
        int address = 0;
        for (Instruction instr : prog.getInstructions()) {
            if (instr.getLabel() != null) {
                registerAllocator.mapLabelToAddress(instr.getLabel(), address);
            }
            address++;
        }

        // Generate assembly code
        for (Instruction instr : prog.getInstructions()) {
            StringBuilder asm = new StringBuilder();
            asm.append(instr.getName()).append(" ");
            for (String var : instr.getDefinedVariables()) {
                if (registerAllocator.isInMemory(var)) {
                    asm.append("LD R30, MEM[").append(registerAllocator.getMemoryAddress(var)).append("], ");
                    asm.append("R30, ");
                } else {
                    asm.append(registerAllocator.getRegister(var)).append(", ");
                }
            }
            for (String var : instr.getUsedVariables()) {
                if (registerAllocator.isInMemory(var)) {
                    asm.append("LD R31, MEM[").append(registerAllocator.getMemoryAddress(var)).append("], ");
                    asm.append("R31, ");
                } else {
                    asm.append(registerAllocator.getRegister(var)).append(", ");
                }
            }
            if (instr.getName().equals("JMP") || instr.getName().equals("JNE")) {
                Integer labelAddress = registerAllocator.getLabelAddress(instr.getDefinedVariables().get(0));
                if (labelAddress != null) {
                    asm.append("MEM[").append(labelAddress).append("]");
                } else {
                    asm.append("MEM[null]");
                }
            }
            System.out.println(asm.toString().replaceAll(", $", ""));
            for (String var : instr.getDefinedVariables()) {
                if (registerAllocator.isInMemory(var)) {
                    System.out.println("ST R30, MEM[" + registerAllocator.getMemoryAddress(var) + "]");
                }
            }
        }
    }

    public static void main(String[] args) {
        String fileName = "input.txt";
        StringBuilder input = new StringBuilder();

        try {
            InputStream ips = new FileInputStream(fileName);
            InputStreamReader ipsr = new InputStreamReader(ips);
            BufferedReader br = new BufferedReader(ipsr);
            String line;
            while ((line = br.readLine()) != null) {
                input.append(line).append("\n");
            }
            br.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        // Create a sample Program object with more than 32 variables
        Program prog = new Program(new ArrayList<>(Arrays.asList(
            new SubInstruction("L1", "MOV", Arrays.asList("a1"), Arrays.asList("b1", "c1")),
            new SubInstruction("L2", "ADD", Arrays.asList("b1"), Arrays.asList("a1", "d1")),
            new SubInstruction("L3", "CMP", Arrays.asList("a1"), Arrays.asList("b1")),
            new SubInstruction("L4", "JNE", Arrays.asList("L6"), Arrays.asList()),
            new SubInstruction("L5", "SUB", Arrays.asList("c1"), Arrays.asList("b1")),
            new SubInstruction("L6", "MUL", Arrays.asList("d1"), Arrays.asList("c1")),
            new SubInstruction("L7", "JMP", Arrays.asList("L9"), Arrays.asList()),
            new SubInstruction("L8", "DIV", Arrays.asList("a1"), Arrays.asList("d1")),
            new SubInstruction("L9", "NOP", Arrays.asList(), Arrays.asList()),
            new SubInstruction("L10", "MOV", Arrays.asList("a2"), Arrays.asList("b2", "c2")),
            new SubInstruction("L11", "ADD", Arrays.asList("b2"), Arrays.asList("a2", "d2")),
            new SubInstruction("L12", "CMP", Arrays.asList("a2"), Arrays.asList("b2")),
            new SubInstruction("L13", "JNE", Arrays.asList("L15"), Arrays.asList()),
            new SubInstruction("L14", "SUB", Arrays.asList("c2"), Arrays.asList("b2")),
            new SubInstruction("L15", "MUL", Arrays.asList("d2"), Arrays.asList("c2")),
            new SubInstruction("L16", "JMP", Arrays.asList("L18"), Arrays.asList()),
            new SubInstruction("L17", "DIV", Arrays.asList("a2"), Arrays.asList("d2")),
            new SubInstruction("L18", "NOP", Arrays.asList(), Arrays.asList()),
            new SubInstruction("L19", "MOV", Arrays.asList("a3"), Arrays.asList("b3", "c3")),
            new SubInstruction("L20", "ADD", Arrays.asList("b3"), Arrays.asList("a3", "d3")),
            new SubInstruction("L21", "CMP", Arrays.asList("a3"), Arrays.asList("b3")),
            new SubInstruction("L22", "JNE", Arrays.asList("L24"), Arrays.asList()),
            new SubInstruction("L23", "SUB", Arrays.asList("c3"), Arrays.asList("b3")),
            new SubInstruction("L24", "MUL", Arrays.asList("d3"), Arrays.asList("c3")),
            new SubInstruction("L25", "JMP", Arrays.asList("L27"), Arrays.asList()),
            new SubInstruction("L26", "DIV", Arrays.asList("a3"), Arrays.asList("d3")),
            new SubInstruction("L27", "NOP", Arrays.asList(), Arrays.asList()),
            new SubInstruction("L28", "MOV", Arrays.asList("a4"), Arrays.asList("b4", "c4")),
            new SubInstruction("L29", "ADD", Arrays.asList("b4"), Arrays.asList("a4", "d4")),
            new SubInstruction("L30", "CMP", Arrays.asList("a4"), Arrays.asList("b4")),
            new SubInstruction("L31", "JNE", Arrays.asList("L33"), Arrays.asList()),
            new SubInstruction("L32", "SUB", Arrays.asList("c4"), Arrays.asList("b4")),
            new SubInstruction("L33", "MUL", Arrays.asList("d4"), Arrays.asList("c4")),
            new SubInstruction("L34", "JMP", Arrays.asList("L36"), Arrays.asList()),
            new SubInstruction("L35", "DIV", Arrays.asList("a4"), Arrays.asList("d4")),
            new SubInstruction("L36", "NOP", Arrays.asList(), Arrays.asList()),
            new SubInstruction("L37", "MOV", Arrays.asList("a5"), Arrays.asList("b5", "c5")),
            new SubInstruction("L38", "ADD", Arrays.asList("b5"), Arrays.asList("a5", "d5")),
            new SubInstruction("L39", "CMP", Arrays.asList("a5"), Arrays.asList("b5")),
            new SubInstruction("L40", "JNE", Arrays.asList("L42"), Arrays.asList()),
            new SubInstruction("L41", "SUB", Arrays.asList("c5"), Arrays.asList("b5")),
            new SubInstruction("L42", "MUL", Arrays.asList("d5"), Arrays.asList("c5")),
            new SubInstruction("L43", "JMP", Arrays.asList("L45"), Arrays.asList()),
            new SubInstruction("L44", "DIV", Arrays.asList("a5"), Arrays.asList("d5")),
            new SubInstruction("L45", "NOP", Arrays.asList(), Arrays.asList())
        )));

        // Build conflict graph
        ConflictGraph conflictGraph = new ConflictGraph();
        conflictGraph.buildConflictGraph(prog);

        // Allocate registers
        RegisterAllocator registerAllocator = new RegisterAllocator();
        registerAllocator.allocateRegisters(conflictGraph);

        // Generate and display assembly code
        AsmGenerator asmGenerator = new AsmGenerator(registerAllocator);
        asmGenerator.generateAssembly(prog);
    }
}