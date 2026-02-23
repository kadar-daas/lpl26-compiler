package compile.ast;

import compile.SymbolTable;

import java.util.List;

public class Program extends AST {

    public final List<VarDecl> globals;
    public final List<Stm> body;

    /**
     * Initialise a new Program AST.
     * @param globals the global variable declarations
     * @param body the statements in the main body of the program
     */
    public Program(List<VarDecl> globals, List<Stm> body) {
        this.globals = List.copyOf(globals);
        this.body = List.copyOf(body);
    }

    /**
     * Emit SSM assembly code for this program.
     */
    public void compile() {
        SymbolTable st = new SymbolTable(this);

        for (VarDecl global: globals) {
            global.compile(st);
        }
        for(Stm stm: body) {
            stm.compile(st);
        }
        emit("halt");

        emit(".data");
        for (VarDecl global: st.getGlobals()) {
            emit("$" + global.name + ": 0");
        }
    }

}
