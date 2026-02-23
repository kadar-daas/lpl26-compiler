package parse;

import compile.ast.*;
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
        lex.eat("BEGIN");
        List<VarDecl> globals = new LinkedList<>();
        while (lex.tok().isType("INT_TYPE")) {
            globals.add(VarDecl());
        }
        List<Stm> body = new LinkedList<>();
        while (!lex.tok().isType("END")) {
            body.add(Stm());
        }
        lex.eat("END");
        return new Program(globals, body);
    }

    /**
     VarDecl -> Type ID SEMIC
     */
    private VarDecl VarDecl() {
        Type t = Type();
        String id = lex.eat("ID");
        lex.eat("SEMIC");
        return new VarDecl(t, id);
    }

    /**
     Type -> INT_TYPE
     */
    private Type Type() {
        switch (lex.tok().type) {
            case "INT_TYPE":
                lex.next();
                return new TypeInt();
            default: throw new ParseException(lex.tok(), "INT_TYPE");
        }
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
            case "PRINTLN": {
                lex.next();
                lex.eat("LBR");
                Exp e = Exp();
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
            case "ID": {
                String target = lex.eat("ID");
                lex.eat("ASSIGN");
                Exp rhs = Exp();
                lex.eat("SEMIC");
                return new StmAssign(target, rhs);
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
            case "ID": {
                String id = lex.tok().image;
                lex.next();
                return new ExpVar(id);
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
}
