package com.translator.lexer;

import java.util.ArrayList;
import java.util.List;

/**
 * Строка токенов. Каждая строка входного файла превращается в набор(строку) токенов.
 */
public class TokenLine {
    public final int lineNumber;
    private List<Token> _tokens;

    private int currentTokenIndex=0;

    public TokenLine(int lineNumber){
        this.lineNumber = lineNumber;
        this._tokens = new ArrayList<>();
    }

    public List<Token> getTokens() {
        return _tokens;
    }

    public void addToken(Token token){
        token.tokenLine = this;
        this._tokens.add(token);
    }

    public boolean isEmpty(){
        return this._tokens.size()==0;
    }

    public boolean hasMoreTokens(){
        return currentTokenIndex<this._tokens.size();
    }

    public Token nextToken(){
        if(hasMoreTokens())
            return _tokens.get(currentTokenIndex++);
        else return null;
    }
}
