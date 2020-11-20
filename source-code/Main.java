package ro.ubb;

import ro.ubb.FA.FA;
import ro.ubb.grammar.Grammar;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {

//        SymbolTableWithHash<String,String> table1 = new SymbolTableWithHash<>();
//        System.out.println("\nProgram1\n");
//        MyScanner myScanner1 = new MyScanner(table1);
//        try{ myScanner1.scan("src/ro/ubb/program1.txt"); }
//        catch (IOException e) { e.printStackTrace(); }
//
//        SymbolTableWithHash<String,String> table2 = new SymbolTableWithHash<>();
//        System.out.println("\nProgram2\n");
//        MyScanner myScanner2 = new MyScanner(table2);
//        try{ myScanner2.scan("src/ro/ubb/program2.txt"); }
//        catch (IOException e) { e.printStackTrace(); }
//
//        SymbolTableWithHash<String,String> table3 = new SymbolTableWithHash<>();
//        System.out.println("\nProgram3\n");
//        MyScanner myScanner3 = new MyScanner(table3);
//        try{ myScanner3.scan("src/ro/ubb/program3.txt"); }
//        catch (IOException e) { e.printStackTrace(); }
//
//        SymbolTableWithHash<String,String> tableE = new SymbolTableWithHash<>();
//        System.out.println("\nProgram Error\n");
//        MyScanner myScannerE = new MyScanner(tableE);
//        try{ myScannerE.scan("src/ro/ubb/program-e.txt"); }
//        catch (IOException e) { e.printStackTrace(); }
//
//        FA fa = new FA();
//        fa.start("src/ro/ubb/FA/FA.in");

        Grammar grammar = new Grammar();
        System.out.println(grammar.getNonTerminals());
        System.out.println(grammar.getTerminals());
        System.out.println(grammar.getProductions());
        System.out.println(grammar.getProductionsForNonTerminal("A"));
    }
}
