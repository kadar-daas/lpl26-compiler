package compile.ast;

import compile.SymbolTable;

public class ExpNot extends Exp {
    public final Exp exp;

    public ExpNot(Exp exp) {
        this.exp = exp;
    }

    @Override
    public void compile(SymbolTable st) {
        exp.compile(st);
        emit("test_z");
    }
}