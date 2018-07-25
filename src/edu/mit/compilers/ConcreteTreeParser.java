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

    protected ProgramDescriptor pgmDesc;
    protected ASTNode  root;

    public ConcreteTreeParser(CommonAST parseTree) {
        this.parseTree  = parseTree;
        this.debug      = false;
        this.traceDepth = 0;

        pgmDesc = new ProgramDescriptor();
    }

    ProgramDescriptor getProgDescriptor() {
        return pgmDesc;
    }

    ASTNode getAST() {
        return root;
    }

    public void setTrace(boolean debug) {
        this.debug = debug;
    }

    public void CstTraceIn(AST cstNode) {
        if(!this.debug) {
            return;
        }
        if(cstNode == null) {
            return;
        }

        for(int i = 0; i < traceDepth; i ++) {
            System.out.printf("\t");
        }
        System.out.println("CST:" + cstNode.getText());

        traceDepth += 1;
    }

    public void CstTraceOut() {
        traceDepth -= 1;
        if(!this.debug) {
            return;
        }
    }

    public void reportError() {
    }
    
    ASTNode program() {
        if(parseTree.getFirstChild() == null) {
            System.out.println("no child");
            return null;
        }
        // debug
        CstTraceIn(parseTree);

        ASTNode n = null;
        AST child = parseTree.getFirstChild();

        //System.out.println(parseTree.getNumberOfChildren());
        while(child.getText() != null) {
            switch(child.getType()) {
                case IMPORT_DECL: 
                    importDecl(child);
                    break;
                case FIELD_DECL:
                    fieldDecl(child);
                    break;
                case METHOD_DECL:
                    methodDecl(child);
                    break;
            }
            child = child.getNextSibling();
        }

        CstTraceOut(); 
        // n.dump();
        return n;
    }

    ASTNode importDecl(AST cstNode) {
        CstTraceIn(cstNode);

        // AST node
        ImportDeclNode importDeclNode;
        String         idNode;

        // CST node
        AST childNode   = cstNode.getFirstChild();

        // create AST node
        importDeclNode  = new ImportDeclNode();

        // walk on CST tree 
        // collect necessary info
        while(childNode != null) {
            CstTraceIn(childNode);
            CstTraceOut();

            if(childNode.getType() == ID) {
                idNode = childNode.getText();
                importDeclNode.addChild(idNode);
            }

            childNode = childNode.getNextSibling();
        }


        CstTraceOut(); 
        return importDeclNode;
    }

    ASTNode fieldDecl(AST cstNode) {
        CstTraceIn(cstNode);

        // AST node
        FieldDeclNode  fieldDeclNode;
        TypeNode       typeNode;
        VarDeclNode    varNode;
        IntLiteralNode width;

        // CST node
        AST childNode   = cstNode.getFirstChild();

        // create ast node
        fieldDeclNode = new FieldDeclNode();

        // walk on CST tree 
        // collect necessary info and build ast
        while(childNode != null) {
            // recursive call and print cst
            switch(childNode.getType()) {
                case TYPE:
                    typeNode = (TypeNode) type(childNode);
                    fieldDeclNode.addType(typeNode);
                    break;
                default:
                    CstTraceIn(childNode);
                    CstTraceOut();
            }
            
            // check if is array
            if(childNode.getNextSibling() != null && 
               childNode.getNextSibling().getType() == INTLITERAL) {

               // create IntLiteralNode
               width = new IntLiteralNode(childNode.getNextSibling().getText());
               varNode = new VarDeclNode(childNode.getText(), width); 
               fieldDeclNode.addVar(varNode);

            } else {
               varNode = new VarDeclNode(childNode.getText()); 
               fieldDeclNode.addVar(varNode);
            }

            childNode = childNode.getNextSibling();
        }


        CstTraceOut(); 

        return fieldDeclNode;
    }

    ASTNode methodDecl(AST cstNode) {
        CstTraceIn(cstNode);

        ASTNode astNode = null;
        AST childNode   = cstNode.getFirstChild();

        while(childNode != null) {
            switch(childNode.getType()) {
                case TYPE:
                    type(childNode);
                    break;
                case BLOCK:
                    block(childNode);
                    break;
                default:
                    CstTraceIn(childNode);
                    CstTraceOut();
            }
            childNode = childNode.getNextSibling();
        }

        CstTraceOut(); 
        return astNode;
    }

    ASTNode type(AST cstNode) {
        CstTraceIn(cstNode);
        ASTNode astNode = null;

        AST childNode   = cstNode.getFirstChild();
        CstTraceIn(childNode);
        CstTraceOut();

        CstTraceOut(); 
        return astNode;
    }

    ASTNode block(AST cstNode) {
        CstTraceIn(cstNode);

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
                    CstTraceIn(childNode);
                    CstTraceOut();
            }
            childNode = childNode.getNextSibling();
        }

        CstTraceOut(); 
        return astNode;
    }

    ASTNode statement(AST cstNode) {
        CstTraceIn(cstNode);

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
                    CstTraceIn(childNode);
                    CstTraceOut();
            }
            childNode = childNode.getNextSibling();
        }

        CstTraceOut(); 
        return astNode;
    }

    ASTNode expr(AST cstNode) {
        CstTraceIn(cstNode);

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
                    CstTraceIn(childNode);
                    CstTraceOut();
            }
            childNode = childNode.getNextSibling();
        }

        CstTraceOut(); 
        return astNode;
    }

    ASTNode location(AST cstNode) {
        CstTraceIn(cstNode);

        ASTNode astNode = null;
        AST childNode   = cstNode.getFirstChild();

        while(childNode != null) {
            switch(childNode.getType()) {
                case EXPR:
                    expr(childNode);
                    break;
                default:
                    CstTraceIn(childNode);
                    CstTraceOut();
            }
            childNode = childNode.getNextSibling();
        }

        CstTraceOut(); 
        return astNode;
    }

    ASTNode assignExpr(AST cstNode) {
        CstTraceIn(cstNode);

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
                    CstTraceIn(childNode);
                    CstTraceOut();
            }
            childNode = childNode.getNextSibling();
        }

        CstTraceOut(); 
        return astNode;
    }

    ASTNode assignOp(AST cstNode) {
        CstTraceIn(cstNode);

        ASTNode astNode = null;
        AST childNode   = cstNode.getFirstChild();

        while(childNode != null) {
            switch(childNode.getType()) {
                case COMPOUND_ASSIGN_OP:
                    compoundAssignOp(childNode);
                    break;
                default:
                    CstTraceIn(childNode);
                    CstTraceOut();
            }
            childNode = childNode.getNextSibling();
        }

        CstTraceOut(); 
        return astNode;
    }

    ASTNode compoundAssignOp(AST cstNode) {
        CstTraceIn(cstNode);

        ASTNode astNode = null;
        AST childNode   = cstNode.getFirstChild();

        while(childNode != null) {
            switch(childNode.getType()) {
                default:
                    CstTraceIn(childNode);
                    CstTraceOut();
            }
            childNode = childNode.getNextSibling();
        }

        CstTraceOut(); 
        return astNode;
    }

    ASTNode increment(AST cstNode) {
        CstTraceIn(cstNode);

        ASTNode astNode = null;
        AST childNode   = cstNode.getFirstChild();

        while(childNode != null) {
            switch(childNode.getType()) {
                default:
                    CstTraceIn(childNode);
                    CstTraceOut();
            }
            childNode = childNode.getNextSibling();
        }

        CstTraceOut(); 
        return astNode;
    }

    ASTNode methodCall(AST cstNode) {
        CstTraceIn(cstNode);

        ASTNode astNode = null;
        AST childNode   = cstNode.getFirstChild();

        while(childNode != null) {
            switch(childNode.getType()) {
                case METHOD_NAME:
                    methodName(childNode);
                    break;
                default:
                    CstTraceIn(childNode);
                    CstTraceOut();
            }
            childNode = childNode.getNextSibling();
        }

        CstTraceOut(); 
        return astNode;
    }

    ASTNode methodName(AST cstNode) {
        CstTraceIn(cstNode);

        ASTNode astNode = null;
        AST childNode   = cstNode.getFirstChild();

        while(childNode != null) {
            switch(childNode.getType()) {
                default:
                    CstTraceIn(childNode);
                    CstTraceOut();
            }
            childNode = childNode.getNextSibling();
        }

        CstTraceOut(); 
        return astNode;
    }

    ASTNode importArg(AST cstNode) {
        CstTraceIn(cstNode);

        ASTNode astNode = null;
        AST childNode   = cstNode.getFirstChild();

        while(childNode != null) {
            switch(childNode.getType()) {
                case EXPR:
                    expr(childNode);
                    break;
                default:
                    CstTraceIn(childNode);
                    CstTraceOut();
            }
            childNode = childNode.getNextSibling();
        }

        CstTraceOut(); 
        return astNode;
    }

    ASTNode binOp(AST cstNode) {
        CstTraceIn(cstNode);

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
                    CstTraceIn(childNode);
                    CstTraceOut();
            }
            childNode = childNode.getNextSibling();
        }

        CstTraceOut(); 
        return astNode;
    }

    ASTNode arithOp(AST cstNode) {
        CstTraceIn(cstNode);

        ASTNode astNode = null;
        AST childNode   = cstNode.getFirstChild();

        while(childNode != null) {
            switch(childNode.getType()) {
                default:
                    CstTraceIn(childNode);
                    CstTraceOut();
            }
            childNode = childNode.getNextSibling();
        }

        CstTraceOut(); 
        return astNode;
    }

    ASTNode relOp(AST cstNode) {
        CstTraceIn(cstNode);

        ASTNode astNode = null;
        AST childNode   = cstNode.getFirstChild();

        while(childNode != null) {
            switch(childNode.getType()) {
                default:
                    CstTraceIn(childNode);
                    CstTraceOut();
            }
            childNode = childNode.getNextSibling();
        }

        CstTraceOut(); 
        return astNode;
    }

    ASTNode eqOp(AST cstNode) {
        CstTraceIn(cstNode);

        ASTNode astNode = null;
        AST childNode   = cstNode.getFirstChild();

        while(childNode != null) {
            switch(childNode.getType()) {
                default:
                    CstTraceIn(childNode);
                    CstTraceOut();
            }
            childNode = childNode.getNextSibling();
        }

        CstTraceOut(); 
        return astNode;
    }

    ASTNode condOp(AST cstNode) {
        CstTraceIn(cstNode);

        ASTNode astNode = null;
        AST childNode   = cstNode.getFirstChild();

        while(childNode != null) {
            switch(childNode.getType()) {
                default:
                    CstTraceIn(childNode);
                    CstTraceOut();
            }
            childNode = childNode.getNextSibling();
        }

        CstTraceOut(); 
        return astNode;
    }

    ASTNode literal(AST cstNode) {
        CstTraceIn(cstNode);

        ASTNode astNode = null;
        AST childNode   = cstNode.getFirstChild();

        while(childNode != null) {
            switch(childNode.getType()) {
                case BOOL_LITERAL:
                    boolLiteral(childNode);
                    break;
                default:
                    CstTraceIn(childNode);
                    CstTraceOut();
            }
            childNode = childNode.getNextSibling();
        }

        CstTraceOut(); 
        return astNode;
    }

    ASTNode boolLiteral(AST cstNode) {
        CstTraceIn(cstNode);

        ASTNode astNode = null;
        AST childNode   = cstNode.getFirstChild();

        while(childNode != null) {
            switch(childNode.getType()) {
                default:
                    CstTraceIn(childNode);
                    CstTraceOut();
            }
            childNode = childNode.getNextSibling();
        }

        CstTraceOut(); 
        return astNode;
    }

}
