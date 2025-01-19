package src;

import src.Graph.UnorientedGraph;
import src.Asm.Program;
import src.Asm.Ret;
import src.Asm.Stop;
import src.Asm.UAL;
import src.Asm.UALi;
import src.Asm.CondJump;
import src.Asm.IO;
import src.Asm.Instruction;
import src.Asm.JumpCall;
import src.Asm.Mem;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

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
        do {
            changed = false;
            for (Instruction instruction : controlGraph.getAllVertices(program)) {
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

    /**
     * Construit le graphe de conflit à partir du graphe de contrôle et du programme
     * @param controlGraph
     * @param program
     */
    private void buildConflictGraph(ControlGraph controlGraph, Program program) {
        for (Instruction instruction : controlGraph.getAllVertices(program)) {
            Set<String> def = getDef(instruction); // Variables définies
            Set<String> out = this.out.getOrDefault(instruction, new HashSet<>());
            for (String d : def) {
                for (String o : out) {
                    if (!d.equals(o)) {
                        this.addEdge(d, o);
                    }
                }
            }
        }
    }

    /**
     * Retourne les variables utilisées par une instruction
     * @param instruction
     * @return
     */
    private Set<String> getUse(Instruction instruction) {
        Set<String> use = new HashSet<>();
        if (instruction instanceof IO) {
            IO ioInstr = (IO) instruction;
            use.add("R" + ioInstr.getReg());
        } else if (instruction instanceof UAL) {
            UAL ualInstr = (UAL) instruction;
            use.add("R" + ualInstr.getSr1());
            use.add("R" + ualInstr.getSr2());
        } else if (instruction instanceof UALi) {
            UALi ualiInstr = (UALi) instruction;
            use.add("R" + ualiInstr.getSr());
        } else if (instruction instanceof Mem) {
            Mem memInstr = (Mem) instruction;
            use.add("R" + memInstr.getDest());
            use.add("R" + memInstr.getAddress());
        } else if (instruction instanceof CondJump) {
            CondJump condJump = (CondJump) instruction;
            use.add("R" + condJump.getSr1());
            use.add("R" + condJump.getSr2());
        }
        return use;
    }

    /**
     * Retourne les variables définies par une instruction
     * @param instruction
     * @return
     */
    private Set<String> getDef(Instruction instruction) {
        Set<String> def = new HashSet<>();
        if (instruction instanceof IO) {
            IO ioInstr = (IO) instruction;
            def.add("R" + ioInstr.getReg());
        } else if (instruction instanceof UAL) {
            UAL ualInstr = (UAL) instruction;
            def.add("R" + ualInstr.getDest());
        } else if (instruction instanceof UALi) {
            UALi ualiInstr = (UALi) instruction;
            def.add("R" + ualiInstr.getDest());
        } else if (instruction instanceof Mem) {
            Mem memInstr = (Mem) instruction;
            def.add("R" + memInstr.getDest());   
            def.add("R" + memInstr.getAddress());         
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
                sb.setLength(sb.length() - 2);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        Program program = new Program();

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

