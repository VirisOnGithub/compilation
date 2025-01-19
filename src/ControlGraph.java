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
        // Première passe : construction du graphe et de la map des labels
        Instruction prevInstruction = null;
        for (Instruction instruction : program.getInstructions()) {
            int index = program.getInstructions().indexOf(instruction);
            this.addVertex(instruction);
            if (!instruction.getLabel().isEmpty()) {
                labelMap.put(instruction.getLabel(), instruction);
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

        // Deuxième passe : gestion des sauts (JMP, CALL et RET)
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
                sb.setLength(sb.length() - 2); // Remove the last comma and space
            }
            sb.append("\n");
        }
        return sb.toString();
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

        ControlGraph controlGraph = new ControlGraph(program);
        System.out.println(controlGraph);
    }
}
