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
        LocationNode locationNode = location(locationCST);

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
        LocationNode locationNode = location(locationCST);

        int increment = incrementCST.getFristChild.getType();
        plusAssignStmtNode = new PlusAssignStmtNode(locationNode, increment);
        traceOut(null); 
        return plusAssignStmtNode;
    }

    CallStmtNode callStmt(AST cstNode) {
        // cst: "method_call" node
        traceIn(cstNode);

        // CST
        AST childNode   = cstNode.getFirstChild();

        String methodName = childNode.getFirstChild().getText();
        HashMap<String, MethodDescriptor> methodST = pgmDesc.getMethodST();

        if(methodST.contains(methodName)) {
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
        
        // AST
        MethodCallStmtNode = methodCallStmtNode;

        String name;
        name = methodName.getText();
        methodCallStmtNode = new MethodCallStmtNode(name);

        // CST
        // expression list
        AST callExpr = methodName.getNextSibling().getFirstChild();

        while(callExpr != null) {

            ExpressionNode exprCall = expr(callNode);
            methodCallStmtNode.addExpr(exprCall);
            callExpr = callExpr.getNextSibling();
        }

        return methodCallStmt;
    }

    CalloutStmtNode calloutStmtNode(AST methodName) {
        // cst: "method_name" node
        traceIn(cstNode);

        // AST
        CalloutStmtNode calloutStmtNode;
        String name;

        name = methodName.getText();
        calloutStmtNode = new CalloutStmtNode(name);

        // CST
        //AST callExpr = methodName.getNextSibling().getFirstChild();

        return calloutStmtNode;
    }

    IfStmtNode ifStmt(AST ifCST) {
        // "if"
        traceIn(ifCST);

        // AST
        IfStmtNode ifStmtNode;

        // CST
        AST conditionExprCST;
        AST tBlockCST;
        AST fBlockCST;
        
        // CST condition node
        conditionExprCST = ifCST.getNextSibling();
        ExpressionNode conditionExpr = expr(conditionExprCST);
        
        // CST block node
        tBlockCST = conditionExprCST.getNextSibling();

        // build AST
        if(tBlockCST.getSibling() == null) {
            ifStmtNode = new IfStmtNode(condition, false);
            // true block
            AST tBlockChild = tBlockCST.getFirstChild();
            while(tBlockChild != null) {
                switch(tBlockChild.getType()) {
                    case FIELD_DECL:
                        FieldDeclNode fieldDeclNode = fieldDecl(tBlockChild);
                        ifStmtNode.addTFieldDecl(fieldDeclNode);
                        break;
                    case STATEMENT:
                        StatementNode stmtNode = statement(tBlockChild);
                        ifStmtNode.addTStmt(stmtNode);
                        break;
                }
                tBlockChild = tBlockChild.getNextSibling();
            }
            return ifStmtNode;
        } else {
            ifStmtNode = new IfStmtNode(condition, true);
            // true block
            AST tBlockChild = tBlockCST.getFirstChild();
            while(tBlockChild != null) {
                switch(tBlockChild.getType()) {
                    case FIELD_DECL:
                        FieldDeclNode fieldDeclNode = fieldDecl(tBlockChild);
                        ifStmtNode.addTFieldDecl(fieldDeclNode);
                        break;
                    case STATEMENT:
                        StatementNode stmtNode = statement(tBlockChild);
                        ifStmtNode.addTStmt(stmtNode);
                        break;
                }
                tBlockChild = tBlockChild.getNextSibling();
            }

            // false block
            fBlockCST = tBlockCST.getSibling().getSibling();
            AST fBlockChild = fBlockCST.getFirstChild();
            while(fBlockChild != null) {
                switch(tBlockChild.getType()) {
                    case FIELD_DECL:
                        FieldDeclNode fieldDeclNode = fieldDecl(fBlockChild);
                        ifStmtNode.addFFieldDecl(fieldDeclNode);
                        break;
                    case STATEMENT:
                        StatementNode stmtNode = statement(fBlockChild);
                        ifStmtNode.addFStmt(stmtNode);
                        break;
                }
                fBlockChild = fBlockChild.getNextSibling();
            }
            return ifStmtNode;
        }
        
    }

    ForStmtNode forStmt(AST cst) {
        // "for"
        traceIn(cstNode);

        // AST
        ForStmtNode forStmtNode;

        // CST
        AST intialIDCST      = cst.getNextSibling();
        AST initialExprCST   = initialIDCST.getNextSibling().getNextSibling();
        AST contitionExprCST = initialExprCST.getNextSibling();
        AST locationCST      = conditionExprCST.getNextSibling();
        AST blockCST;

        String initialID                  = initialIDCST.getText();
        ExpressionNode initialExprNode    = expr(intitalExprCST);
        ExpressionNode conditionNode      = expr(conditionExprCST);
        LocationNOde   updateLocationNode = location(locationCST);

        if(locationCST.getNextSibling().getType() == COMPOUND_ASSIGN_OP) {
            AST compoundAssignOpCST = locationCST.getNextSibling();
            AST updateExprCST       = compoundAssignOp.getNextSibling();
            blockCST                = updateExprCST.getNextSibling();
            ExpressionNode updatExprNode = expr(updateExprCST);
            forStmtNode = new ForStmtNode(initialID, initialExprNode, conditionNode,
                                          updateLocationNode, 
                                          compoundAssignOpCST.getFirstChild().getType(),
                                          updateExprNode);
        } else {
            AST incrementCST = locationCST.getNextSibling();
            blockCST         = incrementCST.getNextSibling();
            forStmtNode = new ForStmtNode(initialID, initialExprNode, conditionNode,
                                          updateLocationNode, 
                                          increment.getFirstChild().getType());
        }

        // block
        AST blockChild = blockCST.getFirstChild();
        while(blockChild != null) {
            switch(blockChild.getType()) {
                case FIELD_DECL:
                    FieldDeclNode fieldDeclNode = fieldDecl(blockChild);
                    forStmtNode.addFFieldDecl(fieldDeclNode);
                    break;
                case STATEMENT:
                    StatementNode stmtNode = statement(blockChild);
                    forStmtNode.addFStmt(stmtNode);
                    break;
            }
            blockChild = blockChild.getNextSibling();
        }
        return forStmtNode;

    }

    WhileStmtNode whileStmt(AST cst) {
        // "while"
        traceIn(cst);

        // AST
        WhileStmeNode whileStmtNode;

        // CST
        AST conditionCST = cst.getNextSibling();
        AST blockCST     = conditionCST.getNextSibling();

        ExpressionNode conditionNode = expr(conditionCST);

        whileStmeNode = new whildStmtNode(conditionNode);

        AST blockChild = blockCST.getFirstChild();
        while(blockChild != null) {
            switch(blockChild.getType()) {
                case FIELD_DECL:
                    FieldDeclNode fieldDeclNode = fieldDecl(blockChild);
                    whileStmtNode.addFFieldDecl(fieldDeclNode);
                    break;
                case STATEMENT:
                    StatementNode stmtNode = statement(blockChild);
                    whileStmtNode.addFStmt(stmtNode);
                    break;
            }
            blockChild = blockChild.getNextSibling();
        }
        return forStmtNode;
    }

    BreakStmtNode breakStmt(AST cst) {
        // "break"
        traceIn(cstNode);

        return new BreakStmtNode();
    }

    ReturnStmtNode returnStmt(AST cst) {
        // "return"
        traceIn(cstNode);

        // AST
        ReturnStmtNode returnNode;

        if(cst.getNextSibling() == null) {
            returnNode = new ReturnNode(null);    
        } else {
            AST returnExprCST = cst.getNextSibling();
            ExpressionNode returnExprNode = expr(returnExprCST);
            returnNode = new Return(returnExprNode);
        }

        return returnNode;
    }

    ExpressionNode expr(AST cst) {
        // "expr"
        traceIn(cst);

        //ASTNode astNode = null;
        AST childNode = cst.getFirstChild();

        switch(childNode.getType()) {
            case LOCATION:
                return location(childNode);
            case METHOD_CALL:
                return methodCallExpr(childNode);
            case LITERAL:
                return literalExpr(childNode);
            case EXPR:
                return expr(childNode);
            case BIN_OP:
                return binOpExpr(childNode);
            case MINUS:
                return minusExpr(childNode);
            case NOT:
                return notExpr(childNode);
            case TK_len:
                return lenExpr(childNode);
            default:
                traceIn(childNode);
                traceOut(null);
        }

        traceOut(null); 
        //return astNode;
    }

    LocationNode location(AST cstNode) {
        // "location"
        traceIn(cstNode);

        // AST
        Location locationNode;
        
        // CST
        AST childNode   = cstNode.getFirstChild();

        if(cstNode.getNumberOfChildren == 1) {
            locationNode = new LocationNOde(childNode.getText());
        } else {
            locationNode = new LocationNode(childNode.getText(), childNode.getNextSibling().getText());
        }

        traceOut(null); 
        return locationNode;
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
