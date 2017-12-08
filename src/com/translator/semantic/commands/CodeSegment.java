package com.translator.semantic.commands;

import com.translator.semantic.AnalyzeResult;
import com.translator.semantic.Segment;

import java.util.*;

public class CodeSegment extends Segment implements CodeGeneratable{
    private int displacementInBytes;
    public Map<Integer,Command> commands;
    public Boolean isClosed = false;

    public CodeSegment(int displacementInBytes){
        this.displacementInBytes = displacementInBytes;
        this.commands = new HashMap<>();
    }

    public void add(int line,Command c){
        if(c==null) throw new IllegalArgumentException();
        commands.put(line,c);
    }

    public String generateCode(){
        StringBuilder builder  = new StringBuilder();
        for(Command c: commands.values()){
            String resultHexCode = AnalyzeResult.getHexCode(c);
            builder.append(resultHexCode);
            builder.append("\n");
        }
        return builder.toString();
    }

    public Map<String, Integer> labelsOffsets = new HashMap<String,Integer>();

    public int getSize()  {
        int currentSize = 0;
        for(Command command: commands.values())
        {
            currentSize+=command.getSize();
        }
        return currentSize;
    }

    @Override
    public void closeSegment() {
        isClosed = true;
    }

    @Override
    public void add(int line,CodeGeneratable t) {
        if(t instanceof Command)
            this.add(line, (Command)t);
    }
}
