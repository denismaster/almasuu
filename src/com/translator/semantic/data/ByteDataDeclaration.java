package com.translator.semantic.data;


import com.translator.semantic.AnalyzerUtils;

public class ByteDataDeclaration implements DataDeclaration {
    public String name;
    public int value;

    public ByteDataDeclaration(String name, int value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String generateCode() {
        return AnalyzerUtils.getLHValueFromInt(value);
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getValue() {
        return this.value;
    }
}
