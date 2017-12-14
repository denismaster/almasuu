package com.translator.lexer;

import com.translator.semantic.data.Segment;
import com.translator.semantic.data.Variable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokenParsingResult {
    public List<String> sourceLines = new ArrayList<>();

    public List<String> labels = new ArrayList<>();
    public List<TokenLine> tokenLines = new ArrayList<>();
    public List<String> segments = new ArrayList<>();
    public Map<String,Segment> _segments = new HashMap<>();
    public List<String> names = new ArrayList<>();
    public Map<String,Variable> variables = new HashMap<>();
    public List<String> errors = new ArrayList<>();

    public Variable getVariableByName(String name)
    {
        return variables.get(name);
    }

    public int org;
}
