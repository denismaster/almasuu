package com.translator.semantic;

import com.translator.semantic.commands.CodeGeneratable;

public abstract class Segment {
    public String name;
    public abstract void closeSegment();
    public abstract void add(int line, CodeGeneratable t);
}
