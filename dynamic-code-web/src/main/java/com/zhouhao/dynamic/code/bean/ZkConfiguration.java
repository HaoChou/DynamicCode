//package com.zhouhao.dynamic.code.bean;
//
//import com.zhouhao.dynamic.code.CodeGetterImpl;
//import com.zhouhao.dynamic.code.core.DynamicCodeClassLoader;
//import com.zhouhao.dynamic.code.core.DynamicCodeManager;
//import com.zhouhao.dynamic.code.core.groovy.GroovyCompiler;
//import com.zhouhao.dynamic.code.core.zookeeper.ZkClient;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
////Created by zhou on 2017/10/24
//@Configuration
//public class ZkConfiguration {
//    @Value("${zookeeper.server}")
//    private String zookeeperServer;
//    @Value(("${zookeeper.sessionTimeoutMs}"))
//    private int sessionTimeoutMs;
//    @Value("${zookeeper.connectionTimeoutMs}")
//    private int connectionTimeoutMs;
//    @Value("${zookeeper.maxRetries}")
//    private int maxRetries;
//    @Value("${zookeeper.baseSleepTimeMs}")
//    private int baseSleepTimeMs;
//
//    @Bean(initMethod = "init", destroyMethod = "stop")
//    public ZkClient zkClient() {
//        ZkClient zkClient = ZkClient.INSTANCE;
//        zkClient.setZookeeperServer(zookeeperServer);
//        zkClient.setSessionTimeoutMs(sessionTimeoutMs);
//        zkClient.setConnectionTimeoutMs(connectionTimeoutMs);
//        zkClient.setMaxRetries(maxRetries);
//        zkClient.setBaseSleepTimeMs(baseSleepTimeMs);
//        return zkClient;
//    }
//
//    @Bean
//    public DynamicCodeClassLoader DynamicCodeClassLoader() {
//        DynamicCodeClassLoader.INSTANCE.setCompiler(new GroovyCompiler());
//        return DynamicCodeClassLoader.INSTANCE;
//    }
//
//    @Bean(initMethod = "manageCodes" )
//    public DynamicCodeManager DynamicCodeManager() {
//        DynamicCodeManager.INSTANCE.setCodeGetter(new CodeGetterImpl());
//        return DynamicCodeManager.INSTANCE;
//    }
//
//    public String getZookeeperServer() {
//        return zookeeperServer;
//    }
//
//    public void setZookeeperServer(String zookeeperServer) {
//        this.zookeeperServer = zookeeperServer;
//    }
//
//    public int getSessionTimeoutMs() {
//        return sessionTimeoutMs;
//    }
//
//    public void setSessionTimeoutMs(int sessionTimeoutMs) {
//        this.sessionTimeoutMs = sessionTimeoutMs;
//    }
//
//    public int getConnectionTimeoutMs() {
//        return connectionTimeoutMs;
//    }
//
//    public void setConnectionTimeoutMs(int connectionTimeoutMs) {
//        this.connectionTimeoutMs = connectionTimeoutMs;
//    }
//
//    public int getMaxRetries() {
//        return maxRetries;
//    }
//
//    public void setMaxRetries(int maxRetries) {
//        this.maxRetries = maxRetries;
//    }
//
//    public int getBaseSleepTimeMs() {
//        return baseSleepTimeMs;
//    }
//
//    public void setBaseSleepTimeMs(int baseSleepTimeMs) {
//        this.baseSleepTimeMs = baseSleepTimeMs;
//    }
//}
