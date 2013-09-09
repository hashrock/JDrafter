/*
 * JStyleMenu.java
 *
 * Created on 2008/06/04, 18:06
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jdraw.typemenus;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.KeyStroke;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 *
 * @author takashi
 */
public class JAlignmentMenu extends AbstractTypeMenuItem{
    private ButtonGroup buttonGroup=new ButtonGroup();
    private int style;
    private HashMap<Integer, AlignmentMenu> itemMap=new HashMap<Integer, AlignmentMenu>();
    /** Creates a new instance of JStyleMenu */
    public JAlignmentMenu() {
        setText(java.util.ResourceBundle.getBundle("main").getString("am_align_row_mne"));
        setMnemonic(KeyEvent.VK_S);
        AlignmentMenu sb=new AlignmentMenu(StyleConstants.ALIGN_LEFT,java.util.ResourceBundle.getBundle("main").getString("am_align_left_mne"));
        sb.setMnemonic(KeyEvent.VK_L);
        sb.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
        add(sb);
        buttonGroup.add(sb);
        sb.addActionListener(this);
        itemMap.put(StyleConstants.ALIGN_LEFT,sb);
        //
        sb=new AlignmentMenu(StyleConstants.ALIGN_CENTER,java.util.ResourceBundle.getBundle("main").getString("am_align_center_mne"));
        sb.setMnemonic(KeyEvent.VK_C);
        sb.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,ActionEvent.CTRL_MASK|ActionEvent.SHIFT_MASK));
        add(sb);
        buttonGroup.add(sb);
        sb.addActionListener(this);
        itemMap.put(StyleConstants.ALIGN_CENTER,sb);
        //
        sb=new AlignmentMenu(StyleConstants.ALIGN_RIGHT,java.util.ResourceBundle.getBundle("main").getString("am_align_right_mne"));
        sb.setMnemonic(KeyEvent.VK_R);
        sb.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,ActionEvent.CTRL_MASK|ActionEvent.SHIFT_MASK));
        add(sb);
        buttonGroup.add(sb);
        sb.addActionListener(this);
        itemMap.put(StyleConstants.ALIGN_RIGHT,sb);
        MutableAttributeSet attr=new SimpleAttributeSet();
        StyleConstants.setAlignment(attr,StyleConstants.ALIGN_LEFT);
        setAttributes(attr);
    }
    public void actionPerformed(ActionEvent e) {
        AlignmentMenu s=(AlignmentMenu)e.getSource();
        style=s.getStyle();
        fireActionEvents("Alignment"); //NOI18N
    }
    
    public void setAttributes(AttributeSet attr) {
        int al=StyleConstants.getAlignment(attr);
        AlignmentMenu sb=itemMap.get(al);
        if (sb==null) return;
        sb.setSelected(true);
        style=al;
    }
    
    public AttributeSet getAttributes() {
        MutableAttributeSet attr=new SimpleAttributeSet();
        StyleConstants.setAlignment(attr,style);
        return attr;
    }
    public class AlignmentMenu extends JCheckBoxMenuItem{
        int style;
        public AlignmentMenu(int s,String name){
            style=s;
            setText(name);
        }
        public int getStyle(){
            return style;
        }
    }
}
