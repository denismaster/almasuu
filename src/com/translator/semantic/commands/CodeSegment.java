package com.translator.semantic.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodeSegment implements CodeGeneratable{
    private int displacementInBytes;
    public List<Command> commands;

    public CodeSegment(int displacementInBytes){
        this.displacementInBytes = displacementInBytes;
        this.commands = new ArrayList<Command>();
    }

    public String generateCode(){
        StringBuilder builder  = new StringBuilder();
        for(Command c: commands){
            builder.append(c.generateCode());
            builder.append(" ");
        }
        return builder.toString();
    }

    public Map<String, Integer> labelsOffsets = new HashMap<String,Integer>();

    public int getSize()  {
        int currentSize = 0;
        for(Command command: commands)
        {
            currentSize+=command.getSize();
        }
        return currentSize;
    }
}
