package com.translator.semantic;

import com.sun.org.apache.xpath.internal.operations.Mod;
import com.translator.lexer.Token;
import com.translator.lexer.TokenLine;
import com.translator.lexer.TokenParsingResult;
import com.translator.lexer.TokenType;
import com.translator.semantic.commands.Command;
import com.translator.semantic.commands.mov.ImRegMoveCommand;
import com.translator.semantic.commands.test.ImDataAccTestCommand;
import com.translator.semantic.commands.test.RegMemRegisterTestCommand;

public class Analyzer {

    public void analyze(TokenParsingResult result){
        System.out.println();
        System.out.println();
        for(TokenLine tokenLine: result.tokenLines){
            for(int i=0;i<tokenLine.getTokens().size();){
                Token token =  tokenLine.getTokens().get(i);
                if(isMoveCommand(token)){
                    i = processMovCommands(tokenLine, i);
                }
                if(isTestCommand(token)){
                    i = processTestCommands(tokenLine, i);
                }
                i++;
            }
        }
    }

    private int processMovCommands(TokenLine tokenLine, int i) {
        //Если больше токенов нет, то выдаем ошибку
        if(i==tokenLine.getTokens().size()-1){
            System.out.println("Не хватает операндов!");
        }
        Token firstOperand = tokenLine.getTokens().get(++i);
        if(firstOperand.getTokenType()== TokenType.Register){
            if(i==tokenLine.getTokens().size()-1){
                System.out.println("Не хватает операндов!");
            }
            Token secondOperand = tokenLine.getTokens().get(++i);
            if(secondOperand.getTokenType()==TokenType.Number) {
                int value = Integer.parseInt(secondOperand.getValue(), 16);
                Command command = new ImRegMoveCommand(firstOperand.getValue(), value, value > 256);
                System.out.print(command.generateCode() + " ");
            }
            if(secondOperand.getTokenType()==TokenType.Register){

            }
        }
        System.out.println();
        return i;
    }

    private int processTestCommands(TokenLine tokenLine, int i) {
        //Если больше токенов нет, то выдаем ошибку
        if(i==tokenLine.getTokens().size()-1){
            System.out.println("Не хватает операндов!");
        }
        Token firstOperand = tokenLine.getTokens().get(++i);
        if(firstOperand.getTokenType()== TokenType.Register){
            if(i==tokenLine.getTokens().size()-1){
                System.out.println("Не хватает операндов!");
            }
            Token secondOperand = tokenLine.getTokens().get(++i);

            if(secondOperand.getTokenType()==TokenType.Register){
                boolean isWide = AnalyzerUtils.isWideRegister(firstOperand.getValue());
                Command command = new RegMemRegisterTestCommand(firstOperand.getValue(),
                        secondOperand.getValue(),isWide, ModeType.RegisterAddressing );
                System.out.print(command.generateCode() + " ");
            }
        }
        if(firstOperand.getTokenType()==TokenType.Number) {
            int value = Integer.parseInt(firstOperand.getValue(), 16);
            Command command = new ImDataAccTestCommand(value, value > 256);
            System.out.print(command.generateCode() + " ");
        }
        System.out.println();
        return i;
    }

    public boolean isMoveCommand(Token token){
        if(token.getTokenType()!= TokenType.Command) return false;
        return token.getValue().equalsIgnoreCase("MOV");
    }

    public boolean isTestCommand(Token token){
        if(token.getTokenType()!= TokenType.Command) return false;
        return token.getValue().equalsIgnoreCase("TEST");
    }

    public boolean isJaeCommand(Token token){
        if(token.getTokenType()!= TokenType.Command) return false;
        return token.getValue().equalsIgnoreCase("JAE");
    }

    public boolean isDivCommand(Token token){
        if(token.getTokenType()!= TokenType.Command) return false;
        return token.getValue().equalsIgnoreCase("DIV");
    }
}
