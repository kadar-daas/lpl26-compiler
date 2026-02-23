package compile.ast;

import compile.SymbolTable;

public class ExpVar extends Exp {
    public final String name;

    public ExpVar(String name) {
        this.name = name;
    }

    @Override
    public void compile(SymbolTable st) {
        emit("loadi " + "$" + name);
    }

}
