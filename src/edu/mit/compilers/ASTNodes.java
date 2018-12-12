package edu.mit.compilers;

import java.io.*;
import java.util.*;
import antlr.Token;
import antlr.CommonAST;
import antlr.collections.AST;
import edu.mit.compilers.grammar.*;
import edu.mit.compilers.tools.CLI;
import edu.mit.compilers.tools.CLI.Action;

abstract class ASTNode implements DecafParserTokenTypes {

    protected final String nodeName;
    protected final int    nodeType;

    protected int   lineNo;
    protected int   columnNo;

    ASTNode(String name, int type) {
        this.nodeName = name;
        this.nodeType = type;
    }

    String getText() {
        return nodeName;
    }
    int getType() {
        return nodeType;
    }

    int getLineNo() {
        return lineNo;
    }

    int getColumnNo() {
        return columnNo;
    }

    abstract void dump();
}


abstract class ExpressionNode extends ASTNode {
    
    ExpressionNode(String name, int type) {
        super(name, type);
    }

    void dump() {
    }
}

abstract class LiteralNode extends ExpressionNode {

    LiteralNode(String name, int type) {
        super(name, type);
    }

    void dump() {
        System.out.println("AST: " + nodeName);
    }

}

class IntLiteralNode extends LiteralNode {

    private final int num;

    IntLiteralNode(int num) {
        super("int_literal", INTLITERAL);
        this.num = num;
    }

}

class BoolLiteralNode extends LiteralNode {

    private final boolean value;

    BoolLiteralNode(boolean value) {
        super("bool_literal", BOOL_LITERAL);

        this.value = value;
    }

}

class CharLiteralNode extends LiteralNode {

    private final char c;
    CharLiteralNode(char c) {
        super("char_literal", CHARLITERAL);

        this.c = c;
    }
}

class StringLiteralNode extends LiteralNode {
    
    private final String content;
    StringLiteralNode(String content) {
        super("string_literal", STRINGLITERAL);

        this.content = content;
    }
}

class LocationNode extends ExpressionNode {
    
    private final String name;
    private final ExpressionNode index;
    private final boolean isArrayElement;

    LocationNode(String name) {
        super("location", LOCATION);
        this.name           = name;
        this.index          = null;
        this.isArrayElement = false;
    }

    LocationNode(String name, ExpressionNode index) {
        super("location", LOCATION);
        this.name           = name;
        this.index          = index;
        this.isArrayElement = true;
    }

    boolean isArrayElement() {
        return isArrayElement;
    }

    String getLocationName() {
        return name;
    }

    ExpressionNode getLocationIndex() {
        return index;
    }
}

class CallExprNode extends ExpressionNode {

    CallExprNode(String name, int type) {
        super(name, type);
    }

}

class MethodCallExprNode extends CallExprNode {

    private final String methodName;
    private final ArrayList<ExpressionNode> expressionNodes;
    
    MethodCallExprNode(String methodName) {
        super("methodCall", METHOD_CALL);

        this.methodName      = methodName;
        this.expressionNodes = new ArrayList<ExpressionNode>();
    }

    void addExpr(ExpressionNode expr) {
        expressionNodes.add(expr);
    }

    String getMethodName() {
        return methodName;
    }

    ArrayList<ExpressionNode> getExpressionNodes() {
        return expressionNodes;
    }
}

class CalloutExprNode extends CallExprNode {

    private final String methodName;
    
    CalloutExprNode(String methodName) {
        super("callout", METHOD_CALL);

        this.methodName = methodName;
    }

    String getMethodName() {
        return methodName;
    }
}

class BinopExprNode extends ExpressionNode {

    private final int          operator;
    private final ExpressionNode lhs;
    private final ExpressionNode rhs;

    BinopExprNode(int operator, ExpressionNode lhs, ExpressionNode rhs) {
        super("bin_op", BIN_OP);

        this.operator = operator;
        this.lhs      = lhs;
        this.rhs      = rhs;
    }

}

class MinusExprNode extends ExpressionNode {

    private final ExpressionNode expr;

    MinusExprNode(ExpressionNode expr) {
        super("minum", MINUS);

        this.expr = expr;
    }
}

class NotExprNode extends ExpressionNode {

    private final ExpressionNode expr;
    
    NotExprNode(ExpressionNode expr) {
        super("not", NOT);

        this.expr = expr;
    }
}

class LenExprNode extends ExpressionNode {
    
    private final String id;
    LenExprNode(String id) {
        super("len", TK_len);

        this.id = id;
    }
}

class TenaryExprNode extends ExpressionNode {

    private ExpressionNode conditionExprNode;
    private ExpressionNode trueExprNode;
    private ExpressionNode falseExprNode;

    TenaryExprNode(ExpressionNode conditionExprNode,
                   ExpressionNode trueExprNode,
                   ExpressionNode falseExprNode) {
        super("tenary", QUESTION);

        this.conditionExprNode = conditionExprNode;
        this.trueExprNode      = trueExprNode;
        this.falseExprNode     = falseExprNode;
    }


}

abstract class StatementNode extends ASTNode {

    StatementNode(String name, int type) {
        super(name, type);
    }

    void dump() {
    }
}


class AssignStmtNode extends StatementNode {
    
    private final int            assignOp;
    private final LocationNode   lhs;
    private final ExpressionNode rhs;

    AssignStmtNode(int assignOp, LocationNode locationNode, ExpressionNode expressionNode) {
        super("assign", ASSIGN_EXPR);

        this.assignOp = assignOp;
        this.lhs      = locationNode;
        this.rhs      = expressionNode;
    }

//    void addLocation(LocationNode location) {
//        this.lhs = location;
//    }
//
//    void addExpression(Expression expr) {
//        this.rhs = expr;
//    }

    int getAssignOp() {
        return assignOp;
    }

    LocationNode getLocation() {
        return lhs;
    }

    ExpressionNode getExpression() {
        return rhs;
    }

}

class PlusAssignStmtNode extends StatementNode {

    private final LocationNode locationNode;
    private final int increment;

    PlusAssignStmtNode(LocationNode ln, int increment) {
        super("plus_assign", PLUS_ASSIGN);

        this.locationNode = ln;
        this.increment    = increment;
    }

//    void addLocation(LocationNode location) {
//        this.location = location;
//    }
    
    LocationNode getLocation() {
        return locationNode;
    }

    int getIncrement() {
        return increment;
    }

}

abstract class CallStmtNode extends StatementNode {

    CallStmtNode(String name, int type) {
        super(name, type);
    }
}

class MethodCallStmtNode extends CallStmtNode {

    private final String methodName;
    private final ArrayList<ExpressionNode> expressionNodes;
    
    MethodCallStmtNode(String methodName) {
        super("methodCall", METHOD_CALL);

        this.methodName      = methodName;
        this.expressionNodes = new ArrayList<ExpressionNode>();
    }

    void addExpr(ExpressionNode expr) {
        expressionNodes.add(expr);
    }

    String getMethodName() {
        return methodName;
    }

    ArrayList<ExpressionNode> getExpressionNodes() {
        return expressionNodes;
    }
}

class CalloutStmtNode extends CallStmtNode {

    private final String methodName;
    
    CalloutStmtNode(String methodName) {
        super("callout", METHOD_CALL);

        this.methodName = methodName;
    }

    String getMethodName() {
        return methodName;
    }
}

class IfStmtNode extends StatementNode {

    private final ExpressionNode condition;
    private final ArrayList<FieldDeclNode> tFDNodes;
    private final ArrayList<StatementNode> tStmtNodes;
    private final ArrayList<FieldDeclNode> fFDNodes;
    private final ArrayList<StatementNode> fStmtNodes;
    private final boolean haveElse;
    
    IfStmtNode(ExpressionNode condition, boolean haveElse) {
        super("if", TK_if);

        this.condition = condition;
        tFDNodes       = new ArrayList<FieldDeclNode>();
        tStmtNodes     = new ArrayList<StatementNode>();

        this.haveElse  = haveElse;

        if(haveElse) {
            fFDNodes       = new ArrayList<FieldDeclNode>();
            fStmtNodes     = new ArrayList<StatementNode>();
        } else {
            fFDNodes       = null;
            fStmtNodes     = null;
        }
    }

    void addTFieldDecl(FieldDeclNode fd) {
        tFDNodes.add(fd);
    }

    void addTStmt(StatementNode sn) {
        tStmtNodes.add(sn);
    }

    void addFFieldDecl(FieldDeclNode fd) {
        fFDNodes.add(fd);
    }

    void addFStmt(StatementNode sn) {
        fStmtNodes.add(sn);
    }
}

class ForStmtNode extends StatementNode {

    private final String initialID;
    private final ExpressionNode initialExpr;
    private final ExpressionNode conditionExpr;
    private final LocationNode   updateLocation;
    private final int compoundAssignOp;
    private final ExpressionNode updateExpr;
    private final int increment;
    private final ArrayList<FieldDeclNode> fieldDeclNodes;
    private final ArrayList<StatementNode> stmtNodes;

    ForStmtNode(String initialID, ExpressionNode initialExpr, 
                ExpressionNode conditionExpr, LocationNode updateLocation,
                int compoundAssignOp, ExpressionNode updateExpr) {
        super("for", TK_for);

        this.initialID        = initialID;
        this.initialExpr      = initialExpr;
        this.conditionExpr    = conditionExpr;
        this.updateLocation   = updateLocation;
        this.compoundAssignOp = compoundAssignOp;
        this.updateExpr       = updateExpr;
        this.increment        = -1;
        this.fieldDeclNodes   = new ArrayList<FieldDeclNode>();
        this.stmtNodes        = new ArrayList<StatementNode>();
    }

    ForStmtNode(String initialID, ExpressionNode initialExpr, 
                ExpressionNode conditionExpr, LocationNode updateLocation,
                int increment) {
        super("for", TK_for);

        this.initialID        = initialID;
        this.initialExpr      = initialExpr;
        this.conditionExpr    = conditionExpr;
        this.updateLocation   = updateLocation;
        this.increment        = increment;
        this.compoundAssignOp = -1;
        this.updateExpr       = null;
        this.fieldDeclNodes   = new ArrayList<FieldDeclNode>();
        this.stmtNodes        = new ArrayList<StatementNode>();
    }

    void addFieldDecl(FieldDeclNode fn) {
        fieldDeclNodes.add(fn);
    }

    void addStmt(StatementNode sn) {
        stmtNodes.add(sn);
    }

}

class WhileStmtNode extends StatementNode {

    private final ExpressionNode conditionExpr;
    private final ArrayList<FieldDeclNode> fieldDeclNodes;
    private final ArrayList<StatementNode> stmtNodes;

    WhileStmtNode(ExpressionNode conditionExpr) {
        super("while", TK_while);

        this.conditionExpr  = conditionExpr;
        this.fieldDeclNodes = new ArrayList<FieldDeclNode>();
        this.stmtNodes      = new ArrayList<StatementNode>();
    }

    void addFieldDecl(FieldDeclNode fn) {
        fieldDeclNodes.add(fn);
    }

    void addStmt(StatementNode sn) {
        stmtNodes.add(sn);
    }
}

class BreakStmtNode extends StatementNode {

    BreakStmtNode() {
        super("break", TK_break);
    }
}

class ReturnStmtNode extends StatementNode {
 
    private final ExpressionNode expr;
 
    ReturnStmtNode(ExpressionNode expr) {
        super("return", TK_return);

        this.expr = expr;
    }
}

abstract class MemberDeclNode extends ASTNode {

    MemberDeclNode(String name, int type) {
        super(name, type);
    }

    void dump() {
    }
}

// import_decl            import
//     import    =====>       ID
//     ID
class ImportDeclNode extends MemberDeclNode {
    
    private final String   exMethodName;
    private final TypeNode typeNode;

    ImportDeclNode(String exMethodName) {
        super("import", TK_import);
        this.exMethodName = exMethodName;
        this.typeNode     = new TypeNode("int", TK_int);
    }

//    void addChild(MethodDeclNode child) {
//        this.child = child;
//
//        // external method has no arg list and no statements 
//        this.child.setExMethod();
//    }

    String getExMethod() {
        return exMethodName;
    }

    void dump() {
        System.out.println("AST: import");
        System.out.printf("\t");
        System.out.printf("AST: " + exMethodName);
    }

}

class FieldDeclNode extends MemberDeclNode {
  
    private final TypeNode typeNode;
    private final ArrayList<VarDeclNode> varNodes;

    FieldDeclNode(TypeNode typeNode) {
        super("field_decl", FIELD_DECL);
        this.typeNode = typeNode;
        this.varNodes = new ArrayList<VarDeclNode>();
    }

//    void addType(TypeNode typeNode) {
//        this.typeNode = typeNode;
//    }

    void addVar(VarDeclNode varNode) {
        varNodes.add(varNode);
    }

    TypeNode getTypeNode() {
        return typeNode;
    }

    ArrayList<VarDeclNode> getVarNodes() {
        return varNodes;
    }

    void dump() {
        typeNode.dump();
        for(VarDeclNode varNode: varNodes) {
            varNode.dump();
        }
    }

}

class MethodDeclNode extends MemberDeclNode {

    private final  TypeNode typeNode;
    private final  String methodName;
    private final  ArrayList<ParaDeclNode> paraDeclNodes;
    private final  ArrayList<StatementNode> stmtNodes;
    private final  ArrayList<FieldDeclNode> localFieldDeclNodes;


    MethodDeclNode(TypeNode typeNode, String methodName) {
        super("method_decl", METHOD_DECL);

        this.typeNode            = typeNode;
        this.methodName          = methodName;
        this.paraDeclNodes       = new ArrayList<ParaDeclNode>();
        this.stmtNodes           = new ArrayList<StatementNode>();
        this.localFieldDeclNodes = new ArrayList<FieldDeclNode>();
    }

//    void addType(TypeNode typeNode) {
//        this.typeNode = typeNode;
//    }

//    void setMethodName(String name) {
//        this.methodName = name;
//    }

    void addPara(ParaDeclNode paraDeclNode) {
        this.paraDeclNodes.add(paraDeclNode);
    }

    void addFieldDecl(FieldDeclNode fieldDeclNode) {
        this.localFieldDeclNodes.add(fieldDeclNode);
    }

    void addStmt(StatementNode stmtNode) {
        this.stmtNodes.add(stmtNode);
    }

    TypeNode getTypeNode() {
        return typeNode;
    }

    String getMethodName() {
        return methodName;
    }

    ArrayList<ParaDeclNode> getParaDeclNodes() {
        return paraDeclNodes;
    }

    ArrayList<StatementNode> getStmtNodes() {
        return stmtNodes;
    }

    ArrayList<FieldDeclNode> getLocalFieldDeclNodes() {
        return localFieldDeclNodes;
    }

}

class VarDeclNode extends ASTNode {

    protected String          name;
    protected boolean         isArray;
    protected IntLiteralNode  width;

    VarDeclNode(String name) {
        super(name, ID);

        this.isArray = false;
        this.width   = null;
    }

    VarDeclNode(String name, IntLiteralNode width) {
        super(name, ID);

        this.isArray = true;
        this.width   = width;
    }

    boolean isArray() {
        return this.isArray;
    }

    void dump() {
        if(isArray) {
            System.out.println("AST: " + name);
        } else {
            System.out.println("AST: " + name);
            width.dump();
        }
    }

}

class ParaDeclNode extends MemberDeclNode {

    private final TypeNode    typeNode;
    private final VarDeclNode varDeclNode;

    ParaDeclNode(TypeNode typeNode, VarDeclNode varDeclNode) {
        super("para_decl", FIELD_DECL);

        this.typeNode    = typeNode;
        this.varDeclNode = varDeclNode;
    }

//    void addType(TypeNode typeNode) {
//        this.typeNodes.add(typeNode);
//    }
//
//    void addVar(VarDeclNode varNode) {
//        varNodes.add(varNode);
//    }

    TypeNode getTypeNode() {
        return typeNode;
    }

    VarDeclNode getVarNode() {
        return varDeclNode;
    }

    void dump() {
        typeNode.dump();
        varDeclNode.dump();
    }

}

class TypeNode extends ASTNode {

    private final String name;
    private int    type;
    TypeNode(String name, int type) {
        super("type", TYPE);

        this.name = name;
        this.type = type;
    }

    void dump() {
        System.out.println("AST: " + nodeName);
    }

}

class ProgramNode extends ASTNode {

    private final ArrayList<ImportDeclNode> importDeclNodes;
    private final ArrayList<FieldDeclNode>  fieldDeclNodes;
    private final ArrayList<MethodDeclNode> methodDeclNodes;

    ProgramNode() {
        super("program", PROGRAM);

        importDeclNodes = new ArrayList<ImportDeclNode>();
        fieldDeclNodes  = new ArrayList<FieldDeclNode>();
        methodDeclNodes = new ArrayList<MethodDeclNode>();
    }

    void addImportDecl(ImportDeclNode idn) {
        importDeclNodes.add(idn);
    }

    void addFieldDecl(FieldDeclNode fdn) {
        fieldDeclNodes.add(fdn);
    }

    void addMethodDecl(MethodDeclNode mdn) {
        methodDeclNodes.add(mdn);
    }

    ArrayList<ImportDeclNode> getImportDeclNodes() {
        return importDeclNodes;
    }

    ArrayList<FieldDeclNode> getFieldDeclNodes() {
        return fieldDeclNodes;
    }

    ArrayList<MethodDeclNode> getMethodDeclNodes() {
        return methodDeclNodes;
    }

    void dump() {
        for(ImportDeclNode idn: importDeclNodes) {
            idn.dump();
        }
        for(FieldDeclNode fdn: fieldDeclNodes) {
            fdn.dump();
        }
        for(MethodDeclNode mdn: methodDeclNodes) {
            mdn.dump();
        }
    }
}
