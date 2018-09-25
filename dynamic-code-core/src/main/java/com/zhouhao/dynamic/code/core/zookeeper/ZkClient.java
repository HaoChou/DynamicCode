package com.zhouhao.dynamic.code.core.zookeeper;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


//Created by zhou on 2017/10/24
public class ZkClient {

    public final static ZkClient INSTANCE = new ZkClient();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private CuratorFramework client;
    private NodeCache nodeCache;
    private PathChildrenCache pathChildrenCache;
    private TreeCache treeCache;
    private String zookeeperServer;
    private int sessionTimeoutMs;
    private int connectionTimeoutMs;
    private int baseSleepTimeMs;
    private int maxRetries;

    public void setZookeeperServer(String zookeeperServer) {
        this.zookeeperServer = zookeeperServer;
    }
    public String getZookeeperServer() {
        return zookeeperServer;
    }
    public void setSessionTimeoutMs(int sessionTimeoutMs) {
        this.sessionTimeoutMs = sessionTimeoutMs;
    }
    public int getSessionTimeoutMs() {
        return sessionTimeoutMs;
    }
    public void setConnectionTimeoutMs(int connectionTimeoutMs) {
        this.connectionTimeoutMs = connectionTimeoutMs;
    }
    public int getConnectionTimeoutMs() {
        return connectionTimeoutMs;
    }
    public void setBaseSleepTimeMs(int baseSleepTimeMs) {
        this.baseSleepTimeMs = baseSleepTimeMs;
    }
    public int getBaseSleepTimeMs() {
        return baseSleepTimeMs;
    }
    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }
    public int getMaxRetries() {
        return maxRetries;
    }

    public void init() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(baseSleepTimeMs, maxRetries);
        client = CuratorFrameworkFactory.builder().connectString(zookeeperServer).retryPolicy(retryPolicy)
                .sessionTimeoutMs(sessionTimeoutMs).connectionTimeoutMs(connectionTimeoutMs).build();
        client.start();
        logger.info("zkClient started!");
    }

    public void stop() {
        if (client != null) CloseableUtils.closeQuietly(client);
        if (pathChildrenCache != null) CloseableUtils.closeQuietly(pathChildrenCache);
        if (nodeCache != null) CloseableUtils.closeQuietly(nodeCache);
        if (treeCache != null) CloseableUtils.closeQuietly(treeCache);
    }

    public CuratorFramework getClient() {
        return client;
    }

    public void setData(String path,String data) {
        try {
            Stat stat = client.checkExists().forPath(path);

            if(stat==null) {
                client.create().forPath(path, data.getBytes());
                logger.info("create path:"+path+",and data:"+data);
            }else {
                client.setData().forPath(path,data.getBytes());
                logger.info("set data path:"+path+",and data:"+data);
            }
        }
        catch (Exception e)
        {
            logger.error("error to set data for path:"+path+",and data:"+data,e);
        }
    }

    public void removePath(String path){
        try {
            Stat stat = client.checkExists().forPath(path);

            if(stat==null) {
                logger.info("the path:"+path+" is not exist");
            }else {
                client.delete().forPath(path);
                logger.info("the path:"+path+" is deleted");
            }
        }
        catch (Exception e)
        {
            logger.error("error to delete the path:"+path,e);
        }
    }
    /*
        *  设置Path Cache, 监控本节点的子节点被创建,更新或者删除，注意是子节点, 子节点下的子节点不能递归监控
        *  事件类型有3个, 可以根据不同的动作触发不同的动作
        *  本例子只是演示, 所以只是打印了状态改变的信息, 并没有在PathChildrenCacheListener中实现复杂的逻辑
        *  @Param path 监控的节点路径, cacheData 是否缓存data
        *  可重入监听
        * */
    public void setPathCacheListener(String path, boolean cacheData) {
            try {
                pathChildrenCache = new PathChildrenCache(client, path, cacheData);
                PathChildrenCacheListener childrenCacheListener = new PathChildrenCacheListener() {
                    @Override
                    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) {
                        ChildData data = event.getData();
                        switch (event.getType()) {
                            case CHILD_ADDED:
                                logger.info("子节点增加, path={}, data={}", data.getPath(), data.getData());
                                break;
                            case CHILD_UPDATED:
                                logger.info("子节点更新, path={}, data={}", data.getPath(), data.getData());
                                break;
                            case CHILD_REMOVED:
                                logger.info("子节点删除, path={}, data={}", data.getPath(), data.getData());
                                break;
                            default:
                                break;
                        }
                    }
                };
                pathChildrenCache.getListenable().addListener(childrenCacheListener);
                pathChildrenCache.start(StartMode.POST_INITIALIZED_EVENT);
            } catch (Exception e) {
                logger.error("PathCache监听失败, path=", path);
            }

    }

    /*
    *  设置Node Cache, 监控本节点的新增,删除,更新
    *  节点的update可以监控到, 如果删除会自动再次创建空节点
    *  本例子只是演示, 所以只是打印了状态改变的信息, 并没有在NodeCacheListener中实现复杂的逻辑
    *  @Param path 监控的节点路径, dataIsCompressed 数据是否压缩
    *  不可重入监听
    * */
    public void setNodeCacheListener(String path, boolean dataIsCompressed) {
        try {
            nodeCache = new NodeCache(client, path, dataIsCompressed);
            NodeCacheListener nodeCacheListener = new NodeCacheListener() {
                @Override
                public void nodeChanged() throws Exception {
                    ChildData childData = nodeCache.getCurrentData();
                    logger.info("ZNode节点状态改变, path={}", childData.getPath());
                    logger.info("ZNode节点状态改变, data={}", childData.getData());
                    logger.info("ZNode节点状态改变, stat={}", childData.getStat());
                }
            };
            nodeCache.getListenable().addListener(nodeCacheListener);
            nodeCache.start();
        } catch (Exception e) {
            logger.error("创建NodeCache监听失败, path={}", path);
        }
    }


    /*
    *  设置Tree Cache, 监控本节点的新增,删除,更新
    *  节点的update可以监控到, 如果删除不会自动再次创建
    *  本例子只是演示, 所以只是打印了状态改变的信息, 并没有在NodeCacheListener中实现复杂的逻辑
    *  @Param path 监控的节点路径, dataIsCompressed 数据是否压缩
    *  可重入监听
    * */
    public void setTreeCacheListener(final String path) {
        try {
            treeCache = new TreeCache(client, path);
            TreeCacheListener treeCacheListener = new TreeCacheListener() {
                @Override
                public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
                    ChildData data = event.getData();
                    if(data != null){
                        switch (event.getType()) {
                            case NODE_ADDED:
                                logger.info("[TreeCache]节点增加, path={}, data={}", data.getPath(), data.getData());
                                break;
                            case NODE_UPDATED:
                                logger.info("[TreeCache]节点更新, path={}, data={}", data.getPath(), data.getData());
                                break;
                            case NODE_REMOVED:
                                logger.info("[TreeCache]节点删除, path={}, data={}", data.getPath(), data.getData());
                                break;
                            default:
                                break;
                        }
                    }else{
                        logger.info("[TreeCache]节点数据为空, path={}", data.getPath());
                    }
                }
            };
            treeCache.getListenable().addListener(treeCacheListener);
            treeCache.start();
        } catch (Exception e) {
            logger.error("创建TreeCache监听失败, path={}", path);
        }

    }
}
