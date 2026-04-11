package compile.ast;

import compile.SymbolTable;

public class ExpFieldAccess extends Exp {
    public final Exp recordExp;
    public final String fieldName;

    public ExpFieldAccess(Exp recordExp, String fieldName) {
        this.recordExp = recordExp;
        this.fieldName = fieldName;
    }

    @Override
    public void compile(SymbolTable st) {
        String nullLabel = st.freshLabel("null_err");
        String okLabel = st.freshLabel("field_ok");

        int offset = st.getFieldOffsetByName(fieldName);

        recordExp.compile(st);
        emit("dup");
        emit("jumpi_z " + nullLabel);
        emit("push " + offset);
        emit("add");
        emit("load");
        emit("jumpi " + okLabel);
        emit(nullLabel + ":");
        emit("push 1");
        emit("halt");
        emit(okLabel + ":");
    }
}