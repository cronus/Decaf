package edu.mit.compilers;

import java.io.*;
import java.util.*;
import antlr.Token;
import antlr.CommonAST;
import antlr.collections.AST;
import edu.mit.compilers.grammar.*;
import edu.mit.compilers.tools.CLI;
import edu.mit.compilers.tools.CLI.Action;

abstract class Descriptor {

    Descriptor() {
    }
}

class ProgramDescriptor extends Descriptor {

    protected HashMap<String, MethodDescriptor> exMethodST;

    protected HashMap<String, VarDescriptor> globalVarST;

    protected HashMap<String, MethodDescriptor>   methodST;

    ProgramDescriptor() {

        super();

        exMethodST  = new HashMap<>();
        globalVarST = new HashMap<>();
        methodST    = new HashMap<>();
    }

    HashMap<String, MethodDescriptor> getExMethodST() {
        return exMethodST;
    }

    HashMap<String, VarDescriptor> getGlobalVarST() {
        return globalVarST;
    }

    HashMap<String, MethodDescriptor> getMethodST() {
        return methodST;
    }

}

class MethodDescriptor extends Descriptor {

    protected HashMap<String, VarDescriptor> localVarST;
    protected HashMap<String, VarDescriptor> parameterST;

    MethodDescriptor() {
        super();
        
        localVarST  = new HashMap<>();
        parameterST = new HashMap<>();
    }

    HashMap<String, VarDescriptor> getLocalVarST() {
        return localVarST;
    }

    HashMap<String, VarDescriptor> getParameterST() {
        return parameterST;
    }
}

class VarDescriptor extends Descriptor {

    protected HashMap<String, TypeDescriptor> typeST;

    VarDescriptor() {
        super();

        typeST = new HashMap<>();

        TypeDescriptor intDesc  = new TypeDescriptor();
        TypeDescriptor boolDesc = new TypeDescriptor();

        typeST.put("int", intDesc);
        typeST.put("bool", boolDesc);
    }

    HashMap<String, TypeDescriptor> getTypeST() {
        return typeST;
    }
}

class TypeDescriptor extends Descriptor {
  
    TypeDescriptor() {
        super();
    }
}
