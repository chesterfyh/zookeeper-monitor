package com.flynn.zk;

import java.awt.Container;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public abstract class AbstractFrame extends JFrame {

	private Container contentContainer;
	public AbstractFrame(String title) {
		super(title);
		this.contentContainer = getContentPane();
//		this.contentContainer.setLayout(new GridLayout(1, 2));
		this.contentContainer.setLayout(null);
		
		add(this.contentContainer);
//		this.pack();
		this.setSize(1500, 870);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
	public Container getContentContainer() {
		return contentContainer;
	}


	public abstract void add(Container contentContainer) ;
}
