package com.translator.semantic.data;

import com.translator.semantic.AnalyzerUtils;

/**
 * Точка входа в приложение. Директива END <метка>
 */
public class MountPointDeclaration implements Declaration {

    private final int address;

    public MountPointDeclaration(int address) {

        this.address = address;
    }

    @Override
    public String generateCode() {
        return AnalyzerUtils.getLHValueFromInt(address);
    }

    @Override
    public int getSize() {
        return 0;
    }
}
