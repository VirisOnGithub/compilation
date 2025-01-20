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
     * Calcul de la vivacité des registres
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
     * Construit le graphe de conflit en ajoutant des arêtes entre registres en conflit
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
        }
    }

    /**
     * Retourne les registres utilisés (read) par une instruction
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
            Mem memInstr = (Mem) instruction;
            use.add("R" + memInstr.getAddress());
        } else if (instruction instanceof CondJump) {
            CondJump condJump = (CondJump) instruction;
            use.add("R" + condJump.getSr1());
            use.add("R" + condJump.getSr2());
        }
        return use;
    }

    /**
     * Retourne les registres définis (write) par une instruction
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
            if (this.adjList.get(u).size() > 0) {
                sb.setLength(sb.length() - 2);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        Program program = new Program();

        Instruction instr0 = new IO(IO.Op.READ, 1);
        program.addInstruction(instr0);
        
        for (int i = 1; i < 40; i++) {
            Instruction instr = new UALi(UALi.Op.ADD, i, i - 1, i + 1);
            program.addInstruction(instr);
        }

        Instruction instr5 = new Mem(Mem.Op.LD, 3, 2);
        Instruction instr6 = new IO(IO.Op.PRINT, 4);
        Instruction instr7 = new Stop("STOP");

        program.addInstruction(instr5);
        program.addInstruction(instr6);
        program.addInstruction(instr7);

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
