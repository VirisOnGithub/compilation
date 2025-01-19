package src;

import src.Asm.Program;
import src.Asm.Ret;
import src.Asm.Stop;
import src.Asm.UAL;
import src.Asm.UALi;
import src.Asm.Instruction;
import src.Asm.CondJump;
import src.Asm.IO;
import src.Asm.JumpCall;
import src.Asm.Mem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RegisterAllocator {
    private ConflictGraph conflictGraph;
    private Map<String, String> registerMap;
    private Map<String, String> memoryAllocationMap;
    private List<Mem> dynamicInstructions;
    private int dynamicArrayIndex;

    /**
	 * Constructeur
	 * @param conflictGraph
	 */
	public RegisterAllocator(ConflictGraph conflictGraph) {
        this.conflictGraph = conflictGraph;
        this.registerMap = new HashMap<>();
        this.memoryAllocationMap = new HashMap<>();
        this.dynamicInstructions = new ArrayList<>();
        this.dynamicArrayIndex = 0;
        allocateRegisters();
    }


	 /**
     * Sauvegarde les variables dans la pile avant l'appel de la fonction
     * @param variables
     * @param result
     */
    public void saveGPR(List<String> variables, StringBuilder result) {
        for (String variable : variables) {
            String register = getRegister(variable);
            result.append("PUSH ").append(register).append("\n");
        }
    }


	/**
     * Restaure les variables de la pile après le retour de la fonction
     * @param variables
     * @param result
     */
    public void restoreGPR(List<String> variables, StringBuilder result) {
        for (int i = variables.size() - 1; i >= 0; i--) {
            String variable = variables.get(i);
            String register = getRegister(variable);
            result.append("POP ").append(register).append("\n");
        }
    }

   /**
	* Alloue les registres
    */
	private void allocateRegisters() {
        int registerIndex = 1;
        for (String variable : conflictGraph.getVertices()) {
            if (variable.startsWith("R")) {
				// Si la variable est un registre
                if (registerIndex < 30) {
					// Si le nombre de registres est inférieur à 30
                    registerMap.put(variable, "R" + registerIndex);
                    registerIndex++;
                } else {
					// Sinon, on alloue la variable en mémoire
                    String memLabel = "Mem[" + dynamicArrayIndex + "]";
                    int variableIndex = Integer.parseInt(variable.substring(1));
                    dynamicInstructions.add(new Mem(memLabel, Mem.Op.ST, variableIndex, dynamicArrayIndex));
                    memoryAllocationMap.put(variable, memLabel);
                    dynamicArrayIndex++;
                }
            } else {
				// Si la variable est une constante
                System.out.println("Ignoring constant: " + variable);
            }
        }
    }

    /**
	 * Récupère le registre associé à une variable
	 * @param variable
	 * @return
	 */
	public String getRegister(String variable) {
        if (registerMap.containsKey(variable)) {
            return registerMap.get(variable);
        } else if (memoryAllocationMap.containsKey(variable)) {
            return memoryAllocationMap.get(variable);
        } else {
            throw new IllegalArgumentException("Variable not found in register allocation: " + variable);
        }
    }

    /**
	 * Affiche l'allocation des registres
	 */
	public void printAllocation() {
        System.out.println("Register Allocation:");
        for (Map.Entry<String, String> entry : registerMap.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
        for (String variable : memoryAllocationMap.keySet()) {
            String memLabel = memoryAllocationMap.get(variable);
            System.out.println(variable + " -> " + memLabel + ": ST " + variable + " " + memLabel);
        }
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
        Instruction instr17 = new Mem(Mem.Op.ST, 1, 2) {};

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
        program.addInstruction(instr17);

        ControlGraph controlGraph = new ControlGraph(program);
        ConflictGraph conflictGraph = new ConflictGraph(controlGraph, program);

        RegisterAllocator allocator = new RegisterAllocator(conflictGraph);
        allocator.printAllocation();
    }
}