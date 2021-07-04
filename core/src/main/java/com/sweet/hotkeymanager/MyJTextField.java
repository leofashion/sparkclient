package com.sweet.hotkeymanager;

import java.awt.Color;

import javax.swing.JTextField;

/**
 * 自定义 JTextField
 * 
 * @author liuh 2014-5-6下午5:26:04
 */
public class MyJTextField extends JTextField {
	private static final long serialVersionUID = -3360081395615409280L;

	public MyJTextField() {
		super();
	}

	public MyJTextField(int columns) {
		super(columns);
		setHorizontalAlignment(JTextField.LEFT);
		setBackground(new Color(255, 255, 255));// white
		setEditable(false);
	}

}
