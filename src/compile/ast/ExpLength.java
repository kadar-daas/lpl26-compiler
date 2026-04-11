package compile.ast;

import compile.SymbolTable;

public class ExpLength extends Exp {
    public final Exp arrayExp;

    public ExpLength(Exp arrayExp) {
        this.arrayExp = arrayExp;
    }

    @Override
    public void compile(SymbolTable st) {
        String nullLabel = st.freshLabel("null_err");
        String okLabel   = st.freshLabel("null_ok");

        arrayExp.compile(st);
        emit("dup");
        emit("jumpi_z " + nullLabel);
        emit("load");
        emit("jumpi " + okLabel);

        emit(nullLabel + ":");
        emit("push 1");
        emit("halt");

        emit(okLabel + ":");
    }
}