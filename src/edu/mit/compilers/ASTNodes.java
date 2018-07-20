package edu.mit.compilers;

abstract class ASTNode {
}

abstract class ExpressionNode extends ASTNode {
}

abstract class LiteralNode extends ExpressionNode {

}

class IntLiteralNode extends LiteralNode {

}

class BoolLiteranNode extends LiteralNode {
}

class CallExprNode extends ExpressionNode {
}

class MethodCallExprNode extends CallExprNode {
}

class CalloutExprNode extends CallExprNode {
}

class BinopExprNode extends ExpressionNode {

    //private final int          operator;
    //private final IrExpression lhs;
    //private final IrExpression rhs;
}

abstract class StatementNode extends ASTNode {
}


class AssignStmtNode extends StatementNode {
    
    //private final IrLocation   lhs;
    //private final IrExpression rhs;
}

class PlusAssignStmtNode extends StatementNode {
}

class BreakStmtNode extends StatementNode {
}

class IfStmtNode extends StatementNode {
}


abstract class MemberDeclNode extends ASTNode {
}


class MethodDeclNode extends MemberDeclNode {
}


class FieldDeclNode extends MemberDeclNode {
}

class VarDeclNode extends ASTNode {
}

class TypeNode extends ASTNode {
}
