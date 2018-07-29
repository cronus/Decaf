java-skeleton
=============

Java Skeleton Code for 6.035


Structure of compiler (to lab 3, code generation)
-------------------------------------------------

 ---------------                                  ----------                                        --------------
 | source code | ------- lexer analysis --------> | tokens | -------- syntatic analysis ----------->| parse tree |                     Using ANTLR   
 ---------------                                  ----------                                        --------------
                                                                                                          |
 ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                                                                                                          |                            
                                                                                                          |  -------------------       Hand Coded
                                                                                                          |  | semantic action |
                                                                                                          |  -------------------
                                                                                                          |  /     
                                                                                                          | /    
                                                                                                          |/        
                                                                                              syntax directed translation  
                                                                                                          |
                                                                                                         \|/
                                                                                             ------------------------------                        
                                                                                             | abstract syntax tree (AST) | <----------------------- tranverse ------- semantics analysis 
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
------------
### 1. local optimization
        common subexpression elimation (CSE)
        copy propagation
        dead code elimation
        algebra simplication (not a data flow optimization)
### 2. data flow analysis based on Control flow graph                                        ==========>   Unified Framework with data flow iterative algorithm
        reachness
        availability
        liveness
### 3. global optimizaiton
        based on data flow analysis
        apply the items in local optimization globally after data flow analysis
        loop optimization
                                                                                                      
    -----------------------------------------------------------------------------------

### 4. Register Allocation
        webs
            def-use chains: connect definition to all reachable uses
            unit of register allocation
        graph coloring
            each web is allocated a register
            if two webs interfere they cannot use the same register

### 5. Parallelization
        Finding FORALL Loops out of FOR loops
        Dependence analysis
            if there are no loop carried dependences --> parallelizable

            Method
                1. Distance Vector Method: the ith loop is parallelizable for all dependence d = [d1, ..., di, ... dn]
                either
                    one of d1, ..., di-1 is > 0
                or
                    all d1,..., di = 0

                2. Integer Programming Method
                exist an integer vector T, such that AT <= b where A is an integer matrix and b is an integer vector

### 6. Memory Optimization: reduce cache misses
        Loop Transformation
        Data Transformation
            strip-mining
            permutation

### 7. Instructions Scheduling
        List scheduling
            1. Rename to avoid antidependences
            2. Build a dependence graph, topological sort of the DAG
            3. Assign priorities to each operation
            4. Iteratively select an operation and schedule it
            note. use heuristics when necessary
