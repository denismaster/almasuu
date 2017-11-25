package com.translator.semantic;

import com.translator.semantic.commands.CodeSegment;

import java.util.ArrayList;
import java.util.List;

public class AnalyzeResult {
    public List<String> errors = new ArrayList<>();
    public CodeSegment codeSegment;
}
