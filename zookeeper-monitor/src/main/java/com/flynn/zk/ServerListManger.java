package com.flynn.zk;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;

import com.flynn.zk.TreeDir.Node;
import com.google.common.util.concurrent.FutureCallback;

/**
 * 服务器列表管理
 * @author flynn.fan
 *
 */
public class ServerListManger {

	private TreeDir serverList;
	//服务器列表配置文件
	private Properties serverProperties;
	
	private File file;
	
	private String selectServer;
	public ServerListManger() {
		
	}
	public ServerListManger(TreeDir serverList) throws IOException {
		this.serverList = serverList;
		
		
		
		
		String filePath = "serverlist.properties";
		file = new File(filePath);

		serverProperties = new Properties();
		if (!file.exists()) {
			file.createNewFile();
			serverProperties.load(new FileInputStream(file));

//			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//			System.out.println("配置文件目录：");
//			properties.setProperty("excel_dir", reader.readLine());
//			System.out.println("生成的服务端配置存放目录: ");
//			properties.setProperty("server_dir", reader.readLine());
//			System.out.println("生成的客户端配置存放目录: ");
//			properties.setProperty("client_dir", reader.readLine());
			
//			serverProperties.put(String.valueOf(serverProperties.keySet().size()+1), "127.0.0.1:2181");
//			serverProperties.put(String.valueOf(serverProperties.keySet().size()+1), "192.168.37.5:2181");
			serverProperties.store(new FileOutputStream(file), "生成配置文件");
		}
		serverProperties.load(new FileInputStream(file));
		
		serverProperties.values().stream().forEach(serverInfo->serverList.createCurrNode(serverInfo.toString()));
		
		
		serverList.clickItem(selectedNode->{
			selectServer = selectedNode.getPath();
			
			AppWin.getInstance().getServerTab().getAddress().setText(selectServer);
			System.out.println("Path: "+selectedNode.getPath());
		});
		serverList.expandAll(new TreePath(serverList.getTreeModel().getRoot()),true);
		
		serverList.addMouseListener(new MouseAdapter() {
			
			 /**
		     * {@inheritDoc}
		     */
		    public void mouseClicked(MouseEvent e) {
		    	if(e.getClickCount() ==2) {
		    		DefaultMutableTreeNode selectNode = (DefaultMutableTreeNode)serverList.getLastSelectedPathComponent();
		    		if(selectNode.getParent() == null) {
		    			return;
		    		}
		    		String path = selectNode.getUserObject().toString();
		    		AppWin.getInstance().createZkClient(path,new ConnectionStateListener() {
		    			
		    			@Override
		    			public void stateChanged(CuratorFramework client, ConnectionState newState) {
		    				// TODO Auto-generated method stub
		    				if(newState == ConnectionState.CONNECTED || newState == ConnectionState.RECONNECTED) {
		    					AppWin.getInstance().getStatus().setText("Connect Zookeeper ["+path+"] successfully!!!");
		    					CompletableFuture.runAsync(()->{
		    						try {
		    							initTreeDir();
		    							addNodeListener();//增加节点监听
		    						} catch (Exception e) {
		    							e.printStackTrace();
		    						}
		    					});
		    				}
		    				System.err.println("Zookeeper,status: "+ newState);
		    			}
		    		});
		    	}
		    }
		});
	}
	
	public void initTreeDir() throws Exception {
		AppWin.getInstance().setTreeDir();
	}
	
	public void addNodeListener() throws Exception {
		AppWin.getInstance().getZkClient().addListener("/", event->{
			   switch(event.getType()) {
             case NODE_ADDED:
            	 AppWin.getInstance().getMessageTab().output("CREATE: "+event.getData().getPath()+"\n");
            	 break;
             case NODE_UPDATED:
            	 AppWin.getInstance().getMessageTab().output("UPDATE: "+event.getData().getPath()+"\n");
                 System.out.println("tree:发生节点更新: "+event.getData().getPath()); 
                 break;
             case NODE_REMOVED:
            	 AppWin.getInstance().getMessageTab().output("REMOVE: "+event.getData().getPath()+"\n");
                 System.out.println("tree:发生节点删除"); 
                 break;
             case CONNECTION_SUSPENDED: 
                 break;
             case CONNECTION_RECONNECTED:
                 break;
             case CONNECTION_LOST:
                 break;
             case INITIALIZED:
                 System.out.println("初始化的操作"); break;
             default:
                 break;
             }
		});
	}
	/**
	 * 新增
	 * @param serverInfo
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void put(String serverInfo) throws FileNotFoundException, IOException {
		if(serverProperties.contains(serverInfo)) {
			return;
		}
		serverProperties.put(String.valueOf(serverProperties.keySet().size()+1), serverInfo);
		serverProperties.store(new FileOutputStream(file), "生成配置文件");
		Node node = serverList.createCurrNode(serverInfo);
		serverList.setSelectionPath(new TreePath(node.getInner().getPath()));
	}
	/**
	 * 更新
	 * @param serverInfo
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void update(String serverInfo) throws FileNotFoundException, IOException {
		if(serverProperties.contains(serverInfo)) {
			return;
		}
		if(selectServer!=null && !selectServer.isEmpty()) {
			
			Optional<Object> selectKey = serverProperties.keySet().stream().filter(key->selectServer.equals(serverProperties.getProperty(key.toString()))).findFirst();
			if(selectKey.isPresent()) {
				String old = serverProperties.getProperty(selectKey.get().toString());
				serverProperties.put(selectKey.get().toString(), serverInfo);
				serverProperties.store(new FileOutputStream(file), "生成配置文件");
				
				serverList.remove(serverList.getNode(old));
				
				Node node = serverList.createCurrNode(serverInfo);
				serverList.setSelectionPath(new TreePath(node.getInner().getPath()));
			}
		}
	}
	
	/**
	 * 更新
	 * @param serverInfo
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void delete(String serverInfo) throws FileNotFoundException, IOException {
		if(!serverProperties.contains(serverInfo)) {
			return;
		}
		if(serverInfo!=null && !serverInfo.isEmpty()) {
			
			Optional<Object> selectKey = serverProperties.keySet().stream().filter(key->serverInfo.equals(serverProperties.getProperty(key.toString()))).findFirst();
			if(selectKey.isPresent()) {
				serverProperties.remove(selectKey.get().toString());
				serverProperties.store(new FileOutputStream(file), "生成配置文件");
				serverList.remove(serverList.getNode(serverInfo));
			}
		}
	}
}
