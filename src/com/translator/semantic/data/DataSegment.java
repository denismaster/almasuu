package com.translator.semantic.data;

import com.translator.semantic.Segment;
import com.translator.semantic.commands.CodeGeneratable;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataSegment extends Segment implements CodeGeneratable {
    public boolean isClosed = false;

    public Map<Integer, Declaration> declarations = new HashMap<>();

    @Override
    public String generateCode() {
        return "";
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public void closeSegment() {
        isClosed = true;
    }

    @Override
    public void add(int line, CodeGeneratable t) {
        if(t instanceof Declaration)
        {
            declarations.put(line,(Declaration)t);
        }
        if(t instanceof DataDeclaration)
        {
            variables.add((DataDeclaration)t);
        }
    }

    public List<DataDeclaration> variables = new ArrayList<>();

    public DataDeclaration findVariable(String name){
        for (DataDeclaration variable:variables) {
            if(variable.getName().equals(name))
                return variable;
        }
        return null;
    }

    public Boolean hasEndpointDeclaration(){
        return declarations.values().stream().anyMatch(decl->decl instanceof MountPointDeclaration);
    }
}
