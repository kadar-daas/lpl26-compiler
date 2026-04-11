package compile.ast;

import compile.SymbolTable;
import java.util.List;

public class FunDef extends MethodDef {
    public final Type returnType;
    public final Exp returnExp;
    public final List<VarDecl> locals;

    public FunDef(Type returnType, String name, List<VarDecl> formals,
                  List<VarDecl> locals, List<Stm> body, Exp returnExp) {
        super(name, formals, body);
        this.returnType = returnType;
        this.returnExp = returnExp;
        this.locals = List.copyOf(locals);
    }

    @Override
    public void compile(SymbolTable st) {
        String label = "$" + name.substring(1);
        emit(label + ":");
        st.enterMethod(this);

        for (Stm stm : body) {
            stm.compile(st);
        }

        // compute return value
        returnExp.compile(st);

        // free method's initial scope locals
        int topLocals = st.exitBlock();
        if (topLocals > 0) {
            emit("sfree " + topLocals);
        }

        emit("push " + formals.size());
        emit("ret");

        st.exitMethod();
    }
}