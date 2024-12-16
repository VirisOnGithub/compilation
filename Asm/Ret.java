package Asm;
public class Ret extends Instruction{

    /**
     * Constructeur
     * @param label label de l'instruction
     */
    public Ret(String label) {
        super(label,"RET");
    }
    /**
     * Constructeur sans label
     */
    public Ret() {
        super("","RET");
    }
    /** 
     * Conversion en String
     * @return String texte de l'instruction
     */
    public String toString() {
        return this.label+ (this.label==""?"":": ") + this.name + "\n";
    } 
}
