package com.translator.lexer;

import java.util.*;

public class Utils {

    public final static String[] Commands = {
            "MOV",
            "DIV",
            "JAE",
            "TEST"
    };

    public final static String[] Registers = {
            "AX","AH","AL",
            "BX","BH","BL",
            "CX","CH","CL",
            "DX","DH","DL",
            "SI","DI",
            "F",
            "CS","DS","ES","SS",
            "IP"
    };

    public final static String[] Directives = {
            "SEGMENT", "ENDS",
            "DW",
            "DB"
    };

    public static boolean isCommand(String lexeme){
        for(String command: Commands){
            if(command.equalsIgnoreCase(lexeme))
                return true;
        }
        return false;
    }

    public static boolean isRegister(String lexeme){
        return Arrays.stream(Registers).anyMatch(x->x.equalsIgnoreCase(lexeme));
    }

    public static boolean isDirective(String lexeme){
        return Arrays.stream(Directives).anyMatch(x->x.equalsIgnoreCase(lexeme));
    }

    public static boolean isNumber(String lexeme){
        return lexeme.trim().matches("[0-9a-fA-F]+");
    }

    public static boolean isLabel(String lexeme){
        return lexeme.trim().matches("^\\w+:$");
    }
}
