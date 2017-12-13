package com.translator.semantic.commands.div;

import com.translator.semantic.AnalyzerUtils;
import com.translator.semantic.ModeType;
import com.translator.semantic.commands.Command;
import com.translator.semantic.commands.OperandType;

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
        String opCode =  "1111011" + (isWide ? "1":"0"); //1 байт
        String paramByte = // второй байт
                AnalyzerUtils.getModValue(this.mod)
                +"110"
                + AnalyzerUtils.getRegisterCode(register.toUpperCase());
        return opCode+ " "+ paramByte;
    }

    @Override
    public int getSize() {
        return 2;
    }

    @Override
    public Boolean canApplyTo(OperandType op1, OperandType op2) {
        if (op2 != OperandType.no) {
            return false;
        }
        return op1 == OperandType.register8 || op1 == OperandType.register16;
    }
}
