package com.translator.semantic;

import java.util.Arrays;
import java.util.Optional;

public class AnalyzerUtils {
    public static String getRegisterCode(String register){
        switch(register) {

            //16-бит
            case "AX": return "000";
            case "CX": return "001";
            case "DX": return "010";
            case "BX": return "011";
            case "SP": return "100";
            case "BP": return "101";
            case "SI": return "110";
            case "DI": return "111";

            //8-бит
            case "AL": return "000";
            case "CL": return "001";
            case "DL": return "010";
            case "BL": return "011";
            case "AH": return "100";
            case "CH": return "101";
            case "DH": return "110";
            case "BH": return "111";

            //сегменты
            case "ES": return "00";
            case "CS": return "01";
            case "SS": return "10";
            case "DS": return "11";
        }
        return register;
    }

    public static boolean isWideRegister(String register){
        String[] registers = {
              "AX","CX","DX","BX","SP","BP","SI","DI"
        };

        return Arrays.stream(registers).anyMatch(x->x.equalsIgnoreCase(register));
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

    public static String getModValue(ModeType mod){
        switch(mod) {

            //16-бит
            case RegisterAddressing: return "11";
            case RegisterIndirect: return "00";
        }
        return "";
    }

    /**
     * Читает десятичное или шестнадцатиричное число из строки
     * @param str строка, содержащая число
     * @return число или None
     */
    public static Optional<Integer> readDecHex(String str){
        try{
            str = str.trim();
            if(str.endsWith("H")){
               return Optional.of(Integer.parseInt(str.substring(0,str.length()-1),16));
            }
            else{
                return Optional.of(Integer.parseInt(str,10));
            }
        }
        catch(Exception e){
            return Optional.empty();
        }
    }
}
