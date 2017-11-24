package com.translator.semantic.commands;

public interface CodeGeneratable {
    /**
     * Генерация кода
     * @return код ассемблера
     */
    String generateCode();

    /**
     * Вес сегмента или команды в байтах
     * @return число байт
     */
    int getSize();
}
