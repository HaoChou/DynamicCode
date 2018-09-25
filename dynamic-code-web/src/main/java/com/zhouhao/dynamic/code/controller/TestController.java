package com.zhouhao.dynamic.code.controller;

import com.zhouhao.dynamic.code.NameInterface;
import com.zhouhao.dynamic.code.core.DynamicCodeClassLoader;
import com.zhouhao.dynamic.code.core.DynamicCodeManager;
import com.zhouhao.dynamic.code.core.zookeeper.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

//Created by zhou on 2017/10/25
@RestController
public class TestController {

//
//    @Autowired
//    ZkClient zkClient;

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("/hello")
    public void getHello(HttpServletResponse response) throws IOException
    {
        try
        {
            new Integer("sss");
            response.sendRedirect("http://www.baidu.com");
            return;
        }
        catch (Exception e)
        {
            LOG.error("error",e);
        }
        response.sendRedirect("http://www.baidu.com");
    }

    @RequestMapping("/getName")
    public String setData()
    {
        NameInterface nameInterface= (NameInterface) DynamicCodeClassLoader.INSTANCE.getInstanceByClassName("NameInterfaceImpl");
        return nameInterface.getName();
    }
    @RequestMapping("/refresh")
    public String refresh(String className)
    {
        DynamicCodeManager.INSTANCE.refreshRemoteCodeByClassName(className);
        return "refreshRemoteCodeByClassName:"+className;
    }
    @RequestMapping("/delete")
    public String delete(String className)
    {
        DynamicCodeManager.INSTANCE.deleteRemoteClassAndInstanceByClassName(className);
        return "deleteRemoteClassAndInstanceByClassName:"+className;
    }
    @RequestMapping("/getAllClass")
    public List<String> setPathCacheListener()
    {
        return DynamicCodeClassLoader.INSTANCE.getAllClassName();
    }


}
