package compile.ast;

import compile.SymbolTable;
import java.util.List;

public class StmFieldAssign extends Stm {
    public final Exp recordExp;   // the record expression
    public final String fieldName; // the field being assigned
    public final Exp rhs;

    public StmFieldAssign(Exp recordExp, String fieldName, Exp rhs) {
        this.recordExp = recordExp;
        this.fieldName = fieldName;
        this.rhs = rhs;
    }

    @Override
    public void compile(SymbolTable st) {
        String nullLabel = st.freshLabel("null_err");
        String okLabel   = st.freshLabel("field_ok");

        int offset = st.getFieldOffsetByName(fieldName);

        // compute address
        recordExp.compile(st);
        emit("dup");
        emit("jumpi_z " + nullLabel);
        emit("push " + offset);
        emit("add");
        rhs.compile(st);
        emit("store");
        emit("jumpi " + okLabel);
        emit(nullLabel + ":");
        emit("push 1");
        emit("halt");
        emit(okLabel + ":");
    }
}