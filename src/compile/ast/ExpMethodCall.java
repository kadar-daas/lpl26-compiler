package compile.ast;

import compile.SymbolTable;
import java.util.List;

public class ExpMethodCall extends Exp {
    public final String methodName;
    public final List<Exp> args;

    public ExpMethodCall(String methodName, List<Exp> args) {
        this.methodName = methodName;
        this.args = List.copyOf(args);
    }

    @Override
    public void compile(SymbolTable st) {
        for (Exp arg : args) {
            arg.compile(st);
        }
        // push arg count
        emit("push " + args.size());
        emit("calli $" + methodName.substring(1));
        // return value is left on opstack by ret
    }
}