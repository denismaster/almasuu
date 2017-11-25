package com.translator.semantic;

import com.translator.lexer.Token;
import com.translator.lexer.TokenParsingResult;
import com.translator.lexer.TokenType;
import com.translator.semantic.commands.CodeSegment;
import com.translator.semantic.commands.Command;
import com.translator.semantic.commands.div.DivCommand;
import com.translator.semantic.commands.jae.JaeCommand;
import com.translator.semantic.commands.mov.ImRegMoveCommand;
import com.translator.semantic.commands.test.ImDataAccTestCommand;
import com.translator.semantic.commands.test.RegMemRegisterTestCommand;

import java.util.List;

public class Analyzer {
    AnalyzeResult result = new AnalyzeResult();
    CodeSegment currentSegment = result.codeSegment;
    public AnalyzeResult analyze(TokenParsingResult result) {
        List<Token> tokens = result.tokens;

        for (int i = 0; i < tokens.size(); ) {
            Token token = tokens.get(i);
            if (isLabel(token)) {
                String labelName = token.getValue();
                currentSegment.labelsOffsets.put(labelName, currentSegment.getSize());
            }
            if (isMoveCommand(token)) {
                i = processMovCommands(tokens, i);
            }
            if (isTestCommand(token)) {
                i = processTestCommands(tokens, i);
            }
            if (isDivCommand(token)) {
                i = processDivCommands(tokens, i);
            }
            if (isJaeCommand(token)) {
                i = processJaeCommands(tokens, i, result.labels);
            }
            i++;
        }

        return this.result;
    }

    private int processMovCommands(List<Token> tokens, int i) {
        //Если больше токенов нет, то выдаем ошибку
        if (i == tokens.size() - 1) {
            result.errors.add("Не хватает операндов!");
        }
        Token firstOperand = tokens.get(++i);
        if (firstOperand.getTokenType() == TokenType.Register) {
            if (i == tokens.size() - 1) {
                result.errors.add("Не хватает операндов!");
            }
            Token secondOperand = tokens.get(++i);
            if (secondOperand.getTokenType() == TokenType.Number) {
                int value = Integer.parseInt(secondOperand.getValue(), 16);
                Command command = new ImRegMoveCommand(firstOperand.getValue(), value, value > 256);
                currentSegment.commands.add(command);
            }
            if (secondOperand.getTokenType() == TokenType.Register) {

            }
        }
        return i;
    }

    private int processTestCommands(List<Token> tokens, int i) {
        //Если больше токенов нет, то выдаем ошибку
        if (i == tokens.size() - 1) {
            result.errors.add("Не хватает операндов!");
        }
        Token firstOperand = tokens.get(++i);
        if (firstOperand.getTokenType() == TokenType.Register) {
            if (i == tokens.size() - 1) {
                result.errors.add("Не хватает операндов!");
            }
            Token secondOperand = tokens.get(++i);

            if (secondOperand.getTokenType() == TokenType.Register) {
                boolean isWide = AnalyzerUtils.isWideRegister(firstOperand.getValue());
                Command command = new RegMemRegisterTestCommand(firstOperand.getValue(),
                        secondOperand.getValue(), isWide, ModeType.RegisterAddressing);
                currentSegment.commands.add(command);
            }
        }
        if (firstOperand.getTokenType() == TokenType.Number) {
            int value = Integer.parseInt(firstOperand.getValue(), 16);
            Command command = new ImDataAccTestCommand(value, value > 256);
            currentSegment.commands.add(command);
        }
        return i;
    }

    private int processDivCommands(List<Token> tokens, int i) {
        //Если больше токенов нет, то выдаем ошибку
        if (i == tokens.size() - 1) {
            result.errors.add("Не хватает операндов!");
        }
        Token firstOperand = tokens.get(++i);
        if (firstOperand.getTokenType() == TokenType.Register) {
            boolean isWide = AnalyzerUtils.isWideRegister(firstOperand.getValue());
            Command command = new DivCommand(isWide,
                    firstOperand.getValue(), ModeType.RegisterAddressing);
            currentSegment.commands.add(command);
        }else
        {
            result.errors.add("Операнды не совпадают");
        }
        return i;
    }

    private int processJaeCommands(List<Token> tokens, int i, List<String> labels) {
        //Если больше токенов нет, то выдаем ошибку
        if (i == tokens.size() - 1) {
            result.errors.add("Не хватает операндов!");
        }
        Token firstOperand = tokens.get(++i);
        if (firstOperand.getTokenType() == TokenType.Name) {
            if (!labels.contains(firstOperand.getValue()))
            {
                result.errors.add("Метка не определена");
            }
            String labelName = firstOperand.getValue();
            Command command = new JaeCommand(currentSegment.labelsOffsets.get(labelName));
            currentSegment.commands.add(command);
        }else
        {
            result.errors.add("Операнды не совпадают");
        }
        return i;
    }


    public boolean isMoveCommand(Token token) {
        if (token.getTokenType() != TokenType.Command) return false;
        return token.getValue().equalsIgnoreCase("MOV");
    }

    public boolean isTestCommand(Token token) {
        if (token.getTokenType() != TokenType.Command) return false;
        return token.getValue().equalsIgnoreCase("TEST");
    }

    public boolean isJaeCommand(Token token) {
        if (token.getTokenType() != TokenType.Command) return false;
        return token.getValue().equalsIgnoreCase("JAE");
    }

    public boolean isDivCommand(Token token) {
        if (token.getTokenType() != TokenType.Command) return false;
        return token.getValue().equalsIgnoreCase("DIV");
    }

    public boolean isLabel(Token token) {
        return token.getTokenType()==TokenType.Label;
    }
}
