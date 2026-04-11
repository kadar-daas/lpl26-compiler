package compile.ast;

import compile.SymbolTable;
import java.util.List;

public class ExpNewRecord extends Exp {
    public final String recordName;
    public final List<Exp> args;

    public ExpNewRecord(String recordName, List<Exp> args) {
        this.recordName = recordName;
        this.args = List.copyOf(args);
    }

    @Override
    public void compile(SymbolTable st) {
        String heapErrLabel = st.freshLabel("heap_err");
        String okLabel = st.freshLabel("malloc_ok");

        int numFields = st.getRecordDef(recordName).fields.size();
        int size = numFields * 4;

        emit("push " + size);
        emit("sysc CALLOC");

        emit("dup");
        emit("jumpi_z " + heapErrLabel);
        emit("jumpi " + okLabel);

        emit(heapErrLabel + ":");
        emit("push 3");
        emit("halt");

        emit(okLabel + ":");

        for (int i = 0; i < args.size(); i++) {
            emit("dup");
            emit("push " + (i * 4));
            emit("add");
            args.get(i).compile(st);
            emit("store");
        }
        // ptr remains on stack as result
    }
}