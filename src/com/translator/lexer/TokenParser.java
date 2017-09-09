package com.translator.lexer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class TokenParser {
    private final String COMMENT_START_SYMBOL = ";";

    private final List<String> _labels = new ArrayList<String>();
    private final List<String> _errors = new ArrayList<String>();

    public TokenParsingResult parse(String fileName) {
        List<TokenLine> tokenLines = new ArrayList<>();
        List<String> input = this.loadFile(fileName);
        TokenParsingResult result = new TokenParsingResult();
        for (String line : input) {
            tokenLines.add(this.parseLine(line));
        }
        // определяем метки
        for(TokenLine tokenLine: tokenLines)
        {
            for(Token token: tokenLine.getTokens()) {
                if (token.getTokenType() == TokenType.Other && Utils.isLabel(token.getValue())) {
                    token.setTokenType(TokenType.Label);
                    _labels.add(token.getValue().substring(0, token.getValue().length() - 1));
                }
            }
        }
        // определяем имена переменных
        for(TokenLine tokenLine: tokenLines)
        {
            for(int i=0;i<tokenLine.getTokens().size();i++){
                Token token = tokenLine.getTokens().get(i);
                if(token.getTokenType()==TokenType.Other && i < tokenLine.getTokens().size()-1){
                    Token nextToken = tokenLine.getTokens().get(i+1);
                    if(nextToken.getTokenType()==TokenType.Directive){
                        token.setTokenType(TokenType.Name);
                        result.segments.add(token.getValue());
                }
                }
                if(token.getTokenType()==TokenType.Other
                        && (_labels.contains(token.getValue()) || result.segments.contains(token.getValue()))){
                    token.setTokenType(TokenType.Name);
                }
            }
        }
        for(TokenLine tokenLine: tokenLines){
            if(!tokenLine.getTokens().isEmpty())
            {
                for(Token token: tokenLine.getTokens()){
                    System.out.print(String.format("{%s,\"%s\"} ",token.getTokenType().toString(),token.getValue()));
                }
                System.out.println();
            }

        }
        result.tokenLines = tokenLines;
        return result;
    }

    public TokenLine parseLine(String line) {
        StringTokenizer st = new StringTokenizer(line, " \t\n\r,.");

        TokenLine tokenLine = new TokenLine();
        List<String> stringTokens  = new ArrayList<>();

        while (st.hasMoreTokens()) {
            stringTokens.add(st.nextToken());
        }
        for(String str: stringTokens)
        {
            if(str.startsWith(this.COMMENT_START_SYMBOL)) {
                //Если это комментарий, то дальше после него токенов не будет, это будет лишь один комментарий
                //Поэтому ставим флаг, что надо объединять)
                break;
            }

            if(Utils.isCommand(str)){
                tokenLine.addToken(new Token(TokenType.Command, str));
                continue;
            }

            if(Utils.isRegister(str)){
                tokenLine.addToken(new Token(TokenType.Register, str));
                continue;
            }

            if(Utils.isDirective(str)){
                tokenLine.addToken(new Token(TokenType.Directive,str));
                continue;
            }

            if(Utils.isNumber(str)){
                tokenLine.addToken(new Token(TokenType.Number,str));
                continue;
            }

            tokenLine.addToken(new Token(TokenType.Other, str));
        }
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