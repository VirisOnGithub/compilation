package src.Asm;
public class UALi extends Instruction{
    
    public static enum Op { ADD, SUB, MUL, DIV, MOD, XOR, AND, OR, SL, SR };
    private int dest;
    private int sr;
    private int imm;

    /**
     * Getter du registre de destination
     * @return numéro du registre de destination
     */
    public int getDest() {
        return dest;
    }

    /**
     * Setter du registre de destination
     * @param dest numéro du registre de destination
     */
    public void setDest(int dest) {
        this.dest = dest;
    }

    /**
     * Getter du premier registre source
     * @return numéro du premier registre source
     */
    public int getSr() {
        return sr;
    }

    /**
     * Setter du premier registre source
     * @param sr numéro du premier registre source
     */
    public void setSr(int sr) {
        this.sr = sr;
    }

    /**
     * Getter de la constante
     * @return constante immédiate
     */
    public int getImm() {
        return imm;
    }

    /**
     * Setter de la constante
     * @param imm constante immédiate
     */
    public void setImm(int imm) {
        this.imm = imm;
    }

    /**
     * Constructeur 
     * @param label label de l'instruction
     * @param op type d'opération
     * @param dest numéro du registre de destination
     * @param sr numéro du registre source
     * @param imm constante immédiate
     */
    public UALi(String label, Op op, int dest, int sr, int imm) {
        super(label,op.toString());
        this.dest = dest;
        this.sr = sr;
        this.imm = imm;
    }
    /**
     * Constructeur 
     * @param op type d'opération
     * @param dest numéro du registre de destination
     * @param sr numéro du registre source
     * @param imm constante immédiate
     */
    public UALi(Op op, int dest, int sr, int imm) {
        super("",op.toString());
        this.dest = dest;
        this.sr = sr;
        this.imm = imm;
    }

    /** 
     * Conversion en String
     * @return String texte de l'instruction
     */
    public String toString() {
        return this.label+ (this.label==""?"":": ") + this.name + "i R" + dest + " R" + sr + " " + imm + "\n";
    }
    
}