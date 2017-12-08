package com.translator.semantic;

import com.translator.lexer.TokenParsingResult;
import com.translator.semantic.commands.CodeSegment;
import com.translator.semantic.data.DataSegment;

import java.util.ArrayList;
import java.util.List;

public class AnalyzeResult {
    public List<String> errors = new ArrayList<>();
    public TokenParsingResult parsingResult;
    public CodeSegment codeSegment = new CodeSegment(0);
    public DataSegment dataSegment = new DataSegment();

    public boolean hasErrors(){
        return errors.size()>0;
    }
}
