package com.translator.semantic;

import com.translator.lexer.Token;
import com.translator.lexer.TokenLine;
import com.translator.lexer.TokenParsingResult;
import com.translator.lexer.TokenType;
import com.translator.semantic.commands.CodeSegment;
import com.translator.semantic.commands.Command;
import com.translator.semantic.commands.div.DivCommand;
import com.translator.semantic.commands.intpt.InterruptCommand;
import com.translator.semantic.commands.jae.JaeCommand;
import com.translator.semantic.commands.mov.ImRegMoveCommand;
import com.translator.semantic.commands.test.RegMemRegisterTestCommand;
import com.translator.semantic.data.*;

import java.util.List;
import java.util.Optional;

public class Analyzer {
    AnalyzeResult result = new AnalyzeResult();
    CodeSegment currentSegment;
    int currentLineNumber;
    int currentDisplacement = 0;

    public AnalyzeResult analyze(TokenParsingResult parsingResult) {
        List<TokenLine> tokenLines = parsingResult.tokenLines;
        result.parsingResult = parsingResult;
        currentSegment = new CodeSegment(parsingResult.org);
        if (parsingResult.errors.size() > 0) {
            return result;
        }

        for(int i=0;i<tokenLines.size();i++)
        {
            TokenLine tokenLine = tokenLines.get(i);
            currentLineNumber = tokenLine.lineNumber;

            while(tokenLine.hasMoreTokens())
            {
                Token token = tokenLine.nextToken();

                if (isLabel(token)) {
                    String labelName = token.getValue();
                    currentSegment.labelsOffsets.put(labelName, currentSegment.getSize());
                }

                if (isMoveCommand(token)) {
                    processMovCommands(tokenLine);
                }
                if (isTestCommand(token)) {
                    processTestCommands(tokenLine);
                }
                if (isDivCommand(token)) {
                    processDivCommands(tokenLine);
                }
                if (isJaeCommand(token)) {
                    processJaeCommands(tokenLine, parsingResult.labels);
                }
                if (isInterruptCommand(token)) {
                    processIntCommands(tokenLine);
                }
            }
        }

        result.codeSegment = currentSegment;
        return result;
    }

    private void processMovCommands(TokenLine line) {
        //Если больше токенов нет, то выдаем ошибку
        if (!line.hasMoreTokens()) {
            result.errors.add("Не хватает операндов! Строка "+currentLineNumber);
        }
        Token firstOperand = line.nextToken();
        if (firstOperand.getTokenType() == TokenType.Register) {
            if (!line.hasMoreTokens()) {
                result.errors.add("Не хватает операндов! Строка "+currentLineNumber);
            }
            Token secondOperand = line.nextToken();
            if (secondOperand.getTokenType() == TokenType.Number) {
                Optional<Integer> value = AnalyzerUtils.readDecHex(secondOperand.getValue());
                if (value.isPresent()) {
                    int val = value.get();
                    Command command = new ImRegMoveCommand(firstOperand.getValue(), val, val > 256);
                    currentSegment.add(currentLineNumber, command);
                }

            }
            else
            if (secondOperand.getTokenType() == TokenType.Name) {
                String variableName = secondOperand.getValue();
                DataDeclaration variable = result.dataSegment.findVariable(variableName);
                int val = variable.getValue();
                Command command = new ImRegMoveCommand(firstOperand.getValue(), val, val > 256);
                currentSegment.add(currentLineNumber, command);
            }
        }
    }

    private void processTestCommands(TokenLine line) {
        //Если больше токенов нет, то выдаем ошибку
        if (!line.hasMoreTokens()) {
            result.errors.add("Не хватает операндов! Строка "+currentLineNumber);
        }
        Token firstOperand = line.nextToken();
        if (firstOperand.getTokenType() == TokenType.Register) {
            if (!line.hasMoreTokens()) {
                result.errors.add("Не хватает операндов! Строка "+currentLineNumber);
            }
            Token secondOperand = line.nextToken();

            if (secondOperand.getTokenType() == TokenType.IndirectAddress) {
                boolean isWide = AnalyzerUtils.isWideRegister(firstOperand.getValue());
                Command command = new RegMemRegisterTestCommand(firstOperand.getValue(),
                        secondOperand.getValue(), isWide, ModeType.RegisterIndirect);
                currentSegment.add(currentLineNumber, command);
            }
        } else {
            result.errors.add("Операнды не совпадают. Строка "+currentLineNumber);
        }
    }

    private void processDivCommands(TokenLine line) {
        //Если больше токенов нет, то выдаем ошибку
        if (!line.hasMoreTokens()) {
            result.errors.add("Не хватает операндов!. Строка "+ currentLineNumber);
        }
        Token firstOperand = line.nextToken();
        if (firstOperand.getTokenType() == TokenType.Register) {

            boolean isWide = AnalyzerUtils.isWideRegister(firstOperand.getValue());
            Command command = new DivCommand(isWide,
                    firstOperand.getValue(), ModeType.RegisterAddressing);
            currentSegment.add(currentLineNumber, command);
        } else {
            result.errors.add("Операнды не совпадают. Строка "+currentLineNumber);
        }
    }

    private void processJaeCommands(TokenLine line, List<String> labels) {
        //Если больше токенов нет, то выдаем ошибку
        if (!line.hasMoreTokens()) {
            result.errors.add("Не хватает операндов!. Строка "+currentLineNumber);
        }
        Token firstOperand = line.nextToken();
        if (firstOperand.getTokenType() == TokenType.Name) {
            if (!labels.contains(firstOperand.getValue())) {
                result.errors.add("Метка не определена. Строка "+currentLineNumber);
            }
            String labelName = firstOperand.getValue();
            Command command = new JaeCommand(currentSegment.labelsOffsets.get(labelName));
            currentSegment.add(currentLineNumber, command);
        } else {
            result.errors.add("Операнды не совпадают. Строка "+currentLineNumber);
        }
    }

    private void processIntCommands(TokenLine line) {
        //Если больше токенов нет, то выдаем ошибку
        if (!line.hasMoreTokens()) {
            result.errors.add("Не хватает операндов! Строка "+currentLineNumber);
        }
        Token firstOperand = line.nextToken();
        if (firstOperand.getTokenType() == TokenType.Number) {
            Optional<Integer> value = AnalyzerUtils.readDecHex(firstOperand.getValue());
            if (!value.isPresent()) {
                result.errors.add("Ошибочный операнд! Строка "+currentLineNumber);
                return;
            }

            int type = value.get();
            Command command = new InterruptCommand(type);
            currentSegment.add(currentLineNumber, command);
        } else {
            result.errors.add("Операнды не совпадают. Строка "+currentLineNumber);
        }
    }

    /*
    private int processDeclarations(List<Token> tokens, int i) {
        if (i == tokens.size() - 1) {
            result.errors.add("Не хватает операндов! Строка "+ currentLineNumber);
            return i;
        }
        Token token = tokens.get(i);
        if (isOrgDirective(token)) {
            if (i == tokens.size() - 1) {
                result.errors.add("Не хватает операндов! Строка "+currentLineNumber);
                return i;
            }
            Token firstOperand = tokens.get(++i);
            if (firstOperand.getTokenType() == TokenType.Number) {
                Optional<Integer> value = AnalyzerUtils.readDecHex(firstOperand.getValue());
                if (!value.isPresent()) {
                    result.errors.add("Ошибочный операнд! Строка "+currentLineNumber);
                    return i;
                }
                currentDisplacement = value.get();
            }
        }
        if (isSegmentDirective(token)) {
            if (result.dataSegment == null) {
                result.dataSegment = new DataSegment();
                result.dataSegment.isClosed = false;
                result.dataSegment.name = tokens.get(i - 1).getValue();
            }
            else
            {
                result.codeSegment = new CodeSegment(currentDisplacement > 0 ? currentDisplacement : 0);
                result.codeSegment.isClosed = false;
                result.codeSegment.name = tokens.get(i - 1).getValue();
                currentSegment = result.codeSegment;
            }
        }
        if (isEndsDirective(token)) {
            String segmentName = tokens.get(i - 1).getValue();
            if (result.dataSegment != null) {
                result.dataSegment.closeSegment();
            } else {
                result.codeSegment.closeSegment();
            }
        }
        if (isEndDirective(token))
        {
            if(result.dataSegment!=null && result.dataSegment.hasEndpointDeclaration())
            {
                result.errors.add("Точка входа уже была задана. Строка "+currentLineNumber);
                return i;
            }
            if (i == tokens.size() - 1  && result.dataSegment!=null && !result.dataSegment.hasEndpointDeclaration()) {
                result.errors.add("Не задана точка входа. Строка "+currentLineNumber);
                return i;
            }
            Token firstOperand = tokens.get(++i);
            if (firstOperand.getTokenType() == TokenType.Name) {
                String labelName = firstOperand.getValue();
                int offset = currentSegment.labelsOffsets.get(labelName);
                result.dataSegment.add(currentLineNumber,new MountPointDeclaration(offset+currentDisplacement));
            } else {
                result.errors.add("Операнды не совпадают. Строка "+currentLineNumber);
                return i;
            }
        }
        if (isDataWordDirective(token)) {
            Token previousToken = tokens.get(i-1);
            Token nextToken = tokens.get(i+1);
            if(!(previousToken.getTokenType()==TokenType.Name)||!(nextToken.getTokenType()==TokenType.Number))
            {
                result.errors.add("Операнды не совпадают. Строка"+currentLineNumber);
                return i;
            }
            String variable = previousToken.getValue();
            String valueStr = nextToken.getValue();
            Optional<Integer> value = AnalyzerUtils.readDecHex(valueStr);
            if (!value.isPresent()) {
                result.errors.add("Ошибочный операнд! Строка "+currentLineNumber);
                return i;
            }
            result.dataSegment.add(currentLineNumber, new WordDataDeclaration(variable,value.get()));
        }
        if (isDataByteDirective(token)) {
            Token previousToken = tokens.get(i-1);
            Token nextToken = tokens.get(i+1);
            if(!(previousToken.getTokenType()==TokenType.Name)||!(nextToken.getTokenType()==TokenType.Number))
            {
                result.errors.add("Операнды не совпадают. Строка"+currentLineNumber);
                return i;
            }
            String variable = previousToken.getValue();
            String valueStr = nextToken.getValue();
            Optional<Integer> value = AnalyzerUtils.readDecHex(valueStr);
            if (!value.isPresent()) {
                result.errors.add("Ошибочный операнд! Строка "+currentLineNumber);
                return i;
            }
            result.dataSegment.add(currentLineNumber, new ByteDataDeclaration(variable,value.get()));
        }
        return i;
    }*/

    //MOV
    public boolean isMoveCommand(Token token) {
        if (token.getTokenType() != TokenType.Command) return false;
        return token.getValue().equalsIgnoreCase("MOV");
    }

    //TEST
    public boolean isTestCommand(Token token) {
        if (token.getTokenType() != TokenType.Command) return false;
        return token.getValue().equalsIgnoreCase("TEST");
    }

    //JAE
    public boolean isJaeCommand(Token token) {
        if (token.getTokenType() != TokenType.Command) return false;
        return token.getValue().equalsIgnoreCase("JAE");
    }

    //DIV
    public boolean isDivCommand(Token token) {
        if (token.getTokenType() != TokenType.Command) return false;
        return token.getValue().equalsIgnoreCase("DIV");
    }

    //INT 20H
    public boolean isInterruptCommand(Token token) {
        if (token.getTokenType() != TokenType.Command) return false;
        return token.getValue().equalsIgnoreCase("INT");
    }

    public boolean isLabel(Token token) {
        return token.getTokenType() == TokenType.Label;
    }
}
