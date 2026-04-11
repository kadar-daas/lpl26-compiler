package compile.ast;

import compile.SymbolTable;

public class StmVarDecl extends Stm {
    public final VarDecl decl;

    public StmVarDecl(VarDecl decl) {
        this.decl = decl;
    }

    @Override
    public void compile(SymbolTable st) {
        decl.compile(st);
    }
}