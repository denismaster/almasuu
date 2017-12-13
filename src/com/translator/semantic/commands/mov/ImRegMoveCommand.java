package com.translator.semantic.commands.mov;

import com.translator.semantic.AnalyzerUtils;
import com.translator.semantic.commands.Command;
import com.translator.semantic.commands.OperandType;

public class ImRegMoveCommand implements Command {
    public String register;
    public int value;
    public boolean isWide;

    public ImRegMoveCommand(String register, int value, boolean isWide) {
        this.register = register;
        this.value = value;
        this.isWide = isWide;
    }

    public String generateCode(){
        String result =  "1011"+ (this.isWide ? "1":"0") + AnalyzerUtils.getRegisterCode(register.toUpperCase());
        result = result + " " + AnalyzerUtils.getLHValueFromInt(this.value);
        return result;
    }

    @Override
    public int getSize() {
        return isWide ? 3 : 2;
    }

    @Override
    public Boolean canApplyTo(OperandType op1, OperandType op2) {
        if(op1==OperandType.register16 && op2==OperandType.immediate16) return true;
        if(op1==OperandType.register8 && op2==OperandType.immediate8) return true;
        return false;
    }
}
