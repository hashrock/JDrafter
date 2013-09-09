/*
 * JSelectToggle.java
 *
 * Created on 2007/09/29, 12:02
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jui;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
/**
 *複数のツールを格納するトグルボタンです.
 * @author TK
 */
public class JSelectToggle extends JToggle{
    private GeneralPath path=new GeneralPath();
    private JPopupMenu popup;
    private boolean mouseIsDown;
    private Thread kicker;
    private MenuLouncher louncher;
    private ButtonGroup buttonGroup;
    private Vector<JDMenuItem> menuItems;
    private InnerChangeListener changeListener;
    /** Creates a new instance of JSelectToggle */
    public JSelectToggle() {
        path.moveTo(23,19);
        path.lineTo(23,23);
        path.lineTo(19,23);
        path.closePath();
        buttonGroup=new ButtonGroup();
        popup=new JPopupMenu();
        louncher=new MenuLouncher();
        changeListener=new InnerChangeListener();
        this.addMouseListener(new InnerMouseAdapter());
        this.addActionListener(new InnerActionListener());
        menuItems=new Vector<JDMenuItem>();
        mouseIsDown=false;
        kicker=null;
    }
    public void add(JDMenuItem item){
        popup.add(item);
        buttonGroup.add(item);
        menuItems.add(item);
        item.addChangeListener(changeListener);
        if (menuItems.size()==1){
            item.setSelected(true);
        }
    }
    public void paint(Graphics g){
        super.paint(g);
        Graphics2D g2d=(Graphics2D)g;
        if (isEnabled())
            g2d.setColor(Color.DARK_GRAY);
        else
            g2d.setColor(Color.GRAY);
        g2d.fill(path);        
    }
    private JSelectToggle ownerToggle(){
        return this;
    }
    public Vector<JDMenuItem> getMenuItems(){
        return menuItems;
    }
    class InnerMouseAdapter extends MouseAdapter{
        public void mousePressed(MouseEvent e){
            if (!isEnabled())return;
            mouseIsDown=true;
            if (kicker==null){
                kicker=new Thread(louncher);
                kicker.start();
            }
        }
        public void mouseReleased(MouseEvent e){
            if (!isEnabled()) return;
            mouseIsDown=false;
        }
    }
    public class MenuLouncher implements Runnable{
        public void run() {
            Thread th=Thread.currentThread();
            try{
                th.sleep(300);
                if (mouseIsDown && !popup.isVisible()){
                    popup.show(ownerToggle(),ownerToggle().getWidth()-10,10);
                    popup.repaint();
                }
            }catch(InterruptedException e){}
            kicker=null;
        }        
    }
    public class InnerChangeListener implements ChangeListener{
        public void stateChanged(ChangeEvent e) {
            for(int i=0;i<menuItems.size();i++){
                if (menuItems.get(i).isSelected() && menuItems.get(i).getIcon()!=ownerToggle().getIcon()){
                    ownerToggle().setIcon(menuItems.get(i).getIcon());
                    ownerToggle().setToolTipText(menuItems.get(i).getToolTipText());
                    break;
                }
            }
        }
        
    }
    public class InnerActionListener implements ActionListener{
        
        public void actionPerformed(ActionEvent e) {
            if (popup.isVisible()) return;
            for (int i=0;i<menuItems.size();i++){
                if (menuItems.get(i).isSelected()){
                    menuItems.get(i).fireActionEvent();
                    break;
                }                   
            }
        }
        
    }
}