/*
 * JFontMenu.java
 *
 * Created on 2008/06/02, 20:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jdraw.typemenus;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 *
 * @author takashi
 */
public class JFontMenu extends AbstractTypeMenuItem{
    HashMap<String, FontSubMenu> itemMap=new HashMap<String, FontSubMenu>();
    ButtonGroup buttonGroup;
    /** Creates a new instance of JFontMenu */
    public JFontMenu() {
        setText(java.util.ResourceBundle.getBundle("main").getString("fm_font_mne"));
        setMnemonic(KeyEvent.VK_F);
        String[] fonts=GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        JMenu parent=this;
        buttonGroup=new ButtonGroup();
        int cnt=0;
        for (int i=0;i<fonts.length;i++){
            FontSubMenu sub=new FontSubMenu(fonts[i]);
            parent.add(sub);
            sub.addActionListener(this);
            buttonGroup.add(sub);
            itemMap.put(fonts[i],sub);
            if (++cnt>30){
                JMenu jm=new JMenu();
                parent.add(jm);
                cnt=0;
                parent=jm;
            }
        }
    }
    private String getselectedFont(){
        Iterator<FontSubMenu> it=itemMap.values().iterator();
        while(it.hasNext()){
            FontSubMenu mn=it.next();
            if (mn.isSelected()) return mn.getFamilyName();
        }
        return null;
    }
    public void actionPerformed(ActionEvent e) {
        fireActionEvents("FontFamily"); //NOI18N
    }

    public void setAttributes(AttributeSet attr) {
        String family=StyleConstants.getFontFamily(attr);
        itemMap.get(family).setSelected(true);
    }

    public AttributeSet getAttributes() {
        MutableAttributeSet attr=new SimpleAttributeSet();
        String family=getselectedFont();
        if (family !=null){
            StyleConstants.setFontFamily(attr,family);
        }
        return attr;
    }

}
