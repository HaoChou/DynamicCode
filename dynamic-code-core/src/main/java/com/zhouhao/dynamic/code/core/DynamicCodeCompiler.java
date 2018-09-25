package com.zhouhao.dynamic.code.core;

import java.io.File;

//Created by zhou on 2017/10/24
public interface DynamicCodeCompiler {

    Class compile(String codeStr) throws Exception;

    Class compile(File file) throws Exception;
}
