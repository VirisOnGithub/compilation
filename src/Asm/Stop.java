package src.Asm;
public class Stop extends Instruction{

    /**
     * Constructeur
     * @param label label de l'instruction
     */
    public Stop(String label) {
        super(label,"STOP");
    }

    /**
     * Constructeur sans label
     */
    public Stop() {
        super("","STOP");
    
    }

    /** 
     * Conversion en String
     * @return String texte de l'instruction
     */
    public String toString() {
        return this.label+ (this.label==""?"":": ") + this.name + "\n";
    } 
}
