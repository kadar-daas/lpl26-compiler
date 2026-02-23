package compile.ast;

import compile.SymbolTable;

public class VarDecl extends AST {

    public final Type type;
    public final String name;

    public VarDecl(Type type, String name) {
        this.type = type;
        this.name = name;
    }

    public void compile(SymbolTable st) {
        st.declareVariable(this);
    }

}
