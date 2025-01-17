package src;

import src.Graph.UnorientedGraph;
import src.Asm.Program;
import src.Asm.Ret;
import src.Asm.Stop;
import src.Asm.CondJump;
import src.Asm.Instruction;
import src.Asm.JumpCall;

import java.util.Set;
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
            // Traverse all instructions in the program, including those in subprograms
            for (Instruction instruction : controlGraph.getAllVertices(program)) {
            Set<String> oldIn = in.getOrDefault(instruction, new HashSet<>());
            Set<String> oldOut = out.getOrDefault(instruction, new HashSet<>());

            Set<String> newIn = new HashSet<>(oldOut);
            newIn.addAll(getDef(instruction)); // Keep defined variables
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
        // Iterate through all instructions
        for (Instruction instruction : controlGraph.getAllVertices(program)) {
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
        if (parts.length > 1) {
            switch (parts[0]) {
                case "XOR":
                case "OR":
                case "AND":
                case "NOT":
                case "MOV":
                case "SUB":
                case "ADD":
                case "MUL":
                case "DIV":
                    if (parts.length > 3) {
                        use.add(parts[2]);
                        use.add(parts[3]);
                    }
                    break;
                case "ADDi":
                case "SUBi":
                case "MULi":
                case "DIVi":
                    if (parts.length > 2) {
                        use.add(parts[2]);
                    }
                    break;
                case "PRINT":
                case "JEQU":
                case "JINF":
                case "JSUP":
                case "JNEQ":
                case "JIEQ":
                case "JSEQ":
                    if (parts.length > 1) {
                        use.add(parts[1]);
                    }
                    break;
                case "CALL":
                    // No use variables for CALL
                    break;
                default:
                    break;
            }
        }
        return use;
    }

    private Set<String> getDef(Instruction instruction) {
        Set<String> def = new HashSet<>();
        String[] parts = instruction.getName().split(" ");
        if (parts.length > 1) {
            switch (parts[0]) {
                case "XOR":
                case "OR":
                case "AND":
                case "NOT":
                case "ADDi":
                case "SUBi":
                case "MULi":
                case "DIVi":
                case "ADD":
                case "SUB":
                case "MUL":
                case "DIV":
                    if (parts.length > 1) {
                        def.add(parts[1]);
                    }
                    break;
                case "PRINT":
                case "JEQU":
                case "JINF":
                case "JSUP":
                case "JNEQ":
                case "JIEQ":
                case "JSEQ":
                case "CALL":
                case "RET":
                case "JMP":
                case "STOP":
                    // No def variables for these instructions
                    break;
                default:
                    break;
            }
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
        Instruction instr4 = new CondJump("L4", CondJump.Op.JEQU, 1000, 1001, "LABEL2") {};
        Instruction instr5 = new CondJump("L5", CondJump.Op.JINF, 1000, 1001, "L6") {};
        Instruction instr6 = new CondJump("L6", CondJump.Op.JSUP, 1000, 1001, "L7") {};
        Instruction instr7 = new JumpCall("L7", JumpCall.Op.CALL, "FUNC1") {};
        Instruction instr8 = new JumpCall("L8", JumpCall.Op.JMP, "END") {};
        Instruction instr9 = new Stop("L9") {};
        Instruction instr10 = new Instruction("LABEL1", "ADD R1002 R1003 R1004") {};
        Instruction instr11 = new Instruction("L11", "MUL R1005 R1006 R1007") {};
        Instruction instr12 = new JumpCall("L12", JumpCall.Op.JMP, "END") {};
        Instruction instr13 = new Instruction("LABEL2", "DIV R1008 R1009 R1010") {};
        Instruction instr14 = new Instruction("L14", "PRINT R1011") {};
        Instruction instr15 = new Ret("END") {};

        // Instructions utilisant plus de registres
        Instruction instr16 = new Instruction("L15", "ADD R1012 R1013 R1014") {};
        Instruction instr17 = new Instruction("L16", "MUL R1015 R1016 R1017") {};
        Instruction instr18 = new Instruction("L17", "SUB R1018 R1019 R1020") {};
        Instruction instr19 = new Instruction("L18", "DIV R1021 R1022 R1023") {};
        Instruction instr20 = new Instruction("L19", "AND R1024 R1025 R1026") {};
        Instruction instr21 = new Instruction("L20", "OR R1027 R1028 R1029") {};
        Instruction instr22 = new Instruction("L21", "ADDi R1030 R1031 5") {};
        Instruction instr23 = new Instruction("L22", "XOR R1032 R1033 R1034") {};
        Instruction instr24 = new Instruction("L23", "ADD R1035 R1036 R1037") {};
        Instruction instr25 = new Instruction("L24", "SUB R1038 R1039 R1040") {};
        Instruction instr26 = new Instruction("L25", "MUL R1041 R1042 R1043") {};
        Instruction instr27 = new Instruction("L26", "DIV R1044 R1045 R1046") {};
        Instruction instr28 = new Instruction("L27", "PRINT R1047") {};
        Instruction instr29 = new Instruction("L28", "ADD R1048 R1049 R1050") {};
        Instruction instr30 = new Instruction("L29", "SUB R1051 R1052 R1053") {};
        Instruction instr31 = new Instruction("L30", "MUL R1054 R1055 R1056") {};
        Instruction instr32 = new Instruction("L31", "DIV R1057 R1058 R1059") {};
        Instruction instr33 = new Instruction("L32", "PRINT R1060") {};

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
        program.addInstruction(instr18);
        program.addInstruction(instr19);
        program.addInstruction(instr20);
        program.addInstruction(instr21);
        program.addInstruction(instr22);
        program.addInstruction(instr23);
        program.addInstruction(instr24);
        program.addInstruction(instr25);
        program.addInstruction(instr26);
        program.addInstruction(instr27);
        program.addInstruction(instr28);
        program.addInstruction(instr29);
        program.addInstruction(instr30);
        program.addInstruction(instr31);
        program.addInstruction(instr32);
        program.addInstruction(instr33);

        ControlGraph controlGraph = new ControlGraph(program);
        ConflictGraph conflictGraph = new ConflictGraph(controlGraph, program);

        // Afficher le graphe de conflit
        System.out.println("Graphe de conflit:");
        System.out.println(conflictGraph);

        // Colorer le graphe de conflit
        int numColors = conflictGraph.color();
        System.out.println("Nombre de couleurs utilisées: " + numColors);
        for (String var : conflictGraph.vertices) {
            System.out.println("Variable " + var + " est colorée avec la couleur " + conflictGraph.getColor(var));
        }
    }
}

