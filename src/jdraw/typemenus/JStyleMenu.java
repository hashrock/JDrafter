/*
 * JStyleMenu.java
 *
 * Created on 2008/06/04, 20:54
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jdraw.typemenus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.KeyStroke;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 *
 * @author takashi
 */
public class JStyleMenu extends AbstractTypeMenuItem{
    private JCheckBoxMenuItem italic;
    private JCheckBoxMenuItem bold;
    private JCheckBoxMenuItem underLine;
    private JCheckBoxMenuItem strikeThrough;
    /** Creates a new instance of JStyleMenu */
    public JStyleMenu() {
        setText(java.util.ResourceBundle.getBundle("main").getString("sm_style"));
        setMnemonic(KeyEvent.VK_T);
        //
        italic=new JCheckBoxMenuItem(java.util.ResourceBundle.getBundle("main").getString("sm_italic"));
        italic.setMnemonic(KeyEvent.VK_I);
        italic.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,ActionEvent.CTRL_MASK));
        italic .addActionListener(this);
        add(italic);
        //
        bold=new JCheckBoxMenuItem(java.util.ResourceBundle.getBundle("main").getString("sm_bold"));
        bold.setMnemonic(KeyEvent.VK_B);
        bold.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B,ActionEvent.CTRL_MASK));
        bold .addActionListener(this);
        add(bold);
        //
        underLine=new JCheckBoxMenuItem(java.util.ResourceBundle.getBundle("main").getString("sm_underline"));
        underLine.setMnemonic(KeyEvent.VK_U);
        underLine.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U,ActionEvent.CTRL_MASK));
        underLine .addActionListener(this);
        add(underLine);
        //
        strikeThrough=new JCheckBoxMenuItem(java.util.ResourceBundle.getBundle("main").getString("sm_line_through"));
        strikeThrough.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,ActionEvent.CTRL_MASK));
        strikeThrough.setMnemonic(KeyEvent.VK_T);
        strikeThrough.addActionListener(this);
        add(strikeThrough);
        //
    }
    public void actionPerformed(ActionEvent e) {
        fireActionEvents(java.util.ResourceBundle.getBundle("main").getString("sm_style"));
    }
    
    public void setAttributes(AttributeSet attr) {
        italic.setSelected(StyleConstants.isItalic(attr));
        bold.setSelected(StyleConstants.isBold(attr));
        underLine.setSelected(StyleConstants.isUnderline(attr));
        strikeThrough.setSelected(StyleConstants.isStrikeThrough(attr));
    }
    
    public AttributeSet getAttributes() {
        MutableAttributeSet attr=new SimpleAttributeSet();
        StyleConstants.setItalic(attr,italic.isSelected());
        StyleConstants.setBold(attr,bold.isSelected());
        StyleConstants.setUnderline(attr,underLine.isSelected());
        StyleConstants.setStrikeThrough(attr,strikeThrough.isSelected());
        return attr;
    }
    
}
