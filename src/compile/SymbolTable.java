package compile;

import compile.ast.*;

import java.util.*;

public class SymbolTable {

    private List<VarDecl> globals;

    private int freshNameCounter;

    /**
     * Initialise a new symbol table.
     *
     * @param program the program
     */
    public SymbolTable(Program program) {
        this.freshNameCounter = 0;
        this.globals = new LinkedList<>();
    }

    /**
     * The list of global variables declared so far.
     * @return the current list of global variable declarations
     */
    public List<VarDecl> getGlobals() {
        return List.copyOf(globals);
    }

    /**
     * Declare a new variable.
     * @param decl the new variable declaration
     */
    public void declareVariable(VarDecl decl) {
        declareGlobal(decl);
    }

    /**
     * Declare a new global variable.
     * @param decl the new global variable declaration
     */
    private void declareGlobal(VarDecl decl) {
        globals.add(decl);
    }


    /**
     * Transform an LPL26 identifier into an SSM label.
     *
     * @param id the LPL26 identifier
     * @return id prefixed with "$"
     */
    public static String makeIdLabel(String id) {
        return "$" + id;
    }

    /**
     * Each call to this method will return a fresh label which is
     * guaranteed not to clash with any name returned by makeIdLabel(x),
     * where x is any LPL26 identifier.
     *
     * @param tag a string to include as part of the generated name.
     * @return a fresh name which is prefixed with "$$".
     */
    public String freshLabel(String tag) {
        return "$$" + tag + "_" + (freshNameCounter++);
    }
}
