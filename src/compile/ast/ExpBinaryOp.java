package compile.ast;

import compile.SymbolTable;

public class ExpBinaryOp extends Exp {

    public final String operator;
    public final Exp left, right;

    public ExpBinaryOp(String operator, Exp left, Exp right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    @Override
    public void compile(SymbolTable st) {
        left.compile(st);
        right.compile(st);
        switch (operator) {
            case "*":
                emit("mul");
                return;
            case "-":
                emit("sub");
                return;
            case "+":
                emit("add");
                return;
            case "<":
                emit("sub", "test_n");
                return;
            case "==":
                emit("sub", "test_z");
                return;
            default:
                throw new IllegalStateException("Unrecognised binary operator: " + operator);
        }
    }

}
