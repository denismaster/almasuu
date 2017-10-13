package com.translator.semantic.commands.test;

import com.translator.semantic.AnalyzerUtils;
import com.translator.semantic.commands.Command;

public class ImDataAccTestCommand implements Command {
    public int value;
    public boolean isWide;

    public ImDataAccTestCommand(int value, boolean isWide) {
        this.value = value;
        this.isWide = isWide;
    }

    public String generateCode(){
        String result =  "1010100"+ (this.isWide ? "1":"0");
        result = result + " " + AnalyzerUtils.getLHValueFromInt(this.value);
        return result;
    }
}
