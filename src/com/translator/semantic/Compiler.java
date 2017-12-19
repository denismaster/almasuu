package com.translator.semantic;

import java.util.*;
import java.io.*;

public class Compiler {

    private enum ErrorEnumeration {
        Success, DuplicateSymbol, InvalidNumber, IllegalInstruction, AlreadyOpened, AlreadyClosed, NotOpened, NotClosed, NotMatchEnds, NotMatchOperand, ExtraCharacters, UndefinedSymbol
    }

    enum OperandType {
        no, r8, i8, r16, m16, i16, d8, od8
    }

    private String nameOfAssemblingFile;
    private int currentAddress;
    private LinkedList<String> assemblingLog = new LinkedList<String>();
    private LinkedList<Card> objectCode = new LinkedList<Card>();
    private Segment segment = new Segment();
    private Map<Key, Command> commands = new HashMap<Key, Command>()

    {

        void add(String mnemo, OperandType op1, OperandType op2, int type,
                 int... code) {
            Byte[] data = new Byte[code.length];
            for (int i = 0; i < code.length; i++)
                data[i] = (byte) code[i];
            put(new Key(mnemo, op1, op2), new Command((byte) type, data));
        }

        {
            add("mov", OperandType.r8,  OperandType.r8,  1, 0x88, 0xC0);
            add("mov", OperandType.r16, OperandType.r16, 1, 0x89, 0xC0);
            add("mov", OperandType.m16, OperandType.r8,  2, 0x88, 0x80, 0x00, 0x00);
            add("mov", OperandType.m16, OperandType.r16, 2, 0x89, 0x80, 0x00, 0x00);
            add("mov", OperandType.r8,  OperandType.m16, 3, 0x8A, 0x80, 0x00, 0x00);
            add("mov", OperandType.r16, OperandType.m16, 3, 0x8B, 0x80, 0x00, 0x00);
            add("mov", OperandType.r8,  OperandType.i8,  4, 0xC6, 0xC0, 0x00);
            add("mov", OperandType.r16, OperandType.i8,  5, 0xC7, 0xC0, 0x00,0x00);
            add("mov", OperandType.r16, OperandType.i16, 5, 0xC7, 0xC0, 0x00,0x00);
            add("mov", OperandType.r16, OperandType.d8,  10, 0x8B, 0x06, 0x00,0x00);
            add("mov", OperandType.r8, OperandType.d8,  10, 0x8A, 0x06, 0x00,0x00);
            add("mov", OperandType.r16, OperandType.m16, 7, 0x8B, 0x38);
            add("mov", OperandType.r16, OperandType.od8,  11, 0xB8, 0x00,0x00);
            add("div" , OperandType.r8  , OperandType.no , 1, 0xF6, 0xF0);
            add("div" , OperandType.r16 , OperandType.no, 1, 0xF7, 0xF0);

            add("test",OperandType.r8,  OperandType.m16,  6, 0x84, 0x00);
            add("test",OperandType.r16, OperandType.m16,  7, 0x85, 0x00);

            add("jae",  OperandType.d8, OperandType.no,   6, 0x73, 0x00);
            add("int", OperandType.i8, OperandType.no,   8, 0xCD, 0x00);
        }
    };
    private List<String> directives = new ArrayList<String>() {
        {
            addAll(Arrays.asList("org", "db", "dw"));
        }
    };
    private LinkedList<String> error = new LinkedList<String>();
    public boolean has_error = false;
    Map<String, Label> symbolTable = new HashMap<String, Label>();
    private Map<ErrorEnumeration, String> errors = new HashMap<ErrorEnumeration, String>() {
        {
            put(ErrorEnumeration.DuplicateSymbol, "Метка '%s' уже определена");
            put(ErrorEnumeration.InvalidNumber, "Некорректное число");
            put(ErrorEnumeration.IllegalInstruction, "Неизвестная инструкция");
            put(ErrorEnumeration.AlreadyOpened, "Сегмент уже открыт");
            put(ErrorEnumeration.AlreadyClosed, "Сегмент уже закрыт");
            put(ErrorEnumeration.NotOpened, "Сегмент не открыт");
            put(ErrorEnumeration.NotClosed, "Сегмент не закрыт");
            put(ErrorEnumeration.NotMatchEnds, "Не совпадает с именем сегмента");
            put(ErrorEnumeration.NotMatchOperand, "Операнды не совпадают");
            put(ErrorEnumeration.ExtraCharacters, "Лишние символы в строке");
            put(ErrorEnumeration.UndefinedSymbol, "Метка не определена");

        }
    };
    private Map<String, Register> registers = new HashMap<String, Register>() {
        {
            String[] idents = { "ax", "cx", "dx", "bx", "sp", "bp", "si", "di",
                    "al", "cl", "dl", "bl", "ah", "ch", "dh", "bh" };
            for (int i = 0; i < idents.length; i++)
                put(idents[i], new Register(i % 8, i < 8 ? OperandType.r16
                        : OperandType.r8));
        }
    };
    private int lineNumber;

    public Compiler(String fileName) {
        this.nameOfAssemblingFile = fileName;
    }

    public void translate() throws IOException {
        analyzeSources();
        generateCode();
        writeObjectCode();
        writeListingFile();
    }

    private void analyzeSources() throws IOException {
        Scanner scanner = new Scanner(new File(nameOfAssemblingFile));
        while (scanner.hasNextLine()) {
            lineNumber++;
            ParsedLine pLine = parse(scanner.nextLine());
            for (Label symbol : pLine.symbols)
                if (!symbolTable.containsKey(symbol.name))
                    symbolTable.put(symbol.name, new Label(symbol.name,
                            currentAddress, symbol.type));
                else
                    error(errors.get(ErrorEnumeration.DuplicateSymbol)
                            + " (%d)", symbol.name, lineNumber);
            currentAddress += compile(pLine, false).size();
        }
        scanner.close();
    }

    private void generateCode() throws IOException {
        objectCode.add(new Card(0));
        Scanner scanner = new Scanner(new File(nameOfAssemblingFile));
        currentAddress = lineNumber = 0;
        while (scanner.hasNextLine()) {
            lineNumber++;
            String current = scanner.nextLine();
            ParsedLine pLine = parse(current);
            ErrorEnumeration key = check(pLine);
            if (key != ErrorEnumeration.Success)
                error(errors.get(key) + " (%d)", lineNumber);
            ArrayList<Byte> code = compile(pLine, true);
            objectCode.getLast().addAll(code);
            log(current, code);
            currentAddress += code.size();
        }
        scanner.close();
    }

    private void log(String message, ArrayList<Byte> code) {
        StringBuilder data = new StringBuilder();
        for (Byte value : code)
            data.append(String.format("%02X ", value));
        assemblingLog.add(String.format("%5d %s \t %20s %s", lineNumber,
                String.format("%04X", currentAddress), data, message));
    }

    private ErrorEnumeration check(ParsedLine pLine) {
        String mnemo = pLine.command;
        if (mnemo.isEmpty())
            return ErrorEnumeration.Success;
        if (commands.containsKey(new Key(mnemo)))
            return checkCommands(pLine);
        else if (directives.contains(mnemo))
            return checkDirectives(pLine);
        else
            return checkSegment(pLine);
    }

    private ErrorEnumeration checkDirectives(ParsedLine pLine) {
        if (pLine.operands.isEmpty())
            return ErrorEnumeration.IllegalInstruction;
        String mnemo = pLine.command;
        if (mnemo.equals("org")) {
            if (pLine.operands.size() > 1)
                return ErrorEnumeration.IllegalInstruction;
        } else {
            OperandType type = mnemo.equals("db") ? OperandType.i8
                    : OperandType.i16;
            for (Operand op : pLine.operands) {
                if (!(op.type == OperandType.i16 || op.type == OperandType.i8))
                    return ErrorEnumeration.InvalidNumber;
            }
        }
        return ErrorEnumeration.Success;
    }

    private ErrorEnumeration checkCommands(ParsedLine pLine) {
        if (pLine.operands.size() > 2)
            return ErrorEnumeration.ExtraCharacters;
        Operand op1 = new Operand(), op2 = new Operand();
        if (pLine.operands.size() > 0)
            op1 = pLine.operands.get(0);
        if (pLine.operands.size() > 1)
            op2 = pLine.operands.get(1);
        ErrorEnumeration value = checkOperand(op1);
        if (value != ErrorEnumeration.Success)
            return value;
        value = checkOperand(op2);
        if (value != ErrorEnumeration.Success)
            return value;
        if (!commands.containsKey(new Key(pLine.command, op1.type, op2.type))) {
            return ErrorEnumeration.NotMatchOperand;
        }
        return ErrorEnumeration.Success;
    }

    private ErrorEnumeration checkOperand(Operand op) {
        switch (op.type) {
            case m16:
                String line = op.ident.substring(1, op.ident.length() - 1).trim();
                String index = line.substring(0, 2);
                if(line.length()==2 && index.equalsIgnoreCase("bx"))
                {
                    line+="+0";
                }
                if (index.equalsIgnoreCase("bp"))
                    op.value = 6;
                else if (index.equalsIgnoreCase("bx"))
                    op.value = 7;
                else
                    return ErrorEnumeration.NotMatchOperand;
                try {
                    op.aux = Integer.parseInt(line.substring(2));
                } catch (Exception e) {
                    return ErrorEnumeration.NotMatchOperand;
                }
                break;
            case i16:
                try {
                    op.value = Integer.parseInt(op.ident);
                } catch (Exception e) {
                    return ErrorEnumeration.NotMatchOperand;
                }
                if (op.value < 256)
                    op.type = OperandType.i8;
                break;
            case r8:
            case r16:
                Register reg = registers.get(op.ident);
                op.value = reg.code;
                op.type = reg.type;
                break;
            case d8:
                Label symbol = symbolTable.get(op.ident);
                if (symbol == null)
                    return ErrorEnumeration.UndefinedSymbol;
                op.aux = 100+symbol.address;// - currentAddress;
                op.value = 100+symbol.address;//  - currentAddress;
                break;
            case od8:
                Label symb = symbolTable.get(op.ident);
                if (symb == null)
                    return ErrorEnumeration.UndefinedSymbol;
                op.aux = 100+symb.address;// - currentAddress;
                op.value = 100+symb.address;//  - currentAddress;
                break;
        }
        return ErrorEnumeration.Success;
    }

    private ErrorEnumeration checkSegment(ParsedLine pLine) {
        if (pLine.operands.size() > 1)
            return ErrorEnumeration.ExtraCharacters;
        String symbol = pLine.operands.isEmpty() ? pLine.command
                : pLine.operands.getFirst().ident;
        if (symbol.equals("segment")) {
            if (segment.opened)
                return ErrorEnumeration.AlreadyOpened;
            segment.name = pLine.command;
            segment.opened = true;
        } else if (symbol.equals("ends")) {
            if (segment.closed)
                return ErrorEnumeration.AlreadyClosed;
            if (!segment.opened)
                return ErrorEnumeration.NotOpened;
            if (!pLine.command.equals(segment.name))
                return ErrorEnumeration.NotMatchEnds;
            segment.closed = true;
        } else if (symbol.equals("end")) {
            if (!segment.closed) {
                return ErrorEnumeration.NotClosed;
            }
            segment.end = true;
        } else
            return ErrorEnumeration.IllegalInstruction;
        return ErrorEnumeration.Success;
    }

    private void writeObjectCode() throws IOException {
        if (error.size() > 0) {
            new File(changeExtension(nameOfAssemblingFile, ".obj")).delete();
            return;
        }
        DataOutputStream writer = null;
        try {
            writer = new DataOutputStream(new FileOutputStream(changeExtension(
                    nameOfAssemblingFile, ".obj")));
            int size = 0;
            for (Card card : objectCode)
                size += card.size();
            writer.write('H');
            writer.writeByte(segment.name.length());
            writer.writeBytes(segment.name);
            writer.writeByte(size & 0xFF);
            writer.writeByte(size >> 8);
            boolean flag = true;
            for (Card card : objectCode) {
                if (flag)
                    segment.entry = card.offset;
                if (card.size() == 0)
                    continue;
                flag = false;
                writer.writeByte('T');
                writer.writeByte(card.offset & 0xFF);
                writer.writeByte(card.offset >> 8);
                writer.writeByte(card.size() & 0xFF);
                writer.writeByte(card.size() >> 8);
                for (Byte data : card)
                    writer.write(data);
            }
            writer.writeByte('E');
            writer.writeByte(segment.entry & 0xFF);
            writer.writeByte(segment.entry >> 8);
        } finally {
            if (writer != null)
                writer.close();
        }
    }

    private void writeListingFile() throws IOException {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(changeExtension(nameOfAssemblingFile, ".lst"));
            writer.println("almasuu - А. Рябцева (с) 2017");
            writer.println("Листинг трансляции\n");
            for (String data : error)
                writer.println(data);
            writer.println(String.format("%5s %s \t  %14s \t %s","№","Адрес","Код","Исходный код"));
            for (String data : assemblingLog)
                writer.println(data);
            writer.println();
        } finally {
            if (writer != null)
                writer.close();
        }
    }

    private String changeExtension(String fileName, String extension) {
        int lastDot = fileName.lastIndexOf(".");
        if (lastDot != 1)
            return fileName.substring(0, lastDot) + extension;
        else
            return fileName + extension;
    }

    private ArrayList<Byte> compile(ParsedLine pLine, boolean assemble) {
        ArrayList<Byte> code = new ArrayList<Byte>();
        if (commands.containsKey(new Key(pLine.command)))
            compileCommand(pLine, code, assemble);
        else if (directives.contains(pLine.command))
            compileDirectives(pLine, code, assemble);
        return code;
    }

    private void compileCommand(ParsedLine pLine, ArrayList<Byte> code,
                                boolean assemble) {
        Operand op1 = new Operand(), op2 = new Operand();
        if (pLine.operands.size() > 0)
            op1 = pLine.operands.get(0);
        if (pLine.operands.size() > 1)
            op2 = pLine.operands.get(1);
        Command com = commands.get(new Key(pLine.command, op1.type, op2.type));
        if (com != null)
            if (assemble) {
                code.addAll(assemble(com, op1, op2));
            } else
                code.addAll(Arrays.asList(com.code));
    }

    private ArrayList<Byte> assemble(Command com, Operand operand1,
                                     Operand operand2) {
        int[] code = new int[com.code.length];
        for (int i = 0; i < code.length; i++)
            code[i] = com.code[i];
        Integer op1 = operand1.value;
        Integer op2 = operand2.value;
        switch (com.type) {
            case 1:
                code[1] = code[1] | (op2 << 3) | op1;
                break;
            case 2:
                code[1] |= op2 << 3 | op1;
                code[2] = operand1.aux & 0xFF;
                code[3] = operand1.aux >> 8;
                break;
            case 3:
                code[1] |= op1 << 3 | op2;
                code[2] = operand2.aux & 0xFF;
                code[3] = operand2.aux >> 8;
                break;
            case 4:
                code[1] |= op1;
                code[2] = op2 & 0xFF;
                break;
            case 5:
                code[1] |= op1;
                code[2] = op2 & 0xFF;
                code[3] = op2 >> 8;
                break;
            case 6:
                code[1] |= op1;
                break;
            case 7:
                code[1] |= op2;
                break;
            case 8:
                code[1] = op1;
                break;
            case 10:
            {
                code[1] |= op1;
                code[2] = operand2.aux & 0xFF;
                code[3] = operand2.aux >> 8;
                break;
            }
            case 11:
            {
                code[0] |= op1;
                operand2.aux+=segment.entry;
                code[1] = operand2.aux & 0xFF;
                code[2] = operand2.aux >> 8;
                break;
            }
        }
        ArrayList<Byte> data = new ArrayList<Byte>();
        for (int i = 0; i < code.length; i++)
            data.add((byte) code[i]);
        return data;
    }

    private ParsedLine parse(String line) {
        ParsedLine pLine = new ParsedLine();
        line = parseComments(line.toLowerCase()).trim();
        if (line.isEmpty())
            return pLine;
        Scanner parser = new Scanner(line);
        Stack<String> stack = new Stack<String>();
        while (parser.hasNext())
            stack.push(parser.next());
        Collections.reverse(stack);
        parseSymbols(stack, pLine);
        parseCommand(stack, pLine);
        parseOperands(stack, pLine);
        return pLine;
    }

    private void compileDirectives(ParsedLine pLine, ArrayList<Byte> code,
                                   boolean assemble) {
        if (pLine.operands.size() == 0)
            return;
        if (pLine.command.equals("org")) {
            currentAddress = Integer.parseInt(pLine.operands.get(0).ident);
            if (assemble)
                objectCode.add(new Card(currentAddress));
            return;
        }
        if (pLine.command.equals("db")) {
            for (Operand op : pLine.operands) {
                try {
                    int v = Integer.parseInt(op.ident);
                    if (v > 256)
                        throw new NumberFormatException();
                    code.add((byte) v);
                } catch (NumberFormatException e) {
                    if (assemble)
                        error(errors.get(ErrorEnumeration.InvalidNumber)
                                + " (%d)", lineNumber);
                }
            }
            return;
        }
        if (pLine.command.equals("dw")) {
            for (Operand op : pLine.operands) {
                try {
                    int v = Integer.parseInt(op.ident);
                    if (v > 65535)
                        throw new NumberFormatException();
                    code.add((byte) (v & 0xFF));
                    code.add((byte) (v >> 8));
                } catch (NumberFormatException e) {
                    if (assemble)
                        error(errors.get(ErrorEnumeration.InvalidNumber)
                                + " (%d)", lineNumber);
                }
            }
            return;
        }
        for (Operand op : pLine.operands) {
            if (op.type == OperandType.i16) {
                int v = Integer.parseInt(op.ident);
                code.add((byte) (v & 0xFF));
                code.add((byte) (v >> 8));
            }
            if (op.type == OperandType.i8)
                code.add(Byte.parseByte(op.ident));
        }
    }

    private void parseOperands(Stack<String> stack, ParsedLine pLine) {
        Boolean offset = false;
        while (!stack.isEmpty()) {
            String[] operands = stack.pop().split(",");
            for (String ident : operands) {
                Operand op = null;
                if (registers.containsKey(ident))
                    op = new Operand(ident, registers.get(ident).type);
                else if (ident.startsWith("["))
                    op = new Operand(ident, OperandType.m16);
                else if( ident.equalsIgnoreCase("offset")) {
                    offset = true; continue;
                } else if (isIntermediate(ident)) {
                    String numPart;
                    int value;
                    if(ident.endsWith("H")||ident.endsWith("h"))
                    {
                        numPart = ident.substring(0,ident.length()-1);
                        value = Integer.parseInt(numPart,16);
                    }
                    else
                    {
                        numPart = ident;
                        value = Integer.parseInt(numPart);
                    }
                    if (Math.abs(value) < 256)
                        op = new Operand(ident, OperandType.i8);
                    else
                        op = new Operand(ident, OperandType.i16);
                    op.value = value;
                } else {
                    if(offset) {
                        op = new Operand(ident, OperandType.od8);
                        op.isOffset = true;
                    } else
                        op = new Operand(ident, OperandType.d8);
                }
                pLine.operands.add(op);
            }
        }
    }

    private void parseSymbols(Stack<String> stack, ParsedLine pline) {
        while (!stack.isEmpty() && stack.peek().endsWith(":")) {
            String label = stack.pop();
            label = label.substring(0, label.length() - 1);
            pline.symbols.add(new Label(label, 0, "label"));
        }
        if (!stack.isEmpty()) {
            String value = stack.pop();
            if (!stack.isEmpty() && stack.peek().matches("db|dw"))
                pline.symbols.add(new Label(value, 0, "var"));
            else
                stack.push(value);
        }
    }

    private void parseCommand(Stack<String> stack, ParsedLine pLine) {
        pLine.command = !stack.isEmpty() ? stack.pop() : "";
    }

    private String parseComments(String line) {
        int pos = line.indexOf(";");
        return pos >= 0 ? line.substring(0, pos) : line;
    }

    private boolean isIntermediate(String ident) {
        try {
            String numPart;
            if(ident.endsWith("H")||ident.endsWith("h"))
            {
                numPart = ident.substring(0,ident.length()-1);
                Integer.parseInt(numPart,16);
            }
            else
            {
                numPart = ident;
                Integer.parseInt(numPart);
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void error(String message, Object... args) {
        error.add(String.format(message, args));
        has_error = true;
    }

    private class Segment {
        int entry;
        boolean opened, closed, end;
        String name = "";
    }

    private class Card extends ArrayList<Byte> {
        int offset;

        Card(int offset) {
            this.offset = offset;
        }
    }

    private class Command {
        byte type;
        Byte[] code;

        Command(byte type, Byte... code) {
            this.type = type;
            this.code = code;
        }
    }

    private class Register {
        OperandType type;
        int code;

        Register(int code, OperandType type) {
            this.code = code;
            this.type = type;
        }
    }

    private class Key {
        private String mnemo;
        private OperandType op1, op2;

        public Key(String mnemo) {
            this(mnemo, null, null);
        }

        public Key(String mnemo, OperandType op1, OperandType op2) {
            this.mnemo = mnemo;
            this.op1 = op1;
            this.op2 = op2;
        }

        @Override
        public boolean equals(Object o) {
            if (this.getClass() != o.getClass())
                return false;
            Key key = (Key) o;
            boolean value = false;
            if (mnemo.equals(key.mnemo))
                if (op1 == null || op2 == null)
                    value = true;
                else
                    value = key.op1 == op1 && key.op2 == op2;
            return value;
        }

        @Override
        public int hashCode() {
            return mnemo.hashCode();
        }
    }

    private class Label {
        String name;
        int address;
        String type;
        Label(String name, int adr, String type) {
            this.name = name;
            this.address = adr;
            this.type = type;
        }
    }

    private class ParsedLine {
        List<Label> symbols = new ArrayList<Label>();
        String command = "";
        LinkedList<Operand> operands = new LinkedList<Operand>();
    }

    private class Operand {
        String ident;
        int value, aux;
        boolean isOffset;
        OperandType type;

        Operand() {
            this("", OperandType.no);
        }

        Operand(String ident, OperandType type) {
            this.ident = ident;
            this.type = type;
        }
    }
}