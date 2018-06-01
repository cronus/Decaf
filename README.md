java-skeleton
=============

Java Skeleton Code for 6.035


Structure of compiler (to lab 3, code generation)

---------------                                  ----------                                        --------------
| source code | ------- lexer analysis --------> | tokens | -------- syntatic analysis ----------->| parse tree |    
---------------                                  ----------                                        --------------
                                                                                                         |
                                                                                                         |  -------------------
                                                                                                         |  | semantic action |
                                                                                                         |  -------------------
                                                                                                         |  /     
                                                                                                         | /    
                                                                                                         |/        
                                                                                             syntax directed translation  
                                                                                                         |
                                                                                                        \|/
                                                                                            ------------------------------                        
                      high level IR:                                                        | abstract syntax tree (AST) | <------------ tranverse ------- semantics analysis 
                                                                                            ------------------------------                                       |
                                                                                                         |        |                                              |
                                                                                                         |     generate                                        lookup         
                                                                                                         |        |              --------------------            |
                                                                                                         |        -------------->| symbol table(s)  | <-----------
                                                                                                         |                       --------------------
                                                                                                         |                                    /|\
                                                                                                         |                                     |
                                                                                                         |---------------------------------- lookup
                                                                                                         |                                     |
                                                                                   ------------------------------------------                  |
                                                                                   |                                        |                  |
                                                                            control flow                                statements             |
                                                                                   |                                        |                  |
                                                                                   |                                        |                  |
                                                                 ----------------- or -----------------                     |                  |
                                                                 |                                    |                     |                  |
                                                  destructure program representation           template matching            |                  |
                                                                 |                                    |                     |                  |
                                                                \|/                                   |                    \|/                 |
                                                   -------------------------------                    |        --------------------------      |
                      low level IR:                | control flow graph (CFG)    |                   or------->|        3-addr op       |      |
                                                   -------------------------------                    |        --------------------------      |
                                                                |                                     |                    |                   |
                                                                |                                     |                    |                   |
                                                                |                                     |                    |                   |
                                                    linearizing CFG to assembler                      |                    |                   |
                                                                |-------------------------------------+--------------------+--------------------
                                                                |                                     |                    |
                                                                |                                     |                    |
                                                                |                                    \|/                   |
                                                                |                               --------------------       |
                                                                ------------------------------->| assembly program |<-------
                                                                                                --------------------
