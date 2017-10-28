package com.translator.semantic.commands;

import java.util.ArrayList;
import java.util.List;

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
}
