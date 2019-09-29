package com.flynn.zk.tab;

import java.awt.BorderLayout;
import java.util.LinkedList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.flynn.zk.DateTimeUtil;

/**
 * 实时信息展示
 * @author flynn.fan
 *
 */
public class MessageTab extends JPanel {

	private static final long serialVersionUID = -3097642482573482034L;
	private JTextArea message;
	
	private LinkedList<String> infos = new LinkedList<String>();
	public MessageTab() {
		super(new BorderLayout());
		JPanel inJPanel = new JPanel(null);
		this.add(inJPanel,BorderLayout.CENTER);
		
		this.message = new JTextArea();
		//this.message.setRows(60);
	//	this.message.setColumns(100);
		inJPanel.add(this.message);
		this.message.setEditable(false);
		
		this.message.setText("messageTab");
		
		JScrollPane scrollpane = new JScrollPane(message);
		
		scrollpane.setBounds(0, 40, 990, 750);
		
		inJPanel.add(scrollpane);
		
	}
	
	public void output(String info) {
		
		infos.add("["+DateTimeUtil.format(DateTimeUtil.millisTime())+"] "+info);
		if(infos.size()>100) {
			infos.removeFirst();
		}
		StringBuilder builder = new StringBuilder();
		
		for(int i=infos.size()-1;i>=0;i--) {
			builder.append(infos.get(i));
		}
		message.setText(builder.toString());
//		message.setCaretPosition(infos.size());//设置定位到底部
	}
//	public JTextArea getMessage() {
//		return message;
//	}
}
