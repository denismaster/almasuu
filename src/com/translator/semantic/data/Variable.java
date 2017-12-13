package com.translator.semantic.data;

import com.translator.semantic.commands.OperandType;

public class Variable {
    public String name;
    public int address;
    public int value;
    public OperandType type;

    public Variable(String name, int address, int value) {
        this.name = name;
        this.address = address;
        this.value = value;
        type = value>255? OperandType.immediate16 : OperandType.immediate8;
    }
}
