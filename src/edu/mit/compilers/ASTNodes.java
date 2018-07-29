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

    IntLiteralNode(String name) {
        super(name, INTLITERAL);
    }

}

class BoolLiteralNode extends LiteralNode {

    BoolLiteralNode(String name, int type) {
        super(name, type);
    }

}

class LocationNode extends ExpressionNode {
    
    private final String name;
    private final int index;
    private final boolean isArrayElement;

    LocationNode(String name) {
        super("location", LOCATION);
        this.name           = name;
        this.index          = -1;
        this.isArrayElement = false;
    }

    LocationNode(String name, int index) {
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

    int getLocationIndex() {
        return index;
    }
}

class CallExprNode extends ExpressionNode {

    CallExprNode(String name, int type) {
        super(name, type);
    }

}

class MethodCallExprNode extends CallExprNode {

    MethodCallExprNode(String name, int type) {
        super(name, type);
    }

}

class CalloutExprNode extends CallExprNode {

    CalloutExprNode(String name, int type) {
        super(name, type);
    }

}

class BinopExprNode extends ExpressionNode {

    //private final int          operator;
    //private final IrExpression lhs;
    //private final IrExpression rhs;

    BinopExprNode(String name, int type) {
        super(name, type);
    }

}

class MinusExprNode extends ExpressionNode {


    MinusExprNode() {
        super("minum", MINUS);
    }
}

class NotExprNode extends ExpressionNode {
    
    NotExprNode() {
        super("not", NOT);
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
    
    private final int            assingOp;
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

abstract CallStmtNode extends StatementNode {

    CallStmtNode(String name, int type) {
        super(name, type);
    }
}

class MethodCallStmtNode extends CallStmtNode {
    
    MethodCallStmtNode() {
        super("methodCall", METHOD_CALL);
    }
}

class CalloutStmtNode extends CallStmtNode {
    
    CalloutStmtNode() {
        super("callout", METHOD_CALL);
    }
}

class BreakStmtNode extends StatementNode {

    BreakStmtNode() {
        super("break", TK_break);
    }

}

class IfStmtNode extends StatementNode {

    IfStmtNode() {
        super("if", TK_if);
    }

}

class ForStmtNode extends StatementNode {

    ForStmtNode() {
        super("for", TK_for);
    }

}

class WhileStmtNode extends StatementNode {

    WhileStmtNode() {
        super("while", TK_while);
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
        this.methodName          = methodName
        this.paraDeclNodes       = new ArrayList<ParaDeclNode>();
        this.stmtModes           = new ArrayList<StatementNode>();
        this.localFieldDeclNodes = new ArrayList<FieldDeclNOde>();
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

    void addFieldDecl(FieldDeclNode fielDeclNode) {
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

    ArrayList<StmtNodes> getStmtNodes() {
        return statNodes;
    }

    ArrayList<FieldDeclNode> getLocalFieldDeclNodes() {
        return localFieldDeclNodes;
    }

//    void setExMethod() {
//        stmtNodes           = null;
//        localFieldDeclNodes = null;
//    }

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
    private final VarDeclNode varNode;

    ParaDeclNode() {
        super("para_decl", FIELD_DECL);

        typeNodes = new ArrayList<TypeNode>();
        varNodes  = new ArrayList<VarDeclNode>();
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

    VarNode getVarNode() {
        return varNode;
    }

    void dump() {
        typeNode.dump();
        for(VarDeclNode varNode: varNodes) {
            varNode.dump();
        }
    }

}

class TypeNode extends ASTNode {

    TypeNode(String name, int type) {
        super(name, type);
    }

    void dump() {
        System.out.println("AST: " + nodeName);
    }

}

class ProgramNode extends ASTNode {

    private final ArrayList<ImportDeclNode> importDeclNodes;
    private final ArrayList<FieldDeclNode>  fieldDeclNodes;
    private final ArrayList<MethodDeclNode> methodDeclNodes

    ProgramNode() {
        super("program", PROGRAM);

        importDeclNodes = new ArrayList<ImportDeclNode>();
        fieldDeclNodes  = new ArrayList<FieldDeclNode>();
        methodDeclNode  = new ArrayList<MethodDeclNode>();
    }

    void addImportDecl(ImportDeclNode idn) {
        importDeclNodes.add(idn);
    }

    void addFieldDeclNode(FieldDeclNode fdn) {
        fieldDeclNodes.add(fdn);
    }

    void addMethodDeclNode(MethodDeclNode mdn) {
        methodDeclNode.add(mdn);
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
