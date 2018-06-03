// Header Section
// A header section contains source code that must be placed before any ANTLR-generated code in the output parser. 
// In Java, this can be used to specify a package for the resulting parser, and any imported classes. 

header {
package edu.mit.compilers.grammar;
}

// Options Section
// Rather than have the programmer specify a bunch of command-line arguments to the parser generator, 
// an options section within the grammar itself serves this purpose. 
// This solution is preferable because it associates the required options with the grammar rather than ANTLR invocation. 
// The section is preceded by the options keyword and contains a series of option/value assignments. 
// An options section may be specified on both a per-file, per-grammar, per-rule, and per-subrule basis.
// You may also specify an option on an element, such as a token reference.

options
{
  mangleLiteralPrefix = "TK_";
  language = "Java";
}

{@SuppressWarnings("unchecked")}
class DecafScanner extends Lexer;
options
{
  k = 2;
}

// Token Section
// If you need to define an "imaginary" token, one that has no corresponding real input symbol, 
// use the tokens section to define them.  
// Imaginary tokens are used often for tree nodes that mark or group a subtree resulting from real input.  
//     For example, you may decide to have an EXPR node be the root of every expression subtree and 
//     DECL for declaration subtrees for easy reference during tree walking.  
//     Because there is no corresponding input symbol for EXPR, you cannot reference it in the grammar to implicitly define it.  
//     Use the following to define those imaginary tokens.
//
//     tokens {
//         EXPR;
//         DECL;
//     }

tokens 
{
  "class";
}

// Selectively turns on debug tracing mode.
// You can insert arbitrary Java code into your parser/lexer this way.
{
  /** Whether to display debug information. */
  private boolean trace = false;

  public void setTrace(boolean shouldTrace) {
    trace = shouldTrace;
  }
  @Override
  public void traceIn(String rname) throws CharStreamException {
    if (trace) {
      super.traceIn(rname);
    }
  }
  @Override
  public void traceOut(String rname) throws CharStreamException {
    if (trace) {
      super.traceOut(rname);
    }
  }
}

LCURLY options { paraphrase = "{"; } : "{";
RCURLY options { paraphrase = "}"; } : "}";

ID options { paraphrase = "an identifier"; } : 
  ('a'..'z' | 'A'..'Z')+;

// Note that here, the {} syntax allows you to literally command the lexer
// to skip mark this token as skipped, or to advance to the next line
// by directly adding Java commands.
WS_ : (' ' | '\n' {newline();}) {_ttype = Token.SKIP; };
SL_COMMENT : "//" (~'\n')* '\n' {_ttype = Token.SKIP; newline (); };

CHAR : '\'' (ESC|~'\'') '\'';
STRING : '"' (ESC|~'"')* '"';

protected
ESC :  '\\' ('n'|'"');
