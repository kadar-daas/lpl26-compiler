package compile.ast;

import compile.SymbolTable;

public class StmAssign extends Stm {
    public final String variableName;
    public final Exp rhsExpression;

    public StmAssign(String variableName, Exp rhsExpression) {
        this.variableName = variableName;
        this.rhsExpression = rhsExpression;
    }

    @Override
    public void compile(SymbolTable st) {
        Integer offset = st.resolveOffset(variableName);
        if (offset != null) {
            // local or block-scoped variable
            emit("get_fp");
            if (offset >= 0) {
                emit("push " + offset);
                emit("add");
            } else {
                emit("push " + (-offset));
                emit("sub");
            }
            rhsExpression.compile(st);
            emit("store");
        } else {
            // global variable
            rhsExpression.compile(st);
            emit("storei $" + variableName);
        }
    }
}