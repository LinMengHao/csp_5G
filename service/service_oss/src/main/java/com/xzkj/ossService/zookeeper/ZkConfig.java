package com.xzkj.ossService.zookeeper;


import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * zookeeper统一配置中心
 */
@Component
public class ZkConfig {
    //这里可以使用redis缓存代替
    Map<String,String> cache=new HashMap<>();
    //支持zookeeper客户端
    private CuratorFramework client;
    private static final String CONFIG_PREFIX="/CONFIG";

    /**
     * 构造函数，只执行一次
     */
    @PostConstruct
    public void init(){
        try {
            //通过工厂生产客户端
            this.client= CuratorFrameworkFactory.newClient("82.157.251.233:2181",new RetryNTimes(3,200000));
            //启动客户端
            this.client.start();

            //从zk中获取配置项并保存到缓存中去
            List<String> childrenNames=client.getChildren().forPath(CONFIG_PREFIX);
            for(String name:childrenNames){
                String value = new String(client.getData().forPath(CONFIG_PREFIX + "/" + name));
                cache.put(name,value);
            }
            //绑定一个监听器cacheData设置为true，事件发生后可以拿到节点发送的内容
            //使用该配置文件的每个应用机器都需要监听
            PathChildrenCache watcher =new PathChildrenCache(client,CONFIG_PREFIX,true);
            watcher.getListenable().addListener(new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                    String path =event.getData().getPath();
                    if(path.startsWith(CONFIG_PREFIX)){
                        String key=path.replace(CONFIG_PREFIX+"/","");
                        //字节点更新或新增时，更新缓存信息
                        if(PathChildrenCacheEvent.Type.CHILD_ADDED.equals(event.getType())
                        ||PathChildrenCacheEvent.Type.CHILD_UPDATED.equals(event.getType())){
                            System.out.println("event.getType()=" + event.getType() + ",key=" + key + ",value="
                                    + new String(event.getData().getData()));
                            cache.put(key, new String(event.getData().getData()));
                        }
                        //字节点被删除时，从缓存中删除
                        if(PathChildrenCacheEvent.Type.CHILD_REMOVED.equals(event.getType())){
                            System.out.println("event.getType()=" + event.getType() + ",key=" + key + ",value="
                                    + new String(event.getData().getData()));
                            cache.remove(key);
                        }
                    }
                }
            });
            //启动监听器
            watcher.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //保存配置信息
    public void save(String name,String value){
        String configFullName=CONFIG_PREFIX+"/"+name;
        try {
            Stat stat=client.checkExists().forPath(configFullName);
            if(stat==null){
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
                        .forPath(configFullName,value.getBytes(StandardCharsets.UTF_8));
            }else {
                client.setData().forPath(configFullName,value.getBytes(StandardCharsets.UTF_8));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //获取配置信息
    public String get(String name){
        return cache.get(name);
    }
}
