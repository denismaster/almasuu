package com.translator.semantic.commands.test;

import com.translator.semantic.AnalyzerUtils;
import com.translator.semantic.ModeType;
import com.translator.semantic.commands.Command;

public class RegMemRegisterTestCommand implements Command {
    private String register1;
    private String register2;
    private boolean isWide;
    private ModeType mod;

    public RegMemRegisterTestCommand(String register1, String register2, boolean isWide, ModeType mod) {

        this.register1 = register1;
        this.register2 = register2;
        this.isWide = isWide;
        this.mod = mod;
    }

    public String generateCode(){
        String opCode = "1000010"+(this.isWide ? "1":"0");
        String rmByte = AnalyzerUtils.getModValue(this.mod)
                + AnalyzerUtils.getRegisterCode(register1.toUpperCase())
                + AnalyzerUtils.getRegisterCode(register2.toUpperCase());
        return opCode+" "+rmByte;
    }

    @Override
    public int getSize() {
        return 2;
    }
}
