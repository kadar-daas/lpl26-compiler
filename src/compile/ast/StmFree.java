package compile.ast;

import compile.SymbolTable;

public class StmFree extends Stm {
    public final Exp exp;

    public StmFree(Exp exp) {
        this.exp = exp;
    }

    @Override
    public void compile(SymbolTable st) {
        exp.compile(st);
        emit("sysc FREE");
    }
}