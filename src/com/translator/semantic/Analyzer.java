package com.translator.semantic;

import com.translator.lexer.Token;
import com.translator.lexer.TokenParsingResult;
import com.translator.lexer.TokenType;
import com.translator.semantic.commands.CodeSegment;
import com.translator.semantic.commands.Command;
import com.translator.semantic.commands.div.DivCommand;
import com.translator.semantic.commands.intpt.InterruptCommand;
import com.translator.semantic.commands.jae.JaeCommand;
import com.translator.semantic.commands.mov.ImRegMoveCommand;
import com.translator.semantic.commands.test.ImDataAccTestCommand;
import com.translator.semantic.commands.test.RegMemRegisterTestCommand;
import com.translator.semantic.data.DataSegment;

import java.util.List;
import java.util.Optional;

public class Analyzer {
    AnalyzeResult result = new AnalyzeResult();
    CodeSegment currentSegment = result.codeSegment;

    public AnalyzeResult analyze(TokenParsingResult parsingResult) {
        List<Token> tokens = parsingResult.tokens;
        result.parsingResult = parsingResult;

        if(parsingResult.errors.size()>0)
        {
            return result;
        }

        for (int i = 0; i < tokens.size(); ) {
            Token token = tokens.get(i);
            if(i<tokens.size() && token.getTokenType()==TokenType.Name && tokens.get(i).getTokenType()==TokenType.Directive)
            {
                i=processDeclarations(tokens,i);
            }
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
                i = processJaeCommands(tokens, i, parsingResult.labels);
            }
            if (isInterruptCommand(token)) {
                i = processIntCommands(tokens, i);
            }
            i++;
        }

        return result;
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
                Optional<Integer> value = AnalyzerUtils.readDecHex(secondOperand.getValue());
                if(value.isPresent())
                {
                    int val = value.get();
                    Command command = new ImRegMoveCommand(firstOperand.getValue(), val, val > 256);
                    currentSegment.commands.add(command);
                }

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
        else
        {
            result.errors.add("Операнды не совпадают");
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

    private int processIntCommands(List<Token> tokens, int i) {
        //Если больше токенов нет, то выдаем ошибку
        if (i == tokens.size() - 1) {
            result.errors.add("Не хватает операндов!");
        }
        Token firstOperand = tokens.get(++i);
        if (firstOperand.getTokenType() == TokenType.Number) {
            Optional<Integer> value = AnalyzerUtils.readDecHex(firstOperand.getValue());
            if(!value.isPresent()){
                result.errors.add("Ошибочный операнд!");
                return i;
            }

            int type = value.get();
            if(type==0x20){
                Command command = new InterruptCommand(type);
                currentSegment.commands.add(command);
            }
            else
            {
                result.errors.add("Не поддерживаемый тип прерывания");
            }
        }else
        {
            result.errors.add("Операнды не совпадают");
        }
        return i;
    }

    private int processDeclarations(List<Token> tokens, int i) {
        if (i == tokens.size() - 1) {
            result.errors.add("Не хватает операндов!");
            return i;
        }
        Token token = tokens.get(i);
        if(isSegmentDirective(token)){
            DataSegment segment = new DataSegment();
        }
        return i;
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
        return token.getTokenType()==TokenType.Label;
    }


    //SEGMENT
    public boolean isSegmentDirective(Token token) {
        if (token.getTokenType() != TokenType.Directive) return false;
        return token.getValue().equalsIgnoreCase("SEGMENT");
    }
}
