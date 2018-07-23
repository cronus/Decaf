package edu.mit.compilers;

import java.io.*;
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

}

class IntLiteralNode extends LiteralNode {

    IntLiteralNode(String name, int type) {
        super(name, type);
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

class MethodDeclNode extends MemberDeclNode {

    MethodDeclNode(String name, int type) {
        super(name, type);
    }

}

class FieldDeclNode extends MemberDeclNode {

    FieldDeclNode(String name, int type) {
        super(name, type);
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

//class VarDeclNode extends ASTNode {
//
//    VarDeclNode(String name, int type) {
//        super(name, type);
//    }
//
//}

class TypeNode extends ASTNode {

    TypeNode(String name, int type) {
        super(name, type);
    }

    void dump() {
    }

}
