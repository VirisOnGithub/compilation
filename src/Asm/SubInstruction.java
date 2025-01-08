package src.Asm;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class SubInstruction extends Instruction {
    private List<String> definedVariables;
    private List<String> usedVariables;
    private List<String> liveVariables;

    public SubInstruction(String label, String name, List<String> definedVariables, List<String> usedVariables) {
        super(label, name);
        this.definedVariables = definedVariables;
        this.usedVariables = usedVariables;
        this.liveVariables = new ArrayList<>();
    }

    public List<String> getDefinedVariables() {
        return definedVariables;
    }

    public List<String> getUsedVariables() {
        return usedVariables;
    }

    public List<String> getLiveVariables() {
        return liveVariables;
    }

    public void setLiveVariables(List<String> liveVariables) {
        this.liveVariables = liveVariables;
    }

    @Override
    public String toAssembly(HashMap<String, String> registerAllocation) {
        StringBuilder assembly = new StringBuilder();
        assembly.append(getLabel()).append(": ").append(getName()).append(" ");
        for (String var : usedVariables) {
            assembly.append(registerAllocation.getOrDefault(var, var)).append(", ");
        }
        assembly.setLength(assembly.length() - 2);
        return assembly.toString();
    }
}