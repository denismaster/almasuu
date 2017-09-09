package com.translator.semantic;

public class AnalyzerUtils {
    public static String getRegisterCode(String register){
        switch(register) {
            case "AX": return "000";
            case "AH": return "100";
        }
        return register;
    }
}
