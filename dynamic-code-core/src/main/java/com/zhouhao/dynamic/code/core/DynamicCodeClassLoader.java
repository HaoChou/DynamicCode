package com.zhouhao.dynamic.code.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//Created by zhou on 2017/10/24
public class DynamicCodeClassLoader {

    public final static DynamicCodeClassLoader INSTANCE = new DynamicCodeClassLoader();
    private static final Logger LOG = LoggerFactory.getLogger(DynamicCodeClassLoader.class);

    private final ConcurrentHashMap<String, Class> clazzByName = new ConcurrentHashMap<String, Class>();
    private final ConcurrentHashMap<String, Object> instanceByName = new ConcurrentHashMap<String, Object>();

    static DynamicCodeCompiler COMPILER;

    private static String POINT=".";

    public void setCompiler(DynamicCodeCompiler compiler) {
        COMPILER = compiler;
    }

    public Class getClassByName(String name) {
        return clazzByName.get(name);
    }


    public boolean putInstance(String className, Object instance) {
        if(StringUtils.isEmpty(className)||null==instance)
            return false;
        instanceByName.put(className,instance);
        return true;
    }
    public List<String> getAllClassName()
    {
        List<String> classNameList=new ArrayList<String>();
        for(Map.Entry entry : clazzByName.entrySet()){
            classNameList.add((String) entry.getKey());
        }
        return classNameList;
    }

    public Object getInstanceByClassName(String className) {
        return instanceByName.get(className);
    }

    public boolean removeClassAndInstance(String className){
        clazzByName.remove(className);
        instanceByName.remove(className);
        LOG.info("删除示例成功,className:{}",className);
        return true;
    }

    public boolean refreshClassAndInstance(String code) {
        try {
            if(StringUtils.isEmpty(code)){
                LOG.error("刷新实例失败,code为空");
                return false;
            }
            Class clazz = COMPILER.compile(code);
            String className = clazz.getName();
            //名字中包含点 不通过 如果groovy文件头部带了package 则类名类似 com.alibaba.test.TestClass
            if(className.contains(POINT)){
                LOG.error("刷新实例失败,类名不能包括英文句号符号:{},请去掉groovy代码头部的package行。",POINT);
                return false;
            }

            if(Modifier.isAbstract(clazz.getModifiers()))
            {
                LOG.error("刷新实例失败,类不能是抽象的，类名:{}",className);
                return false;
            }
            Object instance = getInstanceByName(className);
            clazzByName.put(className, clazz);
            putInstance(className, instance);
            LOG.info("刷新示例成功,className:{}",className);
            return true;
        }catch (Exception e ){
            LOG.error("刷新实例失败,code:"+code,e);
            return false;
        }
    }

    /**
     * 根据类名称和构造函数的参数获取实例
     *
     * @param className
     * @param parameters
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    public Object getInstanceByNameAndConstructorParameters(String className, Object... parameters) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class clazz = clazzByName.get(className);
        if (null != clazz) {
            Class[] paramClazzes = new Class[parameters.length];
            for (int index = 0; index < parameters.length; index++) {
                paramClazzes[index] = parameters[index].getClass();
            }
            return clazz.getConstructor(paramClazzes).newInstance();

        }
        return null;
    }


    public Object getInstanceByName(String className) throws IllegalAccessException, InstantiationException {
        Class clazz = clazzByName.get(className);
        if (null != clazz) {
            return clazz.newInstance();
        }
        return null;
    }
}
