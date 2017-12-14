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
import com.translator.semantic.commands.mov.MemoryToRegisterMovCommand;
import com.translator.semantic.commands.mov.RegMemRegMoveCommand;
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
                Variable variable = result.parsingResult.getVariableByName(variableName);
                if(variable==null){
                    result.errors.add("Неизвестная переменная. Строка "+ currentLineNumber);
                    return;
                }
                boolean isWide = AnalyzerUtils.isWideRegister(firstOperand.getValue());
                Command command = new MemoryToRegisterMovCommand(variable.address,firstOperand.getValue()
                        ,isWide,ModeType.RegisterIndirect);
                currentSegment.add(currentLineNumber, command);
            }
            else
            if (secondOperand.getTokenType() == TokenType.Register) {
                boolean isWide = AnalyzerUtils.isWideRegister(firstOperand.getValue());
                Command command = new RegMemRegMoveCommand(firstOperand.getValue(),
                        secondOperand.getValue(), isWide, ModeType.RegisterAddressing);
                currentSegment.add(currentLineNumber, command);
            }
            else
            {
                result.errors.add("Операнды не совпадают. Строка "+currentLineNumber);
            }
        }
        if(line.hasMoreTokens())
        {
            result.errors.add("Операнды не совпадают. Строка "+currentLineNumber);
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

            if (secondOperand.getTokenType() == TokenType.IndirectAddress && !line.hasMoreTokens()) {
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
            if(line.hasMoreTokens())
            {
                result.errors.add("Операнды не совпадают. Строка "+currentLineNumber);
                return;
            }
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
        if (firstOperand.getTokenType() == TokenType.Name && !line.hasMoreTokens()) {
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
        if (firstOperand.getTokenType() == TokenType.Number && !line.hasMoreTokens()) {
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
