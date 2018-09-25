package com.zhouhao.dynamic.code.core;

import com.zhouhao.dynamic.code.core.zookeeper.ZkClient;
import com.zhouhao.dynamic.code.core.zookeeper.ZkConstant;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.zhouhao.dynamic.code.core.zookeeper.ZkConstant.PATH_SPLIT_CHAR;
import static com.zhouhao.dynamic.code.core.zookeeper.ZkConstant.ZK_ROOT_PATH_NODE;
import static org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type.CHILD_ADDED;
import static org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type.CHILD_REMOVED;
import static org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type.CHILD_UPDATED;

//Created by zhou on 2017/10/25
public class DynamicCodeManager {
    private static final Logger LOG = LoggerFactory.getLogger(DynamicCodeManager.class);

    public static DynamicCodeManager INSTANCE = new DynamicCodeManager();
    private CodeGetter codeGetter;
    private PathChildrenCache pathChildrenCache;

    public void manageCodes(){
        List<String> codes =codeGetter.getAllCode();
        for (String code:codes) {
            DynamicCodeClassLoader.INSTANCE.refreshClassAndInstance(code);
        }
        //如果没有根目录 创建
        ZkClient.INSTANCE.setData(ZK_ROOT_PATH_NODE,String.valueOf(System.currentTimeMillis()));

        for(String className: DynamicCodeClassLoader.INSTANCE.getAllClassName()){
            refreshRemoteCodeByClassName(className);//如果zk上没有这个类的节点 那么创建
        }
        String rootPath=ZK_ROOT_PATH_NODE;
        try {
            pathChildrenCache = new PathChildrenCache(ZkClient.INSTANCE.getClient(), rootPath, true);
            PathChildrenCacheListener childrenCacheListener = new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) {
                    ChildData data = event.getData();
                    String lastNodeName=null;
                    //节点初始化的情况data空
                    if(null!=data) {
                        lastNodeName = getLastNodeName(data.getPath());//最后一个节点 就是className
                    }
                    PathChildrenCacheEvent.Type eventType=event.getType();

                    if(eventType.equals(CHILD_ADDED)){
                        LOG.info("子节点增加, path={}, data={}", data.getPath(), data.getData());
                        String code=codeGetter.getCodeByClassName(lastNodeName);
                        DynamicCodeClassLoader.INSTANCE.refreshClassAndInstance(code);
                    }else if (eventType.equals(CHILD_UPDATED)){
                        LOG.info("子节点更新, path={}, data={}", data.getPath(), data.getData());
                        String code=codeGetter.getCodeByClassName(lastNodeName);
                        DynamicCodeClassLoader.INSTANCE.refreshClassAndInstance(code);
                    }else if(eventType.equals(CHILD_REMOVED)){
                        LOG.info("子节点删除, path={}, data={}", data.getPath(), data.getData());
                        DynamicCodeClassLoader.INSTANCE.removeClassAndInstance(lastNodeName);
                    }
                }
            };
            pathChildrenCache.getListenable().addListener(childrenCacheListener);
            pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        } catch (Exception e) {
            LOG.error("PathCache监听失败, path="+rootPath, rootPath);
        }
    }


    public boolean refreshRemoteCodeByClassName(String className){
        String path=getEntirePathByClassName(className);
        ZkClient.INSTANCE.setData(path,String.valueOf(System.currentTimeMillis()));
        return true;
    }
    public boolean deleteRemoteClassAndInstanceByClassName(String className){
        String path=getEntirePathByClassName(className);
        ZkClient.INSTANCE.removePath(path);
        return true;
    }


    private String getEntirePathByClassName(String className){
        return ZkConstant.ZK_ROOT_PATH+className;
    }

    /**
     *
     * @param path  path=/dynamic_code/zhouhaotest
     * @return  zhouhaotest
     */
    private String getLastNodeName(String path){
        if(!path.contains(PATH_SPLIT_CHAR))
            return null;
        String result=path;
        while (result.contains(PATH_SPLIT_CHAR)) {
            result=result.substring(result.indexOf(PATH_SPLIT_CHAR)+1);
        }
        return result;
    }

    public void setPathChildrenCache(PathChildrenCache pathChildrenCache) {
        this.pathChildrenCache = pathChildrenCache;
    }

    public void setCodeGetter(CodeGetter codeGetter) {
        this.codeGetter = codeGetter;
    }

}
