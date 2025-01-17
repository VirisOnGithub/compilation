package src;

import src.Graph.OrientedGraph;
import src.Asm.Program;
import src.Asm.CondJump;
import src.Asm.Instruction;
import src.Asm.JumpCall;
import src.Asm.Ret;
import src.Asm.Stop;

import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class ControlGraph extends OrientedGraph<Instruction> {
    private Stack<Instruction> callStack = new Stack<>();
    private Map<String, Instruction> labelMap;

    /**
     * Constructeur
     * @param program le programme à partir duquel construire le graphe de contrôle
     */
    public ControlGraph(Program program) {
        super();
        this.labelMap = new HashMap<>();
        buildControlGraph(program);
    }

    /**
     * Construit le graphe de contrôle à partir d'un programme
     * @param program le programme à partir duquel construire le graphe de contrôle
     */
    private void buildControlGraph(Program program) {
        Instruction prevInstruction = null;
    
        // Construire la map des étiquettes
        for (Instruction instruction : program.getInstructions()) {
            this.addVertex(instruction);
            if (!instruction.getLabel().isEmpty()) {
                labelMap.put(instruction.getLabel(), instruction);
            }
        }
    
        // Construire les arêtes du graphe
        for (Instruction instruction : program.getInstructions()) {
            if (prevInstruction != null && !(prevInstruction instanceof Stop) && !(prevInstruction instanceof JumpCall)) {
                this.addEdge(prevInstruction, instruction);
            }
    
            if (instruction instanceof CondJump) {
                CondJump condJump = (CondJump) instruction;
                String targetLabel = condJump.getAddress();
                if (labelMap.containsKey(targetLabel)) {
                    this.addEdge(instruction, labelMap.get(targetLabel));
                }
            } else if (instruction instanceof JumpCall) {
                JumpCall jumpCall = (JumpCall) instruction;
                if (jumpCall.getName().equals("CALL")) {
                    String targetLabel = jumpCall.getAddress();
                    if (labelMap.containsKey(targetLabel)) {
                        this.addEdge(instruction, labelMap.get(targetLabel));
                        callStack.push(instruction); // Empiler l'appel pour connecter au retour
                    }
                } else if (jumpCall.getName().equals("JMP")) {
                    String targetLabel = jumpCall.getAddress();
                    if (labelMap.containsKey(targetLabel)) {
                        this.addEdge(instruction, labelMap.get(targetLabel));
                    }
                }
            } else if (instruction instanceof Ret) {
                if (!callStack.isEmpty()) {
                    Instruction lastCall = callStack.pop();
                    this.addEdge(lastCall, instruction); // Connecter le retour à l'appel correspondant
                }
            }
    
            if (!(instruction instanceof Stop)) {
                prevInstruction = instruction;
            } else {
                prevInstruction = null;
            }
        }
    }    

    /**
     * Retourne l'ensemble des sommets du graphe 
     */
    public Set<Instruction> getVertices(Program program) {
        Set<Instruction> vertices = new HashSet<>();
        for (Instruction instruction : program.getInstructions()) {
            vertices.add(instruction);
        }
        return vertices;
    }

    /**
     * Retourne l'ensemble des sommets du graphe
     * @param program
     * @return
     */
    public Set<Instruction> getAllVertices(Program program) {
        Set<Instruction> vertices = new HashSet<>();
        for (Instruction instruction : program.getInstructions()) {
            vertices.add(instruction);
        }
        return vertices;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Instruction u : this.vertices) {
            sb.append(u.getLabel()).append(" -> ");
            for (Instruction v : this.adjList.get(u)) {
                sb.append(v.getLabel()).append(", ");
            }
            if (this.adjList.get(u).size() > 0) {
                sb.setLength(sb.length() - 2); // Remove the last comma and space
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        Program program = new Program();

        Instruction instr1 = new Instruction("L1", "XOR R1000 R1000 R1000") {};
        Instruction instr2 = new Instruction("L2", "SUBi R1000 R1000 1") {};
        Instruction instr3 = new Instruction("L3", "PRINT R1001") {};
        Instruction instr4 = new CondJump("L4", CondJump.Op.JEQU, 1000, 1001, "LABEL2") {};
        Instruction instr5 = new CondJump("L5", CondJump.Op.JINF, 1000, 1001, "FUNC1") {};
        Instruction instr6 = new CondJump("L6", CondJump.Op.JSUP, 1000, 1001, "LABEL1") {};
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
        System.out.println(controlGraph);
    }
}
