package compile;

import compile.ast.*;
import java.util.*;

public class SymbolTable {

    private List<VarDecl> globals;
    private Deque<List<VarDecl>> localScopes;
    private List<VarDecl> params;
    private MethodDef currentMethod;
    private int freshNameCounter;
    private Map<String, RecordDef> records;
    private int blockDepth;
    private boolean inMainBody;

    public SymbolTable(Program program) {
        this.freshNameCounter = 0;
        this.globals = new LinkedList<>();
        this.localScopes = null;
        this.params = null;
        this.currentMethod = null;
        this.records = new LinkedHashMap<>();
        this.blockDepth = 0;
        this.inMainBody = false;
    }

    public void declareRecord(RecordDef rd) { records.put(rd.name, rd); }

    public RecordDef getRecordDef(String name) {
        RecordDef rd = records.get(name);
        if (rd == null) throw new RuntimeException("Unknown record type: " + name);
        return rd;
    }

    public int getFieldOffsetByName(String fieldName) {
        for (RecordDef rd : records.values()) {
            for (int i = 0; i < rd.fields.size(); i++) {
                if (rd.fields.get(i).name.equals(fieldName)) return i * 4;
            }
        }
        throw new RuntimeException("Field not found: " + fieldName);
    }

    public List<VarDecl> getGlobals() { return List.copyOf(globals); }

    public void enterMainBody() {
        this.inMainBody = true;
        this.blockDepth = 0;
        this.localScopes = null;
    }

    public void exitMainBody() {
        this.inMainBody = false;
        this.localScopes = null;
        this.blockDepth = 0;
    }

    public boolean isInMainBody() { return inMainBody; }
    public int getBlockDepth() { return blockDepth; }

    public void declareVariable(VarDecl decl) {
        boolean useStack = currentMethod != null ||
                (inMainBody && blockDepth > 0);
        if (useStack) {
            if (localScopes == null) localScopes = new ArrayDeque<>();
            if (localScopes.isEmpty()) {
                localScopes.push(new LinkedList<>());
            }
            localScopes.peek().add(decl);
        } else {
            globals.add(decl);
        }
    }

    public void enterMethod(MethodDef method) {
        this.currentMethod = method;
        this.params = new LinkedList<>(method.formals);
        this.localScopes = new ArrayDeque<>();
        this.localScopes.push(new LinkedList<>());
        this.blockDepth = 1;
        this.inMainBody = false;
    }

    public void exitMethod() {
        this.currentMethod = null;
        this.params = null;
        this.localScopes = null;
        this.blockDepth = 0;
    }

    public void enterBlock() {
        if (localScopes == null) localScopes = new ArrayDeque<>();
        localScopes.push(new LinkedList<>());
        blockDepth++;
    }

    public int exitBlock() {
        blockDepth--;
        if (localScopes != null && !localScopes.isEmpty()) {
            List<VarDecl> scope = localScopes.pop();
            if (localScopes.isEmpty()) localScopes = null;
            return scope.size();
        }
        return 0;
    }

    public MethodDef getCurrentMethod() { return currentMethod; }
    public boolean isInMethod() { return currentMethod != null; }

    public Integer resolveOffset(String name) {
        if (localScopes != null && !localScopes.isEmpty()) {
            List<VarDecl> allLocals = new ArrayList<>();
            List<List<VarDecl>> scopeList = new ArrayList<>(localScopes);
            Collections.reverse(scopeList);
            for (List<VarDecl> scope : scopeList) {
                allLocals.addAll(scope);
            }
            for (int i = allLocals.size() - 1; i >= 0; i--) {
                if (allLocals.get(i).name.equals(name)) {
                    return -(i + 1) * 4;
                }
            }
        }
        if (params != null) {
            int nParams = params.size();
            for (int i = 0; i < nParams; i++) {
                if (params.get(i).name.equals(name)) return 4 * (i + 1);
            }
        }
        return null;
    }

    public static String makeIdLabel(String id) { return "$" + id; }

    public String freshLabel(String tag) {
        return "$$" + tag + "_" + (freshNameCounter++);
    }
}