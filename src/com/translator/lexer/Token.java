package com.translator.lexer;

/**
 * Токен. Кусок входной строки, имеющий лексическое значение. При трансляции на первом проходе мы получаем набор этих токенов.
 * При синтаксическом анализе оперируем не текстовыми данными сырыми из файла, а набором токенов.
 */
public class Token {
    private TokenType _tokenType; // тип токена
    private String _value; // значение токена

    //Конструктор токена
    public Token(TokenType type, String value){
        this._tokenType = type;
        this._value = value;
    }

    public TokenType getTokenType(){
        return this._tokenType;
    }

    public void setTokenType(TokenType type){
        this._tokenType = type;
    }

    public String getValue(){
        return this._value;
    }

    public void setValue(String newValue){
        this._value = newValue;
    }
}
