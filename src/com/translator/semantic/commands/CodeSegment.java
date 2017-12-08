package com.translator.semantic.commands;

import com.translator.semantic.Segment;

import java.util.*;

public class CodeSegment extends Segment implements CodeGeneratable{
    private int displacementInBytes;
    public List<Command> commands;

    public CodeSegment(int displacementInBytes){
        this.displacementInBytes = displacementInBytes;
        this.commands = new ArrayList<Command>();
    }

    public void add(Command c){
        if(c==null) throw new IllegalArgumentException();
        commands.add(c);
    }

    public String generateCode(){
        StringBuilder builder  = new StringBuilder();
        for(Command c: commands){
            StringTokenizer st = new StringTokenizer(c.generateCode(), " ");
            String resultHexCode = "";
            while(st.hasMoreTokens()){
                String byteCode = st.nextToken();
                resultHexCode += Integer.toHexString(Integer.parseInt(byteCode,2))+" ";
            }
            builder.append(resultHexCode);
            builder.append("\n");
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
