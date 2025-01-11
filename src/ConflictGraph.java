package src;

import src.Graph.UnorientedGraph;
import src.Asm.Program;
import src.Asm.SubInstruction;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import src.Asm.Instruction;

public class ConflictGraph {
    private UnorientedGraph<String> conflictGraph;

    public ConflictGraph() {
        this.conflictGraph = new UnorientedGraph<>();
    }

    public void buildConflictGraph(Program prog) {
        // Calculer les variables vivantes en entrée et en sortie
        calculateLiveVariables(prog);

        // Construire le graphe de conflits
        for (Instruction instr : prog.getInstructions()) {
            for (String var1 : instr.getLiveVariables()) {
                for (String var2 : instr.getLiveVariables()) {
                    if (!var1.equals(var2)) {
                        conflictGraph.addEdge(var1, var2);
                    }
                }
            }
        }
    }

    private void calculateLiveVariables(Program prog) {
        boolean changed;
        do {
            changed = false;
            for (int i = prog.getInstructions().size() - 1; i >= 0; i--) {
                Instruction instr = prog.getInstructions().get(i);
                List<String> liveOut = new ArrayList<>();
                if (i < prog.getInstructions().size() - 1) {
                    liveOut.addAll(prog.getInstructions().get(i + 1).getLiveVariables());
                }
                List<String> liveIn = new ArrayList<>(liveOut);
                liveIn.removeAll(instr.getDefinedVariables());
                liveIn.addAll(instr.getUsedVariables());

                if (!liveIn.equals(instr.getLiveVariables())) {
                    instr.setLiveVariables(liveIn);
                    changed = true;
                }
            }
        } while (changed);
    }

    public Set<String> getVertices() {
        return conflictGraph.getVertices();
    }

    public void printConflictGraph() {
        System.out.println("Conflict Graph:");
        for (String vertex : conflictGraph.getVertices()) {
            System.out.print(vertex + ": ");
            for (String neighbor : conflictGraph.getNeighbors(vertex)) {
                System.out.print(neighbor + " ");
            }
            System.out.println();
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

        // Create a sample Program object with more complex instructions
        Program prog = new Program(new ArrayList<>(Arrays.asList(
            new SubInstruction("L1", "MOV", Arrays.asList("a"), Arrays.asList("b", "c")),
            new SubInstruction("L2", "ADD", Arrays.asList("b"), Arrays.asList("a", "d")),
            new SubInstruction("L3", "CMP", Arrays.asList("a"), Arrays.asList("b")),
            new SubInstruction("L4", "JNE", Arrays.asList("L6"), Arrays.asList()),
            new SubInstruction("L5", "SUB", Arrays.asList("c"), Arrays.asList("b")),
            new SubInstruction("L6", "MUL", Arrays.asList("d"), Arrays.asList("c")),
            new SubInstruction("L7", "JMP", Arrays.asList("L9"), Arrays.asList()),
            new SubInstruction("L8", "DIV", Arrays.asList("a"), Arrays.asList("d")),
            new SubInstruction("L9", "NOP", Arrays.asList(), Arrays.asList())
        )));

        // Build conflict graph
        ConflictGraph conflictGraph = new ConflictGraph();
        conflictGraph.buildConflictGraph(prog);

        // Print conflict graph
        conflictGraph.printConflictGraph();
    }

    
}