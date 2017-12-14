package com.translator.semantic.commands.mov;

import com.translator.semantic.Analyzer;
import com.translator.semantic.AnalyzerUtils;
import com.translator.semantic.ModeType;
import com.translator.semantic.commands.Command;
import com.translator.semantic.commands.OperandType;

public class MemoryToRegisterMovCommand implements Command {
    private final int memoryAddress;
    private String register;
    private boolean isWide;
    private ModeType mod;

    public MemoryToRegisterMovCommand(int memoryAddress, String register, boolean isWide, ModeType mod) {
        this.memoryAddress = memoryAddress;

        this.register = register;
        this.isWide = isWide;
        this.mod = mod;
    }

    public String generateCode(){
        String opCode = "1000101"+(this.isWide ? "1":"0");
        String rmByte = AnalyzerUtils.getModValue(this.mod)
                + AnalyzerUtils.getRegisterCode(register.toUpperCase())
                + "101";
        return opCode+" "+rmByte + " "+ AnalyzerUtils.getLHValueFromInt(memoryAddress);
    }

    @Override
    public int getSize() {
        return 2;
    }

    @Override
    public Boolean canApplyTo(OperandType op1, OperandType op2) {
        if(op1==OperandType.register16 && op2==OperandType.register16) return true;
        if(op1==OperandType.register8 && op2==OperandType.register8) return true;
        return false;
    }
}