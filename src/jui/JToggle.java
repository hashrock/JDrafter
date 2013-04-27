/*
 * JToggle.java
 *
 * Created on 2007/09/29, 10:02
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
/**
 *
 * @author TK
 */
public class JToggle extends JToggleButton {
    private boolean mouseIsIn;
    public static final JIcons JICONS=new JIcons();
    /** Creates a new instance of JToggle */
    public JToggle() {
        this.setFocusable(false);
        Dimension d=new Dimension(26,26);
        this.setMaximumSize(d);
        this.setMinimumSize(d);
        this.setPreferredSize(d);
        if(isSelected()){
            this.setContentAreaFilled(true);
        }else{
            this.setContentAreaFilled(false);
        }
        this.addMouseListener(new InnerMouseAdapter());
        this.addChangeListener(new innerChangeListener());
        mouseIsIn=false;        
    }
    public JToggle(Icon icon){
        this();
        setIcon(icon);
    }
    public void paint(Graphics g){
        Graphics2D scratch=(Graphics2D) g.create();
        if (!isEnabled ()){
            scratch.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,0.6f));
        }
        if (isSelected())
            JICONS.SELECT_BUTTON.paintIcon(this,g,0,0);
        else if (mouseIsIn)
            JICONS.HOVER_BUTTON.paintIcon(this,g,0,0);
        if (this.getIcon()!=null)
            this.getIcon().paintIcon(this,scratch,3,3);
        scratch.dispose();
    }
    private JToggle returnthis(){
        return this;
    }
    public class InnerMouseAdapter extends MouseAdapter{
        public void mouseEntered(MouseEvent e){
            if (!isEnabled())return;
            mouseIsIn=true;
            repaint();
        }
        public void mouseExited(MouseEvent e){
            if (!isEnabled()) return;
            mouseIsIn=false;
            repaint();
        }
        public void mousePressed(MouseEvent e){
            if (!isEnabled())return;
            if(!isSelected()){
                setSelected(true);
            }
        }
        public void mouseReleased(MouseEvent e){
            if (!isEnabled()) return;
            Point p=SwingUtilities.convertPoint(returnthis(),e.getPoint(),getParent());
            if (!getBounds().contains(p)){
                fireActionPerformed(new ActionEvent(returnthis(),ActionEvent.ACTION_PERFORMED,returnthis().getActionCommand()));
            }
        }
    }
    public class innerChangeListener implements ChangeListener{
        public void stateChanged(ChangeEvent e) {
        }
    }

    
}

