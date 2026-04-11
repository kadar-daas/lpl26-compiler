package compile.ast;

import compile.SymbolTable;

public class StmPrintch extends Stm {
    public final Exp exp;

    public StmPrintch(Exp exp) {
        this.exp = exp;
    }

    @Override
    public void compile(SymbolTable st) {
        exp.compile(st);
        emit("sysc OUT_CHAR");
    }
}