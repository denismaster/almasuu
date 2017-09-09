package com.translator.semantic;

public class AnalyzerUtils {
    public static String getRegisterCode(String register){
        switch(register) {
            case "AX": return "000";
            case "AH": return "100";
        }
        return register;
    }

    public static String byteToString(byte value){
        return String.format("%8s", Integer.toBinaryString(value & 0xFF)).replace(' ', '0');
    }
}
