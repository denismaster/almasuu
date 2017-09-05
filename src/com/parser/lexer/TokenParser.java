package com.parser.lexer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TokenParser {
    private final String COMMENT_START_SYMBOL = ";";

    public List<TokenLine> parse(String fileName) {
        List<TokenLine> tokenLines = new ArrayList<>();
        List<String> input = this.loadFile(fileName);
        for (String line : input) {
            tokenLines.add(this.parseLine(line));
        }
        return tokenLines;
    }
}
