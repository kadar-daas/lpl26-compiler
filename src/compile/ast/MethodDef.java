package compile.ast;

import compile.SymbolTable;
import java.util.List;

public abstract class MethodDef extends AST {
    public final String name;
    public final List<VarDecl> formals;
    public final List<Stm> body;

    public MethodDef(String name, List<VarDecl> formals, List<Stm> body) {
        this.name = name;
        this.formals = List.copyOf(formals);
        this.body = List.copyOf(body);
    }

    public abstract void compile(SymbolTable st);
}