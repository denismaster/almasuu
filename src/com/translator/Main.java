package com.translator;

import com.translator.lexer.TokenParser;

public class Main {

    public static void main(String[] args) {
        System.out.println("i8086 Assembler (с) Рябцева А. 2017");
        System.out.println("Парсинг входного файла...");

        String fileName = "E:\\code.asm";
        TokenParser parser = new TokenParser();

        parser.parse(fileName);
    }
}
