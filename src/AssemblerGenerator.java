package src;

import src.Asm.Program;
import src.Asm.Instruction;
import src.Asm.CondJump;
import src.Asm.IO;
import src.Asm.JumpCall;
import src.Asm.Mem;
import src.Asm.Ret;
import src.Asm.Stop;
import src.Asm.UAL;
import src.Asm.UALi;

public class AssemblerGenerator {
    private Program program;
    private ConflictGraph conflictGraph;
    private int dynamicArrayIndex;

    /**
     * Constructeur
     * @param program
     */
    public AssemblerGenerator(Program program, ConflictGraph conflictGraph) {
        this.program = program;
        this.conflictGraph = conflictGraph;
        this.dynamicArrayIndex = 57000;
    }

    /**
     * Retourne le registre à utiliser
     * @param register
     * @return
     */
    private int getActualRegister(int register) {
        return conflictGraph.getColor("R" + register);
    }

    /**
     * Retourne le registre à utiliser
     * @param register
     * @param result
     * @param operationRegister
     * @return
     */
    private String getRegister(int register, StringBuilder result, int operationRegister) {
        register = getActualRegister(register);
        if (register < 30) {
            return "R" + register;
        }
        else {
            int memLocation = (register - 30) + dynamicArrayIndex;
            result.append("XOR R" + operationRegister + " R" + operationRegister + " R" + operationRegister + "\n");
            result.append("ADDi R" + operationRegister + " R" + operationRegister + " " + memLocation + "\n");
            result.append("LD R" + operationRegister + " R" + operationRegister + "\n");
            return "R" + operationRegister;
        }
    }

    /**
     * Retourne le numéro du registre
     * @param register
     * @return
     */
    private String getRegisterNumber(int register) {
        register = getActualRegister(register);
        if (register < 30) {
            return "R" + register;
        }
        else {
            return "R30";
        }
    }

    private void returnRegister(int register, StringBuilder result) {
        register = getActualRegister(register);
        if (register < 30) return;
        int memLocation = (register - 30) + dynamicArrayIndex;
        result.append("XOR R31 R31 R31\n");
        result.append("ADDi R31 R31 " + memLocation + "\n");
        result.append("ST R30 R31\n");
    }

    /**
     * Génère le code assembleur à partir du programme et de l'allocation de registres
     * @return
     */
    public String generateAssembly() {
        if (program.getInstructions().isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        String currentlyBuildInstruction;

        for (Instruction instruction : program.getInstructions()) {
            String[] parts = instruction.getName().split(" ");
            currentlyBuildInstruction = "";
            if (!instruction.getLabel().isEmpty()) {
                currentlyBuildInstruction += (instruction.getLabel()) + (": ");
            }
            if (parts.length < 1) {
                throw new IllegalArgumentException("Format d'instruction invalide : " + instruction.getName());
            }
            String op = parts[0];

            switch (op) {
                case "XOR":
                case "OR":
                case "AND":
                case "SUB":
                case "ADD":
                case "MUL":
                case "DIV":
                case "MOD":
                case "SL":
                case "SR": {
                    if (!(instruction instanceof UAL) && !(instruction instanceof UALi)) {
                        throw new IllegalArgumentException("Type d'instruction invalide pour UAL/UALi: " + instruction.getName());
                    }
                    if (instruction instanceof UAL) {
                        UAL ual = (UAL) instruction;
                        int destReg = ual.getDest();
                        int reg1 = ual.getSr1();
                        int reg2 = ual.getSr2();
                        String newDestReg = this.getRegisterNumber(destReg);
                        String newReg1 = this.getRegister(reg1, result, 30);
                        String newReg2 = this.getRegister(reg2, result, 31);
                        currentlyBuildInstruction += (op + " " + newDestReg + " " + newReg1 + " " + newReg2 + "\n");
                        result.append(currentlyBuildInstruction);
                        this.returnRegister(destReg, result);
                    } else {
                        UALi uali = (UALi) instruction;
                        int destReg = uali.getDest();
                        int reg1 = uali.getSr();
                        int imm = uali.getImm();
                        String newDestReg = this.getRegisterNumber(destReg);
                        String newReg1 = this.getRegister(reg1, result, 30);
                        currentlyBuildInstruction += (op + " " + newDestReg + " " + newReg1 + " " + imm + "\n");
                        result.append(currentlyBuildInstruction);
                        this.returnRegister(destReg, result);
                    }
                    break;
                }

                case "JEQU":
                case "JINF":
                case "JSUP":
                case "JNEQ":
                case "JIEQ":
                case "JSEQ": {
                    if (!(instruction instanceof CondJump)) {
                        throw new IllegalArgumentException("Invalid instruction type for conditional jump: " + instruction.getName());
                    }
                    CondJump condJump = (CondJump) instruction;
                    String address = condJump.getAddress();
                    int reg1 = condJump.getSr1();
                    int reg2 = condJump.getSr2();
                    String newReg1 = this.getRegister(reg1, result, 30);
                    String newReg2 = this.getRegister(reg2, result, 31);
                    currentlyBuildInstruction += (op + " " + newReg1 + " " + newReg2 + " " + address + "\n");
                    result.append(currentlyBuildInstruction);
                    break;
                }

                case "LD": {
                    if (!(instruction instanceof Mem)) {
                        throw new IllegalArgumentException("Invalid instruction type for load: " + instruction.getName());
                    }
                    Mem mem = (Mem) instruction;
                    int reg1 = mem.getDest();
                    int reg2 = mem.getAddress();
                    String newReg1 = this.getRegister(reg1, result, 30);
                    String newReg2 = this.getRegister(reg2, result, 31);
                    currentlyBuildInstruction += (op + " " + newReg1 + " " + newReg2 + "\n");
                    result.append(currentlyBuildInstruction);
                    this.returnRegister(reg1, result);
                    break;
                }

                case "ST": {
                    if (!(instruction instanceof Mem)) {
                        throw new IllegalArgumentException("Invalid instruction type for load: " + instruction.getName());
                    }
                    Mem mem = (Mem) instruction;
                    int reg1 = mem.getDest();
                    int reg2 = mem.getAddress();
                    String newReg1 = this.getRegister(reg1, result, 30);
                    String newReg2 = this.getRegister(reg2, result, 31);
                    currentlyBuildInstruction += (op + " " + newReg1 + " " + newReg2 + "\n");
                    result.append(currentlyBuildInstruction);
                    break;
                }

                case "CALL":
                case "JMP": {
                    if (!(instruction instanceof JumpCall)) {
                        throw new IllegalArgumentException("Invalid instruction type for jump/call: " + instruction.getName());
                    }
                    JumpCall jumpCall = (JumpCall) instruction;
                    String jumpLabel = jumpCall.getAddress();
                    currentlyBuildInstruction += (op + " " + jumpLabel + "\n");
                    result.append(currentlyBuildInstruction);
                    break;
                }

                case "STOP":
                case "RET": {
                    if (!(instruction instanceof Stop) && !(instruction instanceof Ret)) {
                        throw new IllegalArgumentException("Invalid instruction type for stop: " + instruction.getName());
                    }
                    currentlyBuildInstruction += (op + "\n");
                    result.append(currentlyBuildInstruction);
                    break;
                }

                case "IN":
                case "READ": {
                    if (!(instruction instanceof IO)) {
                        throw new IllegalArgumentException("Invalid instruction type for print: " + instruction.getName());
                    }
                    IO io = (IO) instruction;
                    int reg = io.getReg();
                    String newReg1 = this.getRegister(reg, result, 30);
                    currentlyBuildInstruction += (op + " " + newReg1 + "\n");
                    result.append(currentlyBuildInstruction);
                    this.returnRegister(reg, result);
                    break;
                }

                case "OUT":
                case "PRINT": {
                    if (!(instruction instanceof IO)) {
                        throw new IllegalArgumentException("Invalid instruction type for print: " + instruction.getName());
                    }
                    IO io = (IO) instruction;
                    int reg = io.getReg();
                    String newReg1 = this.getRegister(reg, result, 30);
                    currentlyBuildInstruction += (op + " " + newReg1 + "\n");
                    result.append(currentlyBuildInstruction);
                    break;
                }

                default:
                    throw new IllegalArgumentException("Unknown instruction: " + parts[0]);
            }
        }

        return result.toString();
    }
}
