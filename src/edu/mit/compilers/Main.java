package edu.mit.compilers;

import java.io.*;
import antlr.Token;
import antlr.CommonAST;
import antlr.collections.AST;
import edu.mit.compilers.grammar.*;
import edu.mit.compilers.tools.CLI;
import edu.mit.compilers.tools.CLI.Action;

class Main {
  public static void main(String[] args) {
    try {
      CLI.parse(args, new String[0]);
      InputStream inputStream = args.length == 0 ?
          System.in : new java.io.FileInputStream(CLI.infile);
      PrintStream outputStream = CLI.outfile == null ? System.out : new java.io.PrintStream(new java.io.FileOutputStream(CLI.outfile));
      if (CLI.target == Action.SCAN) {
        DecafScanner scanner =
            new DecafScanner(new DataInputStream(inputStream));
        scanner.setTrace(CLI.debug);
        Token token;
        boolean done = false;
        while (!done) {
          try {
            for (token = scanner.nextToken();
                 token.getType() != DecafParserTokenTypes.EOF;
                 token = scanner.nextToken()) {
              String type = "";
              String text = token.getText();
              switch (token.getType()) {
               // TODO: add strings for the other types here...
               case DecafScannerTokenTypes.ID:
                type = " IDENTIFIER";
                break;
               case DecafScannerTokenTypes.CHARLITERAL:
                type = " CHARLITERAL";
                break;
               case DecafScannerTokenTypes.STRINGLITERAL:
                type = " STRINGLITERAL";
                break;
               case DecafScannerTokenTypes.INTLITERAL:
                type = " INTLITERAL";
                break;
               case DecafScannerTokenTypes.TK_true:
               case DecafScannerTokenTypes.TK_false:
                 type = " BOOLEANLITERAL";
                 break;
               default:
                type = "";
                break;
              }
              outputStream.println(token.getLine() + type + " " + text);
            }
            done = true;
          } catch(Exception e) {
            // print the error:
            System.err.println(CLI.infile + " " + e);
            //System.err.println(e.getClass().getCanonicalName() + e.getMessage());
            if (e.getMessage().contains("0xA")) {
                scanner.consume();
                scanner.newline();
            } else {
                scanner.consume();
            }
            //scanner.consume();
          }
        }
      } else if (CLI.target == Action.PARSE ||
                 CLI.target == Action.DEFAULT) {
        DecafScanner scanner =
            new DecafScanner(new DataInputStream(inputStream));
        DecafParser parser = new DecafParser(scanner);
        parser.setTrace(CLI.debug);
        parser.program();
        if(parser.getError()) {
          System.exit(1);
        }
      } else if (CLI.target == Action.INTER) {
          DecafScanner scanner = new DecafScanner(new DataInputStream(inputStream));
          DecafParser parser = new DecafParser(scanner);
          parser.setTrace(CLI.debug);
          parser.program();

          CommonAST t = (CommonAST)parser.getAST();
          //System.out.println(t.toStringList());
          //System.out.println(t.getNumberOfChildren());
          //System.out.println(t.getFirstChild());
          //t=(CommonAST)t.getFirstChild();
          //while ( t.getText() != null ) {
          //    System.out.println(t);
          //    t=(CommonAST)t.getNextSibling();
          //}

          ConcreteTreeParser cTreeParser = new ConcreteTreeParser(t);
          cTreeParser.setTrace(CLI.debug);
      
      } else if (CLI.target == Action.ASSEMBLY) {
          System.err.println("code generation not implemented!");
          System.exit(1);
      }
    } catch(Exception e) {
      // print the error:
      System.err.println(CLI.infile+" "+e);
      //System.err.println(e.getClass().getCanonicalName());
    }
  }
}
