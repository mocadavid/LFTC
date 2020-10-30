package ro.ubb;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class MyScanner {

    private SymbolTableWithHash<String,String> symbolTable;
    private Map<String,Integer> reservedWords = new HashMap<>();
    private Map<String, Integer> operators = new HashMap<>();
    private Map<String, Integer> separators = new HashMap<>();
    private Map<String, Integer> specialChars = new HashMap<>();
    private List<Pair> PIF = new LinkedList<>();

    public MyScanner(SymbolTableWithHash<String, String> symbolTable) {
        this.symbolTable = symbolTable;
        reservedWordsInit(reservedWords);
        operatorsInit(operators);
        specialCharsInit(specialChars);
        separatorsInit(separators);
    }

    public boolean scan(String filename) throws IOException {
        BufferedReader bufferedReader;
        bufferedReader = new BufferedReader(new FileReader(filename));

        boolean valid = true;
        boolean composed = false;
        boolean negative = false;
        boolean substraction = false;
        String lastToken = "";
        int lineNumber = 0;
        String lastSign = "";

        String line = bufferedReader.readLine();
        System.out.println("Tokens line by line:\n");
        String errorToken = "";
        while (line != null){
            if (!line.isBlank()){
                List<String> tokens = detect(line);


                System.out.println(tokens);

                for(String token: tokens){
                    if (token.equals(" ") || token.equals(""))
                        continue;

                    token = token.strip();

                    if(!composed){
                        if(reservedWords.containsKey(token)){
                            negative = false;
                            substraction = false;
                            lastSign ="";
                            PIF.add(new Pair(token,-1));
                        }
                        else if (separators.containsKey(token)){
                            negative = false;
                            substraction = false;
                            lastSign ="";
                            PIF.add(new Pair(token, -1));
                        }
                        else if (specialChars.containsKey(token)){
                            lastSign ="";
                            substraction = false;
                            negative = false;
                            PIF.add(new Pair(token, -1));
                        }
                        else if (Arrays.asList("+","-","*","/","%").contains(token)){
                            if (token.equals("-") && !substraction){
                                negative = true;
                            }
                            lastSign = token;
                            PIF.add(new Pair(token, -1));
                        }
                        else if (Arrays.asList("<",">","=").contains(token)){
                            substraction = false;
                            negative = false;
                            lastSign ="";
                            composed = true;
                            lastToken = token;
                        }
                        else if (isIdentifier(token) || isConstant(token,lastSign)){
                            if (negative){
                                PIF.remove(PIF.size()-1);
                                symbolTable.put("-"+token,"0");
                                int index = symbolTable.index(token);
                                PIF.add(new Pair("-"+token,index));
                                negative = false;
                                substraction = true;
                            }
                            else{
                                symbolTable.put(token,"0");
                                int index = symbolTable.index(token);
                                PIF.add(new Pair(token,index));
                                lastSign ="";
                                substraction = true;
                            }
                        }
                        else{
                            valid = false;
                            errorToken = token;
                            break;
                        }
                    }else{
                        if(reservedWords.containsKey(token)){
                            PIF.add(new Pair(lastToken, -1));
                            composed = false;
                            lastToken = "";
                            PIF.add(new Pair(token,-1));
                            lastSign ="";
                        }
                        else if(separators.containsKey(token)){
                            PIF.add(new Pair(lastToken, -1));
                            composed = false;
                            lastToken = "";
                            PIF.add(new Pair(token, -1));
                            lastSign ="";
                        }
                        else if (specialChars.containsKey(token)){
                            PIF.add(new Pair(lastToken, -1));
                            composed = false;
                            lastToken = "";
                            PIF.add(new Pair(token, -1));
                            lastSign ="";
                        }
                        else if (Arrays.asList("+","-","*","/","%").contains(token)){
                            lastSign =token;
                            PIF.add(new Pair(lastToken, -1));
                            composed = false;
                            lastToken = "";
                            PIF.add(new Pair(token, -1));
                            if (!substraction){
                                negative = true;
                            }
                        }
                        else if (Arrays.asList("<",">","=").contains(token)){
                            lastSign ="";
                            String composedToken = lastToken + token;
                            if (operators.containsKey(composedToken)){
                                PIF.add(new Pair(composedToken,-1));
                                lastToken = "";
                                composed = false;
                            }else{
                                lastSign ="";
                                PIF.add(new Pair(lastToken,-1));
                                PIF.add(new Pair(token, -1));
                            }
                        }
                        else if (isIdentifier(token) || isConstant(token, lastSign)){
                            PIF.add(new Pair(lastToken, -1));
                            composed = false;
                            lastToken = "";
                            symbolTable.put(token,"0");
                            int index = symbolTable.index(token);
                            PIF.add(new Pair(token,index));
                            substraction = true;
                        }
                        else{
                            valid = false;
                            errorToken = token;
                            break;
                        }
                    }

                }
            }
            lineNumber+=1;
            if (!valid) break;
            line = bufferedReader.readLine();
        }

        bufferedReader.close();
        if (valid){
            System.out.println("\nLexically correct!\n");
            System.out.println("Program Internal Form: pair by pair separated with comma.\n");
            System.out.println(PIF);
            System.out.println("\nSymbol Table: (HashTable with separate chaining)\n");
            symbolTable.keys().forEach(x->System.out.println(x+": "+symbolTable.index(x)));

        }else{
            if (errorToken.equals("0") && (lastSign.equals("+") || lastSign.equals("-"))){
                System.out.println("\nLexical error: line "+lineNumber + " at: "+ lastSign +errorToken);
                return valid;
            }
            System.out.println("\nLexical error: line "+lineNumber + " at: "+ errorToken);
        }
        return valid;
    }

    private List<String> detect(String line){
        List<String> tokens;
        tokens = Arrays.asList(line.split("((?<=;)|(?=;)|(?<= )|(?<=\\())|(?=\\()|(?<=\\))|(?=\\)|(?<=\\[))|(?=\\[)|(?<=])|(?=])|" +
                "(?<=#)|(?=#)|" +
                "(?<=\\+)|(?=\\+)|(?<=-)|(?=-)|(?<=/)|(?=/)|(?<=\\*)|(?=\\*)|(?<=%)|(?=%)|" +
                "(?<=<>)|(?=<>)|(?<=<=)|(?=<=)|(?<==>)|(?==>)|(?<=<)|(?=<)|(?<=>)|(?=>)|(?<==)|(?==)"));
        return tokens;
    }

    private boolean isIdentifier(String token){
        return token.matches("[a-zA-Z]+[0-9]*");
    }

    private boolean isConstant(String token, String lastSign){
        if (token.matches("'[a-zA-Z0-9]{1}'"))
            return true;
        else if (token.matches("\"[ a-zA-Z0-9!?-@,.;:+]+\""))
            return true;
        else{
            if (lastSign.equals("-") || lastSign.equals("+")){
                if (token.equals("0")){
                    return false;
                }
            }
            return token.matches("^([+-]?[1-9]\\d*|0)$");//[-]?[1-9]{1}[0-9]*|0
        }

    }

    private void operatorsInit(Map<String, Integer> operators){
        operators.put("+",2);
        operators.put("-",3);
        operators.put("*",4);
        operators.put("/",5);
        operators.put("=",6);
        operators.put("<",7);
        operators.put(">",8);
        operators.put("<=",9);
        operators.put("=>",10);
        operators.put("%",11);
        operators.put("<>",12);
    }
    private void separatorsInit(Map<String, Integer> separators){
        separators.put(";",13);
        separators.put("[",14);
        separators.put("]",15);
        separators.put("(",16);
        separators.put(")",17);
    }

    private void specialCharsInit(Map<String, Integer> specialChars){
        specialChars.put("#",18);
    }

    private void reservedWordsInit(Map<String,Integer> reservedWords){
        reservedWords.put("num",19);
        reservedWords.put("char",20);
        reservedWords.put("string",21);
        reservedWords.put("read",22);
        reservedWords.put("write",23);
        reservedWords.put("if",24);
        reservedWords.put("else",25);
        reservedWords.put("while",26);
        reservedWords.put("bool",27);
        reservedWords.put("false",28);
        reservedWords.put("true",29);
        reservedWords.put("is",30);
        reservedWords.put("and",31);
        reservedWords.put("or",32);
        reservedWords.put("not",33);
        reservedWords.put("endif",34);
        reservedWords.put("endwhile",35);
        reservedWords.put("endelse",36);
        reservedWords.put("program",37);
        reservedWords.put("endprogram",38);
    }

}
