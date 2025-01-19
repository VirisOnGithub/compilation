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

    public Map<String, String> getRegisterMap() {
        return registerMap;
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
        Instruction instr46 = new Stop("STOP") {};

        // Ajout des instructions restantes au programme
        program.addInstruction(instr40);
        program.addInstruction(instr41);
        program.addInstruction(instr42);
        program.addInstruction(instr43);
        program.addInstruction(instr44);
        program.addInstruction(instr45);
        program.addInstruction(instr46);

        // Ajout d'un label et d'une instruction de retour pour valider les sauts
        Instruction instrLabel = new UAL("LABEL_EXTRA", UAL.Op.MUL, 10, 9, 8) {};
        Instruction instrReturn = new Ret("FUNC_EXTRA") {};
        program.addInstruction(instrLabel);
        program.addInstruction(instrReturn);

        ControlGraph controlGraph = new ControlGraph(program);
        ConflictGraph conflictGraph = new ConflictGraph(controlGraph, program);

        RegisterAllocator allocator = new RegisterAllocator(conflictGraph);
        allocator.printAllocation();
    }
}