/*
 * ResetFonts.java
 *
 * Created on 2008/06/02, 22:07
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jdraw.typemenus;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.LinkedHashMap;
import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 *
 * @author takashi
 */
public class ResentFonts extends AbstractTypeMenuItem{
    private static JMenuItem EMPTY=new JMenuItem(java.util.ResourceBundle.getBundle("main").getString("rf_font_none"));
    private static final int MAX_ROW=20;
    private LinkedHashMap<String,FontSubMenu> itemMap=new LinkedHashMap<String,FontSubMenu>();
    private ButtonGroup buttonGroup=new ButtonGroup();
    /** Creates a new instance of ResetFonts */
    public ResentFonts() {
        setText(java.util.ResourceBundle.getBundle("main").getString("rf_recent_used_font"));
        EMPTY.setEnabled(false);
        setMnemonic(KeyEvent.VK_R);
        this.add(EMPTY);
    }
    public  void addItem(AttributeSet attr){
        String s=StyleConstants.getFontFamily(attr);
        if (s.equals("") || s==null|| itemMap.containsKey(s)) return; //NOI18N
        remove(EMPTY);
        FontSubMenu sm=new FontSubMenu(s);
        buttonGroup.add(sm);
        add(sm);
        sm.setSelected(true);
        sm.addActionListener(this);
        itemMap.put(s,sm);
        if (itemMap.size()>MAX_ROW){
            Iterator<String> it=itemMap.keySet().iterator();
            String firstKey=it.next();
            FontSubMenu firstValue=itemMap.get(firstKey);
            firstValue.removeActionListener(this);
            buttonGroup.remove(firstValue);
            remove(firstValue);
            itemMap.remove(firstKey);
        }
        return;
    }
    public void actionPerformed(ActionEvent e) {
        fireActionEvents(java.util.ResourceBundle.getBundle("main").getString("rf_font"));
    }
    private String getSelectedFont(){
        Iterator<FontSubMenu> it=itemMap.values().iterator();
        while (it.hasNext()){
            FontSubMenu fs=it.next();
            if (fs.isSelected())
                return fs.getFamilyName();

        }
        return null;
    }
    public void setAttributes(AttributeSet attr) {
        String family=StyleConstants.getFontFamily(attr);
        FontSubMenu fs=itemMap.get(family);
        if (fs==null){
            addItem(attr);
        }
        itemMap.get(family).setSelected(true);
    }

    public AttributeSet getAttributes() {
        MutableAttributeSet attr=new SimpleAttributeSet();
        String family=getSelectedFont();
        if (family!=null){
            StyleConstants.setFontFamily(attr,family);
        }
        return attr;
    }
    
}
