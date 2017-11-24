package com.translator.writer;

import com.translator.semantic.commands.CodeSegment;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class Writer {
    public void generateOutputFiles(String filename, CodeSegment segment)
    {
        //filename правильный, т.к проверен в начале. Создаем новые файлы
        String fileNameWithOutExt = removeExtension(filename);

        String outputListingFile = fileNameWithOutExt+".lst";
        String outputObjFile = fileNameWithOutExt+".obj";

        writeStringToFile(outputObjFile, segment.generateCode());
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
