package compile.ast;

import compile.SymbolTable;
import java.util.List;

public class StmMethodCall extends Stm {
    public final String methodName;
    public final List<Exp> args;

    public StmMethodCall(String methodName, List<Exp> args) {
        this.methodName = methodName;
        this.args = List.copyOf(args);
    }

    @Override
    public void compile(SymbolTable st) {
        // push args in order
        for (Exp arg : args) {
            arg.compile(st);
        }
        // push arg count
        emit("push " + args.size());
        emit("calli $" + methodName.substring(1));
        // proc returns nothing but ret pops a return value
        emit("pop");
    }
}