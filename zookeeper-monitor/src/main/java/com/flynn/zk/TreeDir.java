package com.flynn.zk;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class TreeDir extends JTree{
	private static final long serialVersionUID = -3449818443464590410L;
	private DefaultTreeModel treeModel;
	//目录分隔符
	private String seperator  = "/";
	
	private String rootName;
	
	//节点映射表
	private Map<String, Node> nodeMapper = new ConcurrentHashMap<String, TreeDir.Node>();
	
	public class Node{
		
		private DefaultMutableTreeNode inner;
		private String value;
		private String path;
		private List<Node> subNodes = new ArrayList<TreeDir.Node>();
		private int flag;
		
		public Node(String path) {
			this.path = path;
			String nodeName = path.substring(path.lastIndexOf(seperator)+1);
			inner = new DefaultMutableTreeNode(nodeName);
			inner.setUserObject(path);
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public String getPath() {
			return path;
		}
	
		public List<Node> getSubNodes() {
			return subNodes;
		}
		
		public void addSubNode(Node node) {
			subNodes.add(node);
		}
//		public void setSubNodes(List<Node> subNodes) {
//			this.subNodes = subNodes;
//		}
		public int getFlag() {
			return flag;
		}
		public void setFlag(int flag) {
			this.flag = flag;
		}
		public DefaultMutableTreeNode getInner() {
			return inner;
		}
	}
	
	
	public TreeDir(String rootName,String seperator) {
		super(new DefaultTreeModel(new DefaultMutableTreeNode(rootName)));
		treeModel = (DefaultTreeModel)this.getModel();
		
		this.seperator = seperator;
		this.rootName = rootName;
		//创建根目录
		createNode(rootName);
		
	}
	public TreeDir(String rootName) {
		this(rootName, "/");
	}
	
	public void reset() {
		Node rootNode = nodeMapper.remove(rootName);
//		nodeMapper.clear();
		nodeMapper.keySet().forEach(path->{
			remove(nodeMapper.get(path));
		});
		if(rootNode!=null) {
			nodeMapper.put(rootName, rootNode);
		}
	}
	public Node createNode(String path) {
		
		String[] names = path.split(seperator);
		if(names.length == 0) {
			
			Node node = new Node(rootName);
			node.inner = (DefaultMutableTreeNode) treeModel.getRoot();
			node.inner.setUserObject(path);
			nodeMapper.put(path,node);
			return nodeMapper.get(path);
		}
		String currPath = names[0];
		for(int i=1;i<names.length;i++) {
			currPath += seperator+names[i];
			if(!nodeMapper.containsKey(currPath)) {
				createCurrNode(currPath);
			}
		}
		return nodeMapper.get(path);
	}
	public void remove(Node node) {
		
		DefaultMutableTreeNode selectedNode  = (DefaultMutableTreeNode) node.inner;   
	    if (selectedNode != null && selectedNode.getParent() != null)   
	    {   
	        //删除指定节点   
	        getTreeModel().removeNodeFromParent(selectedNode);   
	    }   
    
		nodeMapper.remove(node.getPath());
	}
	public DefaultTreeModel getTreeModel() {
		return treeModel;
	}
	public Node createCurrNode(String path) {
		Node node = new Node(path);
		nodeMapper.put(path, node);
		Node parent = getParent(node);
		if(parent != null) {
			treeModel.insertNodeInto(node.inner, parent.inner, parent.getSubNodes().size());
		}else {
			DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
			treeModel.insertNodeInto(node.inner,root, root.getChildCount());
		}
		return nodeMapper.get(path);
	}
	public Node getNode(String path) {
		return nodeMapper.get(path);
	}
	public Node getParent(Node node) {
		String path = node.getPath();
		int pos = path.lastIndexOf(seperator);
		if(pos == -1) {
			return null;
		}
		String parentPath = path.substring(0, pos);
//		if("".equals(parentPath)) {
//			parentPath = rootName;
//		}
		return getNode(parentPath);
	}
	
	public void clickItem(Consumer<Node> consumer) {
		TreeDir self = this;
		this.addMouseListener(new MouseAdapter() {
			 public void mouseClicked(MouseEvent e) {
				 DefaultMutableTreeNode selected = (DefaultMutableTreeNode)self.getLastSelectedPathComponent();
				 if(selected == null) {
					 return;
				 }
				 System.out.println(selected.getUserObject());
				 Node selectedNode = getNode(selected.getUserObject().toString());
				 if(selectedNode!=null) {
					 consumer.accept(selectedNode);
				 }
			 }
		});
	}
//	/**
//	* 展开一棵树
//	*
//	* @param tree
//	*/
//	private void expandTree(JTree tree) {
//		TreeNode node = (TreeNode) tree.getModel().getRoot();
//		expandAll(tree, new TreePath(node), true);
//	}

	/**
	* 完全展开一棵树或关闭一棵树
	*
	* @param tree
	* JTree
	* @param parent
	* 父节点
	* @param expand
	* true 表示展开，false 表示关闭
	*/
	@SuppressWarnings("rawtypes")
	public void expandAll(TreePath parent, boolean expand) {
		TreeNode node = (TreeNode) parent.getLastPathComponent();
	
		if (node.getChildCount() > 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandAll(path, expand);
			}
		}
		if (expand) {
			expandPath(parent);
		} else {
			collapsePath(parent);
		}
	}
}
