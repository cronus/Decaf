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
                    ImportDeclNode importDeclNode = importDecl(cstChild);
                    root.addChild(importDeclNode);
                    break;
                case FIELD_DECL:
                    FieldDeclNode fieldDeclNode = fieldDecl(cstChild);
                    root.addChild(fieldDeclNode);
                    break;
                case METHOD_DECL:
                    MethodDeclNode methodDeclNode = methodDecl(cstChild);
                    root.addChild(methodDeclNode);
                    break;
            }
            cstChild = cstChild.getNextSibling();
        }

        traceOut(root); 
        // root.dump();
    }

    ImportDeclNode importDecl(AST cstNode) {
        // "import_decl"
        traceIn(cstNode);

        // CST node
        AST childNode   = cstNode.getFirstChild();

        // walk on CST tree 
        // collect necessary info and build AST
        childNode = childNode.getNextSibling();

        // AST node
        ImportDeclNode importDeclNode = new ImportDeclNode(childNode.getText())

        traceOut(importDeclNode); 
        return importDeclNode;
    }

    FieldDeclNode fieldDecl(AST cstNode) {
        // "field_decl"
        traceIn(cstNode);

        // AST node
        FieldDeclNode  fieldDeclNode;

        // CST node
        AST childNode   = cstNode.getFirstChild();

        // walk on CST tree 
        // collect necessary info and build AST
        
        // first child is type
        AST grandChild = child.getFristChild();
        TypeNode typeNode = new TypeNode(grandChild.getText(), grandChild.getType());
        type(childNode);

        // crate AST node
        fieldDeclNode = new FieldDeclNode(typeNode);

        // var declarations
        childNode   = childNode.getNextSibling();
        while(childNode.getType() != null) {
            // check if is array
            if(childNode.getNextSibling() != null && 
               childNode.getNextSibling().getType() == INTLITERAL) {

               // create IntLiteralNode
               IntLiteralNode width   = new IntLiteralNode(childNode.getNextSibling().getText());
               VarNode varNode = new VarDeclNode(childNode.getText(), width); 
               fieldDeclNode.addVar(varNode);

            } else {
               varNode = new VarDeclNode(childNode.getText()); 
               fieldDeclNode.addVar(varNode);
            }
            childNode   = childNode.getNextSibling();
        }


        traceOut(fieldDeclNode); 
        return fieldDeclNode;
    }

    MethodDeclNode methodDecl(AST cstNode) {
        // "method_decl"
        traceIn(cstNode);

        // AST node
        MethodDeclNode methodDeclNode;

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
        } else {
            TypeNode typeNode = null;
        }

        // method name
        childNode      = childNode.getNextSibling();

        // create AST node
        methodDeclNode = new MethodDeclNode(typeNode, childNode.getText());

        // parameter declaration
        childNode = childNode.getNextSibling();
        while(childNode.getType() != BLOCK) {
            switch(childNode.getType()) {
                case TYPE:
                    TypeNode paraType   = new TypeNode(childNode.getText());
                    VarDeclNode paraVar = new VarDeclNode(childNode.getNextSibling().getText());
                    ParaDeclNode paraDeclNode = new ParaDeclNode(paraType, paraVar);
                    methodDeclNode.addPara(paraDeclNode);
                    type(childNode);
                    break;
                // ID token
                default:
                    traceIn(childNode);
                    traceOut(null);
            }
            childNode = childNode.getNextSibling();
        }

        // block 
        // block(childNode, methodDeclNode);
        grandChildNode = childNode.getFirstChild();
        while(grandChildNode != null) {
            switch(grandChildNode.getType()) {
                case FIELD_DECL:
                    FieldDeclNode fieldDeclNode = fieldDecl(cstChild);
                    methodDeclNode.addFieldDecl(fieldDeclNode);
                    break;
                case STATEMENT:
                    StatementNode stmtNode = statement(grandChild);
                    methodDeclNode.addStmt(stmtNode);
                    break;
            }
            grandChildNode = grandChildNode.getNextSibling();
        }

        traceOut(methodDeclNode); 
        return methodDeclNode;
    }

    void type(AST cstNode) {
        traceIn(cstNode);
        ASTNode astNode = null;

        AST childNode   = cstNode.getFirstChild();
        traceIn(childNode);
        traceOut(null);

        traceOut(null); 
    }

    StatementNode statement(AST cstNode) {
        // "statement"
        traceIn(cstNode);

        // CST node
        AST childNode   = cstNode.getFirstChild();

        switch(childNode.getType()) {
            case LOCATION:
                if(childNode.getNextSibling().getFirstChild().getType() == ASSIGN_OP) {
                    return assign(childNode);
                } else {
                    return plusAssign(childNode);
                }
            case METHOD_CALL:
                return callStmt(childNode);
            case TK_if:
                return = ifStmt(childNode);
            case TK_for:
                return forStmt(childNode);
            case TK_while:
                return whileStmt(childNode);
            case TK_break:
                return breakStmt(childNode);
            case TK_continue:
                return returnStmt(childNode);
        }

        traceOut(null); 
    }

    AssignStmtNode assign(AST locationCST) {
        // "location"
        traceIn(locationCST);

        // AST
        AssignStmtNode assignStmtNode; 

        // CST
        AST assignOpCST   = locationCST.getNextSibling().getFirstChild();
        AST assignExprCST = assignOpCST.getNextSibling();

        // AST location node
        if(locationCST.getNumberOfChildren() == 1) {
            // scalar
            LocationNode locationNode = new LocationNode(locationCST.getFirstChild().getText());
        } else {
            // array element
            LocationNode locationNode = new LocationNode(locationCST.getFirstChild().getText(),
                                            locationCST.getFirstChild().getNextSibling().getText());
        }

        // assignOp
        int assignOp;
        if(assignOpCST.getFirstChild().getType == EQ) {
            assignOp = EQ;
        } else {
            assignOpCST = assignOpCST.getFirstChild();
            assignOp    = assignOpCST.getType(); 
        }

        // assignExpr
        ExpressionNode assignExprNode = expr(assignExprCST);
        
        // build AST
        assignStmtNode = new AssignStmtNode(assignOp, locationNode, assignExprNode);

        traceOut(null); 
        return assignStmtNode;
    }

    PlusAssignStmtNode plusAssign(AST locationCST) {
        // "location"
        traceIn(locationCST);

        // AST
        PlusAssignStmtNode plusAssignStmtNode;

        // CST
        AST incrementCST = locationCST.getNextSibling().getFirstChild();  
      
        // AST location node
        if(locationCST.getNumberOfChildren() == 1) {
            // scalar
            LocationNode locationNode = new LocationNode(locationCST.getFirstChild().getText());
        } else {
            // array element
            LocationNode locationNode = new LocationNode(locationCST.getFirstChild().getText(),
                                            locationCST.getFirstChild().getNextSibling().getText());
        }

        int increment = incrementCST.getFristChild.getType();
        plusAssignStmtNode = new PlusAssignStmtNode(locationNode, increment);
        traceOut(null); 
        return plusAssignStmtNode;
    }

    CallStmtNode callStmt(AST cstNode) {
        // cst: "method_call" node
        traceIn(cstNode);

        AST childNode   = cstNode.getFirstChild();

        if(childNode.getNextSibling().getType() == EXPR) {
            return methodCallStmt(childNode);
        } else {
            return calloutStmt(childNode);
        }

        traceOut(null); 
        //return astNode;
    }

    MethodCallStmtNode methodCallStmt(AST methodName) {
        // cst: "method_name" node
        traceIn(cstNode);
        
        // CST
        AST callExpr = methodName.getNextSibling();


    }

    CalloutStmtNode calloutStmtNode(AST methodName) {
        // cst: "method_name" node
        traceIn(cstNode);

    }

    IfStmtNode ifStmt(AST cst) {
    }

    ForStmtNode forStmt(AST cst) {
    }

    WhileStmtNode whildStmt(AST cst) {
    }

    BreakStmtNode breakStmt(AST cst) {
    }

    ReturnStmtNode returnStmt(AST cst) {
    }

    ExpressionNode expr(AST cstNode) {
        traceIn(cstNode);

        //ASTNode astNode = null;
        AST childNode   = cstNode.getFirstChild();

        switch(childNode.getType()) {
            case LOCATION:
                return locationExpr(childNode);
            case METHOD_CALL:
                return methodCallExpr(childNode);
            case LITERAL:
                literalExpr(childNode);
            case EXPR:
                expr(childNode);
            case BIN_OP:
                binOpExpr(childNode);
            case MINUS:
                minusExpr(childNode);
            case NOT:
                notExpr(childNode);
            case TK_len:
                lenExpr(childNode);
            default:
                traceIn(childNode);
                traceOut(null);
        }

        traceOut(null); 
        //return astNode;
    }

    LocationExprNode locationExpr(AST cstNode) {
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
    }

    MethodCallExprNode methodCallExpr(AST cst) {
    }

    LiteralExprNode literalExpr(AST cstNode) {
        traceIn(cstNode);

        AST childNode = cstNode.getFirstChild();

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

    BinOpExprNode binOpExpr(AST cstNode) {
        traceIn(cstNode);

        //ASTNode astNode = null;
        AST childNode   = cstNode.getFirstChild();

        traceOut(null); 
        //return astNode;
    }

    MinusExprNode minusExpr(AST cst) {
    }

    NotExprNode notExpr(AST cst) {
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
