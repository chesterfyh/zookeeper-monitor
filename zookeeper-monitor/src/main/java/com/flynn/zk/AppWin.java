package com.flynn.zk;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.io.IOException;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.tree.TreePath;

import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

import com.flynn.zk.tab.MessageTab;
import com.flynn.zk.tab.ServerTab;
import com.flynn.zk.tab.ViewTab;

/**
 * 主窗口
 * @author flynn.fan
 *
 */
public class AppWin extends AbstractFrame {
	private static final long serialVersionUID = -7847719131553058725L;
	private static AppWin Instance = null;
	//节点数据
	private TreeDir treeDir;
	
	private ServerListManger serverListManger;
	
	private ZooNodeManager zooNodeManager;
	
	private JLabel status;
	
	private ServerTab serverTab;
	
	private MessageTab messageTab;
	
	private ViewTab viewTab;
	
	private ZookeeperClient zkClient;
	
	private String connectStr;
	public static AppWin getInstance() {
		return Instance;
	}
	public AppWin() {
		super("ZooKeeper客户端");
		Instance = this;
	}
	@Override
	public void add(Container contentContainer) {
		try {
			initServerList(contentContainer);
		} catch (IOException e) {
			System.err.println("服务器配置文件读取错误，你需求有对配置文件更新操作权限");
		}
		initTreeDirectory(contentContainer);
		initContent(contentContainer);
		
		initStatus(contentContainer);
	}
	
	private void initStatus(Container contentContainer) {
		status = new JLabel("双击连接地址进行连接");
		status.setForeground(Color.RED);
		JScrollPane scrollpane = new JScrollPane(status);
		scrollpane.setBounds(0, 800, 1500, 30);
		contentContainer.add(scrollpane);
	}
	
	/**
	 * 创建客户端
	 * @param path
	 * @return
	 */
	public ZookeeperClient createZkClient(String path,ConnectionStateListener connectionStateListener) {
		if(zkClient!=null && !connectStr.equals(path)) {
			zkClient.disconnect();
			status.setText("连接断开 ...... ,等待 ... ...");
			treeDir.reset();
		}
		try {
			zkClient = new ZookeeperClient(path, 10*1000, 3*1000, new ExponentialBackoffRetry(1000, 3),connectionStateListener);
			connectStr = path;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return zkClient;
	}
	
	public ZookeeperClient getZkClient() {
		return zkClient;
	}
	/**
	 * 初始化服务器列表
	 * @param contentContainer
	 * @throws IOException 
	 */
	private void initServerList(Container contentContainer) throws IOException {
		String rootName = "服务器列表";
		TreeDir serverList = new TreeDir(rootName);
//		serverList.expandAll(new TreePath(serverList.getTreeModel().getRoot()), true);
		JScrollPane scrollpane = new JScrollPane(serverList);
		scrollpane.setBounds(0, 0, 200, 800);
		contentContainer.add(scrollpane);
//		serverList.createCurrNode(rootName);
		this.serverListManger = new ServerListManger(serverList);
	}
	
	private void initTreeDirectory(Container contentContainer) {	
		this.treeDir = new TreeDir("/");
		JScrollPane scrollpane = new JScrollPane(treeDir);
		scrollpane.setBounds(200, 0, 300, 800);
		contentContainer.add(scrollpane);
		zooNodeManager = new ZooNodeManager(treeDir);
	}
	public void setTreeDir() throws Exception {
		if(zkClient == null) {
			return;
		}
		setTreeDir("");
		treeDir.expandAll(new TreePath(treeDir.getNode("/").getInner()), true);
	}
	private void setTreeDir(String path) throws Exception {
		if(!"".equals(path)) {
			treeDir.createNode(path);
		}
		List<String> childrenList = zkClient.getChildren("".equals(path)?"/":path);
		if(childrenList.size() == 0) {
			return;
		}
		for (String childName : childrenList) {
			
			String currPath = path+"/"+childName;
			
			setTreeDir(currPath);
		}
		
		
	}
	private void initContent(Container contentContainer) {
		JTabbedPane tabbedPane = new JTabbedPane();
		
		tabbedPane.setLayout(new FlowLayout());
		tabbedPane.setBounds(500, 0, 1000, 800);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);//设置选项卡标签的布局方式为滚动布局
//		tabbedPane.addChangeListener(new ChangeListener() {//添加时间监听器
//			
//			@Override
//			public void stateChanged(ChangeEvent e) {
//				// TODO Auto-generated method stub
//				
//				int selectedIndex = tabbedPane.getSelectedIndex();//获得被选中选项卡的索引
//				String title = tabbedPane.getTitleAt(selectedIndex);//获得指定索引的选项卡标签
//				System.out.println(title);
//			}
//		});
	
		messageTab = new MessageTab();
		tabbedPane.addTab("动态信息", messageTab );
		serverTab = new ServerTab();
		tabbedPane.addTab("连接管理", serverTab);
		viewTab = new ViewTab(this);
		tabbedPane.addTab("检索数据",viewTab);
		
		JPanel selecJPanel = (JPanel)tabbedPane.getSelectedComponent();
		System.out.println(selecJPanel);
		contentContainer.add(tabbedPane);
	}
	public TreeDir getTreeDir() {
		return treeDir;
	}
	
	public ServerListManger getServerListManger() {
		return serverListManger;
	}
	public ZooNodeManager getZooNodeManager() {
		return zooNodeManager;
	}
	
	
	public JLabel getStatus() {
		return status;
	}
	public ServerTab getServerTab() {
		return serverTab;
	}
	public ViewTab getViewTab() {
		return viewTab;
	}
	public MessageTab getMessageTab() {
		return messageTab;
	}

}
