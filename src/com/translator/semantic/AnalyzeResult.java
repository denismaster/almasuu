package com.translator.semantic;

import com.translator.lexer.TokenParsingResult;
import com.translator.semantic.commands.CodeSegment;
import com.translator.semantic.commands.Command;
import com.translator.semantic.data.DataSegment;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class AnalyzeResult {

    public static String getHexCode(Command c) {
        StringTokenizer st = new StringTokenizer(c.generateCode(), " ");
        String resultHexCode = "";
        while(st.hasMoreTokens()){
            String byteCode = st.nextToken();
            resultHexCode += String.format("%02X",Integer.parseInt(byteCode,2))+" ";
        }
        return resultHexCode;
    }

    public class TranslationRecord{
        public int index, offset;
        public String code, sourceCode;

        public TranslationRecord(int index, int offset, String code, String sourceCode) {
            this.index = index;
            this.offset = offset;
            this.code = code;
            this.sourceCode = sourceCode;
        }
    }

    public List<String> errors = new ArrayList<>();
    public TokenParsingResult parsingResult;
    public CodeSegment codeSegment = null;//new CodeSegment(0);
    public DataSegment dataSegment = null;//n;ew DataSegment();

    public boolean hasErrors(){
        return errors.size()>0;
    }

    public String getResults(){

        int i = 0;
        int disp = 0;

        List<TranslationRecord> results = new ArrayList<>();
        while(i<parsingResult.sourceLines.size()){
            String sourceLine = parsingResult.sourceLines.get(i);
            Command c = codeSegment.commands.get(i+1);
            String code;
            if(c==null)
                code = "";
            else
                code = getHexCode(c);

            results.add(new TranslationRecord(i+1,disp,code,sourceLine));

            i++;
            disp+=c==null?0:c.getSize();
        }

        StringBuilder builder = new StringBuilder();
        for (TranslationRecord record: results) {
            builder.append(String.format("%04X\t%8s\t%s\n",
                    record.index,record.code,record.sourceCode));
        }

        return builder.toString();
    }
}
