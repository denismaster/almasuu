package com.parser.lexer;

/**
 * Тип считанного токена.
 */
public enum TokenType {
    /**
     * Токен - метка (start:)
     */
    Label,

    /**
     * Токен - директива (dw, db и так далее)
     */
    Directive,

    /**
     * Токен - имя переменной
     */
    Name,

    /**
     * Токен - число
     */
    Number,

    /**
     * Токен - команда (mov, div, jae, test, int)
     */
    Command,

    /**
     * Токен - регистр(AX,AL и так далее)
     */
    Register,


    /**
     * Другой тип токена
     */
    Other
}
