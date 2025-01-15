package src;

import src.Graph.OrientedGraph;
import src.Asm.Program;
import src.Asm.CondJump;
import src.Asm.Instruction;
import src.Asm.JumpCall;

import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;


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
            this.addVertex(instruction);
            if (!instruction.getLabel().isEmpty()) {
                if (instruction instanceof CondJump){
                    CondJump condJump = (CondJump) instruction;
                    labelMap.put(condJump.getAddress(), condJump);
                }else if (instruction instanceof JumpCall){
                    JumpCall jumpCall = (JumpCall) instruction;
                    labelMap.put(jumpCall.getAddress(), jumpCall);
                }else{
                    labelMap.put(instruction.getLabel(), instruction);
                }
                

            }
            if (prevInstruction != null) {
                this.addEdge(prevInstruction, instruction);
            }
            prevInstruction = instruction;
        }

        // Ajout des arêtes pour les sauts conditionnels et les appels de fonction après avoir construit le graphe
        for (Instruction instruction : program.getInstructions()) {
            String targetLabel = null;

         if (instruction instanceof CondJump) {
           CondJump condJump = (CondJump) instruction;
           targetLabel = condJump.getAddress();
    } else if (instruction instanceof JumpCall) {
           JumpCall jumpCall = (JumpCall) instruction;
          targetLabel = jumpCall.getAddress();
    }

             if (targetLabel != null && labelMap.containsKey(targetLabel)) {
                 this.addEdge(instruction, labelMap.get(targetLabel));
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
        Instruction instr5 = new Instruction("L5", "JINF R1000 R1001 L6") {};
        Instruction instr6 = new Instruction("L6", "JSUP R1000 R1001 L7") {};
        Instruction instr7 = new JumpCall("L7", JumpCall.Op.CALL, "FUNC1") {};
        Instruction instr8 = new JumpCall("L8", JumpCall.Op.JMP, "END") {};
        Instruction instr9 = new Instruction("L9", "STOP") {};
        Instruction instr10 = new Instruction("LABEL1", "ADD R1002 R1000 R1001") {};
        Instruction instr11 = new Instruction("L11", "MUL R1003 R1002 R1000") {};
        Instruction instr12 = new Instruction("L12", "JMP END") {};
        Instruction instr13 = new Instruction("LABEL2", "DIV R1004 R1003 R1001") {};
        Instruction instr14 = new Instruction("L14", "PRINT R1004") {};
        Instruction instr15 = new Instruction("END", "RET") {};
        Instruction instr16 = new Instruction("FUNC1", "ADD R1005 R1000 R1001") {};
        Instruction instr17 = new Instruction("L17", "RET") {};

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