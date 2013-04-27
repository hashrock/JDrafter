/*
 * AngleSlider.java
 *
 * Created on 2007/02/01, 19:39
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jtools.jcontrol;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.util.*;
/**
 *
 * @author TK
 */
public class AngleSlider extends JComponent{
    private boolean hovering;
    private double value;
    private boolean dragging;
    private static final int size=32;
    private Vector<ChangeListener> changeListeners;
    
    /** Creates a new instance of AngleSlider */
    public AngleSlider() {
        hovering=false;
        value=0;
        this.setPreferredSize(new Dimension(size,size));
        InnerAdapter adp=new InnerAdapter();
        this.addMouseListener(adp);
        this.addMouseMotionListener(adp);
        dragging=false;
        changeListeners=new Vector<ChangeListener>();
    }
    public void addChangeListener(ChangeListener c){
        if (!changeListeners.contains(c))
            changeListeners.add(c);
    }
    public void removeChangeListener(ChangeListener c){
        changeListeners.remove(c);
    }
    private void fireStateChange(){
        for (int i=0;i<changeListeners.size();i++)
            changeListeners.get(i).stateChanged(new ChangeEvent(this));
    }
    public double getValue(){
        return value;
    }
    public void setValue(double newValue){
        value=newValue;       
        repaint();
    }
    public void paintComponent(Graphics g){
        double toX=16*Math.cos(value)+16;
        double toY=16*Math.sin(value)+16;
        Line2D line=new Line2D.Double(16,16,toX,toY);
        jui.JToolWindow.JICON.ANGLESLIDER.paintIcon(this,g,0,0);
        if (hovering || dragging){
            g.setColor(java.awt.SystemColor.BLACK);
        }else{
            g.setColor(java.awt.SystemColor.DARK_GRAY);
        }
        ((Graphics2D)g).draw(line);       
    }
    class InnerAdapter extends MouseAdapter{
        public final void mouseEntered(MouseEvent e){
            hovering=true;
            repaint();
        }
        public final void mouseExited(MouseEvent e){
            hovering=false;
            repaint();
        }
        public final void mouseDragged(MouseEvent e){
            dragging=true;
            Point p=e.getPoint();
            value=Math.atan2(p.y-16,p.x-16);
            if (value >0)
                value=value -Math.PI*2;
            value=Math.round(value/Math.PI*180)*Math.PI/180;
            if (e.isShiftDown()){
                double unit=Math.PI/4;
                value=unit*Math.floor(value/unit);
            }
            fireStateChange();
            e.consume();
            repaint();
        }
        public final void mouseClicked(MouseEvent e){
            Point p=e.getPoint();
            value=Math.atan2(p.y-16,p.x-16);
            if (value>0)
                value=value-Math.PI*2;
            value=Math.round(value/Math.PI*180)*Math.PI/180;
            dragging=true;
            fireStateChange();
            repaint();            
        }
        public final void mouseReleased(MouseEvent e){
            dragging=false;
            repaint();
        }
    }
    
}
