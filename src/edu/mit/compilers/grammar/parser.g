header {
package edu.mit.compilers.grammar;
}

options
{
  mangleLiteralPrefix = "TK_";
  language = "Java";
}

class DecafParser extends Parser;
options
{
  importVocab = DecafScanner;
  k = 3;
  buildAST = true;
}

tokens
{
    PROGRAM;
    IMPORT_DECL;
    FIELD_DECL;
    METHOD_DECL;
    TYPE;
    BLOCK;
    STATEMENT;
    EXPR;
    LOCATION;
    ASSIGN_EXPR;
    ASSIGN_OP;
    COMPOUND_ASSIGN_OP;
    INCREMENT;
    METHOD_CALL;
    METHOD_NAME;
    IMPORT_ARG;
    BIN_OP;
    ARITH_OP;
    REL_OP;
    EQ_OP;
    COND_OP;
    LITERAL;
    BOOL_LITERAL;
}

// Java glue code that makes error reporting easier.
// You can insert arbitrary Java code into your parser/lexer this way.
{
  // Do our own reporting of errors so the parser can return a non-zero status
  // if any errors are detected.
  /** Reports if any errors were reported during parse. */
  private boolean error;

  @Override
  public void reportError (RecognitionException ex) {
    // Print the error via some kind of error reporting mechanism.
    error = true;
    System.err.println("1:"+ex);
  }
  @Override
  public void reportError (String s) {
    // Print the error via some kind of error reporting mechanism.
    error = true;
    System.err.println(s);
  }
  public boolean getError () {
    return error;
  }

  // Selectively turns on debug mode.

  /** Whether to display debug information. */
  private boolean trace = false;

  public void setTrace(boolean shouldTrace) {
    trace = shouldTrace;
  }
  @Override
  public void traceIn(String rname) throws TokenStreamException {
    if (trace) {
      super.traceIn(rname);
    }
  }
  @Override
  public void traceOut(String rname) throws TokenStreamException {
    if (trace) {
      super.traceOut(rname);
    }
  }
}

program
    : ( import_decl )* ( field_decl )* ( method_decl )* EOF
    {#program = #([PROGRAM, "program"], #program);}
    ;

import_decl
    : TK_import ID SEMI!
    {#import_decl = #([IMPORT_DECL, "import_decl"], #import_decl);}
    ;

field_decl
    : type field_decl_id_list SEMI!
    {#field_decl = #([FIELD_DECL, "field_decl"], #field_decl);}
    ;

field_decl_id_list
    : ID field_decl_id_idx more_field_decl_id_list;

more_field_decl_id_list
    : COMMA! ID field_decl_id_idx more_field_decl_id_list
    |
    ;

field_decl_id_idx
    : LBRACK! INTLITERAL RBRACK!
    |
    ;

method_decl
    : method_decl_type ID LPAREN! ( method_decl_args_list )? RPAREN! block
    {#method_decl = #([METHOD_DECL, "method_decl"], #method_decl);}
    ;

method_decl_type
    : type
    | TK_void
    ;

method_decl_args_list
    : type ID more_method_decl_args;

more_method_decl_args
    : COMMA! type ID more_method_decl_args
    |
    ;

block
    : LCURLY! ( field_decl )* ( statement )* RCURLY!
    {#block = #([BLOCK, "block"], #block);}
    ;

type
    : TK_int 
    {#type = #([TYPE, "type"], #type);}
    | TK_bool
    ;

statement
    : location assign_expr SEMI!
    {#statement = #([STATEMENT, "statement"], #statement);}
    | method_call SEMI!
    {#statement = #([STATEMENT, "statement"], #statement);}
    | TK_if LPAREN! expr RPAREN! block (TK_else block)?
    {#statement = #([STATEMENT, "statement"], #statement);}
    | TK_for LPAREN! ID ASSIGN expr SEMI! expr SEMI! location (compound_assign_op expr | increment) RPAREN! block
    {#statement = #([STATEMENT, "statement"], #statement);}
    | TK_while LPAREN! expr RPAREN! block
    {#statement = #([STATEMENT, "statement"], #statement);}
    | TK_return (expr)? SEMI!
    {#statement = #([STATEMENT, "statement"], #statement);}
    | TK_break SEMI!
    {#statement = #([STATEMENT, "statement"], #statement);}
    | TK_continue SEMI!
    {#statement = #([STATEMENT, "statement"], #statement);}
    ;

assign_expr
    : assign_op expr
    {#assign_expr = #([ASSIGN_EXPR, "assign_expr"], #assign_expr);}
    | increment
    {#assign_expr = #([ASSIGN_EXPR, "assign_expr"], #assign_expr);}
    ;

assign_op
    : ASSIGN
    {#assign_op = #([ASSIGN_OP, "assign_op"], #assign_op);}
    | compound_assign_op
    {#assign_op = #([ASSIGN_OP, "assign_op"], #assign_op);}
    ;

compound_assign_op
    : PLUS_ASSIGN
    {#compound_assign_op = #([COMPOUND_ASSIGN_OP, "compound_assign_op"], #compound_assign_op);}
    | MINUS_ASSIGN
    {#compound_assign_op = #([COMPOUND_ASSIGN_OP, "compound_assign_op"], #compound_assign_op);}
    ;

increment
    : INC
    {#increment = #([INCREMENT, "increment"], #increment);}
    | DEC
    {#increment = #([INCREMENT, "increment"], #increment);}
    ;

method_call
    : method_name LPAREN! ( method_call_args_list )? RPAREN! 
    {#method_call = #([METHOD_CALL, "method_call"], #method_call);}
    ;

method_call_args_list
    : import_arg more_import_arg;

more_import_arg
    : COMMA! import_arg more_import_arg
    |
    ;

method_name
    : ID
    {#method_name = #([METHOD_NAME, "method_name"], #method_name);}
    ;

location
    : ID
    {#location = #([LOCATION, "location"], #location);}
    | ID LBRACK! expr RBRACK!
    {#location = #([LOCATION, "location"], #location);}
    ;

expr
    : location expr1
    {#expr = #([EXPR, "expr"], #expr);}
    | method_call expr1
    {#expr = #([EXPR, "expr"], #expr);}
    | literal expr1
    {#expr = #([EXPR, "expr"], #expr);}
    | TK_len LPAREN! ID RPAREN! expr1
    {#expr = #([EXPR, "expr"], #expr);}
//    | expr bin_op expr
    | MINUS expr expr1
    {#expr = #([EXPR, "expr"], #expr);}
    | NOT expr expr1
    {#expr = #([EXPR, "expr"], #expr);}
    | LPAREN! expr RPAREN! expr1
    {#expr = #([EXPR, "expr"], #expr);}
//    | expr QUESTION expr COLON expr
    ;

expr1
    : ( bin_op )   => bin_op expr expr1 
    | ( QUESTION ) => QUESTION expr COLON expr expr1
    |
    ;

import_arg
    : expr
    {#import_arg = #([IMPORT_ARG, "import_arg"], #import_arg);}
    | STRINGLITERAL
    {#import_arg = #([IMPORT_ARG, "import_arg"], #import_arg);}
    ;

bin_op
    : arith_op
    {#bin_op = #([BIN_OP, "bin_op"], #bin_op);}
    | rel_op
    {#bin_op = #([BIN_OP, "bin_op"], #bin_op);}
    | eq_op
    {#bin_op = #([BIN_OP, "bin_op"], #bin_op);}
    | cond_op
    {#bin_op = #([BIN_OP, "bin_op"], #bin_op);}
    ;

arith_op
    : PLUS
    {#arith_op = #([ARITH_OP, "arith_op"], #arith_op);}
    | MINUS
    {#arith_op = #([ARITH_OP, "arith_op"], #arith_op);}
    | MULT
    {#arith_op = #([ARITH_OP, "arith_op"], #arith_op);}
    | DIV
    {#arith_op = #([ARITH_OP, "arith_op"], #arith_op);}
    | MOD
    {#arith_op = #([ARITH_OP, "arith_op"], #arith_op);}
    ;

rel_op
    : LT
    {#rel_op = #([REL_OP, "rel_op"], #rel_op);}
    | GT
    {#rel_op = #([REL_OP, "rel_op"], #rel_op);}
    | LE
    {#rel_op = #([REL_OP, "rel_op"], #rel_op);}
    | GE
    {#rel_op = #([REL_OP, "rel_op"], #rel_op);}
    ;

eq_op
    : EQ
    {#eq_op = #([EQ_OP, "eq_op"], #eq_op);}
    | NEQ
    {#eq_op = #([EQ_OP, "eq_op"], #eq_op);}
    ;

cond_op
    : AND
    {#cond_op = #([COND_OP, "cond_op"], #cond_op);}
    | OR
    {#cond_op = #([COND_OP, "cond_op"], #cond_op);}
    ;

literal
    : INTLITERAL
    {#literal = #([LITERAL, "literal"], #literal);}
    | CHARLITERAL
    {#literal = #([LITERAL, "literal"], #literal);}
    | bool_literal
    {#literal = #([LITERAL, "literal"], #literal);}
    ;

bool_literal
    : TK_true
    {#bool_literal = #([BOOL_LITERAL, "bool_literal"], #bool_literal);}
    | TK_false
    {#bool_literal = #([BOOL_LITERAL, "bool_literal"], #bool_literal);}
    ;
