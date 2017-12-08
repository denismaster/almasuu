package com.translator.semantic.data;

import com.translator.semantic.AnalyzerUtils;

public class WordDataDeclaration implements DataDeclaration {
    public String name;
    public int value;

    public WordDataDeclaration(String name, int value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String generateCode() {
        return AnalyzerUtils.getLHValueFromInt(value);
    }

    @Override
    public int getSize() {
        return 2;
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
