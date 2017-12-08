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

        writeStringToFile(outputObjFile, analyzeResult.codeSegment.generateCode());
        writeStringToFile(outputListingFile, generateListing(analyzeResult));
    }

    private String generateListing(AnalyzeResult analyzeResult){
        StringBuilder builder = new StringBuilder();
        builder.append("almasuu - А.Рябцева (с) 2017\n");
        builder.append("\n\n\n");
        int i=0;
        for(String sourceLine: analyzeResult.parsingResult.sourceLines)
        {
            builder.append(String.format("%04X\t\t%s\n",i,sourceLine));
            i++;
        }
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
