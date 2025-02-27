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

        private void computeLiveness(ControlGraph controlGraph, Program program) {
        boolean changed;
        do {
            changed = false;
            for (Instruction instruction : controlGraph.getAllVertices(program)) {
            Set<String> oldIn = in.getOrDefault(instruction, new HashSet<>());
            Set<String> oldOut = out.getOrDefault(instruction, new HashSet<>());

            Set<String> newIn = new HashSet<>(oldOut);
            newIn.addAll(getDef(instruction)); // Garde les variables définies par l'instruction
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

    /**
     * Retourne les variables utilisées par une instruction
     * @param instruction
     * @return
     */
    private Set<String> getUse(Instruction instruction) {
        Set<String> use = new HashSet<>();
        if (instruction instanceof IO){
            IO ioInstr = (IO) instruction;
            use.add("R" + ioInstr.getReg());
            return use;
        } else if (instruction instanceof UAL){
            UAL ualInstr = (UAL) instruction;
            use.add("R" + ualInstr.getSr1());
            use.add("R" + ualInstr.getSr2());
            return use;
        } else if (instruction instanceof UALi) {
            UALi ualiInstr = (UALi) instruction;
            use.add("R" + ualiInstr.getSr());
            return use;
        } else if (instruction instanceof Mem) {
            Mem memInstr = (Mem) instruction;
            String op = memInstr.getName();
            if (op.equals("LD")) { 
                use.add("R" + memInstr.getAddress());
            } else if (op.equals("ST")) { 
                use.add("R" + memInstr.getDest());
                use.add("R" + memInstr.getAddress());
            }
            return use;
        } else if (instruction instanceof CondJump){
            CondJump condJump = (CondJump) instruction;
            use.add("R" + condJump.getSr1());
            use.add("R" + condJump.getSr2());
            return use;
        } else if ((instruction instanceof Ret) || (instruction instanceof Stop) || (instruction instanceof JumpCall)){
            return use;
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
        if (instruction instanceof IO){
            IO ioInstr = (IO) instruction;
            def.add("R" + ioInstr.getReg());
            return def;
        } else if (instruction instanceof UAL){
            UAL ualInstr = (UAL) instruction;
            def.add("R" + ualInstr.getDest());
            return def;
        } else if (instruction instanceof UALi) {
            UALi ualiInstr = (UALi) instruction;
            def.add("R" + ualiInstr.getDest());
            return def;
        } else if (instruction instanceof Mem) {
            Mem memInstr = (Mem) instruction;
            String op = memInstr.getName();
            if (op.equals("LD")) { 
                def.add("R" + memInstr.getDest());
            }
            return def;
        } else if (instruction instanceof CondJump){
            CondJump condJump = (CondJump) instruction;
            def.add("R" + condJump.getSr1());
            def.add("R" + condJump.getSr2());
            return def;
        } else
        if (instruction instanceof Ret) {
            return def;
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
}

