package com.translator.lexer;

import java.util.ArrayList;
import java.util.List;

public class TokenParsingResult {
    public List<String> sourceLines = new ArrayList<>();

    public List<String> labels = new ArrayList<>();
    public List<Token>  tokens = new ArrayList<>();
    public List<TokenLine> tokenLines = new ArrayList<>();
    public List<String> segments = new ArrayList<>();
    public List<String> names = new ArrayList<>();

    public List<String> errors = new ArrayList<>();
}
