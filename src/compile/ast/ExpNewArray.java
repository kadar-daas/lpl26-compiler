package compile.ast;

import compile.SymbolTable;

public class ExpNewArray extends Exp {
    public final Type elementType;
    public final Exp sizeExp;

    public ExpNewArray(Type elementType, Exp sizeExp) {
        this.elementType = elementType;
        this.sizeExp = sizeExp;
    }

    @Override
    public void compile(SymbolTable st) {
        String heapErrLabel = st.freshLabel("heap_err");
        String okLabel      = st.freshLabel("malloc_ok");

        // Evaluate size once
        sizeExp.compile(st);
        emit("dup");
        emit("push 1");
        emit("add");
        emit("push 4");
        emit("mul");
        emit("sysc CALLOC");

        // check heap exhaustion
        emit("dup");
        emit("jumpi_z " + heapErrLabel);
        emit("jumpi " + okLabel);
        emit(heapErrLabel + ":");
        emit("pop");
        emit("pop");
        emit("push 3");
        emit("halt");
        emit(okLabel + ":");
        emit("dup");
        emit("swap");
        emit("rot");
        emit("store");
    }
}