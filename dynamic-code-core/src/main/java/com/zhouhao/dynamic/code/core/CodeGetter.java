package com.zhouhao.dynamic.code.core;

import java.util.List;
import java.util.Map;

//Created by zhou on 2017/10/25
public interface CodeGetter {
    String getCodeByClassName(String className);
    List<String> getAllCode();
}
