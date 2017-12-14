package com.translator.lexer;

import com.translator.semantic.AnalyzerUtils;
import com.translator.semantic.commands.OperandType;
import com.translator.semantic.data.Label;
import com.translator.semantic.data.Segment;
import com.translator.semantic.data.Variable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;

public class TokenParser {
    private final String COMMENT_START_SYMBOL = ";";

    private final List<String> _labels = new ArrayList<>();

    public TokenParsingResult parse(String fileName) {
        List<TokenLine> tokenLines = new ArrayList<>();
        List<String> input = this.loadFile(fileName);
        TokenParsingResult result = new TokenParsingResult();

        int lineNum = 0;
        for (String line : input) {
            result.sourceLines.add(line.trim());
            tokenLines.add(this.parseLine(line, ++lineNum));
        }

        // определяем метки
        for(TokenLine tokenLine: tokenLines)
        {
            for(Token token: tokenLine.getTokens()) {
                if (token.getTokenType() == TokenType.Other && Utils.isLabel(token.getValue())) {
                    token.setTokenType(TokenType.Label);
                    String labelName = token.getValue();
                    labelName = labelName.substring(0, labelName.length()-1);
                    _labels.add(labelName);
                    token.setValue(labelName);
                }
            }
        }

        result.labels = _labels;

        // определяем имена переменных
        processDeclarations(tokenLines, result);
        List<Token> tokens = new ArrayList<>();

        for(TokenLine tokenLine: tokenLines)
        {
            tokens.addAll(tokenLine.getTokens());
        }

        //токенов с Other быть не должно, помечаем их как ошибку
        List<Token> otherTokens = new ArrayList<>();
        for (Token token: tokens) {
            if(token.getTokenType()==TokenType.Other)
                otherTokens.add(token);
        }
        Boolean hasOtherTokens = otherTokens.size()>0;
        if(hasOtherTokens)
        {
            for (Token token: otherTokens) {
                result.errors.add(String.format("Строка %d: неизвестная инструкция:%s",
                        token.tokenLine.lineNumber,token.getValue()));
            }
        }

        result.tokenLines = tokenLines;
        return result;
    }

    private void processDeclarations(List<TokenLine> tokenLines, TokenParsingResult result) {
        int address = 0;
        for(TokenLine tokenLine: tokenLines)
        {
            for(int i=0;i<tokenLine.getTokens().size();i++){
                Token token = tokenLine.getTokens().get(i);
                if(Utils.isOrgDirective(token) && i < tokenLine.getTokens().size()-1)
                {
                    Token nextToken = tokenLine.getTokens().get(i+1);
                    if(nextToken.getTokenType()==TokenType.Number) {
                        Optional<Integer> number = AnalyzerUtils.readDecHex(nextToken.getValue());
                        if(number.isPresent()) result.org = number.get();
                    }
                    else
                    {
                       result.errors.add("Операнды не совпадают. Строка "+ tokenLine.lineNumber);
                    }
                }
                if(token.getTokenType()== TokenType.Other && i < tokenLine.getTokens().size()-2)
                {
                    Token nextToken = tokenLine.getTokens().get(i+1);
                    Token thirdToken = tokenLine.getTokens().get(i+2);
                    if(Utils.isDataWordDirective(nextToken) || Utils.isDataByteDirective(nextToken)) {
                        Optional<Integer> number = AnalyzerUtils.readDecHex(thirdToken.getValue());
                        if(number.isPresent()) {
                            Variable put = new Variable(token.getValue(), address, number.get());
                                    result.variables.put(token.getValue(), put);
                            if(put.type==OperandType.immediate16 && Utils.isDataByteDirective(nextToken))
                            {
                                result.errors.add("Тип операндов не совпадают. Строка "+ tokenLine.lineNumber);
                            }
                            address+=put.type== OperandType.immediate8 ? 1:2;
                        }
                    }
                }
                if(token.getTokenType()== TokenType.Other && i < tokenLine.getTokens().size()-1){
                    Token nextToken = tokenLine.getTokens().get(i+1);
                    if(nextToken.getTokenType()==TokenType.Directive){
                        token.setTokenType(TokenType.Name);
                        result.segments.add(token.getValue());
                }
                }
                if(token.getTokenType()==TokenType.Other
                        && (result.labels.contains(token.getValue()) || result.segments.contains(token.getValue()))){
                    token.setTokenType(TokenType.Name);
                    result.names.add(token.getValue());
                }
                if(Utils.isEndDirective(token) && i < tokenLine.getTokens().size()-1 )
                {
                    Token nextToken = tokenLine.getTokens().get(i+1);
                }
                if(Utils.isSegmentDirective(token) && i >0)
                {
                    Token previousToken = tokenLine.getTokens().get(i-1);
                    Segment segment = new Segment(previousToken.getValue());
                    segment.isOpened = true;
                    segment.token = token;
                    result._segments.put(segment.name,segment);
                }
                if(Utils.isEndsDirective(token) && i >0)
                {
                    Token nextToken = tokenLine.getTokens().get(i-1);
                    Segment segment = result._segments.get(nextToken.getValue());
                    if(segment!=null)
                    {
                        if(segment.isClosed)
                        {
                            result.errors.add("Сегмент уже закрыт. Строка" + nextToken.tokenLine.lineNumber);
                        }
                        else
                        {
                            segment.close();
                        }
                    }
                    else
                    {
                        result.errors.add("Сегмент не открыт. Строка" + segment.token.tokenLine.lineNumber);
                    }
                }
            }
        }

        for(Segment segment: result._segments.values())
        {
            if(!segment.isClosed) {
                result.errors.add("Сегмент не закрыт. Строка" + segment.token.tokenLine.lineNumber);
                break;
            }
        }
    }

    private TokenLine parseLine(String line, int lineNumber) {
        StringTokenizer st = new StringTokenizer(line, " \t\n\r,.");

        TokenLine tokenLine = new TokenLine(lineNumber);
        List<String> stringTokens  = new ArrayList<>();

        while (st.hasMoreTokens()) {
            stringTokens.add(st.nextToken());
        }
        for(String str: stringTokens)
        {
            str = str.replaceAll("[\uFEFF-\uFFFF]", "");

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

            if(Utils.isIndirectAddressingRegister(str)){
                tokenLine.addToken(new Token(TokenType.IndirectAddress, str));
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
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return list;
        }

    }

}
