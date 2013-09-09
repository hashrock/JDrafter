/*
 * ParagrahOptionMenu.java
 *
 * Created on 2008/06/08, 10:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jdraw.typemenus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JMenuItem;
import javax.swing.text.AttributeSet;

/**
 *
 * @author takashi
 */
public class ParagraphOptionMenu extends JMenuItem implements JTypeMenuItem{
    AttributeSet attr;
    /** Creates a new instance of ParagrahOptionMenu */
    public ParagraphOptionMenu() {
        setText("段落オプション");
        setMnemonic(KeyEvent.VK_O);
        this.addActionListener(this);
    }
    
    public void setAttributes(AttributeSet attr) {
        this.attr=attr;
    }
    
    public AttributeSet getAttributes() {
        return attr;
    }
    
    public void actionPerformed(ActionEvent e) {
        AttributeSet rAttr=ParagraphOption.showAsDialog(this,attr);
        if (rAttr != attr){
            attr=rAttr;
            this.fireActionEvents("Paragraph");
        }
    }
    protected void fireActionPerformed(ActionEvent e){
        ActionListener[] listeners=getActionListeners();
        for (int i=0;i<listeners.length;i++){
            if (listeners[i] !=this) continue;
            listeners[i].actionPerformed(e);
        }
    }
    protected void fireActionEvents(String cmd){
        ActionEvent e=new ActionEvent(this,ActionEvent.ACTION_PERFORMED,cmd);
        ActionListener[] listeners=getActionListeners();
        for (int i=0;i<getActionListeners().length;i++){
            if (listeners[i]==this) continue;
            listeners[i].actionPerformed(e);
        }
    }
}
