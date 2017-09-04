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
     * Токен - команда (mov, div, jae, test, int)
     */
    Command,


    /**
     * Другой тип токена
     */
    Other
}
