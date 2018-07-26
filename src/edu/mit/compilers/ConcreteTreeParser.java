package edu.mit.compilers;

import java.io.*;
import java.util.*;
import antlr.Token;
import antlr.CommonAST;
import antlr.collections.AST;
import edu.mit.compilers.grammar.*;
import edu.mit.compilers.tools.CLI;
import edu.mit.compilers.tools.CLI.Action;


class ConcreteTreeParser implements DecafParserTokenTypes {

    private CommonAST parseTree;

    private boolean debug;

    private int traceDepth;

    // after parsing CST, generate
    // 1. AST
    // 2. Symbol Table
    protected ProgramNode  root;
    protected ProgramDescriptor pgmDesc;

    public ConcreteTreeParser(CommonAST parseTree) {
        this.parseTree  = parseTree;
        this.debug      = false;
        this.traceDepth = 0;

        root    = new ProgramNode();
        pgmDesc = new ProgramDescriptor();
    }

    ASTNode getAST() {
        return root;
    }

    ProgramDescriptor getProgDescriptor() {
        return pgmDesc;
    }

    public void setTrace(boolean debug) {
        this.debug = debug;
    }

    public void traceIn(AST cstNode) {
        if(!this.debug) {
            return;
        }
        traceDepth += 1;
        if(cstNode == null) {
            return;
        }

        for(int i = 0; i < traceDepth - 1; i ++) {
            System.out.printf("\t");
        }
        System.out.println("CST:" + cstNode.getText());

    }

    public void traceOut(ASTNode astNode) {
        if(!this.debug) {
            return;
        }
        traceDepth -= 1;
        if(astNode == null) {
            return;
        }

        for(int i = 0; i < traceDepth; i ++) {
            System.out.printf("\t");
        }
        System.out.println("AST:" + astNode.getText());
    }

    public void reportError() {
    }
    
    // methods to parse CST

    void program() {
        if(parseTree.getFirstChild() == null) {
            System.out.println("no concrete parse tree");
            return;
        }

        traceIn(parseTree);

        // CST node
        AST cstChild = parseTree.getFirstChild();

        // walk on CST tree 
        // collect necessary info and build AST
        while(cstChild.getText() != null) {
            switch(cstChild.getType()) {
                case IMPORT_DECL: 

                    ImportDeclNode astChild = new ImportDeclNode();
                    root.addChild(astChild);

                    importDecl(cstChild, astChild);

                    break;
                case FIELD_DECL:

                    FieldDeclNode astChild = new FieldChildNode();
                    root.addChild(astChild);

                    fieldDecl(cstChild, astChild);

                    break;
                case METHOD_DECL:

                    MethodDeclNode astChild = new MethodDeclNode();
                    root.addChild(astChild);

                    methodDecl(cstChild, astChild);

                    break;
            }
            cstChild = cstChild.getNextSibling();
        }

        traceOut(root); 
        // n.dump();
        //return n;
    }

    void importDecl(AST cstNode, ImportDeclNode importDeclNode) {
        traceIn(cstNode);

        // CST node
        AST childNode   = cstNode.getFirstChild();

        // walk on CST tree 
        // collect necessary info and build AST
        while(childNode != null) {
            traceIn(childNode);
            traceOut(null);

            if(childNode.getType() == ID) {
                MethodDeclNode exMethod   = new MethodDeclNode();
                importDeclNode.addChild(exMethod);

                TypeNode methodType   = new TypeNode("int");
                ParaDeclNode paraDecl = new ParaDeclNode();
                exMethod.addType(methodType);
                exMethod.setExMethod();
            }

            childNode = childNode.getNextSibling();
        }

        traceOut(importDeclNode); 
    }

    void fieldDecl(AST cstNode, FieldDeclNode fieldDeclNode) {
        traceIn(cstNode);

        // AST node
        TypeNode       typeNode;
        VarDeclNode    varNode;
        IntLiteralNode width;

        // CST node
        AST childNode   = cstNode.getFirstChild();

        // walk on CST tree 
        // collect necessary info and build AST
        while(childNode != null) {
            // recursive call and print cst
            switch(childNode.getType()) {
                case TYPE:
                    TypeNode typeNode = new TypeNode(childNode.getText());
                    fieldDeclNode.addType(typeNode);
                    type(childNode);
                    break;
                default:
                    traceIn(childNode);
                    traceOut(null);
            }
            
            // check if is array
            if(childNode.getNextSibling() != null && 
               childNode.getNextSibling().getType() == INTLITERAL) {

               // create IntLiteralNode
               width   = new IntLiteralNode(childNode.getNextSibling().getText());
               varNode = new VarDeclNode(childNode.getText(), width); 
               fieldDeclNode.addVar(varNode);

            } else {
               varNode = new VarDeclNode(childNode.getText()); 
               fieldDeclNode.addVar(varNode);
            }
            childNode = childNode.getNextSibling();
        }

        traceOut(fieldDeclNode); 
    }

    void methodDecl(AST cstNode, MethodDeclNode methodDeclNode) {
        traceIn(cstNode);

        // CST node
        AST childNode  = cstNode.getFirstChild();

        // walk on CST tree 
        // collect necessary info and build AST

        // method return type
        // if first chidl is TYPE type, add a TypeNode
        // else the return type is void, methodDeclNode.typeNode = null
        if(childNode.getType() == TYPE) {
            TypeNode typeNode = new TypeNode(childNode.getText());
            methodDeclNode.addType(typeNode);
        }
        childNode = childNode.getNextSibling();

        // method name
        methodDeclNode.setMethodName(childNode.getText());
        childNode = childNode.getNextSibling();

        // parameter declaration
        ParaDeclNode paraDeclNode = new ParaDeclNode();
        methodDeclNode.addPara(paraDeclNode);

        while(childNode != null) {
            switch(childNode.getType()) {
                case TYPE:
                    TypeNode paraType = new TypeNode(childNode.getText());
                    paraDeclNode.addType(paraType);
                    type(childNode);
                    break;
                case BLOCK:
                    block(childNode, methodDeclNode);
                    break;
                // ID token
                default:
                    VarDeclNode paraVar = new VarDeclNode(childNode.getText());
                    paraDeclNode.addVar(paraVar);
           
                    traceIn(childNode);
                    traceOut(null);
            }
            childNode = childNode.getNextSibling();
        }

        traceOut(methodDeclNode); 
        //return methodDeclNode;
    }

    ASTNode type(AST cstNode) {
        traceIn(cstNode);
        ASTNode astNode = null;

        AST childNode   = cstNode.getFirstChild();
        traceIn(childNode);
        traceOut(null);

        traceOut(null); 
        //return astNode;
    }

    void block(AST cstNode, ASTNode astNode) {
        traceIn(cstNode);

        // method decl ----> block
        if(astNode instanceof fieldDeclNode) {
        
        // if ----> block
        } else if(astNode instanceof ) {

        // for ----> block
        } else if(astNode instanceof ) {

        // while ----> block
        } else if(astNode instanceof ) {

        } else {
            
            System.err.println("not expected caller of block");
            System.exit(1);
        }

        // CST
        AST childNode = cstNode.getFirstChild();

        while(childNode != null) {
            switch(childNode.getType()) {
                case FIELD_DECL:
                    FieldDeclNode fDeclN = new FieldDeclNode();
                    methodDeclNode.addFieldDecl(fDeclN);
                    fieldDecl(childNode, fDeclN);
                    break;
                case STATEMENT:
                    statement(childNode, methodDeclNode);
                    break;
                default:
                    traceIn(childNode);
                    traceOut(null);
            }
            childNode = childNode.getNextSibling();
        }

        traceOut(null); 
    }

    void statement(AST cstNode, ASTNode astNode) {
        traceIn(cstNode);

        ASTNode astNode = null;
        AST childNode   = cstNode.getFirstChild();

        while(childNode != null) {
            switch(childNode.getType()) {
                case LOCATION:
                    location(childNode);
                    break;
                case ASSIGN_EXPR:
                    assignExpr(childNode);
                    break;
                case METHOD_CALL:
                    methodCall(childNode);
                    break;
                case EXPR:
                    expr(childNode);
                    break;
                case BLOCK:
                    block(childNode);
                    break;
                case COMPOUND_ASSIGN_OP:
                    compoundAssignOp(childNode);
                    break;
                case INCREMENT:
                    increment(childNode);
                    break;
                default:
                    traceIn(childNode);
                    traceOut(null);
            }
            childNode = childNode.getNextSibling();
        }

        traceOut(null); 
        //return astNode;
    }

    void expr(AST cstNode, ASTNode astNode) {
        traceIn(cstNode);

        //ASTNode astNode = null;
        AST childNode   = cstNode.getFirstChild();

        while(childNode != null) {
            switch(childNode.getType()) {
                case LOCATION:
                    location(childNode);
                    break;
                case METHOD_CALL:
                    methodCall(childNode);
                    break;
                case LITERAL:
                    literal(childNode);
                    break;
                case EXPR:
                    expr(childNode);
                    break;
                case BIN_OP:
                    binOp(childNode);
                    break;
                default:
                    traceIn(childNode);
                    traceOut(null);
            }
            childNode = childNode.getNextSibling();
        }

        traceOut(null); 
        //return astNode;
    }

    void location(AST cstNode, ASTNode astNode) {
        traceIn(cstNode);

        //ASTNode astNode = null;
        AST childNode   = cstNode.getFirstChild();

        while(childNode != null) {
            switch(childNode.getType()) {
                case EXPR:
                    expr(childNode);
                    break;
                default:
                    traceIn(childNode);
                    traceOut(null);
            }
            childNode = childNode.getNextSibling();
        }

        traceOut(null); 
        //return astNode;
    }

    ASTNode assignExpr(AST cstNode, ASTNode astNode) {
        traceIn(cstNode);

        //ASTNode astNode = null;
        AST childNode   = cstNode.getFirstChild();

        while(childNode != null) {
            switch(childNode.getType()) {
                case ASSIGN_OP:
                    assignOp(childNode);
                    break;
                case EXPR:
                    expr(childNode);
                    break;
                case INCREMENT:
                    increment(childNode);
                    break;
                default:
                    traceIn(childNode);
                    traceOut(null);
            }
            childNode = childNode.getNextSibling();
        }

        traceOut(null); 
        //return astNode;
    }

    void assignOp(AST cstNode, ASTNode astNode) {
        traceIn(cstNode);

        //ASTNode astNode = null;
        AST childNode   = cstNode.getFirstChild();

        while(childNode != null) {
            switch(childNode.getType()) {
                case COMPOUND_ASSIGN_OP:
                    compoundAssignOp(childNode);
                    break;
                default:
                    traceIn(childNode);
                    traceOut(null);
            }
            childNode = childNode.getNextSibling();
        }

        traceOut(null); 
        //return astNode;
    }

    void compoundAssignOp(AST cstNode, ASTNode astNode) {
        traceIn(cstNode);

        //ASTNode astNode = null;
        AST childNode   = cstNode.getFirstChild();

        while(childNode != null) {
            switch(childNode.getType()) {
                default:
                    traceIn(childNode);
                    traceOut(null);
            }
            childNode = childNode.getNextSibling();
        }

        traceOut(null); 
        //return astNode;
    }

    void increment(AST cstNode, ASTNode astNode) {
        traceIn(cstNode);

        //ASTNode astNode = null;
        AST childNode   = cstNode.getFirstChild();

        while(childNode != null) {
            switch(childNode.getType()) {
                default:
                    traceIn(childNode);
                    traceOut(null);
            }
            childNode = childNode.getNextSibling();
        }

        traceOut(null); 
        //return astNode;
    }

    void methodCall(AST cstNode, ASTNode astNode) {
        traceIn(cstNode);

        //ASTNode astNode = null;
        AST childNode   = cstNode.getFirstChild();

        while(childNode != null) {
            switch(childNode.getType()) {
                case METHOD_NAME:
                    methodName(childNode);
                    break;
                default:
                    traceIn(childNode);
                    traceOut(null);
            }
            childNode = childNode.getNextSibling();
        }

        traceOut(null); 
        //return astNode;
    }

    void methodName(AST cstNode, ASTNode astNode) {
        traceIn(cstNode);

        //ASTNode astNode = null;
        AST childNode   = cstNode.getFirstChild();

        while(childNode != null) {
            switch(childNode.getType()) {
                default:
                    traceIn(childNode);
                    traceOut(null);
            }
            childNode = childNode.getNextSibling();
        }

        traceOut(null); 
        //return astNode;
    }

    void importArg(AST cstNode, ASTNode astNode) {
        traceIn(cstNode);

        //ASTNode astNode = null;
        AST childNode   = cstNode.getFirstChild();

        while(childNode != null) {
            switch(childNode.getType()) {
                case EXPR:
                    expr(childNode);
                    break;
                default:
                    traceIn(childNode);
                    traceOut(null);
            }
            childNode = childNode.getNextSibling();
        }

        traceOut(null); 
        //return astNode;
    }

    void binOp(AST cstNode, ASTNode astNode) {
        traceIn(cstNode);

        //ASTNode astNode = null;
        AST childNode   = cstNode.getFirstChild();

        while(childNode != null) {
            switch(childNode.getType()) {
                case ARITH_OP:
                    arithOp(childNode);
                    break;
                case REL_OP:
                    relOp(childNode);
                    break;
                case EQ_OP:
                    eqOp(childNode);
                    break;
                case COND_OP:
                    condOp(childNode);
                    break;
                default:
                    traceIn(childNode);
                    traceOut(null);
            }
            childNode = childNode.getNextSibling();
        }

        traceOut(null); 
        //return astNode;
    }

    void arithOp(AST cstNode, ASTNode astNode) {
        traceIn(cstNode);

        //ASTNode astNode = null;
        AST childNode   = cstNode.getFirstChild();

        while(childNode != null) {
            switch(childNode.getType()) {
                default:
                    traceIn(childNode);
                    traceOut(null);
            }
            childNode = childNode.getNextSibling();
        }

        traceOut(null); 
        //return astNode;
    }

    void relOp(AST cstNode, ASTNode astNode) {
        traceIn(cstNode);

        //ASTNode astNode = null;
        AST childNode   = cstNode.getFirstChild();

        while(childNode != null) {
            switch(childNode.getType()) {
                default:
                    traceIn(childNode);
                    traceOut(null);
            }
            childNode = childNode.getNextSibling();
        }

        traceOut(null); 
        //return astNode;
    }

    void eqOp(AST cstNode, ASTNode astNode) {
        traceIn(cstNode);

        //ASTNode astNode = null;
        AST childNode   = cstNode.getFirstChild();

        while(childNode != null) {
            switch(childNode.getType()) {
                default:
                    traceIn(childNode);
                    traceOut(null);
            }
            childNode = childNode.getNextSibling();
        }

        traceOut(null); 
        //return astNode;
    }

    void condOp(AST cstNode, ASTNode astNode) {
        traceIn(cstNode);

        //ASTNode astNode = null;
        AST childNode   = cstNode.getFirstChild();

        while(childNode != null) {
            switch(childNode.getType()) {
                default:
                    traceIn(childNode);
                    traceOut(null);
            }
            childNode = childNode.getNextSibling();
        }

        traceOut(null); 
        //return astNode;
    }

    void literal(AST cstNode, ASTNode astNode) {
        traceIn(cstNode);

        //ASTNode astNode = null;
        AST childNode   = cstNode.getFirstChild();

        while(childNode != null) {
            switch(childNode.getType()) {
                case BOOL_LITERAL:
                    boolLiteral(childNode);
                    break;
                default:
                    traceIn(childNode);
                    traceOut(null);
            }
            childNode = childNode.getNextSibling();
        }

        traceOut(null); 
        //return astNode;
    }

    void boolLiteral(AST cstNode, ASTNode astNode) {
        traceIn(cstNode);

        //ASTNode astNode = null;
        AST childNode   = cstNode.getFirstChild();

        while(childNode != null) {
            switch(childNode.getType()) {
                default:
                    traceIn(childNode);
                    traceOut(null);
            }
            childNode = childNode.getNextSibling();
        }

        traceOut(null); 
        //return astNode;
    }

}
