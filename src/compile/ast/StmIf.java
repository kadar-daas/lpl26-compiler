package compile.ast;

import compile.SymbolTable;

public class StmIf extends Stm {
    public final Exp condition;
    public final Stm trueBranch;
    public final Stm falseBranch;

    public StmIf(Exp condition, Stm trueBranch, Stm falseBranch) {
        this.condition = condition;
        this.trueBranch = trueBranch;
        this.falseBranch = falseBranch;
    }

    @Override
    public void compile(SymbolTable st) {
        String elseLabel = st.freshLabel("if_else");
        String endLabel  = st.freshLabel("if_end");

        condition.compile(st);
        emit("jumpi_z " + elseLabel);
        trueBranch.compile(st);
        emit("jumpi " + endLabel);
        emit(elseLabel + ":");
        falseBranch.compile(st);
        emit(endLabel + ":");
    }
}