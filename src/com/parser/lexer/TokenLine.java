package com.parser.lexer;

import java.util.ArrayList;
import java.util.List;

/**
 * Строка токенов. Каждая строка входного файла превращается в набор(строку) токенов.
 */
public class TokenLine {
    private List<Token> _tokens;

    public TokenLine(){
        this._tokens = new ArrayList<>();
    }

    public List<Token> getTokens() {
        return _tokens;
    }

    public void addToken(Token token){
        this._tokens.add(token);
    }
}
