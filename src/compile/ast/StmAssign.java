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
        rhsExpression.compile(st);
        emit("storei " + "$" + variableName);
    }
}
