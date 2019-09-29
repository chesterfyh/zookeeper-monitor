package com.flynn.zk;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.transaction.CuratorOp;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;
import org.apache.curator.framework.api.transaction.TransactionOp;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

/**
 * Zookeeper客户端封装
 * @author flynn
 *
 */
public class ZookeeperClient {
	//创建连接实例
	private CuratorFramework client = null;
	//节点事件监听
	private Map<String, TreeCache> nodeListeners = new ConcurrentHashMap<String, TreeCache>();

	public ZookeeperClient(String connectString,int sessionTimeoutMs, int connectionTimeoutMs, RetryPolicy retryPolicy) throws Exception {
		//创建 CuratorFrameworkImpl实例
		client = CuratorFrameworkFactory.newClient(connectString, sessionTimeoutMs, connectionTimeoutMs, retryPolicy);
		//启动
		client.start();
	}
	
	public ZookeeperClient(String connectString,int sessionTimeoutMs, int connectionTimeoutMs, RetryPolicy retryPolicy,ConnectionStateListener connectionStateListener) throws Exception {
		//创建 CuratorFrameworkImpl实例
		client = CuratorFrameworkFactory.newClient(connectString, sessionTimeoutMs, connectionTimeoutMs, retryPolicy);
		//启动
		client.start();
		if(connectionStateListener!=null) {
			client.getConnectionStateListenable().addListener(connectionStateListener);
		}
	}
	
	public void disconnect() {
		client.close();
	}
	/**
	 * 增加某个节点的监听
	 * @param path
	 * @param callback
	 * @throws Exception 
	 */
	public void addListener(String path ,Consumer<TreeCacheEvent> callback) throws Exception {
		TreeCache treeCache = new TreeCache(client, path);
		nodeListeners.put(path,treeCache);
		//启动监听
		treeCache.start();
        // 没有开启模式作为入参的方法
		treeCache.getListenable().addListener(new TreeCacheListener(){
            @Override
            public void childEvent(CuratorFramework client, TreeCacheEvent event)
                    throws Exception {
            	try {
            		callback.accept(event);
				} finally {
					if(event.getType() == Type.NODE_REMOVED) {
						String path = event.getData().getPath();
						if(path!=null) {
							removeLisner(path);
							System.err.println("Delete Listener: "+path);
						}
					}
				}
//                switch(event.getType()) {
//                case NODE_ADDED: 
//                    System.out.println("tree:发生节点添加" + event.getData().toString() ); break;
//                case NODE_UPDATED:
//                    System.out.println("tree:发生节点更新"); 
//                    break;
//                case NODE_REMOVED:
//                    System.out.println("tree:发生节点删除"); 
//                   
//                    break;
//                case CONNECTION_SUSPENDED: 
//                    break;
//                case CONNECTION_RECONNECTED:
//                    break;
//                case CONNECTION_LOST:
//                    break;
//                case INITIALIZED:
//                    System.out.println("初始化的操作"); break;
//                default:
//                    break;
//                }
            }
        });
	}
	/**
	 * 删除此节点上的监听
	 * @param path
	 */
	private void removeLisner(String path) {
		TreeCache treeCache = nodeListeners.remove(path);
		if(treeCache!=null) {
			treeCache.close();
		}
	}
	/**
	 * 创建指定节点，若父节点不存在则自动创建父节点
	 * @param path
	 * @param data
	 * @param mode
	 * @throws Exception
	 */
	public String createNode(String path,String data,CreateMode mode) throws Exception {
//		client.create().creatingParentContainersIfNeeded().withMode(mode).forPath(path, data.getBytes(Charset.forName("UTF-8")));
		return createNode(path, data.getBytes(Charset.forName("UTF-8")), mode);
	}
	public String createNode(String path,byte[] data,CreateMode mode) throws Exception {
		return client.create().creatingParentContainersIfNeeded().withMode(mode).forPath(path, data);
	}
	/**
	 * 设置一个节点，当节点不存在时，会创建一个永久节点
	 * @param path
	 * @param data
	 * @throws Exception
	 */
	public String setNode(String path,String data) throws Exception {
		return setNode(path, data, CreateMode.PERSISTENT);
	}
	/**
	 * 设置节点的内容，若节点不存在，则创建此节点
	 * @param path
	 * @param data
	 * @param mode
	 * @throws Exception
	 */
	public String setNode(String path,String data,CreateMode mode) throws Exception {
		return setNode(path, data.getBytes(Charset.forName("UTF-8")),mode);
	}
	public String setNode(String path,byte[] data,CreateMode mode) throws Exception {
		if(checkExists(path)) {
			client.setData().forPath(path, data);
			return path;
		}else {
			return createNode(path,data,mode);
		}
	}
	/**
	 * 检测节点是否存在
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public boolean checkExists(String path) throws Exception {
		Stat stat = client.checkExists().forPath(path);
		if(stat!=null) {
			return true;
		}
		return false;
	}
	/**
	 * 获取节点内容
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public Optional<String> getNode(String path) throws Exception {
		byte[] bytes = client.getData().forPath(path);
		return Optional.ofNullable(bytes != null ? new String(bytes,Charset.forName("UTF-8")) : null);
	}
	/**
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public List<String> getChildren(String path) throws Exception {
		return client.getChildren().forPath(path);
	}
	/**
	 * 获取子节点数据
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> getChildrenData(String path) throws Exception{
		List<String> children = getChildren(path);
		return children.stream().collect(Collectors.toMap(key->key, childName->{
			try {
				return getNode(path+"/"+childName).get();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}));
	}
	/**
	 * 删除节点
	 * @param path
	 * @throws Exception
	 */
	public void deleteNode(String path) throws Exception {
		//删除该节点
		client.delete().forPath(path);
	}
	/**
	 * 删除节点自身及子节点
	 * @param path
	 * @throws Exception
	 */
	public void deleteNodeChildren(String path) throws Exception {
		//级联删除子节点
		client.delete().guaranteed().deletingChildrenIfNeeded().forPath(path);
	}
	/**
	 * 事务处理
	 * @param processeres
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<CuratorTransactionResult> transaction(Function<TransactionOp , CuratorOp> ... processeres) throws Exception {
		TransactionOp transactionOp = client.transactionOp();
		List<CuratorOp> curatorOps = new ArrayList<CuratorOp>();
		Arrays.stream(processeres).forEach(func->{
			CuratorOp curatorOp = func.apply(transactionOp);
			if(curatorOp != null) {
				curatorOps.add(curatorOp);
			}
		});
		CuratorOp[] ops = new CuratorOp[curatorOps.size()];
		curatorOps.toArray(ops);
		List<CuratorTransactionResult> results = client.transaction()
				.forOperations(ops);
		return results;
//		//遍历输出结果
//		for(CuratorTransactionResult result : results){
//			System.out.println("执行结果是： " + result.getForPath() + "--" + result.getType());
//		}
	}
	public static CuratorOp curatorOpForCreate(TransactionOp transactionOp,String path,String data,CreateMode mode) {
		try {
			return transactionOp.create().withMode(mode).forPath(path, data.getBytes(Charset.forName("UTF-8")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static CuratorOp curatorOpForSet(TransactionOp transactionOp,String path,String data) {
		try {
			return transactionOp.setData().forPath(path, data.getBytes(Charset.forName("UTF-8")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static CuratorOp curatorOpForDelete(TransactionOp transactionOp,String path) {
		try {
			return transactionOp.delete().forPath(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static CuratorOp curatorOpForCheck(TransactionOp transactionOp,String path) {
		try {
			return transactionOp.check().forPath(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
