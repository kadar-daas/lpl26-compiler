package parse;

import compile.ast.*;
import compile.ast.MethodDef;
import compile.ast.FunDef;
import compile.ast.ProcDef;
import compile.ast.StmMethodCall;
import compile.ast.ExpMethodCall;
import sbnf.ParseException;
import sbnf.lex.Lexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/** Parse an LPL26 program and build its AST.  */
public class LPL26Parser {

    /**
     * Path to an SBNF file containing the relevant token definitions.
     */
    public static final String SBNF_FILE = "data/LPL26.sbnf";

    private Lexer lex;

    /**
     * Initialise a new LPL26 parser.
     */
    public LPL26Parser() {
        lex = new Lexer(SBNF_FILE);
    }

    public Program parse(String sourcePath) throws IOException {
        lex.readFile(sourcePath);
        lex.next();
        Program prog = Program();
        if (!lex.tok().isType("EOF")) {
            throw new ParseException(lex.tok(), "EOF");
        }
        return prog;
    }

    /**
     Program -> BEGIN VarDecl* Stm* END
     */
    public Program Program() {
        List<RecordDef> recordDefs = new LinkedList<>();
        while (lex.tok().isType("RECDEF")) {
            recordDefs.add(RecordDef());
        }
        lex.eat("BEGIN");
        List<Stm> body = new LinkedList<>();
        while (!lex.tok().isType("END")) {
            body.add(Stm());
        }
        lex.eat("END");
        List<MethodDef> methods = new LinkedList<>();
        while (lex.tok().isType("FUN") || lex.tok().isType("PROC")) {
            methods.add(MethodDef());
        }
        return new Program(recordDefs, new LinkedList<>(), body, methods);

    }
    private RecordDef RecordDef() {
        lex.eat("RECDEF");
        String name = lex.eat("ID");
        lex.eat("LBR");
        List<VarDecl> fields = new LinkedList<>();
        // FieldDecls -> Type ID AnotherField* (at least one field required)
        Type t = Type();
        String id = lex.eat("ID");
        fields.add(new VarDecl(t, id, null));
        while (lex.tok().isType("COMMA")) {
            lex.next();
            t = Type();
            id = lex.eat("ID");
            fields.add(new VarDecl(t, id, null));
        }
        lex.eat("RBR");
        lex.eat("SEMIC");
        return new RecordDef(name, fields);
    }

    private MethodDef MethodDef() {
        if (lex.tok().isType("FUN")) {
            lex.next();
            Type returnType = Type();
            String name = lex.eat("METHOD_ID");
            lex.eat("LBR");
            List<VarDecl> formals = Formals();
            lex.eat("RBR");
            lex.eat("LCBR");
            List<VarDecl> locals = new LinkedList<>();
            List<Stm> body = new LinkedList<>();
            while (!lex.tok().isType("RETURN")) {
                body.add(Stm());
            }
            lex.eat("RETURN");
            Exp returnExp = Exp();
            lex.eat("SEMIC");
            lex.eat("RCBR");
            return new FunDef(returnType, name, formals, locals, body, returnExp);

        } else {
            lex.eat("PROC");
            String name = lex.eat("METHOD_ID");
            lex.eat("LBR");
            List<VarDecl> formals = Formals();
            lex.eat("RBR");
            lex.eat("LCBR");
            List<VarDecl> locals = new LinkedList<>();
            List<Stm> body = new LinkedList<>();
            while (!lex.tok().isType("RCBR")) {
                body.add(Stm());
            }
            lex.eat("RCBR");
            return new ProcDef(name, formals, locals, body);
        }
    }

    private List<VarDecl> Formals() {
        List<VarDecl> formals = new LinkedList<>();
        if (lex.tok().isType("INT_TYPE") || lex.tok().isType("REC")) {
            Type t = Type();
            String id = lex.eat("ID");
            formals.add(new VarDecl(t, id, null));
            while (lex.tok().isType("COMMA")) {
                lex.next();
                t = Type();
                id = lex.eat("ID");
                formals.add(new VarDecl(t, id, null));
            }
        }
        return formals;
    }

    private List<Exp> ParamList() {
        List<Exp> args = new LinkedList<>();
        args.add(Exp());
        while (lex.tok().isType("COMMA")) {
            lex.next();
            args.add(Exp());
        }
        return args;
    }

    private List<Exp> OptionalParamList() {
        if (lex.tok().isType("RBR")) {
            return new LinkedList<>();
        }
        return ParamList();
    }


    /**
     VarDecl -> Type ID SEMIC
     */
    private VarDecl VarDecl() {
        Type t = Type();
        String id = lex.eat("ID");
        Exp init = null;
        if (lex.tok().isType("ASSIGN")) {
            lex.next();
            init = Exp();
        }
        lex.eat("SEMIC");
        return new VarDecl(t, id, init);
    }

    /**
     Type -> INT_TYPE
     */
    private Type Type() {
        Type t;
        switch (lex.tok().type) {
            case "INT_TYPE":
                lex.next();
                t = new TypeInt();
                break;
            case "REC":
                lex.next();
                lex.eat("LBR");
                String name = lex.eat("ID");
                lex.eat("RBR");
                t = new TypeRecord(name);
                break;
            default:
                throw new ParseException(lex.tok(), "INT_TYPE", "REC");
        }
        while (lex.tok().isType("LSQBR")) {
            lex.next();
            lex.eat("RSQBR");
            t = new TypeArray(t);
        }
        return t;
    }


    /**
      Stm -> PRINTLN LBR Exp RBR SEMIC
      Stm -> WHILE LBR Exp RBR Stm
      Stm -> LCBR Stm* RCBR
      Stm -> ID ASSIGN Exp SEMIC
      Stm -> IF LBR Exp RBR Stm ELSE Stm
     */
    private Stm Stm() {
        switch (lex.tok().type) {
            case "PRINT": {
                lex.next();
                lex.eat("LBR");
                Exp e = Exp();
                lex.eat("RBR");
                lex.eat("SEMIC");
                return new StmPrint(e);
            }
            case "INT_TYPE":
            case "REC": {
                VarDecl vd = VarDecl();
                return new StmVarDecl(vd);
            }
            case "PRINTCH": {
                lex.next();
                lex.eat("LBR");
                Exp e = Exp();
                lex.eat("RBR");
                lex.eat("SEMIC");
                return new StmPrintch(e);
            }
            case "PRINTLN": {
                lex.next();
                lex.eat("LBR");
                Exp e = null;
                if (!lex.tok().isType("RBR")) e = Exp();
                lex.eat("RBR");
                lex.eat("SEMIC");
                return new StmPrintln(e);
            }
            case "WHILE": {
                lex.next();
                lex.eat("LBR");
                Exp cond = Exp();
                lex.eat("RBR");
                Stm body = Stm();
                return new StmWhile(cond, body);
            }
            case "LCBR": {
                lex.next();
                List<Stm> stms = new ArrayList<>();
                while (!lex.tok().isType("RCBR")) {
                    stms.add(Stm());
                }
                lex.next();
                return new StmBlock(stms);
            }
            case "FREE": {
                lex.next();
                lex.eat("LBR");
                Exp e = Exp();
                lex.eat("RBR");
                lex.eat("SEMIC");
                return new StmFree(e);
            }
            case "ID": {
                String name = lex.eat("ID");
                // parse accessors
                List<Object> accessors = new LinkedList<>();
                while (lex.tok().isType("LSQBR") || lex.tok().isType("DOT")) {
                    if (lex.tok().isType("LSQBR")) {
                        lex.next();
                        accessors.add(Exp());
                        lex.eat("RSQBR");
                    } else {
                        lex.next();
                        accessors.add(lex.eat("ID")); // String field name
                    }
                }
                lex.eat("ASSIGN");
                Exp rhs = Exp();
                lex.eat("SEMIC");

                if (accessors.isEmpty()) {
                    return new StmAssign(name, rhs);
                }

                // Build base expression from name + all but last accessor
                Exp base = new ExpVar(name);
                for (int i = 0; i < accessors.size() - 1; i++) {
                    Object acc = accessors.get(i);
                    if (acc instanceof Exp) {
                        base = new ExpArrayAccess(base, (Exp) acc);
                    } else {
                        base = new ExpFieldAccess(base, (String) acc);
                    }
                }

                // Last accessor determines statement type
                Object last = accessors.get(accessors.size() - 1);
                if (last instanceof Exp) {
                    return new StmGenericArrayAssign(base, (Exp) last, rhs);
                } else {
                    return new StmFieldAssign(base, (String) last, rhs);
                }
            }
            case "METHOD_ID": {
                String name = lex.eat("METHOD_ID");
                lex.eat("LBR");
                List<Exp> args = OptionalParamList();
                lex.eat("RBR");
                lex.eat("SEMIC");
                return new StmMethodCall(name, args);
            }
            case "IF": {
                lex.next();
                lex.eat("LBR");
                Exp cond = Exp();
                lex.eat("RBR");
                Stm trueBranch = Stm();
                lex.eat("ELSE");
                Stm falseBranch = Stm();
                return new StmIf(cond, trueBranch, falseBranch);
            }
            default:
                throw new ParseException(lex.tok(), "PRINTLN", "WHILE", "LCBR", "ID", "IF");
        }
    }

    /**
      Exp -> SimpleExp OperatorClause
     */
    private Exp Exp() {
        Exp e1 = SimpleExp();
        return OperatorClause(e1);
    }

    /**
      SimpleExp -> INTLIT
      SimpleExp -> ID
      SimpleExp -> LBR Exp RBR
     */
    private Exp SimpleExp() {
        switch (lex.tok().type) {
            case "INTLIT": {
                int n = Integer.parseInt(lex.tok().image);
                lex.next();
                return new ExpInt(n);
            }
            case "NULL": {
                lex.next();
                return new ExpNull();
            }
            case "LENGTH": {
                lex.next();
                lex.eat("LBR");
                Exp e = Exp();
                lex.eat("RBR");
                return new ExpLength(e);
            }
            case "NEW": {
                lex.next();
                if (lex.tok().isType("LBR")) {
                    // new (Type)[size] — array
                    lex.eat("LBR");
                    Type t = Type();
                    lex.eat("RBR");
                    lex.eat("LSQBR");
                    Exp size = Exp();
                    lex.eat("RSQBR");
                    return new ExpNewArray(t, size);
                } else {
                    // new ID(args) — record
                    String recordName = lex.eat("ID");
                    lex.eat("LBR");
                    List<Exp> args = ParamList();
                    lex.eat("RBR");
                    return new ExpNewRecord(recordName, args);
                }
            }
            case "NOT": {
                lex.next();
                return new ExpNot(SimpleExp());
            }
            case "ID": {
                return LExprAsExp();
            }
            case "METHOD_ID": {
                String name = lex.eat("METHOD_ID");
                lex.eat("LBR");
                List<Exp> args = OptionalParamList();
                lex.eat("RBR");
                return new ExpMethodCall(name, args);
            }
            case "LBR": {
                lex.next();
                Exp e = Exp();
                lex.eat("RBR");
                return e;
            }
            default:
                throw new ParseException(lex.tok(), "INTLIT", "ID", "LBR");
        }
    }

    /**
      OperatorClause -> BINARY_OP SimpleExp
      OperatorClause ->
     */
    private Exp OperatorClause(Exp e) {
        switch (lex.tok().type) {
            case "BINARY_OP": {
                String operator = lex.tok().image;
                lex.next();
                return new ExpBinaryOp(operator, e, SimpleExp());
            }
            case "QM": {
                lex.next();
                Exp thenExp = Exp();
                lex.eat("COLON");
                Exp elseExp = Exp();
                return new ExpTernary(e, thenExp, elseExp);
            }
            default:
                return e;
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: parse.LPL26Parser <source-file>");
            System.exit(1);
        }
        System.out.println("Lexing with token defs from file " + SBNF_FILE);
        parse.LPL26Parser parser = new parse.LPL26Parser();
        System.out.println("Parsing source file " + args[0]);
        parser.parse(args[0]);
        System.out.println("... parse succeeded.");
    }
    private Exp LExprAsExp() {
        String id = lex.eat("ID");
        Exp e = new ExpVar(id);
        while (lex.tok().isType("LSQBR") || lex.tok().isType("DOT")) {
            if (lex.tok().isType("LSQBR")) {
                lex.next();
                Exp index = Exp();
                lex.eat("RSQBR");
                e = new ExpArrayAccess(e, index);
            } else {
                lex.next();
                String field = lex.eat("ID");
                e = new ExpFieldAccess(e, field);
            }
        }
        return e;
    }
    private Object[] LExpr() {
        String id = lex.eat("ID");
        List<Exp> indices = new LinkedList<>();
        while (lex.tok().isType("LSQBR")) {
            lex.next();
            indices.add(Exp());
            lex.eat("RSQBR");
        }
        return new Object[]{id, indices};
    }
}
