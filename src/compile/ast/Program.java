package compile.ast;

import compile.SymbolTable;
import java.util.List;

public class Program extends AST {
    public final List<RecordDef> recordDefs;
    public final List<VarDecl> globals;
    public final List<Stm> body;
    public final List<MethodDef> methods;

    public Program(List<RecordDef> recordDefs, List<VarDecl> globals,
                   List<Stm> body, List<MethodDef> methods) {
        this.recordDefs = List.copyOf(recordDefs);
        this.globals = List.copyOf(globals);
        this.body = List.copyOf(body);
        this.methods = List.copyOf(methods);
    }

    public void compile() {
        SymbolTable st = new SymbolTable(this);

        for (RecordDef rd : recordDefs) {
            rd.compile(st);
        }

        st.enterMainBody();

        for (Stm stm : body) {
            stm.compile(st);
        }

        st.exitMainBody();

        emit("halt");

        for (MethodDef method : methods) {
            method.compile(st);
        }

        emit(".data");
        for (VarDecl global : st.getGlobals()) {
            emit("$" + global.name + ": 0");
        }
    }
}