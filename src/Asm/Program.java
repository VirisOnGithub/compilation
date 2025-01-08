package src.Asm;
import java.util.ArrayList;
import java.util.List;
import src.Asm.Instruction;
/**
 * Ceci est une classe permettant de représenter un programme.
 */
public class Program {
    private ArrayList<Instruction> instructions;
  
    /** 
     * Getter de la liste d'instructions
     * @return ArrayList<Instruction> contenant les instructions du programme
     */
    public ArrayList<Instruction> getInstructions() {
        return instructions;
    }
  
    
    /** 
     * Ajoute instruction à la fin de this.
     * @param instruction Instruction à ajouter
     */
    public void addInstruction(Instruction instruction) {
        this.instructions.add(instruction);
    }

    
    /** 
     * Ajoute toutes les instructions de program à la fin de this.
     * @param program Programme contenant les instructions à ajouter
     */
    public void addInstructions(Program program) {
        this.instructions.addAll(program.getInstructions());
    }

    
    /** 
     * Convertit le programme en String pour l'affichage
     * @return String contenant les instructions du programme
     */
    public String toString() {
        String result = "";
        for (Instruction instr : instructions) {
            result += instr.toString();
        }
        return result;
    }

    /**
     * Constructeur
     */
    public Program() {
        this.instructions = new ArrayList<Instruction>();
    }   

    /**
     * Surcharge du constructeur
     * @param instructions
     */

    public Program(ArrayList<Instruction> instructions) {
        this.instructions = instructions;
    }

}
