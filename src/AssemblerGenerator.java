package src;

import src.Asm.Program;
import src.Asm.Instruction;

import java.util.ArrayList;
import java.util.List;

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
    private RegisterAllocator allocator;

    /**
     * Constructeur
     * @param program
     * @param allocator
     */
    public AssemblerGenerator(Program program, RegisterAllocator allocator) {
        this.program = program;
        this.allocator = allocator;
    }
    

       private void generateFunctionCall(Instruction instruction, StringBuilder result) {
        if (!(instruction instanceof JumpCall)) {
            throw new IllegalArgumentException("Invalid instruction type for function call: " + instruction.getName());
        }
        JumpCall jumpCall = (JumpCall) instruction;
        List<String> parameters = getFunctionParameters(jumpCall);
        allocator.saveGPR(parameters, result);
        result.append("CALL ").append(jumpCall.getAddress()).append("\n");
        allocator.restoreGPR(parameters, result);
    }



    private List<String> getFunctionParameters(JumpCall jumpCall) {
        // Récupérer les parametres ici jspfaire
        return new ArrayList<>();
    }


    /**
     * Génère le code assembleur à partir du programme et de l'allocation de registres
     * @return
     */
    public String generateAssembly() {
        StringBuilder result = new StringBuilder();
        for (Instruction instruction : program.getInstructions()) {
            String[] parts = instruction.getName().split(" ");
            if (!instruction.getLabel().isEmpty()) {
                result.append(instruction.getLabel()).append(": ");
            }
            if (parts.length < 1) {
                throw new IllegalArgumentException("Format d'instruction invalide : " + instruction.getName());
            }
            switch (parts[0]) {
                case "XOR":
                case "OR":
                case "AND":
                case "NOT":
                case "NOR":
                case "NXOR":
                case "NAND":
                case "SUB":
                case "ADD":
                case "MUL":
                case "DIV":
                    if (!(instruction instanceof UAL) && !(instruction instanceof UALi)) {
                        throw new IllegalArgumentException("Type d'instruction invalide pour UAL/UALi: " + instruction.getName());
                    } else if ((instruction instanceof UAL) && !(instruction instanceof UALi)) {
                        UAL ual = (UAL) instruction;
                    String destReg = allocator.getRegister("R" + ual.getDest());
                    String reg1 = allocator.getRegister("R" + ual.getSr1());
                    String reg2 = allocator.getRegister("R" + ual.getSr2());
                    if (reg1.startsWith("DYNAMIC")) {
                        result.append("LD R30, [SP + ").append(reg1.substring(8, reg1.length() - 1)).append("]\n");
                    } else {
                        result.append("LD R30, ").append(reg1).append("\n");
                    }
                    if (reg2.startsWith("DYNAMIC")) {
                        result.append("LD R31, [SP + ").append(reg2.substring(8, reg2.length() - 1)).append("]\n");
                    } else {
                        result.append("LD R31, ").append(reg2).append("\n");
                    }
                    result.append(parts[0]).append(" R30, R30, R31\n");
                    result.append("ST ").append("R30, ").append(destReg).append("\n");
                    } else if (!(instruction instanceof UAL) && (instruction instanceof UALi)) {
                        UALi uali = (UALi) instruction;
                        String destRegi = allocator.getRegister("R" + uali.getDest());
                        String reg1i = allocator.getRegister("R" + uali.getSr());
                        if (reg1i.startsWith("DYNAMIC")) {
                            result.append("LD R30, [SP + ").append(reg1i.substring(8, reg1i.length() - 1)).append("]\n");
                        } else {
                            result.append("LD R30, ").append(reg1i).append("\n");
                        }
                        result.append(parts[0]).append(" R30, R30, ").append(uali.getImm()).append("\n");
                        result.append("ST ").append("R30, ").append(destRegi).append("\n");
                    }
                    break;

                case "SUBi":
                case "ADDi":
                case "MULi":
                case "DIVi":
                if (!(instruction instanceof UALi)) {
                    throw new IllegalArgumentException("Type d'instruction invalide pour UALi: " + instruction.getName());
                }
                UALi uali = (UALi) instruction;
                String destRegi = allocator.getRegister("R" + uali.getDest());
                String reg1i = allocator.getRegister("R" + uali.getSr());
                if (reg1i.startsWith("DYNAMIC")) {
                    result.append("LD R30, [SP + ").append(reg1i.substring(8, reg1i.length() - 1)).append("]\n");
                } else {
                    result.append("LD R30, ").append(reg1i).append("\n");
                }
                result.append(parts[0]).append(" R30, R30, ").append(uali.getImm()).append("\n");
                result.append("ST ").append("R30, ").append(destRegi).append("\n");
                break;

                case "JEQU":
                case "JINF":
                case "JSUP":
                case "JNEQ":
                case "JIEQ":
                case "JSEQ":
                    if (!(instruction instanceof CondJump)) {
                        throw new IllegalArgumentException("Invalid instruction type for conditional jump: " + instruction.getName());
                    }
                    CondJump condJump = (CondJump) instruction;
                    String reg1 = allocator.getRegister("R" + condJump.getSr1());
                    String reg2 = allocator.getRegister("R" + condJump.getSr2());

                    String label = condJump.getAddress();
                    if (reg1.startsWith("DYNAMIC")) {
                        result.append("LD R30, [SP + ").append(reg1.substring(8, reg1.length() - 1)).append("]\n");
                    } else {
                        result.append("LD R30, ").append(reg1).append("\n");
                    }

                    if (reg2.startsWith("DYNAMIC")) {
                        result.append("LD R31, [SP + ").append(reg2.substring(8, reg2.length() - 1)).append("]\n");
                    } else {
                        result.append("LD R31, ").append(reg2).append("\n");
                    }
                    result.append(parts[0]).append(" R30, R31, ").append(label).append("\n");
                    break;
                
                case "LD":
                    if (!(instruction instanceof Mem)) {
                        throw new IllegalArgumentException("Invalid instruction type for load: " + instruction.getName());
                    }
                    Mem mem = (Mem) instruction;
                    result.append("LD ").append(allocator.getRegister("R" + mem.getDest())).append(", ").append(allocator.getRegister("R" + mem.getAddress())).append("\n");
                    break;

                case "ST":
                    if (!(instruction instanceof Mem)) {
                        throw new IllegalArgumentException("Invalid instruction type for store: " + instruction.getName());
                    }
                    mem = (Mem) instruction;
                    result.append("ST ").append(allocator.getRegister("R" + mem.getAddress())).append(", ").append(allocator.getRegister("R" + mem.getDest())).append("\n");
                    break;

                case "CALL":
                    generateFunctionCall(instruction, result);
                break;
                    
                case "JMP":
                    if (!(instruction instanceof JumpCall)) {
                        throw new IllegalArgumentException("Invalid instruction type for jump/call: " + instruction.getName());
                    }
                    JumpCall jumpCall = (JumpCall) instruction;
                    String jumpLabel = jumpCall.getAddress();
                    result.append(parts[0]).append(" ").append(jumpLabel).append("\n");
                    break;

                case "STOP":
                    result.append("STOP\n");
                    break;

                case "RET":
                    result.append("RET\n");
                    break;

                case "PRINT":
                    if (!(instruction instanceof IO)) {
                        throw new IllegalArgumentException("Invalid instruction type for print: " + instruction.getName());
                    }
                    IO io = (IO) instruction;
                    result.append("PRINT ").append(allocator.getRegister("R" + io.getReg())).append("\n");
                    break;


                default:
                    throw new IllegalArgumentException("Unknown instruction: " + parts[0]);
            }
        }

        return result.toString();
    }

    public static void main(String[] args) {
        Program program = new Program();

        // Instructions utilisant des registres uniques et augmentant progressivement le nombre
        for (int i = 0; i < 40; i++) {
            // Exemple d'opérations arithmétiques utilisant des registres R0 à R39
            Instruction instr = new UAL(UAL.Op.ADD, i, i, (i + 1) % 40) {};
            program.addInstruction(instr);
        }

        // Ajout d'opérations avec des registres existants
        Instruction instr40 = new UALi(UALi.Op.SUB, 39, 38, 10) {};
        Instruction instr41 = new CondJump(CondJump.Op.JEQU, 37, 36, "LABEL_EXTRA") {};
        Instruction instr42 = new JumpCall(JumpCall.Op.CALL, "FUNC_EXTRA") {};
        Instruction instr43 = new IO(IO.Op.PRINT, 35) {};
        Instruction instr44 = new Mem(Mem.Op.LD, 34, 33) {};
        Instruction instr45 = new Mem(Mem.Op.ST, 32, 31) {};
        Instruction instr46 = new Stop("STOP") {};

        // Ajout des instructions restantes au programme
        program.addInstruction(instr40);
        program.addInstruction(instr41);
        program.addInstruction(instr42);
        program.addInstruction(instr43);
        program.addInstruction(instr44);
        program.addInstruction(instr45);
        program.addInstruction(instr46);

        // Ajout d'un label et d'une instruction de retour pour valider les sauts
        Instruction instrLabel = new UAL("LABEL_EXTRA", UAL.Op.MUL, 10, 9, 8) {};
        Instruction instrReturn = new Ret("FUNC_EXTRA") {};
        program.addInstruction(instrLabel);
        program.addInstruction(instrReturn);

        ControlGraph controlGraph = new ControlGraph(program);
        ConflictGraph conflictGraph = new ConflictGraph(controlGraph, program);

        RegisterAllocator allocator = new RegisterAllocator(conflictGraph);
        AssemblerGenerator generator = new AssemblerGenerator(program, allocator);

        String assemblyCode = generator.generateAssembly();
        System.out.println(assemblyCode);
    }
}