package com.translator.writer;

import com.translator.semantic.AnalyzeResult;
import com.translator.semantic.commands.CodeSegment;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Date;

public class Writer {
    public void generateOutputFiles(String filename, AnalyzeResult analyzeResult)
    {
        //filename правильный, т.к проверен в начале. Создаем новые файлы
        String fileNameWithOutExt = removeExtension(filename);

        String outputListingFile = fileNameWithOutExt+".lst";
        String outputObjFile = fileNameWithOutExt+".obj";

        writeStringToFile(outputListingFile, generateListing(analyzeResult));
        if(!analyzeResult.hasErrors())
            writeStringToFile(outputObjFile, analyzeResult.getObjectCode());
    }

    private String generateListing(AnalyzeResult analyzeResult){
        StringBuilder builder = new StringBuilder();
        builder.append("almasuu - А.Рябцева (с) 2017\n");
        builder.append("Листинг трансляции\n");
        builder.append("\n");

        ///Обрабатываем и выводим ошибки парсинга
        if(analyzeResult.parsingResult.errors.size()>0)
        {
            builder.append("В ходе парсинга обнаружены ошибки:\n");
            for (String error :
                    analyzeResult.parsingResult.errors) {
                builder.append(error+"\n");
            }

            builder.append("\n");
        }

        ///Обрабатываем и выводим ошибки парсинга
        if(analyzeResult.hasErrors())
        {
            builder.append("В ходе трансляции обнаружены ошибки:\n");
            for (String error :
                    analyzeResult.errors) {
                builder.append(error+"\n");
            }

            builder.append("\n");
        }

        //выводим сгенерированный код
        builder.append(String.format("Line\t%8s\t%15s\n","Код","Исходный код"));
        builder.append(analyzeResult.getResults());
        return builder.toString();
    }

    private String removeExtension(String fileName){
        int pos = fileName.lastIndexOf(".");
        if (pos > 0) {
            return fileName.substring(0, pos);
        }
        return fileName;
    }

    private void writeStringToFile(String fileName, String data)
    {
        PrintWriter writer;
        try {
            writer = new PrintWriter(new FileOutputStream(fileName));
            writer.write(data);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
