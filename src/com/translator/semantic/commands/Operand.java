package com.translator.semantic.commands;

import com.translator.lexer.Token;
import com.translator.semantic.AnalyzerUtils;

public class Operand {
    public Token token;
    public OperandType opType;

    public static Operand empty() {
        Operand op = new Operand();
        op.token = null;
        op.opType = OperandType.no;
        return op;
    }

    public static Operand fromToken(Token token) {
        Operand op = new Operand();
        if (token == null) return empty();
        op.token = token;
        switch (token.getTokenType()) {
            case Number:
                int num = AnalyzerUtils.readDecHex(token.getValue()).orElse(0);
                op.opType = num > 255 ? OperandType.immediate16 : OperandType.immediate8;
                break;
            case Register:
                Boolean isWideReg = AnalyzerUtils.isWideRegister(token.getValue());
                op.opType = isWideReg ? OperandType.register16 : OperandType.register8;
                break;
            default:
                return empty();
        }
        return op;
    }
}
