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
  }
  @Override
  public void reportError (String s) {
    // Print the error via some kind of error reporting mechanism.
    error = true;
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
    : ( import_decl )* ( field_decl )* ( method_decl )* EOF;

import_decl
    : TK_import ID SEMI
    ;

field_decl
    : type 
      ( ID 
      | ID LBRACK INTLITERAL RBRACK
      )+
      COMA SEMI
    ;

method_decl
    : (type 
      | TK_void) 
      ID LPAREN
      ( (type ID)+ COMA
      )?
      block
    ;

block
    : LCURLY ( field_decl )* ( statement )* RCURLY
    ;

type
    : TK_int 
    | TK_bool
    ;

statement
    : location assign_expr SEMI
    | method_call SEMI
    | TK_if LPAREN expr RPAREN block (TK_else block)?
    | TK_for LPAREN ID ASSIGN expr SEMI expr SEMI location (compound_assign_op expr | increment) RPAREN block
    | TK_while LPAREN expr RPAREN block
    | TK_return (expr)? SEMI
    | TK_break SEMI
    | TK_continue SEMI
    ;

assign_expr
    : assign_op expr
    | increment
    ;

assign_op
    : ASSIGN
    | compound_assign_op
    ;

compound_assign_op
    : PLUS_ASSIGN
    | MINUS_ASSIGN
    ;

increment
    : INC
    | DEC
    ;

method_call
    : method_name LPAREN (( expr )+ COMA)? RPAREN
    | method_name LPAREN (( import_arg)+ COMA) RPAREN
    ;

method_name
    : ID
    ;

location
    : ID
    | ID LBRACK expr RBRACK
    ;

expr
    : location
    | method_call
    | literal
    | TK_len LPAREN ID RPAREN
    | expr bin_op expr
    | MINUS expr
    | NOT expr
    | LPAREN expr RPAREN
    | expr QUESTION expr COLON expr
    ;

import_arg
    : expr
    | STRINGLITERAL
    ;

bin_op
    : arith_op
    | rel_op
    | eq_op
    | cond_op
    ;

arith_op
    : PLUS
    | MINUS
    | MULT
    | DIV
    | MOD
    ;

rel_op
    : LT
    | GT
    | LE
    | GE
    ;

eq_op
    : EQ
    | NEQ
    ;

cond_op
    : AND
    | OR
    ;

literal
    : INTLITERAL
    | CHARLITERAL
    | bool_literal
    ;

bool_literal
    : TK_true
    | TK_false
    ;
