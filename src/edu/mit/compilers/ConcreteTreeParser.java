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

    private HashMap<Integer, Integer> priorityHT;

    // after parsing CST, generate
    // 1. AST
    // 2. Symbol Table
    protected ProgramNode  root;
    protected ProgramDescriptor pgmDesc;

    public ConcreteTreeParser(CommonAST parseTree) {
        this.parseTree  = parseTree;
        this.debug      = false;
        this.traceDepth = 0;

        // initialize priority hash table

        root    = new ProgramNode();
        pgmDesc = new ProgramDescriptor();

        priorityHT = new HashMap<Integer, Integer>();
        priorityHT.put(MINUS, 0);
        priorityHT.put(NOT, 0);
        priorityHT.put(MULT, 1);
        priorityHT.put(DIV, 1);
        priorityHT.put(MOD, 1);
        priorityHT.put(PLUS, 2);
        priorityHT.put(MINUS, 2);
        priorityHT.put(LT, 3);
        priorityHT.put(LE, 3);
        priorityHT.put(GE, 3);
        priorityHT.put(GT, 3);
        priorityHT.put(EQ, 4);
        priorityHT.put(NEQ, 4);
        priorityHT.put(AND, 5);
        priorityHT.put(OR, 6);
        priorityHT.put(QUESTION, 7);
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
                    root.addImportDecl(importDeclNode);
                    break;
                case FIELD_DECL:
                    FieldDeclNode fieldDeclNode = fieldDecl(cstChild);
                    root.addFieldDecl(fieldDeclNode);
                    break;
                case METHOD_DECL:
                    MethodDeclNode methodDeclNode = methodDecl(cstChild);
                    root.addMethodDecl(methodDeclNode);
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
        ImportDeclNode importDeclNode = new ImportDeclNode(childNode.getText());

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
        AST grandChild = childNode.getFirstChild();
        TypeNode typeNode = new TypeNode(grandChild.getText(), grandChild.getType());
        type(childNode);

        // crate AST node
        fieldDeclNode = new FieldDeclNode(typeNode);

        // var declarations
        childNode   = childNode.getNextSibling();
        while(childNode != null) {
            // check if is array
            if(childNode.getNextSibling() != null && 
               childNode.getNextSibling().getType() == INTLITERAL) {

               // create IntLiteralNode
               IntLiteralNode width   = new IntLiteralNode(Integer.parseInt(childNode.getNextSibling().getText()));
               VarDeclNode varDeclNode = new VarDeclNode(childNode.getText(), width); 
               fieldDeclNode.addVar(varDeclNode);

            } else {
               VarDeclNode varDeclNode = new VarDeclNode(childNode.getText()); 
               fieldDeclNode.addVar(varDeclNode);
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
        TypeNode       typeNode;

        // CST node
        AST childNode  = cstNode.getFirstChild();
        AST grandChildNode;

        // walk on CST tree 
        // collect necessary info and build AST

        // method return type
        // if first chidl is TYPE type, add a TypeNode
        // else the return type is void, methodDeclNode.typeNode = null
        if(childNode.getType() == TYPE) {
            typeNode = new TypeNode(childNode.getText(), childNode.getType());
        } else {
            typeNode = null;
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
                    TypeNode paraType   = new TypeNode(childNode.getText(), childNode.getType());
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
                    FieldDeclNode fieldDeclNode = fieldDecl(grandChildNode);
                    methodDeclNode.addFieldDecl(fieldDeclNode);
                    break;
                case STATEMENT:
                    StatementNode stmtNode = statement(grandChildNode);
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
                return ifStmt(childNode);
            case TK_for:
                return forStmt(childNode);
            case TK_while:
                return whileStmt(childNode);
            case TK_break:
                return breakStmt(childNode);
            case TK_continue:
                return returnStmt(childNode);
            default:
                System.out.println("not expected statement node");
                System.exit(1);
                return null;
        }

        //traceOut(null); 
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
        if(assignOpCST.getFirstChild().getType() == EQ) {
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

        int increment = incrementCST.getFirstChild().getType();
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

        if(methodST.containsKey(methodName)) {
            return methodCallStmt(childNode);
        } else {
            return calloutStmt(childNode);
        }

        //traceOut(null); 
    }

    MethodCallStmtNode methodCallStmt(AST methodName) {
        // cst: "method_name" node
        traceIn(methodName);
        
        // AST
        MethodCallStmtNode methodCallStmtNode;

        String name;
        name = methodName.getText();
        methodCallStmtNode = new MethodCallStmtNode(name);

        // CST
        // expression list
        AST callExpr = methodName.getNextSibling().getFirstChild();

        while(callExpr != null) {

            ExpressionNode exprCall = expr(callExpr);
            methodCallStmtNode.addExpr(exprCall);
            callExpr = callExpr.getNextSibling();
        }

        return methodCallStmtNode;
    }

    CalloutStmtNode calloutStmt(AST methodName) {
        // cst: "method_name" node
        traceIn(methodName);

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
        if(tBlockCST.getNextSibling() == null) {
            ifStmtNode = new IfStmtNode(conditionExpr, false);
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
            ifStmtNode = new IfStmtNode(conditionExpr, true);
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
            fBlockCST = tBlockCST.getNextSibling().getNextSibling();
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
        traceIn(cst);

        // AST
        ForStmtNode forStmtNode;

        // CST
        AST initialIDCST     = cst.getNextSibling();
        AST initialExprCST   = initialIDCST.getNextSibling().getNextSibling();
        AST conditionExprCST = initialExprCST.getNextSibling();
        AST locationCST      = conditionExprCST.getNextSibling();
        AST blockCST;

        String initialID                  = initialIDCST.getText();
        ExpressionNode initialExprNode    = expr(initialExprCST);
        ExpressionNode conditionNode      = expr(conditionExprCST);
        LocationNode   updateLocationNode = location(locationCST);

        if(locationCST.getNextSibling().getType() == COMPOUND_ASSIGN_OP) {
            AST compoundAssignOpCST = locationCST.getNextSibling();
            AST updateExprCST       = compoundAssignOpCST.getNextSibling();
            blockCST                = updateExprCST.getNextSibling();
            ExpressionNode updateExprNode = expr(updateExprCST);
            forStmtNode = new ForStmtNode(initialID, initialExprNode, conditionNode,
                                          updateLocationNode, 
                                          compoundAssignOpCST.getFirstChild().getType(),
                                          updateExprNode);
        } else {
            AST incrementCST = locationCST.getNextSibling();
            blockCST         = incrementCST.getNextSibling();
            forStmtNode = new ForStmtNode(initialID, initialExprNode, conditionNode,
                                          updateLocationNode, 
                                          incrementCST.getFirstChild().getType());
        }

        // block
        AST blockChild = blockCST.getFirstChild();
        while(blockChild != null) {
            switch(blockChild.getType()) {
                case FIELD_DECL:
                    FieldDeclNode fieldDeclNode = fieldDecl(blockChild);
                    forStmtNode.addFieldDecl(fieldDeclNode);
                    break;
                case STATEMENT:
                    StatementNode stmtNode = statement(blockChild);
                    forStmtNode.addStmt(stmtNode);
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
        WhileStmtNode whileStmtNode;

        // CST
        AST conditionCST = cst.getNextSibling();
        AST blockCST     = conditionCST.getNextSibling();

        ExpressionNode conditionNode = expr(conditionCST);

        whileStmtNode = new WhileStmtNode(conditionNode);

        AST blockChild = blockCST.getFirstChild();
        while(blockChild != null) {
            switch(blockChild.getType()) {
                case FIELD_DECL:
                    FieldDeclNode fieldDeclNode = fieldDecl(blockChild);
                    whileStmtNode.addFieldDecl(fieldDeclNode);
                    break;
                case STATEMENT:
                    StatementNode stmtNode = statement(blockChild);
                    whileStmtNode.addStmt(stmtNode);
                    break;
            }
            blockChild = blockChild.getNextSibling();
        }
        return whileStmtNode;
    }

    BreakStmtNode breakStmt(AST cst) {
        // "break"
        traceIn(cst);

        return new BreakStmtNode();
    }

    ReturnStmtNode returnStmt(AST cst) {
        // "return"
        traceIn(cst);

        // AST
        ReturnStmtNode returnNode;

        if(cst.getNextSibling() == null) {
            returnNode = new ReturnStmtNode(null);    
        } else {
            AST returnExprCST = cst.getNextSibling();
            ExpressionNode returnExprNode = expr(returnExprCST);
            returnNode = new ReturnStmtNode(returnExprNode);
        }

        return returnNode;
    }


    AST appendSibling(AST firstSibling, AST lastSibling) {
        AST sibling = firstSibling;
        AST prevSibling = null;

        while(sibling != null) {
            prevSibling = sibling;
            sibling = sibling.getNextSibling(); 
        }

        if(prevSibling != null) {
            prevSibling.setNextSibling(lastSibling);
        } else {
            firstSibling = lastSibling;
        }
        System.out.println("func: appendSibling: ");
        System.out.println("\tfirstSibling: " + firstSibling);

        return firstSibling;
    }

    CSTTree findLowestOp(AST childNode) {
        
        if(debug) {
            System.out.println("func: findLowestOp");
            System.out.println("\tchildNode: " + childNode);
        }
    
        AST lowPriorityOp   = null;
        AST leftFirstChild  = null;
        AST rightFirstChild = null;

        while(childNode != null) {
          
            switch(childNode.getType()) {
                // find lowest priority
                case QUESTION:
                    if(rightFirstChild != null) {
                        appendSibling(leftFirstChild, lowPriorityOp);
                        lowPriorityOp.setNextSibling(rightFirstChild);
                    } 
                    lowPriorityOp = childNode;
                    break;
                case LOCATION:
                case METHOD_CALL:
                case LITERAL:
                case EXPR:
                case ID:
                case TK_len:
                    if(lowPriorityOp == null) {
                        leftFirstChild = appendSibling(leftFirstChild, childNode);
                    } else {
                        rightFirstChild = appendSibling(rightFirstChild, childNode);
                    }
                    //System.out.println(leftFirstChild);
                    //System.out.println(rightFirstChild);
                    break;
                case MINUS:
                case NOT:
                    if(lowPriorityOp == null) {
                        lowPriorityOp = childNode;
                    }
                    break;
                case BIN_OP:
                    if(lowPriorityOp == null) {
                        lowPriorityOp = childNode;
                    } else if(priorityHT.get(lowPriorityOp.getType()) > priorityHT.get(childNode.getType())) {
                        lowPriorityOp = childNode;
                        appendSibling(leftFirstChild, lowPriorityOp);
                        lowPriorityOp.setNextSibling(rightFirstChild);
                    } else {
                        appendSibling(rightFirstChild, childNode);
                    }
                    break;
                default:
                    traceIn(childNode);
                    traceOut(null);
            }

            childNode = childNode.getNextSibling();
        }

        if(debug) {
            System.out.println("func: findLowestOp out");
            System.out.println("\tlowPriorityOp: " + lowPriorityOp);
            System.out.println("\tleftFirstChild: " + leftFirstChild);
            System.out.println("\trightFirstChild: " + rightFirstChild);
        }
        return new CSTTree(lowPriorityOp, leftFirstChild, rightFirstChild);
    }

    ExpressionNode findExpr(AST lowPriorityOp, AST leftFirstChild, AST rightFirstChild) {
        
        if(debug) {
            System.out.println("func: findExpr");
            System.out.println("\tlowPriorityOp: " + lowPriorityOp);
            System.out.println("\tleftFirstChild: " + leftFirstChild);
            System.out.println("\trightFirstChild: " + rightFirstChild);
        }
        if(lowPriorityOp != null) {
            switch(lowPriorityOp.getType()) {
                // tenery operator
                case QUESTION:
                    return tenaryExpr(leftFirstChild, rightFirstChild);
                // binary operator
                case BIN_OP:
                    return binOpExpr(leftFirstChild, rightFirstChild, lowPriorityOp);
                case NOT:
                    return notExpr(rightFirstChild);
                case MINUS:
                    return minusExpr(rightFirstChild);
                default:
                    System.out.println("not exptected operator!");
                    System.exit(1);
                    return null;

            }
        } else {
            switch(leftFirstChild.getType()) {
                case LOCATION:
                    return location(leftFirstChild);
                case METHOD_CALL:
                    return callExpr(leftFirstChild);
                case LITERAL:
                    return literalExpr(leftFirstChild);
                case TK_len:
                    return lenExpr(leftFirstChild);
                default:
                    System.out.println("not exptected operator!");
                    System.exit(1);
                    return null;
            }
        }
    }

    ExpressionNode expr(AST cst) {
        // "expr"
        traceIn(cst);

        // AST
        ExpressionNode currentExprNode;
        ExpressionNode lastExprNode;

        // CST nodes
        AST childNode = cst.getFirstChild();
        AST leftFirstChild;
        AST rightFirstChild;
        AST lowPriorityOp;

        CSTTree lowOp;

        lowOp = findLowestOp(childNode);

        lowPriorityOp   = lowOp.getRoot();
        leftFirstChild  = lowOp.getLhs();
        rightFirstChild = lowOp.getRhs();

        System.out.println(lowPriorityOp);
        System.out.println(leftFirstChild);
        System.out.println(rightFirstChild);
 
        return findExpr(lowPriorityOp, leftFirstChild, rightFirstChild);

        //traceOut(null); 
        //return currentNode;
    }

    LocationNode location(AST cstNode) {
        // "location"
        traceIn(cstNode);

        // AST
        LocationNode locationNode;
        
        // CST
        AST childNode   = cstNode.getFirstChild();

        if(cstNode.getNumberOfChildren() == 1) {
            System.out.println("aaa");
            locationNode = new LocationNode(childNode.getText());
            System.out.println("aaa");
        } else {
            ExpressionNode indexExprNode = expr(childNode.getNextSibling());
            locationNode = new LocationNode(childNode.getText(), indexExprNode);
            System.out.println("bbb");
        }

        traceOut(null); 
        return locationNode;
    }

    CallExprNode callExpr(AST cstNode) {
        // cst: "method_call" node
        traceIn(cstNode);

        // CST
        AST childNode   = cstNode.getFirstChild();

        String methodName = childNode.getFirstChild().getText();
        HashMap<String, MethodDescriptor> methodST = pgmDesc.getMethodST();

        if(methodST.containsKey(methodName)) {
            return methodCallExpr(childNode);
        } else {
            return calloutExpr(childNode);
        }

        //traceOut(null); 
    }

    MethodCallExprNode methodCallExpr(AST methodName) {
        // cst: "method_name" node
        traceIn(methodName);
        
        // AST
        MethodCallExprNode methodCallExprNode;

        String name;
        name = methodName.getText();
        methodCallExprNode = new MethodCallExprNode(name);

        // CST
        // expression list
        AST callExpr = methodName.getNextSibling().getFirstChild();

        while(callExpr != null) {

            ExpressionNode exprCall = expr(callExpr);
            methodCallExprNode.addExpr(exprCall);
            callExpr = callExpr.getNextSibling();
        }

        return methodCallExprNode;
    }

    CalloutExprNode calloutExpr(AST methodName) {
        // cst: "method_name" node
        traceIn(methodName);

        // AST
        CalloutExprNode calloutExprNode;
        String name;

        name = methodName.getText();
        calloutExprNode = new CalloutExprNode(name);

        // CST
        //AST callExpr = methodName.getNextSibling().getFirstChild();

        return calloutExprNode;
    }

    TenaryExprNode tenaryExpr(AST leftFirstChild, AST rightFirstChild) {
        traceIn(leftFirstChild);

        // AST
        TenaryExprNode tenaryExprNode;
        ExpressionNode conditionExprNode;
        ExpressionNode trueExprNode;
        ExpressionNode falseExprNode;
        
        // CST
        AST trueFirstChild         = rightFirstChild;
        AST falseFirstChild        = null;

        AST conditionLowPriorityOp = null;
        AST conditionLeft          = null;
        AST conditionRight         = null;

        AST trueLowPriorityOp      = null;
        AST trueLeft               = null;
        AST trueRight              = null;

        AST falseLowPriorityOp     = null;
        AST falseLeft              = null;
        AST falseRight             = null;

        CSTTree conditionLowOp;
        CSTTree trueLowOp;
        CSTTree falseLowOp;

        // find true expr and false expr, separated by :
        AST rightChild = rightFirstChild;
        AST prevChild  = null;
        while(rightChild != null) {
            if(rightChild.getType() == COLON) {
                falseFirstChild = rightChild.getNextSibling();
                prevChild.setNextSibling(null);
                break;
            }
            prevChild  = rightChild; 
            rightChild = rightChild.getNextSibling();
        }

        conditionLowOp = findLowestOp(leftFirstChild);
        
        conditionLowPriorityOp = conditionLowOp.getRoot();
        conditionLeft          = conditionLowOp.getLhs();
        conditionRight         = conditionLowOp.getRhs();

        trueLowOp      = findLowestOp(trueFirstChild);

        trueLowPriorityOp = trueLowOp.getRoot();
        trueLeft          = trueLowOp.getLhs();
        trueRight         = trueLowOp.getRhs();

        if(falseFirstChild != null) {
            falseLowOp     = findLowestOp(falseFirstChild);

            falseLowPriorityOp = falseLowOp.getRoot();
            falseLeft          = falseLowOp.getLhs();
            falseRight         = falseLowOp.getRhs();
        }

        conditionExprNode = findExpr(conditionLowPriorityOp, conditionLeft, conditionRight);
        trueExprNode      = findExpr(trueLowPriorityOp, trueLeft, trueRight);
        falseExprNode     = findExpr(falseLowPriorityOp, falseLeft, falseRight);
        tenaryExprNode = new TenaryExprNode(conditionExprNode, trueExprNode, falseExprNode);

        return tenaryExprNode;

    }

    BinopExprNode binOpExpr(AST leftFirstChild, AST rightFirstChild, AST operatorCST) {
        traceIn(operatorCST);

        // AST
        BinopExprNode binopExprNode;
        ExpressionNode leftExpr;
        ExpressionNode rightExpr;

        // CST
        AST leftLowPriorityOp  = null;
        AST leftL              = null;
        AST rightL             = null;

        AST rightLowPriorityOp = null;
        AST leftR              = null;
        AST rightR             = null;

        CSTTree leftLowOp;
        CSTTree rightLowOp;

        int operator = operatorCST.getFirstChild().getFirstChild().getType();

        leftLowOp  = findLowestOp(leftFirstChild);
        rightLowOp = findLowestOp(rightFirstChild);

        leftLowPriorityOp = leftLowOp.getRoot();
        leftL             = leftLowOp.getLhs();
        leftR             = leftLowOp.getRhs();

        rightLowPriorityOp = rightLowOp.getRoot();
        leftR              = rightLowOp.getLhs();
        rightR             = rightLowOp.getRhs();

        leftExpr  = findExpr(leftLowPriorityOp, leftL, rightL);
        rightExpr = findExpr(rightLowPriorityOp, leftR, rightR);

        binopExprNode = new BinopExprNode(operator, leftExpr, rightExpr);
         
        traceOut(null); 
        return binopExprNode;
    }

    MinusExprNode minusExpr(AST firstChild) {
        // "-"
        traceIn(firstChild);

        // AST
        MinusExprNode minusExprNode;
        ExpressionNode exprNode;
        
        // CST
        AST exprCST = firstChild.getNextSibling();

        exprNode = expr(exprCST);
        minusExprNode = new MinusExprNode(exprNode);

        return minusExprNode;
    }

    NotExprNode notExpr(AST firstChild) {
        // "!"
        traceIn(firstChild);

        // AST
        NotExprNode notExpr;
        ExpressionNode exprNode;

        // CST
        AST exprCST = firstChild.getNextSibling();

        exprNode = expr(exprCST);
        notExpr  = new NotExprNode(exprNode);

        return notExpr;
    }

    LenExprNode lenExpr(AST firstChild) {
        // "len"
        traceIn(firstChild);

        // AST
        LenExprNode lenExprNode;

        // CST
        AST idCST = firstChild.getNextSibling();

        lenExprNode = new LenExprNode(idCST.getText());

        return lenExprNode;
    }

    LiteralNode literalExpr(AST cst) {
        traceIn(cst);

        if(cst.getType() == INTLITERAL) {
            return new IntLiteralNode(Integer.parseInt(cst.getText()));
        } else if(cst.getType() == BOOL_LITERAL) {
            return new BoolLiteralNode(Boolean.parseBoolean(cst.getText()));
        } else if(cst.getType() == CHARLITERAL) {
            return new CharLiteralNode(cst.getText().charAt(0));
        } else {
            return new StringLiteralNode(cst.getText());
        }
    }
}

class CSTTree {

    private AST root;
    private AST lhs;
    private AST rhs;

    CSTTree(AST root, AST lhs, AST rhs) {
        this.root = root;
        this.lhs  = lhs;
        this.rhs  = rhs;
    }

    AST getRoot() {
        return root;
    }

    AST getLhs() {
        return lhs;
    }

    AST getRhs() {
        return rhs;
    }
}
