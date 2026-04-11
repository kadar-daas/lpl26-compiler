package compile.ast;

import compile.SymbolTable;

public class StmGenericArrayAssign extends Stm {
    public final Exp arrayExp;
    public final Exp indexExp;
    public final Exp rhs;

    public StmGenericArrayAssign(Exp arrayExp, Exp indexExp, Exp rhs) {
        this.arrayExp = arrayExp;
        this.indexExp = indexExp;
        this.rhs = rhs;
    }

    @Override
    public void compile(SymbolTable st) {
        String nullLabel   = st.freshLabel("null_err");
        String boundsLabel = st.freshLabel("bounds_err");
        String okLabel     = st.freshLabel("bounds_ok");

        // Null check
        arrayExp.compile(st);
        emit("dup");
        emit("jumpi_z " + nullLabel);
        emit("pop");

        // Evaluate index once
        indexExp.compile(st);

        // Check idx >= 0
        emit("dup");
        emit("jumpi_n " + boundsLabel);

        // Check idx < length
        emit("dup");
        arrayExp.compile(st);
        emit("load");
        emit("sub");
        emit("jumpi_n " + okLabel);

        emit(boundsLabel + ":");
        emit("push 2");
        emit("halt");

        emit(nullLabel + ":");
        emit("push 1");
        emit("halt");

        emit(okLabel + ":");
        emit("push 4");
        emit("mul");
        emit("push 4");
        emit("add");
        arrayExp.compile(st);
        emit("add");
        rhs.compile(st);
        emit("store");
    }
}