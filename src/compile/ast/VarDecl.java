package compile.ast;

import compile.SymbolTable;

public class VarDecl extends AST {
    public final Type type;
    public final String name;
    public final Exp initialiser;

    public VarDecl(Type type, String name, Exp initialiser) {
        this.type = type;
        this.name = name;
        this.initialiser = initialiser;
    }

    public void compile(SymbolTable st) {
        st.declareVariable(this);
        Integer offset = st.resolveOffset(name);
        if (offset != null) {
            // stack-allocated
            emit("salloc 1");
            if (initialiser != null) {
                emit("get_fp");
                if (offset >= 0) {
                    emit("push " + offset);
                    emit("add");
                } else {
                    emit("push " + (-offset));
                    emit("sub");
                }
                initialiser.compile(st);
                emit("store");
            }
        } else {
            // global
            if (initialiser != null) {
                initialiser.compile(st);
                emit("storei $" + name);
            }
        }
    }
}