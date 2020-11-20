package ro.ubb.grammar;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * The grammar class.
 */
public class Grammar {

    private List<String> nonTerminals;
    private Set<String> terminals;
    private List<Production> productions;
    private String startingSymbol;

    public Grammar() {
        nonTerminals = new LinkedList<>();
        terminals = new HashSet<>();
        productions = new ArrayList<>();
        loadGrammar();
    }

    public List<String> getNonTerminals() { return nonTerminals; }
    public Set<String> getTerminals() { return terminals; }
    public List<Production> getProductions() { return productions; }
    public String getStartingSymbol() { return startingSymbol; }

    /**
     * Reads a grammar definition from a file.
     */
    private void loadGrammar() {
        try {
            BufferedReader bufferedReader;
            bufferedReader = new BufferedReader(new FileReader("src/ro/ubb/grammar/g1.txt"));

            int lineCount = 0;
            String line;
            line = bufferedReader.readLine();

            while (line != null) {

                if (lineCount <= 2){

                    String[] tokens = line.split(" ");

                    for (String token : tokens) {
                        switch (lineCount) {
                            case 0: {
                                nonTerminals.add(token);
                                break;
                            }
                            case 1: {
                                terminals.add(token);
                                break;
                            }
                            case 2: {
                                startingSymbol = token;
                                break;
                            }
                        }
                    }

                }else {
                    String[] tokens = line.split(" -> ");
                    List<List<String>> rules = new ArrayList<>();

                    for (String rule: tokens[1].split(" \\| "))
                        rules.add(Arrays.asList(rule.split(" ")));

                    productions.add(new Production(tokens[0], rules));
                }

                lineCount++;
                line = bufferedReader.readLine();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    /**
     * Finds all productions for a given nonterminal.
     * @param nonTerminal: String
     * @return The productions found: List<Production>
     */
    public List<Production> getProductionsForNonTerminal(String nonTerminal) {
        List<Production> productionsFound = new LinkedList<>();

        for (Production production : productions) {
            if (production.getStart().equals(nonTerminal)) {
                productionsFound.add(production);
            }
        }
        return productionsFound;
    }
}

