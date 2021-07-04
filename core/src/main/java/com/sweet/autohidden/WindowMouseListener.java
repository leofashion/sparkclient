package com.sweet.autohidden;

import java.awt.Container;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.jivesoftware.MainWindow;

public class WindowMouseListener extends MouseAdapter
		implements ActionListener, WindowFocusListener {

	private Timer timer;
	private MainWindow frame;
	Container container;

	public WindowMouseListener(MainWindow win) {
		frame = win;
		container = frame.getContentPane();
		container.addMouseListener(this);
		// 注册鼠标侦听器到ContentPane上,因为我们可以加大它的Insets以提高鼠标进入和离开的灵敏度
		frame.addWindowFocusListener(this);
		// 注册一个焦点侦听器到窗体上
		timer = new Timer(2000, this);
		timer.setRepeats(false);
	}

	public void mouseEntered(MouseEvent e) {
		// 当鼠标进入,就显示窗体
		if (frame.getStates() == MainWindow.HIDDEN) {
			frame.moveToVisible();
		}
	}

	public void mouseExited(MouseEvent e) {
		// 当鼠标离开,启动计时器
		if (frame.getStates() == MainWindow.CANHIDDEN) {
			if (!container.contains(e.getPoint())) {
				timer.restart();
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		// 计时器到期,检查鼠标是不是还在此窗体里面,不再的话,再开始隐藏
		Point p = MouseInfo.getPointerInfo().getLocation();
		SwingUtilities.convertPointFromScreen(p, container);
		if (!container.contains(p) && frame.getStates() == MainWindow.CANHIDDEN) {
			frame.moveToHidden();
		}
	}

	public void windowGainedFocus(WindowEvent e) {
		// 得到焦点检查鼠标是不是在窗体上
		Point p = MouseInfo.getPointerInfo().getLocation();
		SwingUtilities.convertPointFromScreen(p, container);
		if (container.contains(p) && frame.getStates() == MainWindow.HIDDEN) {
			frame.moveToVisible();
		}
	}

	public void windowLostFocus(WindowEvent e) {
		// 失去焦点,启动计时器
		if (frame.getStates() == MainWindow.CANHIDDEN) {
			timer.restart();
		}
	}
}
