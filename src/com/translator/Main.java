package com.translator;

import com.translator.lexer.TokenParser;
import com.translator.semantic.Analyzer;

public class Main {

    public static void main(String[] args) {
        System.out.println("i8086 Assembler (с) Рябцева А. 2017");
        System.out.println("Парсинг входного файла...");

        String fileName = "E:\\code.txt";
        TokenParser parser = new TokenParser();
        Analyzer analyzer = new Analyzer();

        analyzer.analyze(parser.parse(fileName));
    }
}
