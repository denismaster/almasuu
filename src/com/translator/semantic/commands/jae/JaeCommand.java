package com.translator.semantic.commands.jae;

import com.translator.semantic.AnalyzerUtils;
import com.translator.semantic.commands.Command;
import com.translator.semantic.commands.OperandType;

public class JaeCommand implements Command {
    private final int offset;

    public JaeCommand(int offset){

        this.offset = offset;
    }
    @Override
    public String generateCode() {
        return "01110011 " + AnalyzerUtils.getLHValueFromInt(offset);
    }

    @Override
    public int getSize() {
        return 3;
    }

    @Override
    public Boolean canApplyTo(OperandType op1, OperandType op2) {
        return op2==OperandType.no && op1==OperandType.displacement8;
    }
}
