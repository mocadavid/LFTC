package ro.ubb;

import ro.ubb.FA.FA;
import ro.ubb.grammar.Grammar;
import ro.ubb.grammar.Pair;
import ro.ubb.grammar.Parser;
import ro.ubb.grammar.Production;
import ro.ubb.scanner.MyScanner;
import ro.ubb.scanner.SymbolTableWithHash;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Console {

    private Parser parser;
    private Grammar grammar;
    private FA fa;

    public Console() {
        this.parser = new Parser();
        this.grammar = new Grammar();
        fa = new FA();
    }

    private void showGrammarMenu(){
        System.out.println("0 - exit");
        System.out.println("1 - non terminals");
        System.out.println("2 - terminals");
        System.out.println("3 - productions");
        System.out.println("4 - producitons for non terminal");
    }

    private void showParserMenu(){
        System.out.println("0 - exit");
        System.out.println("1 - first set");
        System.out.println("2 - follow set");
        System.out.println("3 - parse table");
        System.out.println("4 - parse");
    }

    private void showMainMenu(){
        System.out.println("0 - exit");
        System.out.println("1 - parser");
        System.out.println("2 - grammar");
        System.out.println("3 - FA");
        System.out.println("4 - Scanner");
    }

    public void start() throws FileNotFoundException {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        int answer;
        while (!exit){
            showMainMenu();
            answer = Integer.parseInt(scanner.nextLine());
            switch (answer){
                case 0:
                    exit = true;
                    break;
                case 1:
                    startParser();
                    break;
                case 2:
                    startGrammar();
                    break;
                case 3:
                    fa.start("src/ro/ubb/FA/FA.in");
                    break;
                case 4:
                    startScanner();
                    break;
            }
        }
    }

    private void startParser() throws FileNotFoundException {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        int answer;
        PrintWriter out = new PrintWriter("src/ro/ubb/grammar/result.txt");
        while (!exit){
            showParserMenu();
            answer = Integer.parseInt(scanner.nextLine());
            switch (answer){
                case 0:
                    exit = true;
                    break;
                case 1:
                    System.out.println("First set:");
                    System.out.println(parser.getFirstSet());
                    break;
                case 2:
                    System.out.println("Follow set:");
                    System.out.println(parser.getFollowSet());
                    break;
                case 3:
                    System.out.println("Parse table:");
                    System.out.println(parser.getParseTable());
                    break;
                case 4:
                    System.out.println("Parse sequence:");
                    System.out.println("id + id\n");
                    //program IDENTIFIER ; num IDENTIFIER ; num IDENTIFIER ; IDENTIFIER = CONST ; IDENTIFIER = CONST ; num IDENTIFIER ; read ( IDENTIFIER ) ; endprogram ;
                    String sequence = scanner.nextLine().strip();
                    List<String> list = new LinkedList<>(Arrays.asList(sequence.split(" ")));
                    boolean result = parser.parse(list);
                    if (result) {
                        System.out.println("The sequence was accepted.");
                        Stack<String> productionsStack = parser.getPi();
                        System.out.println(productionsStack);
                        String resultString = displayPiProductions(productionsStack,parser);
                        System.out.println(resultString);
                        out.println(resultString);
                        String resultDerivations = displayDerivationString(productionsStack,parser);
                        System.out.println(resultDerivations);
                        out.println(resultDerivations);
                        out.flush();
                        out.close();


                    } else {
                        System.out.println("The sequence is not accepted.");
                    }
                    break;
            }
        }
    }

    /**
     * Wrapper function for displaying derivation strings
     * @param pi: The stack with the order of derivations: Stack<String>
     * @param parser the parser which parsed: Parser
     * @return The content to be displayed as derivation Strings: String
     */
    private String displayDerivationString(Stack<String> pi, Parser parser){
        List<Integer> valuesList = new LinkedList<>();
        List<Pair<String,List<String>>> keysList = new LinkedList<>();
        List<List<String>> orderList = new LinkedList<>();
        List<String> replace = new LinkedList<>();

        for (String productionIndexString : pi) {
            if (productionIndexString.equals("ε")) { continue; }

            Integer productionIndex = Integer.parseInt(productionIndexString);
            parser.getProductionsNumbered().forEach((key, value) ->{
                if (productionIndex.equals(value)){
                    valuesList.add(value);
                    keysList.add(key);
                }
            });
        }

        for (int index=0; index< valuesList.size();index++){
            orderList.add(keysList.get(index).getValue());
            replace.add(keysList.get(index).getKey());
        }

//        System.out.println(valuesList);
//        System.out.println(keysList);
//        System.out.println(orderList);
        return buildDerivations(replace,orderList);
    }

    /**
     * Builds the derivations string based on the productions and productions order in the pi stack.
     * @param replaceNonTerminal nonTerminals which need to be replaced in order given by pi: List<String>
     * @param productionReplacement the production which needs to take place in the order given by pi: List<List<String>>
     * @return The content to be displayed as derivation Strings: String
     */
    private String buildDerivations(List<String> replaceNonTerminal, List<List<String>> productionReplacement){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(replaceNonTerminal.get(0));
        List<String> result = new LinkedList<>();
        result.add(replaceNonTerminal.get(0));

        int i = 0, j = 0;

        while(i<replaceNonTerminal.size()){
            result.remove("ε");
            stringBuilder.append("->");
            while (j< result.size()){
                if (result.get(j).equals(replaceNonTerminal.get(i))) {
                    result.remove(j);
                    result.addAll(j,productionReplacement.get(i));
                    System.out.println(result);
                    result.remove("ε");
                    stringBuilder.append(result);
                    break;
                }
                j++;
            }
            i++;
            result.remove("ε");
        }
        return stringBuilder.toString();
    }

    /**
     * Displays productions string.
     * @param pi: The stack with the order of derivations: Stack<String>
     * @param parser the parser which parsed: Parser
     * @return String as productions string.
     */
    private String displayPiProductions(Stack<String> pi, Parser parser) {
        StringBuilder stringBuilder = new StringBuilder();

        for (String productionIndexString : pi) {
            if (productionIndexString.equals("ε")) { continue; }

            Integer productionIndex = Integer.parseInt(productionIndexString);
            parser.getProductionsNumbered().forEach((key, value) ->{
                if (productionIndex.equals(value))
                    stringBuilder.append(value).append(": ").append(key.getKey()).append(" -> ").append(key.getValue()).append("\n");
            });
        }
        return stringBuilder.toString();
    }

    private void startGrammar(){
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        int answer;
        while (!exit){
            showGrammarMenu();
            answer = Integer.parseInt(scanner.nextLine());
            switch (answer) {
                case 0:
                    exit = true;
                    break;
                case 1:
                    System.out.println(grammar.getNonTerminals());
                    break;
                case 2:
                    System.out.println(grammar.getTerminals());
                    break;
                case 3:
                    System.out.println(grammar.getProductions());
                    break;
                case 4:
                    System.out.println("Input non terminal.");
                    String nonTerminal = scanner.nextLine().strip();
                    System.out.println(grammar.getProductionsForNonTerminal(nonTerminal));
                    break;
            }
        }
    }

    private void startScanner(){
        SymbolTableWithHash<String,String> table1 = new SymbolTableWithHash<>();
        System.out.println("\nProgram1\n");
        MyScanner myScanner1 = new MyScanner(table1);
        try{ myScanner1.scan("src/ro/ubb/scanner/program1.txt"); }
        catch (IOException e) { e.printStackTrace(); }

        SymbolTableWithHash<String,String> table2 = new SymbolTableWithHash<>();
        System.out.println("\nProgram2\n");
        MyScanner myScanner2 = new MyScanner(table2);
        try{ myScanner2.scan("src/ro/ubb/scanner/program2.txt"); }
        catch (IOException e) { e.printStackTrace(); }

        SymbolTableWithHash<String,String> table3 = new SymbolTableWithHash<>();
        System.out.println("\nProgram3\n");
        MyScanner myScanner3 = new MyScanner(table3);
        try{ myScanner3.scan("src/ro/ubb/scanner/program3.txt"); }
        catch (IOException e) { e.printStackTrace(); }

        SymbolTableWithHash<String,String> tableE = new SymbolTableWithHash<>();
        System.out.println("\nProgram Error\n");
        MyScanner myScannerE = new MyScanner(tableE);
        try{ myScannerE.scan("src/ro/ubb/scanner/program-e.txt"); }
        catch (IOException e) { e.printStackTrace(); }
    }
}
