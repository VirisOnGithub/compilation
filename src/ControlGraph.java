package src;

import src.Graph.OrientedGraph;
import src.Asm.Program;
import src.Asm.Instruction;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;
import src.Asm.SubInstruction;

public class ControlGraph {
    private OrientedGraph<String> controlFlowGraph;

    public ControlGraph() {
        this.controlFlowGraph = new OrientedGraph<>();
    }

    public void buildControlFlowGraph(Program prog) {
        String prevLabel = null;
        for (Instruction instr : prog.getInstructions()) {
            String label = instr.getLabel();
            controlFlowGraph.addVertex(label);
            if (prevLabel != null) {
                controlFlowGraph.addEdge(prevLabel, label);
            }
            if (instr.getName().equals("JMP") || instr.getName().equals("JNE")) {
                controlFlowGraph.addEdge(label, instr.getDefinedVariables().get(0));
            }
            if (!instr.getName().equals("JMP")) {
                prevLabel = label;
            } else {
                prevLabel = null;
            }
        }
    }

    public void printControlFlowGraph() {
        System.out.println("Control Flow Graph:");
        for (String vertex : controlFlowGraph.getVertices()) {
            System.out.print(vertex + ": ");
            for (String neighbor : controlFlowGraph.getOutNeighbors(vertex)) {
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

        // Create a sample Program object
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

        // Build control flow graph
        ControlGraph controlGraph = new ControlGraph();
        controlGraph.buildControlFlowGraph(prog);

        // Print control flow graph
        controlGraph.printControlFlowGraph();

        // Generate and display assembly code
        // String assemblyCode = controlGraph.generateAssemblyCode(prog);
        // System.out.println(assemblyCode);
    }
}