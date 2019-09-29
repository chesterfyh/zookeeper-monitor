package com.flynn.zk.tab;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.flynn.zk.AppWin;

/**
 * 服务器选择卡
 * @author flynn.fan
 *
 */
public class ServerTab extends JPanel {
	private static final long serialVersionUID = 2585010211027719716L;
	private JTextField address;
	public ServerTab() {
		super(new BorderLayout());
		JPanel inJPanel = new JPanel(null);
		this.add(inJPanel,BorderLayout.CENTER);
		
		
		JLabel jLabel = new JLabel("连接地址");
		jLabel.setBounds(20, 0, 80, 30);
		inJPanel.add(jLabel);
		
		
		address = new JTextField();
		address.setBounds(100, 0, 400, 30);
		inJPanel.add(address);
		
		JButton addButton = new JButton("添加");
		addButton.setBounds(500, 0, 80, 30);
		inJPanel.add(addButton);
		
		JButton updateButton = new JButton("更新");
		updateButton.setBounds(580, 0, 80, 30);
		inJPanel.add(updateButton);
		
		JButton delButton = new JButton("删除");
		delButton.setBounds(660, 0, 80, 30);
		inJPanel.add(delButton);
		
		addButton.addMouseListener(new MouseAdapter() {
			 /**
		     * {@inheritDoc}
		     */
		    public void mouseReleased(MouseEvent e) {
		    	
		    	try {
					AppWin.getInstance().getServerListManger().put(address.getText());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		    }
		});
		updateButton.addMouseListener(new MouseAdapter() {
			 /**
		     * {@inheritDoc}
		     */
		    public void mouseReleased(MouseEvent e) {
		    	
		    	try {
					AppWin.getInstance().getServerListManger().update(address.getText());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		    }
		});
		
		delButton.addMouseListener(new MouseAdapter() {
			 /**
		     * {@inheritDoc}
		     */
		    public void mouseReleased(MouseEvent e) {
		    	
		    	try {
					AppWin.getInstance().getServerListManger().delete(address.getText());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		    }
		});
	}
	public JTextField getAddress() {
		return address;
	}
	
	
}
