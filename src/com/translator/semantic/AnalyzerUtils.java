package com.translator.semantic;

public class AnalyzerUtils {
    public static String getRegisterCode(String register){
        switch(register) {
            case "AX": return "000";
            case "AH": return "100";
        }
        return register;
    }


    public static String getLHValueFromInt(int in){
        byte[] data = new byte[2]; // <- assuming "in" value in 0..65535 range and we can use 2 bytes only

        data[0] = (byte)(in & 0xFF);
        data[1] = (byte)((in >> 8) & 0xFF);

        int high = data[1] >= 0 ? data[1] : 256 + data[1];
        int low = data[0] >= 0 ? data[0] : 256 + data[0];

        if(high==0){
            return byteToString((byte)low);
        }

        return byteToString((byte)low)+" "+ byteToString((byte)high);
    }

    public static String byteToString(byte value){
        return String.format("%8s", Integer.toBinaryString(value & 0xFF)).replace(' ', '0');
    }
}
