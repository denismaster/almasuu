package com.translator.semantic.commands.intpt;

import com.translator.semantic.AnalyzerUtils;
import com.translator.semantic.commands.Command;
import com.translator.semantic.commands.OperandType;

public class InterruptCommand implements Command {
    private int type;

    public InterruptCommand(int type){

        this.type = type;
    }
    @Override
    public String generateCode() {
        //-------OPCODE--------OPERAND---//
        return "11001101"+" "+ AnalyzerUtils.getLHValueFromInt(type);
    }

    @Override
    public int getSize() {
        return 2;
    }

    @Override
    public Boolean canApplyTo(OperandType op1, OperandType op2) {
        return op2==OperandType.no && op1==OperandType.immediate8;
    }
}
