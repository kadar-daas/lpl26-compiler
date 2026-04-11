package compile.ast;

import compile.SymbolTable;
import java.util.List;

public class ProcDef extends MethodDef {
    public final List<VarDecl> locals;

    public ProcDef(String name, List<VarDecl> formals,
                   List<VarDecl> locals, List<Stm> body) {
        super(name, formals, body);
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

        int topLocals = st.exitBlock();
        if (topLocals > 0) {
            emit("sfree " + topLocals);
        }

        emit("push 0");
        emit("push " + formals.size());
        emit("ret");

        st.exitMethod();
    }
}