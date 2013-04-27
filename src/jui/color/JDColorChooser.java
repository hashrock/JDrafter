/*
 * JDColorChooser.java
 *
 * Created on 2007/02/09, 22:14
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jui.color;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import jpaint.JDHSV3Paint;
import jscreen.JEnvironment;
/**
 *色見本の表示及び選択を行うクラスです.
 * @author TK
 */
public class JDColorChooser extends JComponent{
    private Paint p;
    private Rectangle colorRect,whiteRect,blackRect;
    private Color returnColor;
    private Vector<ChangeListener> listeners;
    private ChangeEvent evt;
    /**
     * Creates a new instance of JDColorChooser
     */
    public JDColorChooser() {
        this.addComponentListener(new InnerCAdapter());
        InnerMAdapter ms=new InnerMAdapter();
        this.addMouseListener(ms);
        this.addMouseMotionListener(ms);
        returnColor=null;
        listeners=new Vector<ChangeListener>();
        evt=null;
        colorRect=new Rectangle();
        whiteRect=new Rectangle();
        blackRect=new Rectangle();
        adjustRect();
        setCursor(JEnvironment.MOUSE_CURSOR.SPOIT);
    }
    public void paintComponent(Graphics g){
        adjustRect();
        Graphics2D g2=(Graphics2D)g;
        g2.setPaint(p);
        g2.fill(colorRect);
        g2.setPaint(Color.WHITE);
        g2.fill(whiteRect);
        g2.setPaint(Color.BLACK);
        g2.fill(blackRect);
    }
    public void setColor(Color c){
        this.returnColor=c;
    }
    public Color getColor(){
        return returnColor;
    }
    public void fireChangeEvent(){
        if (evt==null)
            evt=new ChangeEvent(this);
        for (int i=0;i<listeners.size();i++){
            listeners.get(i).stateChanged(evt);
        }
    }
    public void addChangeListener(ChangeListener listener){
        if (!listeners.contains(listener))
            listeners.add(listener);
    }
    public void removeChangeListener(ChangeListener listener){
        listeners.remove(listener);
    }
    private void adjustRect(){
        colorRect.setFrame(0,0,this.getWidth()-this.getHeight()/2d,(double)this.getHeight());
        whiteRect.setFrame(this.getWidth()-this.getHeight()/2d,0,this.getHeight()/2d,this.getHeight()/2d);
        blackRect.setFrame(this.getWidth()-this.getHeight()/2d,this.getHeight()/2d,this.getHeight()/2d,this.getHeight()/2d);
        p=new JDHSV3Paint(new Rectangle(0,0,(int)colorRect.getWidth(),(int)colorRect.getHeight()));
    }
    class InnerCAdapter extends ComponentAdapter{
        public void componentResized(ComponentEvent e) {
            adjustRect();
        }
    }
    class InnerMAdapter extends MouseAdapter {
        public void mousePressed(MouseEvent e){
            setCursor(JEnvironment.MOUSE_CURSOR.SPOIT_FILLED);
            Point p=e.getPoint();
            if (whiteRect.contains(p)){
                returnColor=Color.WHITE;
                fireChangeEvent();
            }else if (blackRect.contains(p)){
                returnColor=Color.BLACK;
                fireChangeEvent();
            }else if (colorRect.contains(p)){
                float h=(float)(p.x/colorRect.getWidth());
                float s=(float)((p.getY())/(colorRect.getHeight()/2));
                float b=2-s;
                h=h<0 ? 0:(h>1 ? 1:h);
                s=s<0 ? 0:(s>1 ? 1:s);
                b=b<0 ? 0:(b>1 ? 1:b);
                returnColor=Color.getHSBColor(h,s,b);
                fireChangeEvent();
            }
        }
        public void mouseDragged(MouseEvent e){
            mousePressed(e);
        }
        public void mouseReleased(MouseEvent e){
            setCursor(JEnvironment.MOUSE_CURSOR.SPOIT);
        }
    }

}
