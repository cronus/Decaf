package edu.mit.compilers;

import java.io.*;
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

    public ConcreteTreeParser(CommonAST parseTree) {
        this.parseTree  = parseTree;
        this.debug      = false;
        this.traceDepth = 0;
    }


    public void setTrace(boolean debug) {
        this.debug = debug;
    }

    public void traceIn(AST cstNode) {
        if(!this.debug) {
            return;
        }
        for(int i = 0; i < traceDepth; i ++) {
            System.out.printf("\t");
        }
        System.out.println(cstNode.getText());

        traceDepth += 1;
    }

    public void traceOut(ASTNode astNode) {
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
        traceIn(parseTree);

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

        traceOut(n); 
        return n;
    }

    ASTNode importDecl(AST cstNode) {
        traceIn(cstNode);

        ASTNode astNode = new ImportDeclNode();
        AST childNode   = cstNode.getFirstChild();

        while(childNode != null) {
            //System.out.println(childNode.getType()); 
            //switch(childNode.getType()) {
            //    case TK_import:
            //        break;
            //    case ID:
            //        id(childNode);
            //        break;
            //}
            traceIn(childNode);
            traceOut(null);
            childNode = childNode.getNextSibling();
        }

        traceOut(astNode); 
        return astNode;
    }

    ASTNode fieldDecl(AST cstNode) {
        traceIn(cstNode);

        ASTNode astNode = null;
        AST childNode   = cstNode.getFirstChild();

        while(childNode != null) {
            switch(childNode.getType()) {
                case TYPE:
                    type(childNode);
                    break;
                default:
                    traceIn(childNode);
                    traceOut(null);
            }
            childNode = childNode.getNextSibling();
        }

        traceOut(astNode); 
        return astNode;
    }

    ASTNode methodDecl(AST cstNode) {
        traceIn(cstNode);

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
                    traceIn(childNode);
                    traceOut(null);
            }
            childNode = childNode.getNextSibling();
        }

        traceOut(astNode); 
        return astNode;
    }

    ASTNode type(AST cstNode) {
        traceIn(cstNode);
        ASTNode astNode = null;

        AST childNode   = cstNode.getFirstChild();
        traceIn(childNode);
        traceOut(null);

        traceOut(astNode); 
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

        traceOut(astNode); 
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

        traceOut(astNode); 
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

        traceOut(astNode); 
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

        traceOut(astNode); 
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

        traceOut(astNode); 
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

        traceOut(astNode); 
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

        traceOut(astNode); 
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

        traceOut(astNode); 
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

        traceOut(astNode); 
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

        traceOut(astNode); 
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

        traceOut(astNode); 
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

        traceOut(astNode); 
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

        traceOut(astNode); 
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

        traceOut(astNode); 
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

        traceOut(astNode); 
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

        traceOut(astNode); 
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

        traceOut(astNode); 
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

        traceOut(astNode); 
        return astNode;
    }

}
