package src.Asm;
public class CondJump extends Instruction {
    public static enum Op { JINF, JEQU, JSUP, JNEQ, JIEQ, JSEQ };
    private int sr1;
    private int sr2;
    private String address;

    
    /** 
     * Getter du premier registre source
     * @return int numéro du premier registre source
     */
    public int getSr1() {
        return sr1;
    }

    
    /** 
     * Setter du premier registre source
     * @param sr numéro du premier registre source
     */
    public void setSr1(int sr) {
        this.sr1 = sr;
    }

    
    /** 
     * Getter du second register source 
     * @return int numéro du second registre source
     */
    public int getSr2() {
        return sr2;
    }

    
    /** 
     * Setter du second registre source
     * @param sr numéro du second registre source
     */
    public void setSr2(int sr) {
        this.sr2 = sr;
    }

    
    /** 
     * Getter de l'adresse de saut
     * @return String adresse de saut
     */
    public String getAddress() {
        return address;
    }

    
    /** 
     * Setter de l'adresse de saut
     * @param address adresse de saut
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /** 
     * Constructeur
     * @param label label de l'instruction
     * @param op type d'opération
     * @param sr1 numéro du premier registre source
     * @param sr2 numéro du second registre source
     * @param address adresse de saut 
     */
    public CondJump(String label, Op op, int sr1, int sr2, String address) {
        super(label,op.toString());
        this.sr1 = sr1;
        this.sr2 = sr2;
        this.address = address;
    }
    
    /** 
     * Constructeur sans label
     * @param op type d'opération
     * @param sr1 numéro du premier registre source
     * @param sr2 numéro du second registre source
     * @param address adresse de saut 
     */
    public CondJump(Op op, int sr1, int sr2, String address) {
        super("",op.toString());
        this.sr1 = sr1;
        this.sr2 = sr2;
        this.address = address;
    }

    
    /** 
     * Conversion en String
     * @return String texte de l'instruction
     */
    public String toString() {
        return this.label+ (this.label==""?"":": ") + this.name + " R" + sr1 + " R" + sr2 + " " + address + "\n";
    }

    
}
