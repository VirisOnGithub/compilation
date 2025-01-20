package src;

import src.Graph.UnorientedGraph;
import src.Asm.*;
import java.util.*;

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

        Instruction instr0 = new IO(IO.Op.READ, 10000) {};
        program.addInstruction(instr0);
        
        // Instructions utilisant des registres uniques et augmentant progressivement le nombre
        for (int i = 0; i < 40; i++) {
            // Exemple d'opérations arithmétiques utilisant des registres R0 à R39
            Instruction instr = new UAL(UAL.Op.ADD, i, i, (i + 1) % 40) {};
            program.addInstruction(instr);
        }

        // Ajout d'opérations avec des registres existants
        Instruction instr40 = new UALi(UALi.Op.SUB, 39, 38, 10) {};
        Instruction instr41 = new CondJump(CondJump.Op.JEQU, 37, 36, "LABEL_EXTRA") {};
        Instruction instr42 = new JumpCall(JumpCall.Op.CALL, "FUNC_EXTRA") {};
        Instruction instr43 = new IO(IO.Op.PRINT, 35) {};
        Instruction instr44 = new Mem(Mem.Op.LD, 34, 33) {};
        Instruction instr45 = new Mem(Mem.Op.ST, 32, 31) {};
        Instruction instr46 = new Stop("STOP") {};

        // Ajout des instructions restantes au programme
        program.addInstruction(instr40);
        program.addInstruction(instr41);
        program.addInstruction(instr42);
        program.addInstruction(instr43);
        program.addInstruction(instr44);
        program.addInstruction(instr45);
        program.addInstruction(instr46);

        // Ajout d'un label et d'une instruction de retour pour valider les sauts
        Instruction instrLabel = new UAL("LABEL_EXTRA", UAL.Op.MUL, 10, 9, 8) {};
        Instruction instrReturn = new Ret("FUNC_EXTRA") {};
        program.addInstruction(instrLabel);
        program.addInstruction(instrReturn);

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
