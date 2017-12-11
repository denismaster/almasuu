package com.translator.semantic.commands;

public interface Command extends CodeGeneratable{
    Boolean canApplyTo(OperandType op1, OperandType op2);
}
