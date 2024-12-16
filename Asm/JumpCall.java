package Asm;
public class JumpCall extends Instruction{
    public static enum Op { JMP, CALL };
    private String address;

    /**
     * Getter de l'adresse de saut
     * @return adresse de saut
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
     * @param address adresse de saut
     */
    public JumpCall(String label, Op op, String address) {
        super(label,op.toString());
        this.address = address;
    
    }

    /**
     * Constructeur sans label
     * @param op type d'opération
     * @param address adresse de saut
     */
    public JumpCall(Op op, String address) {
        super("",op.toString());
        this.address = address;
    
    }

    /** 
     * Conversion en String
     * @return String texte de l'instruction
     */
    public String toString() {
        return this.label+ (this.label==""?"":": ") + this.name + " " + address + "\n";
    } 
}
