package com.parser.lexer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class TokenParser {
    private final String COMMENT_START_SYMBOL = ";";

    public List<TokenLine> parse(String fileName) {
        List<TokenLine> tokenLines = new ArrayList<>();
        List<String> input = this.loadFile(fileName);
        for (String line : input) {
            tokenLines.add(this.parseLine(line));
        }
        return tokenLines;
    }

    public TokenLine parseLine(String line) {
        StringTokenizer st = new StringTokenizer(line, " \t\n\r,.");

        TokenLine tokenLine = new TokenLine();
        while (st.hasMoreTokens()) {
            String str = st.nextToken();

            if(str.startsWith(this.COMMENT_START_SYMBOL)) {
                //Если это комментарий, то дальше после него токенов не будет, это будет лишь один комментарий
                //Поэтому ставим флаг, что надо объединять)
                return tokenLine;

            }

            if(Utils.isCommand(str)){
                tokenLine.addToken(new Token(TokenType.Command, str));
                continue;
            }

            if(Utils.isRegister(str)){
                tokenLine.addToken(new Token(TokenType.Register, str));
                continue;
            }

            tokenLine.addToken(new Token(TokenType.Other, str));
        }
        for(Token token: tokenLine.getTokens()){
            System.out.print(String.format("{%s,\"%s\"} ",token.getTokenType().toString(),token.getValue()));
        }
        System.out.println();

        return tokenLine;
    }

    private List<String> loadFile(String fileName) {
        List<String> list = new ArrayList<>();
        try {
            File file = new File(fileName);
            //создаем объект FileReader для объекта File
            FileReader fr = new FileReader(file);
            //создаем BufferedReader с существующего FileReader для построчного считывания
            BufferedReader reader = new BufferedReader(fr);
            // считаем сначала первую строку
            String line = reader.readLine();
            while (line != null) {
                list.add(line);
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return list;
        }

    }

}
