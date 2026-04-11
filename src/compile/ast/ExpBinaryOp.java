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
            case "+":
                emit("add");
                return;
            case "-":
                emit("sub");
                return;
            case "*":
                emit("mul");
                return;
            case "/":
                emit("div");
                return;

            case "==":
                emit("sub", "test_z");
                return;

            case "<":
                emit("sub", "test_n");
                return;

            case ">":
                emit("swap", "sub", "test_n");
                return;

            case "<=":
                emit("swap", "sub", "test_n");
                emit("push 0", "swap", "sub", "test_z");
                return;

            case ">=":
                emit("sub", "test_n");
                emit("push 0", "swap", "sub", "test_z");
                return;

            case "&&":
                emit("test_z");
                emit("push 0", "swap", "sub", "test_z");
                emit("swap");
                emit("test_z");
                emit("push 0", "swap", "sub", "test_z");
                emit("mul");
                return;

            case "||":
                emit("add");
                emit("test_z");
                emit("push 0", "swap", "sub", "test_z");
                return;
            default:
                throw new IllegalStateException("Unrecognised binary operator: " + operator);
        }
    }
}