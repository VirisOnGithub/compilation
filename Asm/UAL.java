package Asm;
public class UAL extends Instruction{
    
    public static enum Op { ADD, SUB, MUL, DIV, MOD, XOR, AND, OR, SL, SR };
    private int dest;
    private int sr1;
    private int sr2;

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
    public int getSr1() {
        return sr1;
    }

    /**
     * Setter du premier registre source
     * @param sr1 numéro du premier registre source
     */
    public void setSr1(int sr1) {
        this.sr1 = sr1;
    }
    
    /**
     * Getter du second registre source
     * @return numéro du second registre source
     */
    public int getSr2() {
        return sr2;
    }

    /**
     * Setter du second registre source
     * @param sr2 numéro du second registre source
     */
    public void setSr2(int sr2) {
        this.sr2 = sr2;
    }

    /**
     * Constructeur
     * @param label label de l'instruction
     * @param op type d'opération
     * @param dest numéro du registre de destination
     * @param sr1 numéro du premier registre source
     * @param sr2 numéro du second registre source
     */
    public UAL(String label, Op op, int dest, int sr1, int sr2) {
        super(label,op.toString());
        this.dest = dest;
        this.sr1 = sr1;
        this.sr2 = sr2;
    }

    /**
     * Constructeur sans label
     * @param op type d'opération
     * @param dest numéro du registre de destination
     * @param sr1 numéro du premier registre source
     * @param sr2 numéro du second registre source
     */
    public UAL(Op op, int dest, int sr1, int sr2) {
        super("",op.toString());
        this.dest = dest;
        this.sr1 = sr1;
        this.sr2 = sr2;
    }

    /** 
     * Conversion en String
     * @return String texte de l'instruction
     */
    public String toString() {
        return this.label+ (this.label==""?"":": ") + this.name + " R" + dest + " R" + sr1 + " R" + sr2 + "\n";
    }
    
}

 