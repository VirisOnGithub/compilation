package src;

import src.Graph.UnorientedGraph;
import src.Asm.Program;
import java.util.Set;

import src.Asm.Instruction;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ConflictGraph extends UnorientedGraph<String> {

    private Map<Instruction, Set<String>> in;
    private Map<Instruction, Set<String>> out;

    public ConflictGraph(ControlGraph controlGraph, Program program) {
        super();
        this.in = new HashMap<>();
        this.out = new HashMap<>();
        computeLiveness(controlGraph, program);
        buildConflictGraph(controlGraph, program);
    }

    private void computeLiveness(ControlGraph controlGraph, Program program) {
        boolean changed;
        do {
            changed = false;
            for (Instruction instruction : controlGraph.getVertices(program)) {
                Set<String> oldIn = in.getOrDefault(instruction, new HashSet<>());
                Set<String> oldOut = out.getOrDefault(instruction, new HashSet<>());

                Set<String> newIn = new HashSet<>(oldOut);
                newIn.removeAll(getDef(instruction));
                newIn.addAll(getUse(instruction));

                Set<String> newOut = new HashSet<>();
                for (Instruction succ : controlGraph.getOutNeighbors(instruction)) {
                    newOut.addAll(in.getOrDefault(succ, new HashSet<>()));
                }

                if (!newIn.equals(oldIn) || !newOut.equals(oldOut)) {
                    in.put(instruction, newIn);
                    out.put(instruction, newOut);
                    changed = true;
                }
            }
        } while (changed);
    }

    private void buildConflictGraph(ControlGraph controlGraph, Program program) {
        for (Instruction instruction : controlGraph.getVertices(program)) {
            Set<String> live = new HashSet<>(in.getOrDefault(instruction, new HashSet<>()));
            live.addAll(out.getOrDefault(instruction, new HashSet<>()));

            for (String var1 : live) {
                for (String var2 : live) {
                    if (!var1.equals(var2)) {
                        this.addEdge(var1, var2);
                    }
                }
            }
        }
    }

    private Set<String> getUse(Instruction instruction) {
        Set<String> use = new HashSet<>();
        String[] parts = instruction.getName().split(" ");
        switch (parts[0]) {
            case "XOR":
            case "SUBi":
            case "ADD":
            case "MUL":
            case "DIV":
                use.add(parts[2]);
                use.add(parts[3]);
                break;
            case "PRINT":
            case "JEQU":
            case "JINF":
            case "JSUP":
                use.add(parts[1]);
                break;
            case "CALL":
                // No use variables for CALL
                break;
            default:
                break;
        }
        return use;
    }

    private Set<String> getDef(Instruction instruction) {
        Set<String> def = new HashSet<>();
        String[] parts = instruction.getName().split(" ");
        switch (parts[0]) {
            case "XOR":
            case "SUBi":
            case "ADD":
            case "MUL":
            case "DIV":
                def.add(parts[1]);
                break;
            case "PRINT":
            case "JEQU":
            case "JINF":
            case "JSUP":
            case "CALL":
            case "RET":
            case "JMP":
            case "STOP":
                // No def variables for these instructions
                break;
            default:
                break;
        }
        return def;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String u : this.vertices) {
            sb.append(u).append(" -> ");
            for (String v : this.adjList.get(u)) {
                sb.append(v).append(", ");
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
        Instruction instr4 = new Instruction("L4", "JEQU R1000 R1001 LABEL2") {};
        Instruction instr5 = new Instruction("L5", "JINF R1000 R1001 L6") {};
        Instruction instr6 = new Instruction("L6", "JSUP R1000 R1001 L7") {};
        Instruction instr7 = new Instruction("L7", "CALL FUNC1") {};
        Instruction instr8 = new Instruction("L8", "JMP END") {};
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
        ConflictGraph conflictGraph = new ConflictGraph(controlGraph, program);
        System.out.println(conflictGraph);
    }
}