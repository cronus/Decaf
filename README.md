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
                                                                                            | abstract syntax tree (AST) | <-------------------------------- tranverse ------- semantics analysis 
                                                                                            ------------------------------                                                           |
                                                                                                         |        |                                                                  |
                                                                                                         |     generate                                                            lookup         
                                                                                                         |        |                                  --------------------            |
                                                                                                         |        ---------------------------------->| symbol table(s)  | <-----------
                                                                                                         |                                           --------------------
                                                                                                         |                                                        /|\
                                                                                                         |                                                         |
                                                                                                         |------------------------------------------------------ lookup
                                                                                                         |                                                         |
                                                                                                         |                                                         |
                          ---- Data ------------------------------------------------------------------------ Code ---                                              |
                          |                                                                                         |                                              |
                          |                                                        -----------------------------------------------------------------------         |
                          |                                                        |                                        |               |            |         |
                          |                                                 control flow                                statements    data access   procedures     |
                          |                                                        |                                        |               |            |         |
               ----------------------                                              |                                        |               |            |         |
               |                    |                                              |                                        |               |            |         |
               |    Dynamic (Heap)  |                                              |                                        |               |            |         |
               |                    |                                              |                                        |               |        activation    |
               ---------------------                                destructure program representation                      |               |          records     |
  Local var    |                    |                                              |                                        |               |            &         |
  Temporaries  |      Stack         |                                             \|/                                      \|/              |          calling     |
  parameters   |                    |                                -------------------------------           --------------------------   |        convention    |
                --------------------                                 | control flow graph (CFG)    |<-hybrid-->|        3-addr op       |   |            |         |
  Global var   |       Data         |                                -------------------------------           --------------------------   |            |         |
  Rd-only const|                    |                                              \                              /                         |            |         |
               ---------------------                                                \                 template matching                     |            |         |
               |                    |                                         generate labels                or                             |            |         |
  Program      |       Text         |                                                 \                   covering                          |            |         |
                --------------------                                                   \                    /                               |            |         |
               |     Unmapped       |                                                   \                  /                                |            |         |
               ---------------------                                                     \                /                                 |            |         |
                        |                                                                 \              /                                  |            |         |
                        |                                                                  \            /                                   |            |         |
                        |                                                                   \          /                                    |            |         | 
                        |                                                                    \        /                                     |            |         | 
                        |                                                                     \      /                                      |            |         |
                        |                                                                      \    /                                       |            |         |
                        |                                                                       \  /                                        |            |         |
                        |                                                                        \/                                         |            |         |
                        |                                                                        |                                          |            |         |
                        |------------------------------------------------------------------------+------------------------------------------+------------+----------
                        |                                                                        |                                          |            |
                        |                                                                        |       --------------------               |            |
                        -------------------------------------------------------------------------------->| assembly program |<----------------------------
                                                                                                         --------------------
                                                                                                         

Optimization
    1. local optimization
        common subexpression elimation (CSE)
        copy propagation
        dead code elimation
        algebra simplication (not a data flow optimization)
    2. data flow analysis based on Control flow graph
        reachness
        availability
        liveness
    3. global optimizaiton
        based on data flow analysis
        apply the items in local optimization globally after data flow analysis
        loop optimization
                                                                                                      
