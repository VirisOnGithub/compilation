package src;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class RegisterAllocator {
    private static final int NUM_REGISTERS = 30;
    private HashMap<String, String> registerMap;
    private HashMap<String, Integer> memoryMap;
    private Set<String> memoryVariables;
    private int nextRegister;
    private int nextMemoryAddress;

    public RegisterAllocator() {
        this.registerMap = new HashMap<>();
        this.memoryMap = new HashMap<>();
        this.memoryVariables = new HashSet<>();
        this.nextRegister = 0;
        this.nextMemoryAddress = 0;
    }

    public void allocateRegisters(ConflictGraph conflictGraph) {
        Set<String> variables = conflictGraph.getVertices();
        for (String variable : variables) {
            if (nextRegister < NUM_REGISTERS) {
                registerMap.put(variable, "R" + nextRegister);
                nextRegister++;
            } else {
                memoryVariables.add(variable);
                memoryMap.put(variable, nextMemoryAddress);
                nextMemoryAddress++;
            }
        }
    }

    public String getRegister(String variable) {
        return registerMap.getOrDefault(variable, "MEM[" + memoryMap.get(variable) + "]");
    }

    public boolean isInMemory(String variable) {
        return memoryVariables.contains(variable);
    }

    public int getMemoryAddress(String variable) {
        return memoryMap.get(variable);
    }

    public Set<String> getMemoryVariables() {
        return memoryVariables;
    }

    public void mapLabelToAddress(String label, int address) {
        memoryMap.put(label, address);
    }

    public Integer getLabelAddress(String label) {
        return memoryMap.get(label);
    }
}