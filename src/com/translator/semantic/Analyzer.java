package com.translator.semantic;

import com.translator.lexer.Token;
import com.translator.lexer.TokenLine;
import com.translator.lexer.TokenParsingResult;
import com.translator.lexer.TokenType;
import com.translator.semantic.commands.mov.ImRegMoveCommand;

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
                ImRegMoveCommand command = new ImRegMoveCommand(firstOperand.getValue(), value, value > 256);
                System.out.print(command.generateCode() + " ");
            }
            if(secondOperand.getTokenType()==TokenType.Register){

            }
        }
        return i;
    }

    public boolean isMoveCommand(Token token){
        if(token.getTokenType()!= TokenType.Command) return false;
        return token.getValue().equalsIgnoreCase("MOV");
    }
}
