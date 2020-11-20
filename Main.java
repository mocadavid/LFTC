package ro.ubb;

public class Main {
    public static void main(String[] args) {
        SymbolTableWithHash<String,String> table = new SymbolTableWithHash<>();
        table.put("a","123");
        table.put("b","12");
        table.put("c","431");
        table.put("d","11");
        table.remove("b");
        System.out.println(table.get("a"));
        System.out.println(table.index("c"));
    }
}
