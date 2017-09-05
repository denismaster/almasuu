package com.parser.lexer;

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

    public static boolean isCommand(String lexeme){
        return Arrays.stream(Commands).anyMatch(x->x.equalsIgnoreCase(lexeme));
    }
}
