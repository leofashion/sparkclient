/**
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
package org.jivesoftware.spark.ui;

import org.dom4j.DocumentHelper;
import org.jivesoftware.Spark;
import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.ContextMenuListener;
import org.jivesoftware.spark.util.BrowserLauncher;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.plugin.emoticons.EmoticonManager;
import org.jivesoftware.sparkimpl.plugin.filetransfer.transfer.Downloads;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * The ChatArea class handles proper chat text formatting such as url handling. Use ChatArea for proper
 * formatting of bold, italics, underlined and urls.
 */
public class ChatArea extends JTextPane implements MouseListener, MouseMotionListener, ActionListener {


    public final SimpleAttributeSet styles = new SimpleAttributeSet();
    /**
     * The default Hand cursor.
     */
    public static final Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);

    /**
     * The default Text Cursor.
     */
    public static final Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);

	/**
	 * The currently selected Font Family to use.
	 */
	private String fontFamily;

	/**
	 * The currently selected Font Size to use.
	 */
	private int fontSize;

	private List<ContextMenuListener> contextMenuListener = new ArrayList<ContextMenuListener>();

    private JPopupMenu popup;

    private JMenuItem cutMenu;
    private JMenuItem copyMenu;
    private JMenuItem pasteMenu;
    private JMenuItem selectAll;

    private List<LinkInterceptor> interceptors = new ArrayList<>();

    protected EmoticonManager emoticonManager;

    protected Boolean forceEmoticons = false;
    
    protected Boolean emoticonsAvailable = true;

    /**
     * ChatArea Constructor.
     */
    public ChatArea() {
        emoticonManager = EmoticonManager.getInstance();
        
        Collection<String> emoticonPacks;
        emoticonPacks = emoticonManager.getEmoticonPacks();
        
        if(emoticonPacks == null) {
        	emoticonsAvailable = false;
        }
		// Set Default Font
		final LocalPreferences pref = SettingsManager.getLocalPreferences();
		int fs = pref.getChatRoomFontSize();
		fontSize = fs;
		setFontSize(fs);

        cutMenu = new JMenuItem(Res.getString("action.cut"));
        cutMenu.addActionListener(this);

        copyMenu = new JMenuItem(Res.getString("action.copy"));
        copyMenu.addActionListener(this);

        pasteMenu = new JMenuItem(Res.getString("action.paste"));
        pasteMenu.addActionListener(this);

        selectAll = new JMenuItem(Res.getString("action.select.all"));
        selectAll.addActionListener(this);

		// Set Default Font
		setFont(new Font("宋体", Font.PLAIN, 12));


        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("Ctrl x"), "cut");

        getActionMap().put("cut", new AbstractAction("cut") {
			private static final long serialVersionUID = 9117190151545566922L;

			@Override
			public void actionPerformed(ActionEvent evt) {
                cutAction();
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("Ctrl c"), "copy");

        getActionMap().put("copy", new AbstractAction("copy") {
			private static final long serialVersionUID = 4949716854440264528L;

			@Override
			public void actionPerformed(ActionEvent evt) {
                SparkManager.setClipboard(getSelectedText());
            }
        });

        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("Ctrl v"), "paste");

        getActionMap().put("paste", new AbstractAction("paste") {
			private static final long serialVersionUID = -8767763580660683678L;

			@Override
			public void actionPerformed(ActionEvent evt) {
                pasteAction();
            }
        });

        setEditorKit( new WrapEditorKit() ); // SPARK-1613 Ensure that long text wraps.
    }
	/**
	 * Set the current text of the ChatArea.
	 *
	 * @param message inserts the text directly into the ChatArea
	 */
	public void setText(String message, String loginId) {
		// By default, use the hand cursor for link selection
		// and scrolling.
		// setCursor(HAND_CURSOR);

		// Make sure the message is not null.
		// message = message.trim();
		// Why?
		// message = message.replaceAll("/\"", "");
		if (ModelUtil.hasLength(message)) {
			try {
				insert(message, loginId);
			} catch (BadLocationException e) {
				Log.error(e);
			}
		}
	}

    /**
     * setText is a core JTextPane method that can beused to inject a different Document type
     * for instance HTMLDocument (setText("<HTML></HTML>")
     * We should keep the functionality - it is useful when we want to inject a different Document type
     * instead of StyleDocument 
     * @param content
     */
    public void setInitialContent(String content) {
        super.setText(content);
    } 
    
   
    /**
     * Removes the last appearance of word from the TextArea
     * @param word
     */
    public void removeLastWord(String word)
    {
	select(getText().lastIndexOf(word),getText().length());	
	replaceSelection("");
    }
    
    /**
     * Removes everything in between <b>begin</b> and <b>end</b>
     * @param begin
     * @param end
     */
    public void removeWordInBetween(int begin, int end){
	select(begin, end);
	replaceSelection("");
    }

    /**
     * Clear the current document. This will remove all text and element
     * attributes such as bold, italics, and underlining. Note that the font family  and
     * font size will be persisted.
     */
    public void clear() {
        super.setText("");
		if (fontFamily != null) {
			setFont(fontFamily);
		}

		if (fontSize != 0) {
			setFontSize(fontSize);
		}

		StyleConstants.setUnderline(styles, false);
		StyleConstants.setBold(styles, false);
		StyleConstants.setItalic(styles, false);
		setCharacterAttributes(styles, false);
    }

    /**
     * Does the actual insertion of text, adhering to the styles specified during message creation in either the thin or thick client.
     *
     * @param text - the text to insert.
     * @throws BadLocationException if location is not available to insert into.
     */
    public void insert(String text, String loginId) throws BadLocationException {
        boolean bold = false;
        boolean italic = false;
        boolean underlined = false;

        final StringTokenizer tokenizer = new StringTokenizer(text, "\n\t", true);
        while (tokenizer.hasMoreTokens()) {
            String textFound = tokenizer.nextToken();
            if (textFound != null) {
                // 格式化字体信息截取
                if (textFound.indexOf("<fontFormate ") != -1) {
                    int sp1 = textFound.indexOf("<imsg ");
                    int sp2 = 0;
                    if (sp1 >= 0) {
                        sp2 = textFound.indexOf("</imsg>", sp1);
                    }
                    // 附件
                    int sp3 = textFound.indexOf("_FILE_");

                    // 自动发送文件的消息截取
                    int ofsp1 = textFound.indexOf("<offlinefile ");
                    int ofsp2 = 0;
                    if (ofsp1 >= 0) {
                        ofsp2 = textFound.indexOf("</offlinefile>", ofsp1);
                    }

                    if (ofsp1 >= 0 && ofsp2 > 0) {//
                        textFound = textFound.substring(ofsp1, ofsp2 + 14);
                        insertOffLineFileLink(textFound, loginId);
                        continue;
                    }

                    // 手动发送文件的消息截取
                    int cansp1 = textFound.indexOf("<cancelFile ");
                    int cansp2 = 0;
                    if (cansp1 >= 0) {
                        cansp2 = textFound.indexOf("</cancelFile>", cansp1);
                    }
                    if (cansp1 >= 0 && ofsp2 >= 0) {
                        textFound = textFound.substring(cansp1, cansp2 + 13);
                        insertCancelFileMsg(textFound);
                        continue;
                    }
                    if (sp1 >= 0 && sp2 > 0) {
                        insertOALink(textFound.substring(sp1, sp2 + 7));
                    } else if (sp3 != -1) {
                        int sp4 = textFound.lastIndexOf("</fontFormate>");
                        String strFile = textFound.substring(sp3 + 7, sp4 - 1);
                        insertAddress(strFile);
                    } else {
                        while (textFound.indexOf("</fontFormate>") == -1) {
                            textFound += tokenizer.nextToken();
                        }
                        insertFontFormateMsg(textFound);
                    }
                    continue;
                } else {
                    int sp1 = textFound.indexOf("<imsg ");
                    int sp2 = 0;
                    if (sp1 >= 0) {
                        sp2 = textFound.indexOf("</imsg>", sp1);
                    }
                    if (sp1 >= 0 && sp2 > 0) {
                        if (sp1 > 0) {
                            insertText(textFound.substring(0, sp1));
                        }
                        insertOALink(textFound.substring(sp1, sp2 + 7));
                        continue;
                    }
                }
                // 自动发送文件的消息截取
                int ofsp1 = textFound.indexOf("<offlinefile ");
                int ofsp2 = 0;
                if (ofsp1 >= 0) {
                    ofsp2 = textFound.indexOf("</offlinefile>", ofsp1);
                }

                if (ofsp1 >= 0 && ofsp2 > 0) {
                    textFound = textFound.substring(ofsp1, ofsp2 + 14);
                    insertOffLineFileLink(textFound, loginId);
                    continue;
                }

                // 手动发送文件的消息截取
                int cansp1 = textFound.indexOf("<cancelFile ");
                int cansp2 = 0;
                if (cansp1 >= 0) {
                    cansp2 = textFound.indexOf("</cancelFile>", cansp1);
                }
                if (cansp1 >= 0 && ofsp2 >= 0) {
                    textFound = textFound.substring(cansp1, cansp2 + 13);
                    insertCancelFileMsg(textFound);
                    continue;
                }

            }

            if ((textFound.startsWith("http://") || textFound.startsWith("ftp://")
                || textFound.startsWith("https://") || textFound.startsWith("www."))
                && textFound.indexOf(".") > 1) {
                insertLink(textFound);
            } else if (textFound.startsWith("\\\\")
                || ((textFound.indexOf(":/") > 0 || textFound.indexOf(":\\") > 0)
                && textFound.indexOf(".") > 1)) {
                insertAddress(textFound);
            } else if (!insertImage(textFound)) {
                insertText(textFound);
            }
        }

        // By default, always have decorations off.
        StyleConstants.setBold(styles, bold);
        StyleConstants.setItalic(styles, italic);
        StyleConstants.setUnderline(styles, underlined);
    }

    private void insertOffLineFileLink(String textFound, String loginId) {
        org.dom4j.Document xmldoc;
        try {
            xmldoc = DocumentHelper.parseText(textFound);
            org.dom4j.Element root = xmldoc.getRootElement();
            String fname = root.attributeValue("fname");
            File file = new File(Downloads.getDownloadDirectory(loginId) + "\\"
                + fname.substring(fname.lastIndexOf("-") + 1));
            insertText(Res.getString("message.offlineFile.tip"));
            insertAddress(file.getPath());
            insertText("\n");

            final Document doc = getDocument();
            styles.addAttribute("link", Downloads.getDownloadDirectory() + "");
            StyleConstants.setForeground(styles, (Color) UIManager.get("Link.foreground"));
            StyleConstants.setUnderline(styles, true);
            // doc.insertString(doc.getLength(), Res.getString("open.folder"), styles);
            StyleConstants.setUnderline(styles, false);
            StyleConstants.setForeground(styles, (Color) UIManager.get("TextPane.foreground"));
            styles.removeAttribute("link");
            setCharacterAttributes(styles, false);
            setCaretPosition(doc.getLength());

        } catch (Exception e) {
            Log.error(e.getMessage());
            Log.error("fileByte:" + textFound);
        }
    }

    private void insertCancelFileMsg(String textFound) {
        org.dom4j.Document xmldoc;
        try {
            xmldoc = DocumentHelper.parseText(textFound);
            org.dom4j.Element root = xmldoc.getRootElement();
            String fname = root.attributeValue("fileName");
            String type = root.attributeValue("type");
            String text = "";
            if (type.equals("cancel")) {// 取消
                text = Res.getString("cancelTrasfer.tip") + "\"" + fname + "\"";
            } else {// 转离线发送
                text = Res.getString("cancelTransfer.off.tip") + "\"" + fname + "\"";
            }
            insertText(text);
            final Document doc = getDocument();
            setCaretPosition(doc.getLength());
        } catch (Exception e) {
            Log.error(e.getMessage());
        }
    }

    private void insertOALink(String text) {
        org.dom4j.Document xmldoc;
        try {
            xmldoc = DocumentHelper.parseText(text);
            org.dom4j.Element root = xmldoc.getRootElement();
            String type = root.attributeValue("type");
            String linkid = root.attributeValue("linkid");
            String msg = root.getTextTrim();

            StringBuffer sb = new StringBuffer();
            sb.append(Spark.getOAServerURL());
            sb.append("&type=" + type);
            sb.append("&linkid=" + linkid);
            String link = sb.toString();

            final Document doc = getDocument();
            styles.removeAttribute("link");
            doc.insertString(doc.getLength(), msg + " ", styles);
            setCaretPosition(doc.getLength());

            styles.addAttribute("link", link);
            StyleConstants.setForeground(styles, (Color) UIManager.get("Link.foreground"));
            StyleConstants.setUnderline(styles, true);
            doc.insertString(doc.getLength(), "进入查看", styles);
            StyleConstants.setUnderline(styles, false);
            StyleConstants.setForeground(styles, (Color) UIManager.get("TextPane.foreground"));
            styles.removeAttribute("link");
            setCharacterAttributes(styles, false);
            setCaretPosition(doc.getLength());

        } catch (Exception e) {
            Log.error(text);
            Log.error("插入链接时:", e);
        }
    }

    /**
     * 字体、大小、颜色 name='宋体' size='12' color=''
     *
     * @param textFound
     */
    private void insertFontFormateMsg(String textFound) {
        org.dom4j.Document xmldoc;
        try {
            String tmpxml = textFound.substring(0, textFound.indexOf(">") + 1);
            xmldoc = DocumentHelper.parseText(tmpxml + "</fontFormate>");
            org.dom4j.Element root = xmldoc.getRootElement();
            String name = root.attributeValue("name");
            int size = Integer.valueOf(root.attributeValue("size"));
            String color = root.attributeValue("color");
            if (color == null || color.equals("")) {
                color = "0;0;0";
            }
            String[] colorArr = color.split(";");

            String str = textFound.substring(textFound.indexOf(">") + 1,
                textFound.indexOf("</fontFormate>"));

            if ((str.startsWith("http://") || str.startsWith("ftp://") || str.startsWith("https://")
                || str.startsWith("www.")) && str.indexOf(".") > 1) {
                insertLink(str);
            } else if (str.startsWith("\\\\") || ((str.indexOf(":/") > 0 || str.indexOf(":\\") > 0)
                && str.indexOf(".") > 1)) {
                insertAddress(str);
            } else {
                String imageKey = str;
                while (imageKey.indexOf("{") != -1) {
                    int sp1 = imageKey.indexOf("{");
                    int sp2 = imageKey.indexOf("}", sp1);
                    if (sp1 > 0) {
                        String strText = imageKey.substring(0, sp1);
                        final Document doc = getDocument();
                        StyleConstants.setFontSize(styles, size);
                        StyleConstants.setFontFamily(styles, name);
                        StyleConstants.setForeground(styles, new Color(Integer.valueOf(colorArr[0]),
                            Integer.valueOf(colorArr[1]), Integer.valueOf(colorArr[2])));
                        doc.insertString(doc.getLength(), strText, styles);
                    }
                    if (sp2 != -1) {
                        String image = imageKey.substring(sp1, sp2 + 1);
                        insertImage(image);
                        imageKey = imageKey.substring(sp2 + 1);
                    } else {
                        final Document doc = getDocument();
                        StyleConstants.setFontSize(styles, size);
                        StyleConstants.setFontFamily(styles, name);
                        StyleConstants.setForeground(styles, new Color(Integer.valueOf(colorArr[0]),
                            Integer.valueOf(colorArr[1]), Integer.valueOf(colorArr[2])));
                        doc.insertString(doc.getLength(), imageKey, styles);
                        break;
                    }
                }
                if (imageKey.length() > 0) {
                    final Document doc = getDocument();
                    StyleConstants.setFontSize(styles, size);
                    StyleConstants.setFontFamily(styles, name);
                    StyleConstants.setForeground(styles, new Color(Integer.valueOf(colorArr[0]),
                        Integer.valueOf(colorArr[1]), Integer.valueOf(colorArr[2])));
                    doc.insertString(doc.getLength(), imageKey, styles);
                }
            }
        } catch (Exception e) {
            Log.error(e.getMessage());
        }
    }

    /**
     * Inserts text into the current document.
     *
     * @param text the text to insert
     * @throws BadLocationException if the location is not available for insertion.
     */
    public void insertText(String text) throws BadLocationException {
        final Document doc = getDocument();
        styles.removeAttribute("link");
        doc.insertString(doc.getLength(), text, styles);
        setCaretPosition(doc.getLength());
    }

    /**
     * Inserts text into the current document.
     *
     * @param text the text to insert
     * @param color the color of the text
     * @throws BadLocationException if the location is not available for insertion.
     */
    public void insertText(String text, Color color) throws BadLocationException {
        final Document doc = getDocument();
        StyleConstants.setForeground(styles, color);
        doc.insertString(doc.getLength(), text, styles);
        setCaretPosition(doc.getLength());
    }

    /**
     * Inserts a link into the current document.
     *
     * @param link - the link to insert( ex. http://www.javasoft.com )
     * @throws BadLocationException if the location is not available for insertion.
     */
    public void insertLink(String link) throws BadLocationException {
        final Document doc = getDocument();
        styles.addAttribute("link", link);

        StyleConstants.setForeground(styles, (Color) UIManager.get("Link.foreground"));
        StyleConstants.setUnderline(styles, true);
        doc.insertString(doc.getLength(), link, styles);
        StyleConstants.setUnderline(styles, false);
        StyleConstants.setForeground(styles, (Color) UIManager.get("TextPane.foreground"));
        styles.removeAttribute("link");
        setCharacterAttributes(styles, false);
        setCaretPosition(doc.getLength());

    }

    /**
     * Inserts a network address into the current document.
     *
     * @param address - the address to insert( ex. \superpc\etc\file\ OR http://localhost/ )
     * @throws BadLocationException if the location is not available for insertion.
     */
    public void insertAddress(String address) throws BadLocationException {
        final Document doc = getDocument();
        styles.addAttribute("link", address);

        StyleConstants.setForeground(styles, (Color) UIManager.get("Address.foreground"));
        StyleConstants.setUnderline(styles, true);
        doc.insertString(doc.getLength(), address, styles);
        StyleConstants.setUnderline(styles, false);
        StyleConstants.setForeground(styles, (Color) UIManager.get("TextPane.foreground"));
        styles.removeAttribute("link");
        setCharacterAttributes(styles, false);
        setCaretPosition(doc.getLength());

    }

    /**
     * Inserts an emotion icon into the current document.
     *
     * @param imageKey - the smiley representation of the image.( ex. :) )
     * @return true if the image was found, otherwise false.
     * @throws BadLocationException
     */
    public boolean insertImage(String imageKey) throws BadLocationException {
        boolean bol = false;
        while (imageKey.indexOf("{") != -1) {
            int sp1 = imageKey.indexOf("{");
            int sp2 = imageKey.indexOf("}", sp1);
            if (sp1 > 0) {
                String str = imageKey.substring(0, sp1);
                insertText(str);
            }
            if (sp2 != -1) {
                String image = imageKey.substring(sp1 + 1, sp2);
                if (!insertImages(image)) {
                    insertText(image);
                } else {
                    bol = true;
                }
                imageKey = imageKey.substring(sp2 + 1);
                if (imageKey != null && imageKey.length() > 0 && imageKey.indexOf("{") == -1) {
                    insertText(imageKey);
                }
            } else {
                insertText(imageKey);
                break;
            }
        }
        return bol;
    }

    public boolean insertImages(String imageKeys) {

        if (!forceEmoticons && !SettingsManager.getLocalPreferences().areEmoticonsEnabled()
            || !emoticonsAvailable) {
            return false;
        }
        final Document doc = getDocument();
        Icon emotion = emoticonManager.getEmoticonImage(imageKeys.toLowerCase());
        if (emotion == null) {
            return false;
        }

        select(doc.getLength(), doc.getLength());
        insertIcon(emotion);
        setCaretPosition(doc.getLength());

        return true;

    }

    /**
     * Inserts horizontal line
     */
    public void insertHorizontalLine() {
        try {
            insertComponent(new JSeparator());
            insertText("\n");
        } catch (BadLocationException e) {
            Log.error("Error message.", e);
        }
    }

    /**
     * Sets the current element to be either bold or not depending on the current state. If the element is currently set as bold, it will be set to false, and
     * vice-versa.
     */
    public void setBold() {
        final Element element = getStyledDocument().getCharacterElement(getCaretPosition() - 1);
        if (element != null) {
            AttributeSet as = element.getAttributes();
            boolean isBold = StyleConstants.isBold(as);
            StyleConstants.setBold(styles, !isBold);
            try {
                setCharacterAttributes(styles, true);
            } catch (Exception ex) {
                Log.error("Error settings bold:", ex);
            }
        }
    }

    /**
     * Sets the current element to be either italicized or not depending on the current state. If the element is currently set as italic, it will be set to
     * false, and vice-versa.
     */
    public void setItalics() {
        final Element element = getStyledDocument().getCharacterElement(getCaretPosition() - 1);
        if (element != null) {
            AttributeSet as = element.getAttributes();
            boolean isItalic = StyleConstants.isItalic(as);
            StyleConstants.setItalic(styles, !isItalic);
            try {
                setCharacterAttributes(styles, true);
            } catch (Exception fontException) {
                Log.error("Error settings italics:", fontException);
            }
        }
    }

    /**
     * Sets the current document to be either underlined or not depending on the current state. If the element is currently set as underlined, it will be set to
     * false, and vice-versa.
     */
    public void setUnderlined() {
        final Element element = getStyledDocument().getCharacterElement(getCaretPosition() - 1);
        if (element != null) {
            AttributeSet as = element.getAttributes();
            boolean isUnderlined = StyleConstants.isUnderline(as);
            StyleConstants.setUnderline(styles, !isUnderlined);
            try {
                setCharacterAttributes(styles, true);
            } catch (Exception underlineException) {
                Log.error("Error settings underline:", underlineException);
            }
        }
    }

    /**
     * Set the font on the current element.
     *
     * @param font the font to use with the current element
     */
    public void setFont(String font) {
        StyleConstants.setFontFamily(styles, font);
        try {
            setCharacterAttributes(styles, false);
        } catch (Exception fontException) {
            Log.error("Error settings font:", fontException);
        }

        fontFamily = font;
    }

    /**
     * Set the current font size.
     *
     * @param size the current font size.
     */
    public void setFontSize(int size) {
        StyleConstants.setFontSize(styles, size);
        try {
            setCharacterAttributes(styles, false);
        } catch (Exception fontException) {
            Log.error("Error settings font:", fontException);
        }

        fontSize = size;
    }

    @Override
	public void mouseClicked(MouseEvent e) {
        try {
            final int pos = viewToModel(e.getPoint());
            final Element element = getStyledDocument().getCharacterElement(pos);

            if (element != null) {
                final AttributeSet as = element.getAttributes();
                final Object o = as.getAttribute("link");

                if (o != null) {
                    try {
                        final String url = (String)o;
                        boolean handled = fireLinkInterceptors(e, url);
                        if (!handled) {
                            if(e.getButton() == MouseEvent.BUTTON1)
                        	BrowserLauncher.openURL(url);
			    else if (e.getButton() == MouseEvent.BUTTON3) {
				JPopupMenu popupmenu = new JPopupMenu();
				JMenuItem linkcopy = new JMenuItem(
					Res.getString("action.copy"));
				linkcopy.addActionListener( e1 -> SparkManager.setClipboard(url) );
				linkcopy.setEnabled(true);
				popupmenu.add(linkcopy);
				popupmenu.show(this, e.getX(), e.getY());
			    }
                        }
                    }
                    catch (Exception ioe) {
                        Log.error("Error launching browser:", ioe);
                    }
                }
            }
        }
        catch (Exception ex) {
            Log.error("Visible Error", ex);
        }
    }

    @Override
	public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
            handlePopup(e);
        }
    }

    /**
     * This launches the <code>BrowserLauncher</code> with the URL
     * located in <code>ChatArea</code>. Note that the url will
     * automatically be clickable when added to <code>ChatArea</code>
     *
     * @param e - the MouseReleased event
     */
    @Override
	public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            handlePopup(e);
        }


    }

    @Override
	public void mouseEntered(MouseEvent e) {
    }

    @Override
	public void mouseExited(MouseEvent e) {
    }

    @Override
	public void mouseDragged(MouseEvent e) {
    }

    /**
     * Checks to see if the mouse is located over a browseable
     * link.
     *
     * @param e - the current MouseEvent.
     */
    @Override
	public void mouseMoved(MouseEvent e) {
        checkForLink(e);
    }

    /**
     * Checks to see if the mouse is located over a browseable
     * link.
     *
     * @param e - the current MouseEvent.
     */
    private void checkForLink(MouseEvent e) {
        try {
            final int pos = viewToModel(e.getPoint());
            final Element element = getStyledDocument().getCharacterElement(pos);

            if (element != null) {
                final AttributeSet as = element.getAttributes();
                final Object o = as.getAttribute("link");

                if (o != null) {
                    setCursor(HAND_CURSOR);
                }
                else {
                    setCursor(DEFAULT_CURSOR);
                }
            }
        }
        catch (Exception ex) {
            Log.error("Error in CheckLink:", ex);
        }
    }

    /**
     * Examines the chatInput text pane, and returns a string containing the text with any markup (jive markup in our case). This will strip any terminating new
     * line from the input.
     *
     * @return a string of marked up text.
     */
    public String getMarkup() {
        final StringBuffer buf = new StringBuffer();
        final String text = getText();
        final StyledDocument doc = getStyledDocument();
        final Element rootElem = doc.getDefaultRootElement();

        // MAY RETURN THIS BLOCK
        if (text.trim().length() <= 0) {
            return null;
        }

        boolean endsInNewline = text.charAt(text.length() - 1) == '\n';
        for (int j = 0; j < rootElem.getElementCount(); j++) {
            final Element pElem = rootElem.getElement(j);

            for (int i = 0; i < pElem.getElementCount(); i++) {
                final Element e = pElem.getElement(i);
                final AttributeSet as = e.getAttributes();
                final boolean bold = StyleConstants.isBold(as);
                final boolean italic = StyleConstants.isItalic(as);
                final boolean underline = StyleConstants.isUnderline(as);
                int end = e.getEndOffset();

                if (end > text.length()) {
                    end = text.length();
                }

                if (endsInNewline && end >= text.length() - 1) {
                    end--;
                }

                // swing text.. :-/
                if (j == rootElem.getElementCount() - 1 && i == pElem.getElementCount() - 1) {
                    end = text.length();
                }

                final String current = text.substring(e.getStartOffset(), end);
                if (bold) {
                    buf.append("[b]");
                }
                if (italic) {
                    buf.append("[i]");
                }
                if (underline) {
                    buf.append("[u]");
                }
                // buf.append( "[font face=/\"" + fontFamily + "/\" size=/\"" + fontSize + "/\"/]" );

                // Iterator over current string to find url tokens
                final StringTokenizer tkn = new StringTokenizer(current, " ", true);
                while (tkn.hasMoreTokens()) {
                    final String token = tkn.nextToken();
                    if (token.startsWith("http://") || token.startsWith("ftp://")
                        || token.startsWith("https://")) {
                        buf.append("[url]").append(token).append("[/url]");
                    } else if (token.startsWith("www")) {
                        buf.append("[url ");
                        buf.append("http://").append(token);
                        buf.append("]");
                        buf.append(token);
                        buf.append("[/url]");
                    } else {
                        buf.append(token);
                    }
                }

                // Always add end tags for markup
                if (underline) {
                    buf.append("[/u]");
                }
                if (italic) {
                    buf.append("[/i]");
                }
                if (bold) {
                    buf.append("[/b]");
                }
                // buf.append( "[/font]" );
            }
        }

        return buf.toString();
    }

    private void handlePopup(MouseEvent e) {
        popup = new JPopupMenu();
        popup.add(cutMenu);
        popup.add(copyMenu);
        popup.add(pasteMenu);
        fireContextMenuListeners();
        popup.addSeparator();
        popup.add(selectAll);

        // Handle enable
        boolean textSelected = ModelUtil.hasLength(getSelectedText());
        String clipboard = SparkManager.getClipboard();
        cutMenu.setEnabled(textSelected && isEditable());
        copyMenu.setEnabled(textSelected);
        pasteMenu.setEnabled(ModelUtil.hasLength(clipboard) && isEditable());

        popup.show(this, e.getX(), e.getY());
    }

    /**
     * Adds a <code>ContextMenuListener</code> to ChatArea.
     *
     * @param listener the ContextMenuListener.
     */
    public void addContextMenuListener(ContextMenuListener listener) {
        contextMenuListener.add(listener);
    }

    /**
     * Remove a <code>ContextMenuListener</code> to ChatArea.
     *
     * @param listener the ContextMenuListener.
     */
    public void removeContextMenuListener(ContextMenuListener listener) {
        contextMenuListener.remove(listener);
    }

    private void fireContextMenuListeners()
    {
        for ( final ContextMenuListener listener : contextMenuListener )
        {
            try
            {
                listener.poppingUp( this, popup );
            }
            catch ( Exception e )
            {
                Log.error( "A ContextMenuListener (" + listener + ") threw an exception while processing a 'poppingUp' event. ChatArea: '" + this + "', popup: '" + popup + "'.", e );
            }
        }
    }

    public void addLinkInterceptor(LinkInterceptor interceptor) {
        interceptors.add(interceptor);
    }

    public void removeLinkInterceptor(LinkInterceptor interceptor) {
        interceptors.remove(interceptor);
    }

    public boolean fireLinkInterceptors( MouseEvent event, String link )
    {
        for ( final LinkInterceptor interceptor : interceptors )
        {
            try
            {
                final boolean handled = interceptor.handleLink( event, link );
                if ( handled )
                {
                    return true;
                }
            }
            catch ( Exception e )
            {
                Log.error( "A LinkInterceptor (" + interceptor + ") threw an exception while processing link: '" + link + "', event: '" + event + "'.", e );
            }
        }

        return false;
    }


    @Override
	public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cutMenu) {
            cutAction();
        }
        else if (e.getSource() == copyMenu) {
            SparkManager.setClipboard(getSelectedText());
        }
        else if (e.getSource() == pasteMenu) {
            pasteAction();
        }
        else if (e.getSource() == selectAll) {
            requestFocus();
            selectAll();
        }
    }

    private void cutAction() {
        String selectedText = getSelectedText();
        replaceSelection("");
        SparkManager.setClipboard(selectedText);
    }

    private void pasteAction() {
        String text = SparkManager.getClipboard();
        if (text != null) {
            replaceSelection(text);
        }
    }

    protected void releaseResources() {
        getActionMap().remove("copy");
        getActionMap().remove("cut");
        getActionMap().remove("paste");
    }

    public Boolean getForceEmoticons() {
        return forceEmoticons;
    }

    public void setForceEmoticons(Boolean forceEmoticons) {
        this.forceEmoticons = forceEmoticons;
    }
}
