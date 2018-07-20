package edu.mit.compilers;

import java.io.*;
import antlr.Token;
import antlr.CommonAST;
import edu.mit.compilers.grammar.*;
import edu.mit.compilers.tools.CLI;
import edu.mit.compilers.tools.CLI.Action;


class ConcreteTreeParser {

    private CommonAST parseTree;

    private boolean debug;

    public ConcreteTreeParser(CommonAST parseTree) {
        this.parseTree = parseTree;
        this.debug = false;
    }


    public void setTrace(boolean debug) {
        this.debug = debug;
    }

    public void traceIn(CommonAST parseTree) {
        if (!this.debug) {
            return;
        }
    }
    
    ASTNode program() {
        return null;
    }

    ASTNode importDecl() {
        return null;
    }

    ASTNode fieldDecl() {
        return null;
    }

    ASTNode methodDecl() {
        return null;
    }

    ASTNode type() {
        return null;
    }

    ASTNode block() {
        return null;
    }

    ASTNode statement() {
        return null;
    }

    ASTNode expr() {
        return null;
    }

    ASTNode location() {
        return null;
    }

    ASTNode assignExpr() {
        return null;
    }

    ASTNode assignOp() {
        return null;
    }

    ASTNode compoundAssignOp() {
        return null;
    }

    ASTNode increment() {
        return null;
    }

    ASTNode methodCall() {
        return null;
    }

    ASTNode methodName() {
        return null;
    }

    ASTNode importArg() {
        return null;
    }

    ASTNode binOp() {
        return null;
    }

    ASTNode arithOp() {
        return null;
    }

    ASTNode relOp() {
        return null;
    }

    ASTNode eqOp() {
        return null;
    }

    ASTNode condOp() {
        return null;
    }

    ASTNode literal() {
        return null;
    }

    ASTNode boolLiteral() {
        return null;
    }

}
