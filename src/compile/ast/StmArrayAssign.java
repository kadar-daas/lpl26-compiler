package compile.ast;

import compile.SymbolTable;
import java.util.List;

public class StmArrayAssign extends Stm {
    public final String arrayName;
    public final List<Exp> indices;
    public final Exp rhs;

    public StmArrayAssign(String arrayName, List<Exp> indices, Exp rhs) {
        this.arrayName = arrayName;
        this.indices = List.copyOf(indices);
        this.rhs = rhs;
    }

    @Override
    public void compile(SymbolTable st) {
        // Build up the array expression by chaining accesses
        Exp arrayExp = new ExpVar(arrayName);
        for (int i = 0; i < indices.size() - 1; i++) {
            arrayExp = new ExpArrayAccess(arrayExp, indices.get(i));
        }
        Exp indexExp = indices.get(indices.size() - 1);

        String nullLabel   = st.freshLabel("null_err");
        String boundsLabel = st.freshLabel("bounds_err");
        String okLabel = st.freshLabel("bounds_ok");

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
        // stack: [idx]
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