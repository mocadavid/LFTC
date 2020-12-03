package ro.ubb.grammar;

import java.util.*;

/**
 * Class which can generate the first and follow sets.
 */
public class Parser {
    private Map<String, Set<String>> firstSet;
    private Map<String, Set<String>> followSet;
    private Grammar grammar;
    private static Stack<List<String>> rules = new Stack<>();
    private ParseTable parseTable = new ParseTable();
    private Map<Pair<String, List<String>>, Integer> productionsNumbered = new HashMap<>();
    private Stack<String> alpha = new Stack<>();
    private Stack<String> beta = new Stack<>();
    private Stack<String> pi = new Stack<>();

    /**
     *Constructor
     */
    public Parser() {
        this.grammar = new Grammar();
        this.firstSet = new HashMap<>();
        this.followSet = new HashMap<>();
        generateSets();
        createParsingTable();
    }

    /**
     * getter
     */
    public Map<String, Set<String>> getFirstSet() {
        return firstSet;
    }

    /**
     * getter
     */
    public Map<String, Set<String>> getFollowSet() {
        return followSet;
    }

    /**
     * getter
     */
    public ParseTable getParseTable() {
        return parseTable;
    }

    /**
     * getter
     */
    public Stack<String> getPi() {
        return pi;
    }

    /**
     * getter
     */
    public Map<Pair<String, List<String>>, Integer> getProductionsNumbered() {
        return productionsNumbered;
    }

    /**
     * Initializing the first and follow sets
     */
    private void generateSets() {
        generateFirstSet();
        generateFollowSet();
    }

    /**
     * Generating first for every nonTerminal.
     */
    private void generateFirstSet() {
        for (String nonTerminal : grammar.getNonTerminals()) {
            firstSet.put(nonTerminal, this.firstOf(nonTerminal));
        }
    }

    /**
     *Generates the first set for the given nonTerminal.
     * @param nonTerminal: nonTerminal: String
     * @return The set of terminals for the given nonTerminal.
     */
    private Set<String> firstOf(String nonTerminal) {
        if (firstSet.containsKey(nonTerminal)){ return firstSet.get(nonTerminal); }

        Set<String> terminalsAccess = new HashSet<>();
        Set<String> terminals = grammar.getTerminals();

        for (Production production : grammar.getProductionsForNonTerminal(nonTerminal)){
            for (List<String> rule : production.getRules()) {
                String firstSymbol = rule.get(0);
                if (firstSymbol.equals("ε")){ terminalsAccess.add("ε"); }
                else if (terminals.contains(firstSymbol)){ terminalsAccess.add(firstSymbol); }
                else{ terminalsAccess.addAll(firstOf(firstSymbol)); }
            }
        }
        return terminalsAccess;
    }

    /**
     *Generating the follow set for all the nonTerminals.
     */
    private void generateFollowSet() {
        for (String nonTerminal : grammar.getNonTerminals()) {
            followSet.put(nonTerminal, this.followOf(nonTerminal, nonTerminal));
        }
    }

    /*
     * Analyses the productions in which the given nonTerminal is present and calls accordingly the follow operations with the needed values.
     * @param nonTerminal the given nonTerminal which we examine: String
     * @param initialNonTerminal the starting point for which we search for the follow set: String
     * @return the finalResult of the follow set: Set<String>
     */
    private Set<String> followOf(String nonTerminal, String initialNonTerminal) {
        if (followSet.containsKey(nonTerminal)) { return followSet.get(nonTerminal); }

        Set<String> finalResult = new HashSet<>();
        Set<String> terminals = grammar.getTerminals();

        if (nonTerminal.equals(grammar.getStartingSymbol())){ finalResult.add("$"); }

        for (Production production : grammar.getProductionsContainingNonterminal(nonTerminal)) {

            String productionStart = production.getStart();

            for (List<String> rule : production.getRules()){

                List<String> ruleConflict = new ArrayList<>();
                ruleConflict.add(nonTerminal);
                ruleConflict.addAll(rule);

                if (rule.contains(nonTerminal) && !rules.contains(ruleConflict)) {
                    rules.push(ruleConflict);
                    int indexNonTerminal = rule.indexOf(nonTerminal);
                    finalResult.addAll(followOperation(nonTerminal, finalResult, terminals, productionStart, rule, indexNonTerminal, initialNonTerminal));

                    List<String> sublist = rule.subList(indexNonTerminal + 1, rule.size());

                    if (sublist.contains(nonTerminal)){
                        finalResult.addAll(followOperation(nonTerminal, finalResult, terminals, productionStart, rule, indexNonTerminal + 1 + sublist.indexOf(nonTerminal), initialNonTerminal));
                    }
                    rules.pop();
                }
            }
        }

        return finalResult;
    }

    /**
     * Decides upon the case of the follow in which we are.
     * @param nonTerminal the nonTerminal for which we search follow: String
     * @param intermediaryResult the list in we save the found elements so far: Set<String>
     * @param terminals the terminals from the grammar: Set<String>
     * @param productionStart the starting nonTerminal of the production
     * @param rule the current production we analyse: String
     * @param indexNonTerminal the index of the nonTerminal: int
     * @param initialNonTerminal the given nonTerminal: String
     * @return the result of the follow operation for the given nonTerminal staring from the initialNonTerminal: Set<String>
     */
    private Set<String> followOperation(String nonTerminal, Set<String> intermediaryResult, Set<String> terminals, String productionStart, List<String> rule, int indexNonTerminal, String initialNonTerminal) {
        if (indexNonTerminal == rule.size() - 1) {
            if (productionStart.equals(nonTerminal)) { return intermediaryResult;  }
            if (!initialNonTerminal.equals(productionStart)){ intermediaryResult.addAll(followOf(productionStart, initialNonTerminal)); }
        }
        else{
            String nextSymbol = rule.get(indexNonTerminal + 1);
            if (terminals.contains(nextSymbol)){ intermediaryResult.add(nextSymbol); }
            else{
                if (!initialNonTerminal.equals(nextSymbol)) {
                    Set<String> fists = new HashSet<>(firstSet.get(nextSymbol));

                    if (fists.contains("ε")) {
                        intermediaryResult.addAll(followOf(nextSymbol, initialNonTerminal));
                        fists.remove("ε");
                    }
                    intermediaryResult.addAll(fists);
                }
            }
        }
        return intermediaryResult;
    }

    /**
     * Parses a sequence of the form: a list with symbols.
     * @param givenSequence The list with symbols: List<String>
     * @return true if the parsing succeeded, false otherwise
     */
    public boolean parse(List<String> givenSequence) {
        initParsing(givenSequence);
        boolean go = true;
        boolean result = true;

        while (go) {
            System.out.println("\n##########");
            System.out.println(beta);
            System.out.println("---------");
            System.out.println(alpha);
            //System.out.println(alpha);
            String betaHead = beta.peek();
            String alphaHead = alpha.peek();

            if (betaHead.equals("$") && alphaHead.equals("$")) {
                return result;
            }

            Pair<String, String> heads = new Pair<>(betaHead, alphaHead);
            Pair<List<String>, Integer> parseTableEntry = parseTable.get(heads);

            if (parseTableEntry == null) {
                heads = new Pair<>(betaHead, "ε");
                parseTableEntry = parseTable.get(heads);
                if (parseTableEntry != null) {
                    beta.pop();
                    continue;
                }

            }

            if (parseTableEntry == null) {
                go = false;
                result = false;
            } else {
                List<String> production = parseTableEntry.getKey();
                Integer productionPos = parseTableEntry.getValue();

                if (productionPos == -1 && production.get(0).equals("acc")) {
                    go = false;
                } else if (productionPos == -1 && production.get(0).equals("pop")) {
                    beta.pop();
                    alpha.pop();
                } else {
                    beta.pop();
                    if (!production.get(0).equals("ε")) {
                        addSymbolsToParsingStacks(production, beta);
                    }
                    pi.push(productionPos.toString());
                }
            }
        }

        return result;
    }

    /**
     * Creates the parsing table adding two pairs consisting of the index and column and the production with its number.
     */
    private void createParsingTable() {
        List<String> columnSymbols = new LinkedList<>(grammar.getTerminals());
        columnSymbols.add("$");
        numberingProductions();
        parseTable.put(new Pair<>("$", "$"), new Pair<>(Collections.singletonList("acc"), -1));

        for (String terminal: grammar.getTerminals())
            parseTable.put(new Pair<>(terminal, terminal), new Pair<>(Collections.singletonList("pop"), -1));

        productionsNumbered.forEach((key, value) -> {
            String rowSymbol = key.getKey();
            List<String> rule = key.getValue();
            Pair<List<String>, Integer> parseTableValue = new Pair<>(rule, value);

            for (String columnSymbol : columnSymbols) {
                Pair<String, String> parseTableKey = new Pair<>(rowSymbol, columnSymbol);
                if (rule.get(0).equals(columnSymbol) && !columnSymbol.equals("ε"))
                    parseTable.put(parseTableKey, parseTableValue);
                else if (grammar.getNonTerminals().contains(rule.get(0)) && firstSet.get(rule.get(0)).contains(columnSymbol)) {
                    if (!parseTable.containsKey(parseTableKey)) {
                        parseTable.put(parseTableKey, parseTableValue);
                    }
                } else {
                    if (rule.get(0).equals("ε")) {
                        for (String b : followSet.get(rowSymbol))
                            parseTable.put(new Pair<>(rowSymbol, b), parseTableValue);
                    } else {
                        Set<String> firsts = new HashSet<>();
                        for (String symbol : rule)
                            if (grammar.getNonTerminals().contains(symbol))
                                firsts.addAll(firstSet.get(symbol));
                        if (firsts.contains("ε")) {
                            for (String b : firstSet.get(rowSymbol)) {
                                if (b.equals("ε"))
                                    b = "$";
                                parseTableKey = new Pair<>(rowSymbol, b);
                                if (!parseTable.containsKey(parseTableKey)) {
                                    parseTable.put(parseTableKey, parseTableValue);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * Function used to index the productions in order
     */
    private void numberingProductions() {
        int index = 1;
        for (Production production: grammar.getProductions())
            for (List<String> rule: production.getRules())
                productionsNumbered.put(new Pair<>(production.getStart(), rule), index++);
    }

    /**
     * Used to set up the alpha, beta and pi stacks.
     * @param givenSequence The list with symbols to be parsed: List<String>
     */
    private void initParsing(List<String> givenSequence) {
        alpha.clear();
        alpha.push("$");
        addSymbolsToParsingStacks(givenSequence, alpha);
        beta.clear();
        beta.push("$");
        beta.push(grammar.getStartingSymbol());
        pi.clear();
        pi.push("ε");
    }

    /**
     * Used to add symbols to the stacks used in parsing.
     * @param parsingSequence The list with symbols to be parsed: List<String>
     * @param parsingStacks The stack where to add the symbol.
     */
    private void addSymbolsToParsingStacks(List<String> parsingSequence, Stack<String> parsingStacks) {
        for (int index = parsingSequence.size() - 1; index >= 0; index--) {
            parsingStacks.push(parsingSequence.get(index));
        }
    }
}