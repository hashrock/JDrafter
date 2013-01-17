/*
 * SizeCombo.java
 *
 * Created on 2008/06/08, 19:14
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jdraw.textpalette;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import jdraw.typemenus.FontSizeOption;
import jdraw.typemenus.JFontSize;
import jdraw.typemenus.JTypeMenuItem;

/**
 *
 * @author takashi
 */
public class SizeCombo extends JComboBox implements JTypeMenuItem{
    private boolean activeEvent=true;
    private static String other="その他";
    private AttributeSet attr=null;
    /** Creates a new instance of SizeCombo */
    public SizeCombo() {
        int[] sizes=JFontSize.FONT_SIZES;
        for (int i=0;i<sizes.length;i++){
            addItem(new Integer(sizes[i]));
        }
        addItem(other);
        //setEditor(new Ceditor());
        setMaximumRowCount(20);
        setEditable(true);
        setFocusable(false);
        setToolTipText("フォントサイズ");
        this.addActionListener(this);
    }
    public void actionPerformed(ActionEvent e){
        if (!activeEvent){
            activeEvent =true;
            return;
        }
        if (getSelectedItem().equals(other)){
            int currentSize=StyleConstants.getFontSize(attr);
            hidePopup();
            currentSize=FontSizeOption.showAsDialog(this,currentSize);
            activeEvent=FontSizeOption.getResult();
            
            this.setSelectedItem(new Integer(currentSize));
        }else{
            ActionListener[] l=this.getActionListeners();
            for (int i=0;i<l.length;i++){
                if (l[i]==this) continue;
                l[i].actionPerformed(e);
            }
        }
    }
    
    protected void fireActionEvent(){
        actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"schage"));
    }
    public void setAttributes(AttributeSet attr) {
        int sz=StyleConstants.getFontSize(attr);
        this.attr=attr;
        activeEvent=false;
        this.setSelectedItem(new Integer(sz));
    }
    public AttributeSet getAttributes() {
        MutableAttributeSet attr=new SimpleAttributeSet();
        StyleConstants.setFontSize(attr,((Integer)getSelectedItem()).intValue());
        this.attr=attr;
        return attr;
    }
    public void paint(Graphics g){
        Graphics2D g2=(Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        super.paint(g);
    }
    public class MyCellRenderer extends BasicComboBoxRenderer{
        public void paintComponent(Graphics g){
            Graphics2D g2=(Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            super.paintComponent(g);
        }
    }
}
