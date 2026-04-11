package compile.ast;

import compile.SymbolTable;

public class ExpTernary extends Exp {
    public final Exp condition, thenExp, elseExp;

    public ExpTernary(Exp condition, Exp thenExp, Exp elseExp) {
        this.condition = condition;
        this.thenExp = thenExp;
        this.elseExp = elseExp;
    }

    @Override
    public void compile(SymbolTable st) {
        String elseLabel = st.freshLabel("ternary_else");
        String endLabel  = st.freshLabel("ternary_end");

        condition.compile(st);
        emit("jumpi_z " + elseLabel);
        thenExp.compile(st);
        emit("jumpi " + endLabel);
        emit(elseLabel + ":");
        elseExp.compile(st);
        emit(endLabel + ":");
    }
}