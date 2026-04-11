package compile.ast;

import compile.SymbolTable;

public class ExpVar extends Exp {
    public final String name;

    public ExpVar(String name) {
        this.name = name;
    }

    @Override
    public void compile(SymbolTable st) {
        Integer offset = st.resolveOffset(name);
        if (offset != null) {
            emit("get_fp");
            if (offset >= 0) {
                emit("push " + offset);
                emit("add");
            } else {
                emit("push " + (-offset));
                emit("sub");
            }
            emit("load");
        } else {
            emit("loadi $" + name);
        }
    }
}