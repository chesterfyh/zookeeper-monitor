package com.flynn.zk;

import java.util.Optional;

import javax.swing.tree.TreePath;

import org.apache.zookeeper.CreateMode;

public class ZooNodeManager {

	private TreeDir treeDir;
	
	private String selectNode;
	public ZooNodeManager(TreeDir treeDir) {
		this.treeDir = treeDir;
		
		treeDir.clickItem(selectedNode->{
			selectNode = selectedNode.getPath();
			
			
			AppWin.getInstance().getViewTab().getPath().setText(selectNode);
			try {
				Optional<String> optional = AppWin.getInstance().getZkClient().getNode(selectNode);
				if(optional.isPresent()) {
					AppWin.getInstance().getViewTab().getData().setText(optional.get());
					AppWin.getInstance().getViewTab().showSubNodes();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	public void removeNode(String path) throws Exception {
		AppWin.getInstance().getZkClient().deleteNode(path);
		treeDir.remove(treeDir.getNode(path));
	}
	
	public void setNode(String path,String data,CreateMode createMode) throws Exception {
		String realPath = AppWin.getInstance().getZkClient().setNode(path, data,createMode);
		treeDir.createNode(realPath);
//		setSelectionPath(realPath);
	}
	
	public void setSelectionPath(String path) {
		treeDir.setSelectionPath(new TreePath(treeDir.getNode(path).getInner().getPath()));
	}
}
