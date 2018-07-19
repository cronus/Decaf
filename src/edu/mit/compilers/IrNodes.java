package edu.mit.compilers;

abstract class Ir {
}

abstract class IrExpression extends Ir {
}

abstract class IrLiteral extends IrExpression {

}

class IrIntLiteral extends IrLiteral {

}

class IrBoolLiteral extends IrLiteral {
}

class IrCallExpr extends IrExpression {
}

class IrMethodCallExpr extends IrCallExpr {
}

class IrCalloutExpr extends IrCallExpr {
}

class IrBinopExpr extends IrExpression {

    //private final int          operator;
    //private final IrExpression lhs;
    //private final IrExpression rhs;
}

abstract class IrStatement extends Ir {
}


class IrAssignStmt extends IrStatement {
    
    //private final IrLocation   lhs;
    //private final IrExpression rhs;
}

class IrPlusAssignStmt extends IrStatement {
}

class IrBreakStmt extends IrStatement {
}

class IrIfStmt extends IrStatement {
}


abstract class IrMemberDecl extends Ir {
}


class IrMethodDecl extends IrMemberDecl {
}


class IrFieldDecl extends IrMemberDecl {
}

class IrVarDecl extends Ir {
}

class IrType extends Ir {
}
