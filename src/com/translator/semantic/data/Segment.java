package com.translator.semantic.data;

public class Segment {
    public String name;
    public Boolean isOpened;
    public Boolean isClosed;

    public Segment(String name) {
        this.name = name;
    }

    public void close() { isClosed = true; }
}
