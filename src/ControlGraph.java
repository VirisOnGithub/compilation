package src;

import src.Graph.OrientedGraph;
import src.Asm.Program;
import src.Asm.CondJump;
import src.Asm.Instruction;
import src.Asm.JumpCall;
import src.Asm.Ret;
import src.Asm.Stop;
import src.Asm.Mem;
import src.Asm.UAL;
import src.Asm.UALi;
import src.Asm.IO;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class ControlGraph extends OrientedGraph<Instruction> {
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
        for (Instruction instruction : program.getInstructions()) {
            int index = program.getInstructions().indexOf(instruction);
            this.addVertex(instruction);
            if (!instruction.getLabel().isEmpty()) {
                labelMap.put(instruction.getLabel(), instruction);
            }else{
                labelMap.put(Integer.toString(index), instruction);
            }
            if (instruction instanceof Mem || instruction instanceof UAL || instruction instanceof UALi || instruction instanceof IO) {
                labelMap.put(Integer.toString(index), instruction);
            }
            if (prevInstruction != null && !(prevInstruction instanceof Stop)
                    && !(prevInstruction instanceof JumpCall) // Changement ici
                    && !(prevInstruction instanceof Ret)) {
                this.addEdge(prevInstruction, instruction);
                    }

            if (!(instruction instanceof Stop)) {
                prevInstruction = instruction;
            } else {
                prevInstruction = null;
            }
        }

        Stack<Instruction> callStack = new Stack<>();
        for (Instruction instruction : program.getInstructions()) {
            if (instruction instanceof CondJump) {
                CondJump condJump = (CondJump) instruction;
                connectJump(instruction, condJump.getAddress());
            } else if (instruction instanceof JumpCall) {
                JumpCall jumpCall = (JumpCall) instruction;
                connectJump(instruction, jumpCall.getAddress());
                if (jumpCall.getName().equals("CALL")) { // Changement ici
                    int index = program.getInstructions().indexOf(instruction);
                    Instruction nextInstruction = null;
                    if (index < program.getInstructions().size() - 1) {
                        nextInstruction = program.getInstructions().get(index + 1);
                    }
                    callStack.push(nextInstruction);
                }
            } else if (instruction instanceof Ret) {
                if (!callStack.isEmpty()) {
                    Instruction returnTo = callStack.pop();
                    if (returnTo != null) {
                        this.addEdge(instruction, returnTo);
                    }
                }
            }
        }
    }

    /**
     * Connecte un saut à une instruction
     * @param from
     * @param targetLabel
     */
    private void connectJump(Instruction from, String targetLabel) {
        if (labelMap.containsKey(targetLabel)) {
            this.addEdge(from, labelMap.get(targetLabel));
        } else {
            System.err.println("Label non trouvé : " + targetLabel + " depuis " + from.getLabel());
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
            int index = vertices.indexOf(u);
            if (u.getLabel().isEmpty()) {
                sb.append(index + 1).append("-> ");
            } else {
                sb.append(u.getLabel()).append("-> ");
            }
            for (Instruction v : this.adjList.get(u)) {
                if (v.getLabel().isEmpty()) {
                    sb.append(vertices.indexOf(v) + 1).append(", ");
                } else {
                    sb.append(v.getLabel()).append(", ");
                }
            }
            if (this.adjList.get(u).size() > 0) {
                sb.setLength(sb.length() - 2);
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
