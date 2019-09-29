package com.flynn.zk.tab;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.flynn.zk.AppWin;

/**
 * 数据处理
 * @author flynn.fan
 *
 */
public class ViewTab extends JPanel {
	private static final long serialVersionUID = -8165328454946114831L;
	private JFrame owner;
	//处理的节点路径
	private JTextField path;
	//节点中的数据
	private JTextArea data;
	//用来显示检索的子节点路径
	private JTextArea subNodes;
	public ViewTab(JFrame owner) {
		super(new BorderLayout());
		this.owner = owner;
		JPanel inJPanel = new JPanel(null);
		this.add(inJPanel,BorderLayout.CENTER);
		
		
		JLabel jLabel = new JLabel("节点路径");
		jLabel.setBounds(20, 0, 80, 30);
		inJPanel.add(jLabel);
		
		
		path = new JTextField();
		path.setBounds(100, 0, 400, 30);
		inJPanel.add(path);
		
		JButton addButton = new JButton("添加");
		addButton.setBounds(500, 0, 80, 30);
		inJPanel.add(addButton);
		
		JButton updateButton = new JButton("更新");
		updateButton.setBounds(580, 0, 80, 30);
		inJPanel.add(updateButton);
		
		JButton delButton = new JButton("删除");
		delButton.setBounds(660, 0, 80, 30);
		inJPanel.add(delButton);
		
		
		data = new JTextArea();
//		data.setBounds(0, 40, 1000, 600);
		
		JScrollPane scrollpane = new JScrollPane(data);
		scrollpane.setBounds(0, 40, 980, 600);
		inJPanel.add(scrollpane);
		
		
		subNodes = new JTextArea();
		subNodes.setEditable(false);
		subNodes.setLineWrap(true);
		subNodes.setWrapStyleWord(true);   
		JScrollPane scrollpane2 = new JScrollPane(subNodes);
		scrollpane2.setBounds(0, 640, 980, 130);
		inJPanel.add(scrollpane2);
		
		
		path.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {//按了回车
					System.out.println("Enter ....");
					try {
						Optional<String> optional = AppWin.getInstance().getZkClient().getNode(path.getText().trim());
						if(optional.isPresent()) {
							data.setText(optional.get());
							showSubNodes();
							AppWin.getInstance().getZooNodeManager().setSelectionPath(path.getText().trim());
//							AppWin.getInstance().getZooNodeManager().get.setSelectionPath(new TreePath(node.getInner().getPath()));
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				
			}
		});
		
		addButton.addActionListener(event->{//添加按钮
			new DataAddDialog(this.owner,path.getText(), data.getText()).setVisible(true);
			System.out.println("clicked ......");
		});
		
		updateButton.addActionListener(event->{//更新按钮
			String path = this.path.getText().trim();
			String data = this.data.getText();
			try {
				AppWin.getInstance().getZkClient().setNode(path, data);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		
		delButton.addActionListener(event->{//删除按钮
			String path = this.path.getText().trim();
			try {
				AppWin.getInstance().getZooNodeManager().removeNode(path);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
	}
	
	public void showSubNodes() throws Exception {
		
		List<String> subPaths = AppWin.getInstance().getZkClient().getChildren(path.getText().trim());
		if(subPaths.size()>0) {
			subNodes.setText("子节点：\n"+subPaths.toString());
		}else {
			subNodes.setText("子节点：\n[]");
		}
	}
	public JTextField getPath() {
		return path;
	}
	public JTextArea getData() {
		return data;
	}
}
