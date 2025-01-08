package src;

import src.Graph.UnorientedGraph;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;
import java.util.ArrayList; // Import statement added
import src.Asm.Instruction;
import src.Asm.Program;
import src.Asm.SubInstruction;

public class ControlGraph {
    private UnorientedGraph<String> conflictGraph;
    private HashMap<String, String> registerAllocation;

    public ControlGraph() {
        this.conflictGraph = new UnorientedGraph<>();
        this.registerAllocation = new HashMap<>();
    }

    public void buildControlGraph(Program prog) {
        Set<String> liveVariables = new HashSet<>();
        for (Instruction instr : prog.getInstructions()) {
            liveVariables.addAll(instr.getLiveVariables());
            for (String var : instr.getDefinedVariables()) {
                for (String liveVar : liveVariables) {
                    if (!liveVar.equals(var)) {
                        conflictGraph.addEdge(var, liveVar);
                    }
                }
                liveVariables.remove(var);
            }
            liveVariables.addAll(instr.getUsedVariables());
        }
    }

    public void allocateRegisters() {
        int numColors = conflictGraph.color();
        for (String var : conflictGraph.getVertices()) {
            int color = conflictGraph.getColor(var);
            registerAllocation.put(var, "R" + color);
        }
    }

    public String generateAssemblyCode(Program prog) {
        StringBuilder assemblyCode = new StringBuilder();
        for (Instruction instr : prog.getInstructions()) {
            assemblyCode.append(instr.toAssembly(registerAllocation)).append("\n");
        }
        return assemblyCode.toString();
    }

    public static void main(String[] args) {
        // Program de test
        Program prog = new Program(new ArrayList<>(Arrays.asList(
            new SubInstruction("L1", "MOV", Arrays.asList("a"), Arrays.asList("b", "c")),
            new SubInstruction("L2", "ADD", Arrays.asList("b"), Arrays.asList("a", "d")),
            new SubInstruction("L3", "SUB", Arrays.asList("c"), Arrays.asList("b")),
            new SubInstruction("L4", "MUL", Arrays.asList("d"), Arrays.asList("c"))
        )));

        // Graph de controle
        ControlGraph controlGraph = new ControlGraph();

        // Build graphe de controle pour le programme
        controlGraph.buildControlGraph(prog);

        // Allocation des registres
        controlGraph.allocateRegisters();

        // Generation et affichage du code assembleur
        String assemblyCode = controlGraph.generateAssemblyCode(prog);
        System.out.println(assemblyCode);
    }
}