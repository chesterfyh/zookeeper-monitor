package com.flynn.zk.tab;

import java.awt.BorderLayout;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.zookeeper.CreateMode;

import com.flynn.zk.AppWin;

/**
 * 添加数据
 * @author flynn.fan
 *
 */
public class DataAddDialog extends JDialog {
	private static final long serialVersionUID = -8609930907787717828L;
	//处理的节点路径
	private JTextField path;
	//节点中的数据
	private JTextArea data;
	public DataAddDialog(JFrame owner,String path,String data) {
		super(owner, "添加节点");
		
		
		this.setLayout(new BorderLayout());
		this.setBounds(500, 110, 800,650);
	
		this.path = new JTextField(path);
		this.path.setBounds(100, 0, 400, 30);
		
		this.data = new JTextArea(data);
		JPanel inJPanel = new JPanel(null);
		this.add(inJPanel,BorderLayout.CENTER);
		
		
		JLabel jLabel = new JLabel("节点路径");
		jLabel.setBounds(20, 0, 80, 30);
		inJPanel.add(jLabel);
		
		inJPanel.add(this.path);
		
		JButton addButton = new JButton("添加");
		addButton.setBounds(500, 0, 80, 30);
		inJPanel.add(addButton);
		
		JPanel jPanel = new JPanel();
		ButtonGroup buttonGroup = new ButtonGroup();
//		buttonGroup.add(new JRadioButton("永久"));
		
		JRadioButton radioButton1 = new JRadioButton("永久(PERSISTENT)"); 
		JRadioButton radioButton2 = new JRadioButton("永久有序(PERSISTENT_SEQUENTIAL)"); 
		JRadioButton radioButton3 = new JRadioButton("临时(EPHEMERAL)"); 
		JRadioButton radioButton4 = new JRadioButton("临时有序(EPHEMERAL_SEQUENTIAL)"); 
		buttonGroup.add(radioButton1);
		buttonGroup.add(radioButton2);
		buttonGroup.add(radioButton3);
		buttonGroup.add(radioButton4);
		
		Enumeration<AbstractButton> enumeration = buttonGroup.getElements();
		while (enumeration.hasMoreElements()) {
			AbstractButton abstractButton = (AbstractButton) enumeration.nextElement();
			jPanel.add(abstractButton);
		}
		jPanel.setBounds(0, 40, 790, 30);
		
		inJPanel.add(jPanel);
		
		
		JScrollPane scrollpane = new JScrollPane(this.data);
		
		scrollpane.setBounds(0, 80, 790, 500);
		
		inJPanel.add(scrollpane);
		
		addButton.addActionListener(event->{//添加按钮
			
			Enumeration<AbstractButton> radioButtons = buttonGroup.getElements();
			CreateMode createMode = null;
			while (radioButtons.hasMoreElements()) {
				JRadioButton radioButton = (JRadioButton) radioButtons.nextElement();
				
				if(radioButton.isSelected()) {
					if(radioButton == radioButton1) {
						createMode = CreateMode.PERSISTENT;
						break;
					}else if(radioButton == radioButton2) {
						createMode = CreateMode.PERSISTENT_SEQUENTIAL;
						break;
					}else if(radioButton == radioButton3) {
						createMode = CreateMode.EPHEMERAL;
						break;
					}else if(radioButton == radioButton4) {
						createMode = CreateMode.EPHEMERAL_SEQUENTIAL;
						break;
					}
					
					
//					System.out.println("selected: "+radioButton.getText());
//					System.out.println(buttonGroup.getSelection().getSelectedObjects());
				}
			}
			
			if(createMode == null) {
				return;
			}
			try {
				AppWin.getInstance().getZooNodeManager().setNode(this.path.getText().trim(), this.data.getText(),createMode);
			} catch (Exception e) {
				
				
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
	
	
}
