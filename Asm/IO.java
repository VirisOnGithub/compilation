package Asm;
public class IO extends Instruction {
    public static enum Op { IN, OUT, READ, PRINT };
    private int reg;

    /**
     * Getter du registre
     * @return int numéro de registre
     */
    public int getReg() {
        return reg;
    }

    /**
     * Setter du registre
     * @param reg numéro de registre
     */
    public void setReg(int reg) {
        this.reg = reg;
    }

    /**
     * Constructeur
     * @param label label de l'instruction
     * @param op type d'opération
     * @param reg numéro de registre
     */
    public IO(String label, Op op, int reg) {
        super(label,op.toString());
        this.reg = reg;
    }

    /** 
     * Constructeur sans label
     * @param op type d'opération
     * @param reg numéro de registre
     */
    public IO(Op op, int reg) {
        super("",op.toString());
        this.reg = reg;
    }

    /** 
     * Conversion en String
     * @return String texte de l'instruction
     */
    public String toString() {
        return this.label+ (this.label==""?"":": ") + this.name + " R" + reg + "\n";
    } 
}
