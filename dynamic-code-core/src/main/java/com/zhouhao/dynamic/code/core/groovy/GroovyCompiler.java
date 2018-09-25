package com.zhouhao.dynamic.code.core.groovy;

import com.zhouhao.dynamic.code.core.DynamicCodeCompiler;
import groovy.lang.GroovyClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

//Created by zhou on 2017/10/24
public class GroovyCompiler  implements DynamicCodeCompiler{

    private static final Logger LOG = LoggerFactory.getLogger(GroovyCompiler.class);

    @Override
    public Class compile(String sCode) throws Exception {
        GroovyClassLoader loader = getGroovyClassLoader();
        Class groovyClass = loader.parseClass(sCode);
        return groovyClass;    }

    @Override
    public Class compile(File file) throws Exception {
        GroovyClassLoader loader = getGroovyClassLoader();
        Class groovyClass = loader.parseClass(file);
        return groovyClass;
    }

    GroovyClassLoader getGroovyClassLoader() {
        return new GroovyClassLoader();
    }

}
