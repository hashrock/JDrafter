/*
 * AlignmentButton.java
 *
 * Created on 2008/06/08, 21:01
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jdraw.textpalette;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
public class AlignmentButton extends StyleToggle implements JTypeMenuItem{
    int alignment=StyleConstants.ALIGN_LEFT;
    private boolean activeEvent=true;
    /** Creates a new instance of AlignmentButton */
    public AlignmentButton() {
       setAlignment(alignment);
       addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e){
                mPressed(e);
            }
       });

    }

    public void setAlignment(int al){
        alignment=al;
        if (JTextIcons.ALIGNCENTER==null)
            new JTextIcons();
        switch (alignment){
            case StyleConstants.ALIGN_LEFT:
                setIcon(JTextIcons.ALIGNLEFT);
                break;
            case StyleConstants.ALIGN_CENTER:
                setIcon(JTextIcons.ALIGNCENTER);
                break;
            case StyleConstants.ALIGN_RIGHT:
                setIcon(JTextIcons.ALIGNRIGHT);
                break;
            default:
                alignment=StyleConstants.ALIGN_LEFT;
                setIcon(JTextIcons.ALIGNLEFT);
        }
    }
    private void mPressed(MouseEvent e){
        if (isSelected()){
            activeEvent=false;
        }
    }
    protected void fireActionPerformed(ActionEvent e){
        if (activeEvent){
            super.fireActionPerformed(e);
        }else{
            activeEvent=true;
        }
    }
    public void setAttributes(AttributeSet attr) {
        if (StyleConstants.getAlignment(attr)==alignment)
            setSelected(true);
    }

    public AttributeSet getAttributes() {
        MutableAttributeSet attr=new SimpleAttributeSet();
        StyleConstants.setAlignment(attr,alignment);
        return attr;
    }

    public void actionPerformed(ActionEvent e) {
        
    }
}
