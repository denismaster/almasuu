package com.translator.semantic.commands.div;

import com.translator.semantic.AnalyzerUtils;
import com.translator.semantic.ModeType;
import com.translator.semantic.commands.Command;

public class DivCommand implements Command {
    private boolean isWide;
    private String register;
    private ModeType mod;

    public DivCommand(boolean isWide, String register, ModeType mod){
        this.isWide = isWide;
        this.register = register;
        this.mod = mod;
    }
    @Override
    public String generateCode() {
        String opCode =  "1111011" + (isWide ? "1":"0");
        String paramByte =
                AnalyzerUtils.getModValue(this.mod)
                +"110"
                + AnalyzerUtils.getRegisterCode(register.toUpperCase());
        return opCode+ " "+ paramByte;
    }
}
