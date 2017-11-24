package com.translator;

import com.translator.lexer.TokenParser;
import com.translator.semantic.Analyzer;

import java.io.File;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.println("i8086 Assembler (с) Рябцева А. 2017");

        System.out.println("Введите имя файла для трансляции");
        Scanner cin = new Scanner(System.in);
        String inputFileName = cin.nextLine();

        File f = new File(inputFileName);
        if(!f.exists() || f.isDirectory()) {
            System.out.println("Некорректное имя файла. Работа транслятора будет завершена");
            return;
        }

        System.out.println("Парсинг входного файла...");
        TokenParser parser = new TokenParser();

        Analyzer analyzer = new Analyzer();
        analyzer.analyze(parser.parse(inputFileName));
    }
}
