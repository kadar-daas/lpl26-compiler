package compile.ast;

import compile.SymbolTable;
import java.util.List;

public class RecordDef extends AST {
    public final String name;
    public final List<VarDecl> fields;

    public RecordDef(String name, List<VarDecl> fields) {
        this.name = name;
        this.fields = List.copyOf(fields);
    }

    public void compile(SymbolTable st) {
        st.declareRecord(this);
    }
}