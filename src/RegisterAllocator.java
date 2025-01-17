package src;

import src.Asm.Program;
import src.Asm.Ret;
import src.Asm.Stop;
import src.Asm.Instruction;
import src.Asm.CondJump;
import src.Asm.JumpCall;
import src.Asm.Mem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RegisterAllocator {
    private ConflictGraph conflictGraph;
    private Map<String, String> registerMap;
    private List<Mem> dynamicInstructions;
    private int dynamicArrayIndex;

    public RegisterAllocator(ConflictGraph conflictGraph) {
        this.conflictGraph = conflictGraph;
        this.registerMap = new HashMap<>();
        this.dynamicInstructions = new ArrayList<>();
        this.dynamicArrayIndex = 0;
        allocateRegisters();
    }

    private void allocateRegisters() {
        int registerIndex = 0;
        for (String variable : conflictGraph.getVertices()) {
            if (registerIndex < 30) {
                registerMap.put(variable, "R" + registerIndex);
                registerIndex++;
            } else {
                dynamicInstructions.add(new Mem("DYNAMIC" + dynamicArrayIndex, Mem.Op.LD, registerIndex, dynamicArrayIndex));
                dynamicArrayIndex++;
            }
        }
    }

    public String getRegister(String variable) {
        if (registerMap.containsKey(variable)) {
            return registerMap.get(variable);
        } else {
            for (Mem mem : dynamicInstructions) {
                if (mem.getLabel().equals("DYNAMIC" + dynamicArrayIndex)) {
                    return mem.toString();
                }
            }
            throw new IllegalArgumentException("Variable not found in register allocation: " + variable);
        }
    }

    public void printAllocation() {
        System.out.println("Register Allocation:");
        for (Map.Entry<String, String> entry : registerMap.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
        for (Mem mem : dynamicInstructions) {
            System.out.println(mem.getLabel() + " -> " + mem.toString());
        }
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
        Instruction instr10 = new Instruction("LABEL1", "ADD R1002 R1000 R1001") {};
        Instruction instr11 = new Instruction("L11", "MUL R1003 R1002 R1000") {};
        Instruction instr12 = new JumpCall("L12", JumpCall.Op.JMP ,"END") {};
        Instruction instr13 = new Instruction("LABEL2", "DIV R1004 R1003 R1001") {};
        Instruction instr14 = new Instruction("L14", "PRINT R1004") {};
        Instruction instr15 = new Ret("END") {};
        Instruction instr16 = new Instruction("FUNC1", "ADD R1005 R1000 R1001") {};
        Instruction instr17 = new Ret("L17") {};

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

        ControlGraph controlGraph = new ControlGraph(program);
        ConflictGraph conflictGraph = new ConflictGraph(controlGraph, program);

        RegisterAllocator allocator = new RegisterAllocator(conflictGraph);
        allocator.printAllocation();
    }
}