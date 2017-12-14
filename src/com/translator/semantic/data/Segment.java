package com.translator.semantic.data;

import com.translator.lexer.Token;

public class Segment {
    public Token token;
    public String name;
    public Boolean isOpened = false;;
    public Boolean isClosed = false;

    public Segment(String name) {
        this.name = name;
    }

    public void close() { isClosed = true; }
}
