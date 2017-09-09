package com.translator.semantic;

public class ImRegMoveCommand {
    public String register;
    public int value;
    public boolean isWide;

    public ImRegMoveCommand(String register, int value, boolean isWide) {
        this.register = register;
        this.value = value;
        this.isWide = isWide;
    }

    public String generateCode(){
        String result =  "1011"+ (this.isWide ? "1":"0") + AnalyzerUtils.getRegisterCode(register.toUpperCase());
        result = result + " " + AnalyzerUtils.getLHValueFromInt(this.value);
        return result;
    }
}
