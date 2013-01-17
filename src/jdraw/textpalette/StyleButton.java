/*
 * StyleButton.java
 *
 * Created on 2008/06/09, 12:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jdraw.textpalette;

import java.awt.event.ActionEvent;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import jdraw.typemenus.JTypeMenuItem;
import jui.JTextIcons;

/**
 *
 * @author takashi
 */
public class StyleButton extends StyleToggle implements JTypeMenuItem{
    public static final int BOLD=1;
    public static final int ITALIC=2;
    public static final int UNDERLINE=3;
    public static final int STRIKETHROUGH=4;
    private int mode;
    /** Creates a new instance of StyleButton */
    public StyleButton() {
        setMode(BOLD);
    }
    public void setMode(int m){
        mode=m;
        if (JTextIcons.BOLD==null)
            new JTextIcons();
        switch (mode){
            case BOLD:
                setIcon(JTextIcons.BOLD);
                break;
            case ITALIC:
                setIcon(JTextIcons.ITALIC);
                break;
            case UNDERLINE:
                setIcon(JTextIcons.UNDERLINE);
                break;
            case STRIKETHROUGH:
                setIcon(JTextIcons.STRIKETHROUGH);
                break;
            default:
                setIcon(JTextIcons.BOLD);
                mode=BOLD;
                break;
        }
    }
    public void setAttributes(AttributeSet attr) {
        switch (mode){
            case BOLD:
                setSelected(StyleConstants.isBold(attr));
                break;
            case ITALIC:
                setSelected(StyleConstants.isItalic(attr));
                break;
            case UNDERLINE:
                setSelected(StyleConstants.isUnderline(attr));
                break;
            case STRIKETHROUGH:
                setSelected(StyleConstants.isStrikeThrough(attr));
                break;
        }
    }
    
    public AttributeSet getAttributes() {
        MutableAttributeSet attr=new SimpleAttributeSet();
        switch (mode){
            case BOLD:
                StyleConstants.setBold(attr,isSelected());
                break;
            case ITALIC:
                StyleConstants.setItalic(attr,isSelected());
                break;
            case UNDERLINE:
                StyleConstants.setUnderline(attr,isSelected());
                break;
            case STRIKETHROUGH:
                StyleConstants.setStrikeThrough(attr,isSelected());
                break;
        }
        return attr;
    }
    
    public void actionPerformed(ActionEvent e) {
    }
    
}
