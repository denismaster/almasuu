package com.translator.lexer;

import java.util.*;

public class Utils {
    /***
     * Список токенов-команд
     */
    public final static String[] Commands = {
            "MOV",
            "DIV",
            "JAE",
            "TEST",
            "INT"
    };

    /***
     * Список токенов-регистров
     */
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

    /***
     * Список токенов-директив
     */
    public final static String[] Directives = {
            "SEGMENT", "ENDS",
            "DW",
            "DB",
            "ORG","OFFSET",
            "END",
    };

    /***
     * Список токенов-регистров в режиме косвенной адресации
     */
    public final static String[] IndirectAddressingRegisters = {
            "[SI]", "[DI]",
            "[BX]", "[BP]"
    };

    /***
     * Метод, проверяющий является ли токен командой
     */
    public static boolean isCommand(String lexeme){
        for(String command: Commands){
            if(command.equalsIgnoreCase(lexeme))
                return true;
        }
        return false;
    }

    /***
     * Метод, проверяющий является ли токен регистром
     */
    public static boolean isRegister(String lexeme){
        return Arrays.stream(Registers).anyMatch(x->x.equalsIgnoreCase(lexeme));
    }

    /***
     * Метод, проверяющий является ли токен директивой
     */
    public static boolean isDirective(String lexeme){
        return Arrays.stream(Directives).anyMatch(x->x.equalsIgnoreCase(lexeme));
    }

    /***
     * Метод, проверяющий является ли токен числом
     */
    public static boolean isNumber(String lexeme){
        return lexeme.trim().matches("[0-9a-fA-F]+(H|h)?");
    }

    /***
     * Метод, проверяющий является ли токен меткой
     */
    public static boolean isLabel(String lexeme){
        return lexeme.trim().matches("^\\w+:$");
    }

    /***
     * Метод, проверяющий является ли токен косвенным адресом
     */
    public static boolean isIndirectAddressingRegister(String lexeme) {
        return Arrays.stream(IndirectAddressingRegisters).anyMatch(x->x.equalsIgnoreCase(lexeme));
    }

    //SEGMENT
    public static boolean isSegmentDirective(Token token) {
        if (token.getTokenType() != TokenType.Directive) return false;
        return token.getValue().equalsIgnoreCase("SEGMENT");
    }

    //Org
    public static  boolean isOrgDirective(Token token) {
        if (token.getTokenType() != TokenType.Directive) return false;
        return token.getValue().equalsIgnoreCase("ORG");
    }

    //ENDS
    public static  boolean isEndsDirective(Token token) {
        if (token.getTokenType() != TokenType.Directive) return false;
        return token.getValue().equalsIgnoreCase("ENDS");
    }

    //END
    public static  boolean isEndDirective(Token token) {
        if (token.getTokenType() != TokenType.Directive) return false;
        return token.getValue().equalsIgnoreCase("END");
    }

    //Offset
    public static  boolean isOffsetDirective(Token token) {
        if (token.getTokenType() != TokenType.Directive) return false;
        return token.getValue().equalsIgnoreCase("OFFSET");
    }

    //DW
    public static  boolean isDataWordDirective(Token token) {
        if (token.getTokenType() != TokenType.Directive) return false;
        return token.getValue().equalsIgnoreCase("DW");
    }

    //DB
    public static boolean isDataByteDirective(Token token) {
        if (token.getTokenType() != TokenType.Directive) return false;
        return token.getValue().equalsIgnoreCase("DB");
    }
}
