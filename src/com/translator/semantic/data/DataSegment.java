package com.translator.semantic.data;

import com.translator.semantic.commands.CodeGeneratable;

public class DataSegment implements CodeGeneratable {
    public boolean isClosed = false;

    @Override
    public String generateCode() {
        return "";
    }

    @Override
    public int getSize() {
        return 0;
    }
}
