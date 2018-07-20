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
    
    Ir program() {
        return null;
    }

    Ir import_decl() {
        return null;
    }

    Ir field_decl() {
        return null;
    }

}
