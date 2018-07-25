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
            //return null;
        }

        traceIn(parseTree);

        // AST node
        ASTNode astChild;

        // CST node
        AST cstChild = parseTree.getFirstChild();

        // walk on CST tree 
        // collect necessary info and build AST
        while(cstChild.getText() != null) {
            switch(cstChild.getType()) {
                case IMPORT_DECL: 
                    astChild = importDecl(cstChild);
                    root.addChild(astChild);
                    break;
                case FIELD_DECL:
                    astChild = fieldDecl(cstChild);
                    root.addChild(astChild);
                    break;
                case METHOD_DECL:
                    astChild = methodDecl(cstChild);
                    root.addChild(astChild);
                    break;
            }
            cstChild = cstChild.getNextSibling();
        }

        traceOut(root); 
        // n.dump();
        //return n;
    }

    ASTNode importDecl(AST cstNode) {
        traceIn(cstNode);

        // AST node
        ImportDeclNode importDeclNode = new ImportDeclNode();
        String         idNode;

        // CST node
        AST childNode   = cstNode.getFirstChild();

        // walk on CST tree 
        // collect necessary info and build AST
        while(childNode != null) {
            traceIn(childNode);
            traceOut(null);

            if(childNode.getType() == ID) {
                idNode = childNode.getText();
                importDeclNode.addChild(idNode);
            }

            childNode = childNode.getNextSibling();
        }


        traceOut(importDeclNode); 
        return importDeclNode;
    }

    ASTNode fieldDecl(AST cstNode) {
        traceIn(cstNode);

        // AST node
        FieldDeclNode  fieldDeclNode = new FieldDeclNode();
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
                    typeNode = (TypeNode) type(childNode);
                    fieldDeclNode.addType(typeNode);
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

        return fieldDeclNode;
    }

    ASTNode methodDecl(AST cstNode) {
        traceIn(cstNode);

        // AST node
        MethodDeclNode methodDeclNode = new MethodDeclNode();

        // CST node
        AST childNode  = cstNode.getFirstChild();

        // walk on CST tree 
        // collect necessary info and build AST
        while(childNode != null) {
            switch(childNode.getType()) {
                case TYPE:
                    type(childNode);
                    break;
                case BLOCK:
                    block(childNode);
                    break;
                default:
                    traceIn(childNode);
                    traceOut(null);
            }
            childNode = childNode.getNextSibling();
        }

        traceOut(methodDeclNode); 
        return methodDeclNode;
    }

    ASTNode type(AST cstNode) {
        traceIn(cstNode);
        ASTNode astNode = null;

        AST childNode   = cstNode.getFirstChild();
        traceIn(childNode);
        traceOut(null);

        traceOut(null); 
        return astNode;
    }

    ASTNode block(AST cstNode) {
        traceIn(cstNode);

        ASTNode astNode = null;
        AST childNode   = cstNode.getFirstChild();

        while(childNode != null) {
            switch(childNode.getType()) {
                case FIELD_DECL:
                    fieldDecl(childNode);
                    break;
                case STATEMENT:
                    statement(childNode);
                    break;
                default:
                    traceIn(childNode);
                    traceOut(null);
            }
            childNode = childNode.getNextSibling();
        }

        traceOut(null); 
        return astNode;
    }

    ASTNode statement(AST cstNode) {
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
        return astNode;
    }

    ASTNode expr(AST cstNode) {
        traceIn(cstNode);

        ASTNode astNode = null;
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
        return astNode;
    }

    ASTNode location(AST cstNode) {
        traceIn(cstNode);

        ASTNode astNode = null;
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
        return astNode;
    }

    ASTNode assignExpr(AST cstNode) {
        traceIn(cstNode);

        ASTNode astNode = null;
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
        return astNode;
    }

    ASTNode assignOp(AST cstNode) {
        traceIn(cstNode);

        ASTNode astNode = null;
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
        return astNode;
    }

    ASTNode compoundAssignOp(AST cstNode) {
        traceIn(cstNode);

        ASTNode astNode = null;
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
        return astNode;
    }

    ASTNode increment(AST cstNode) {
        traceIn(cstNode);

        ASTNode astNode = null;
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
        return astNode;
    }

    ASTNode methodCall(AST cstNode) {
        traceIn(cstNode);

        ASTNode astNode = null;
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
        return astNode;
    }

    ASTNode methodName(AST cstNode) {
        traceIn(cstNode);

        ASTNode astNode = null;
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
        return astNode;
    }

    ASTNode importArg(AST cstNode) {
        traceIn(cstNode);

        ASTNode astNode = null;
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
        return astNode;
    }

    ASTNode binOp(AST cstNode) {
        traceIn(cstNode);

        ASTNode astNode = null;
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
        return astNode;
    }

    ASTNode arithOp(AST cstNode) {
        traceIn(cstNode);

        ASTNode astNode = null;
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
        return astNode;
    }

    ASTNode relOp(AST cstNode) {
        traceIn(cstNode);

        ASTNode astNode = null;
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
        return astNode;
    }

    ASTNode eqOp(AST cstNode) {
        traceIn(cstNode);

        ASTNode astNode = null;
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
        return astNode;
    }

    ASTNode condOp(AST cstNode) {
        traceIn(cstNode);

        ASTNode astNode = null;
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
        return astNode;
    }

    ASTNode literal(AST cstNode) {
        traceIn(cstNode);

        ASTNode astNode = null;
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
        return astNode;
    }

    ASTNode boolLiteral(AST cstNode) {
        traceIn(cstNode);

        ASTNode astNode = null;
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
        return astNode;
    }

}
