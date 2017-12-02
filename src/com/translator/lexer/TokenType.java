package com.translator.lexer;

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
     * Токен - регистр косвенной адресации [BX], [BP]
     */
    IndirectAddress,

    /**
     * Другой тип токена
     */
    Other
}
