package src;

import src.Graph.UnorientedGraph;
import src.Asm.*;
import java.util.*;

public class ConflictGraph extends UnorientedGraph<String> {
    private Map<Instruction, Set<String>> in;
    private Map<Instruction, Set<String>> out;

    /**
     * Constructeur
     * @param controlGraph
     * @param program
     */
    public ConflictGraph(ControlGraph controlGraph, Program program) {
        super();
        this.in = new HashMap<>();
        this.out = new HashMap<>();
        computeLiveness(controlGraph, program);
        buildConflictGraph(controlGraph, program);
    }

    /**
     * Retourne les variables vivantes
     * @param controlGraph
     * @param program
     */
    private void computeLiveness(ControlGraph controlGraph, Program program) {
        boolean changed;
        for (Instruction instruction : controlGraph.getAllVertices(program)) {
            in.putIfAbsent(instruction, new HashSet<>());
            out.putIfAbsent(instruction, new HashSet<>());
        }
        do {
            changed = false;
            for (Instruction instruction : controlGraph.getAllVertices(program)) {
                Set<String> oldIn = new HashSet<>(in.get(instruction));
                Set<String> oldOut = new HashSet<>(out.get(instruction));

                Set<String> newOut = new HashSet<>();
                for (Instruction succ : controlGraph.getOutNeighbors(instruction)) {
                    newOut.addAll(in.get(succ));
                }
                Set<String> newIn = new HashSet<>(newOut);
                newIn.removeAll(getDef(instruction));
                newIn.addAll(getUse(instruction));
                if (!newIn.equals(oldIn) || !newOut.equals(oldOut)) {
                    in.put(instruction, newIn);
                    out.put(instruction, newOut);
                    changed = true;
                }
            }
        } while (changed);
    }

    /**
     * Construit le graphe de conflit
     * @param controlGraph
     * @param program
     */
    private void buildConflictGraph(ControlGraph controlGraph, Program program) {
        for (Instruction instruction : controlGraph.getAllVertices(program)) {
            Set<String> def = getDef(instruction);
            Set<String> liveOut = this.out.get(instruction);
    
            for (String d : def) {
                this.addVertex(d);
                for (String o : liveOut) {
                    if (!d.equals(o)) {
                        this.addVertex(o);
                        this.addEdge(d, o);
                    }
                }
            }
    
            for (Instruction succ : controlGraph.getOutNeighbors(instruction)) {
                Set<String> futureLive = this.out.get(succ);
                for (String d : def) {
                    for (String f : futureLive) {
                        if (!d.equals(f)) {
                            this.addVertex(f);
                            this.addEdge(d, f);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Retourne les variables utilisées
     * @param instruction
     * @return
     */
    private Set<String> getUse(Instruction instruction) {
        Set<String> use = new HashSet<>();
        if (instruction instanceof IO) {
            use.add("R" + ((IO) instruction).getReg());
        } else if (instruction instanceof UAL) {
            UAL ualInstr = (UAL) instruction;
            use.add("R" + ualInstr.getSr1());
            use.add("R" + ualInstr.getSr2());
        } else if (instruction instanceof UALi) {
            use.add("R" + ((UALi) instruction).getSr());
        } else if (instruction instanceof Mem) {
            use.add("R" + ((Mem) instruction).getAddress());
        } else if (instruction instanceof CondJump) {
            CondJump condJump = (CondJump) instruction;
            use.add("R" + condJump.getSr1());
            use.add("R" + condJump.getSr2());
        }
        return use;
    }

    /**
     * Retourne les variables définies
     * @param instruction
     * @return
     */
    private Set<String> getDef(Instruction instruction) {
        Set<String> def = new HashSet<>();
        if (instruction instanceof IO) {
            def.add("R" + ((IO) instruction).getReg());
        } else if (instruction instanceof UAL) {
            def.add("R" + ((UAL) instruction).getDest());
        } else if (instruction instanceof UALi) {
            def.add("R" + ((UALi) instruction).getDest());
        } else if (instruction instanceof Mem) {
            def.add("R" + ((Mem) instruction).getDest());
        }
        return def;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Graphe de conflit:\n");
        for (String u : this.vertices) {
            sb.append(u).append(" -> ");
            for (String v : this.adjList.get(u)) {
                sb.append(v).append(", ");
            }
            if (!this.adjList.get(u).isEmpty()) {
                sb.setLength(sb.length() - 2);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        Program program = new Program();
        Instruction instr0 = new Mem(Mem.Op.LD, 0, 1) {};
        Instruction instr1 = new UAL(UAL.Op.XOR, 1000, 1000, 1000) {};
        Instruction instr2 = new UALi(UALi.Op.SUB, 1000, 1000, 1) {};
        Instruction instr3 = new IO(IO.Op.PRINT, 1001) {};
        Instruction instr4 = new CondJump(CondJump.Op.JEQU, 1000, 1001, "LABEL2") {};
        Instruction instr5 = new CondJump(CondJump.Op.JINF, 1000, 1001, "FUNC1") {};
        Instruction instr6 = new CondJump(CondJump.Op.JSUP, 1000, 1001, "LABEL2") {};
        Instruction instr7 = new JumpCall(JumpCall.Op.CALL, "FUNC1") {};
        Instruction instr8 = new JumpCall(JumpCall.Op.JMP, "END") {};
        Instruction instr9 = new Stop("STOP") {};
        Instruction instr10 = new UAL("LABEL1", UAL.Op.ADD, 1002, 1003, 1004) {};
        Instruction instr11 = new UAL("FUNC1", UAL.Op.MUL, 1005, 1006, 1007) {};
        Instruction instr12 = new JumpCall(JumpCall.Op.JMP, "END") {};
        Instruction instr13 = new UAL("LABEL2", UAL.Op.DIV, 1008, 1009, 1010) {};
        Instruction instr14 = new IO(IO.Op.PRINT, 1011) {};
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
        ConflictGraph conflictGraph = new ConflictGraph(controlGraph, program);
        System.out.println(conflictGraph);

        int numColors = conflictGraph.color();
        System.out.println("Nombre de couleurs utilisées: " + numColors);
        for (String var : conflictGraph.vertices) {
            System.out.println("Variable " + var + " est colorée avec la couleur " + conflictGraph.getColor(var));
        }
    }
}
