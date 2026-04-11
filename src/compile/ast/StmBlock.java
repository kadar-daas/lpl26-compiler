package compile.ast;

import compile.SymbolTable;
import java.util.List;

public class StmBlock extends Stm {
    public final List<Stm> stms;

    public StmBlock(List<Stm> stms) {
        this.stms = List.copyOf(stms);
    }

    @Override
    public void compile(SymbolTable st) {
        st.enterBlock();
        for (Stm stm : stms) {
            stm.compile(st);
        }
        int localCount = st.exitBlock();
        if (localCount > 0) {
            emit("sfree " + localCount);
        }
    }
}