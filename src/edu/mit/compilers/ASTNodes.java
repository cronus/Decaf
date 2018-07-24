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

abstract class StatementNode extends ASTNode {

    StatementNode(String name, int type) {
        super(name, type);
    }

    void dump() {
    }
}


class AssignStmtNode extends StatementNode {
    
    //private final IrLocation   lhs;
    //private final IrExpression rhs;

    AssignStmtNode(String name, int type) {
        super(name, type);
    }

}

class PlusAssignStmtNode extends StatementNode {

    PlusAssignStmtNode(String name, int type) {
        super(name, type);
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
    
    protected IDNode child;

    ImportDeclNode() {
        super("import", TK_import);
        this.child = child;
    }

    void addChild(IDNode child) {
        this.child = child;
    }

    void dump() {
        System.out.println("AST: import");
        System.out.printf("\t");
        child.dump();
    }

}

class FieldDeclNode extends MemberDeclNode {
  
    protected TypeNode typeNode;
    protected ArrayList<VarDeclNode> varNodes;

    FieldDeclNode() {
        super("field_decl", FIELD_DECL);

        varNodes = new ArrayList<VarDeclNode>();
    }

    void addType(TypeNode typeNode) {
        this.typeNode = typeNode;
    }

    void addVar(VarDeclNode varNode) {
        varNodes.add(varNode);
    }

    void dump() {
        typeNode.dump();
        for(VarDeclNode varNode: varNodes) {
            varNode.dump();
        }
    }

}

class MethodDeclNode extends MemberDeclNode {

    protected TypeNode typeNode;

    MethodDeclNode() {
        super("method_decl", METHOD_DECL);
    }

}

class IDNode extends ASTNode {
    
    IDNode(String name) {
        super(name, ID);
    }

    void dump() {
        System.out.println("AST:" + nodeName);
    }
}

class VarDeclNode extends ASTNode {

    //TODO, use String name or IDNode?
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

class TypeNode extends ASTNode {

    TypeNode(String name) {
        super(name, TYPE);
    }

    void dump() {
        System.out.println("AST: " + nodeName);
    }

}
