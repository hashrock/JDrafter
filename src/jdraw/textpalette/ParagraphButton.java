/*
 * ParagraphButton.java
 *
 * Created on 2008/06/09, 18:47
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jdraw.textpalette;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.text.AttributeSet;
import jdraw.typemenus.JTypeMenuItem;
import jdraw.typemenus.ParagraphOption;
import jui.JTextIcons;

/**
 *
 * @author takashi
 */
public class ParagraphButton extends StylePushButton implements JTypeMenuItem{
    private AttributeSet attr=null;
    /** Creates a new instance of ParagraphButton */
    public ParagraphButton() {
        if (JTextIcons.PARAGRAPH==null)
            new JTextIcons();
        setIcon(JTextIcons.PARAGRAPH);
        setToolTipText("段落オプション");
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
