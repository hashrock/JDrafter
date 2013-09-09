/*
 * JFontSize.java
 *
 * Created on 2008/06/03, 19:57
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jdraw.typemenus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 *
 * @author takashi
 */
public class JFontSize extends AbstractTypeMenuItem{
    private HashMap<Integer, SizeSubMenu> itemMap=new HashMap<Integer, SizeSubMenu>();
    private ButtonGroup buttonGroup;
    public static final int[] FONT_SIZES={6,8,9,10,11,12,14,16,18,21,24,30,36,40,48,64,72};
    private int currentSize=0;
    private JCheckBoxMenuItem otherSize;
    /** Creates a new instance of JFontSize */
    public JFontSize() {
        setText(java.util.ResourceBundle.getBundle("main").getString("fs_size_mne"));
        buttonGroup=new ButtonGroup();
        otherSize=new JCheckBoxMenuItem(java.util.ResourceBundle.getBundle("main").getString("fs_other"));
        add(otherSize);
        buttonGroup.add(otherSize);
        otherSize.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                otherClicked(e);
            }
        });
        addSeparator();
        for (int i=0;i< FONT_SIZES.length;i++){
            SizeSubMenu sb=new SizeSubMenu(FONT_SIZES[i]);
            buttonGroup.add(sb);
            add(sb);
            itemMap.put(FONT_SIZES[i],sb);
            sb.addActionListener(this);
        }
    }
    private void otherClicked(ActionEvent e){
        int s=FontSizeOption.showAsDialog(this,currentSize);
        MutableAttributeSet attr=new SimpleAttributeSet();
        StyleConstants.setFontSize(attr,s);
        setAttributes(attr);
        if (FontSizeOption.getResult())
            fireActionEvents("Size"); //NOI18N
    }
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof SizeSubMenu){
            SizeSubMenu sb=(SizeSubMenu)e.getSource();
            MutableAttributeSet attr=new SimpleAttributeSet();
            StyleConstants.setFontSize(attr,sb.getFontSize());
            setAttributes(attr);
            fireActionEvents("Size"); //NOI18N
        }
    }
    public void setAttributes(AttributeSet attr) {
        int size=StyleConstants.getFontSize(attr);
        currentSize=size;
        SizeSubMenu s=itemMap.get(size);
        if (s!=null){
            s.setSelected(true);
        }else{
            otherSize.setSelected(true);
        }
    }
    public AttributeSet getAttributes() {
        MutableAttributeSet attr=new SimpleAttributeSet();
        StyleConstants.setFontSize(attr,currentSize);
        return attr;
    }
    
}
