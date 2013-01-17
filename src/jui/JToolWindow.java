/*
 * JToolWindow.java
 *
 * Created on 2007/10/01, 14:03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jui;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.geom.GeneralPath;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.border.AbstractBorder;

/**
 *
 * @author i002060
 */
public class JToolWindow extends JDialog implements WindowFocusListener{
    /** Creates a new instance of JToolWindow */
    private static final int TOP_LEFT=0;
    private static final int TOP=1;
    private static final int TOP_RIGHT=2;
    private static final int RIGHT=3;
    private static final int BOTTOM_RIGHT=4;
    private static final int BOTTOM=5;
    private static final int BOTTOM_LEFT=6;
    private static final int LEFT=7;
    private static final int ANYWHERE=-1;
    private boolean closeBox=true;
    private JBorder jborder=new JBorder();
    public static final JIcons JICON=new JIcons();
    private ImageIcon closeIcon;
    public JToolWindow(Frame parent,boolean modal) {
        super(parent,modal);
        closeIcon=JIcons.CLOSE;
        parent.addWindowFocusListener(this);
        this.addWindowFocusListener(this);
        MAdapter mAdapter=new MAdapter();
        JRootPane jrp=getRootPane();
        jrp.addMouseListener(mAdapter);
        jrp.addMouseMotionListener(mAdapter);
        jrp.setBorder(jborder);
        setUndecorated(true);
        setLocationByPlatform(true);
        setFocusable(false);
    }
    public boolean isCloseBox(){
        return closeBox;
    }
    public void setCloseBox(boolean b){
        if (b != closeBox){
            closeBox=b;
            getRootPane().repaint();
        }
    }
    @Override
    public void windowGainedFocus(WindowEvent e) {
        Window w=e.getWindow();
        if (w==getOwner()){
            setFocusableWindowState(false);
            repaint();
            //getRootPane().repaint();
        }else if (w==this){
            getOwner().requestFocus();
            repaint();
            //getRootPane().repaint();
            
        }
    }
    public JDialog getThis(){
        return this;
    }
    @Override
    public void windowLostFocus(WindowEvent e) {
        Window w=e.getWindow();
        if (w==getOwner()){
            setFocusableWindowState(true);
            getRootPane().repaint();
        }
    }
    public class JBorder extends AbstractBorder{
        private Insets inset=new Insets(18,3,3,3);
        /** Creates a new instance of JBorder */
        public JBorder() {
        }
        public Insets getBorderInsets(Component c){
            return inset;
        }
        public void paintBorder(Component c,Graphics g,int x,int y,int width,int height){
            Graphics2D g2=(Graphics2D)g;
            GeneralPath upperShape=new GeneralPath();
            upperShape.moveTo(0,0);
            upperShape.lineTo(width,0);
            upperShape.lineTo(width-inset.right,inset.top);
            upperShape.lineTo(inset.left,inset.top);
            upperShape.lineTo(0,0);
            upperShape.closePath();
            GeneralPath leftShape=new GeneralPath();
            leftShape.moveTo(0,0);
            leftShape.lineTo(inset.left,inset.top);
            leftShape.lineTo(inset.left,height-inset.bottom);
            leftShape.lineTo(0,height);
            leftShape.lineTo(0,0);
            leftShape.closePath();
            GeneralPath bottomShape=new GeneralPath();
            bottomShape.moveTo(0,height);
            bottomShape.lineTo(inset.left,height-inset.bottom);
            bottomShape.lineTo(width-inset.right,height-inset.bottom);
            bottomShape.lineTo(width,height);
            bottomShape.lineTo(0,height);
            bottomShape.closePath();
            GeneralPath rightShape=new GeneralPath();
            rightShape.moveTo(width,0);
            rightShape.lineTo(width,height);
            rightShape.lineTo(width-inset.right,height-inset.bottom);
            rightShape.lineTo(width-inset.right,inset.top);
            rightShape.lineTo(width,0);
            rightShape.closePath();
            Color bc=java.awt.SystemColor.activeCaption;
            Window w=getOwner();
            if (!w.isActive()){
                bc=java.awt.SystemColor.inactiveCaption;
            }
            int red=bc.getRed()+40;if (red>255) red=255;
            int green=bc.getGreen()+40;if (green>255) green=255;
            int blue=bc.getBlue()+40;if (blue>255) blue=255;
            Color sc=new Color(red,green,blue);
            red=(int)(bc.getRed()*.8f);
            green=(int)(bc.getGreen()*.8f);
            blue=(int)(bc.getBlue()*.8f);
            Color dc=new Color(red,green,blue);
            float[] flactions=new float[]{0f,0.1f,0.2f,0.85f,1f};
            Color[] colors=new Color[]{dc,sc,bc,sc,bc};
            LinearGradientPaint lg=new LinearGradientPaint(0,0,0,inset.top-1,flactions,colors);
            g2.setPaint(lg);
            g2.fill(upperShape);
            lg=new LinearGradientPaint(0,0,inset.left,0,flactions,colors);
            g2.setPaint(lg);
            g2.fill(leftShape);
            lg=new LinearGradientPaint(0,height,0,height-inset.bottom,flactions,colors);
            g2.setPaint(lg);
            g2.fill(bottomShape);
            lg=new LinearGradientPaint(width,0,width-inset.right,0,flactions,colors);
            g2.setPaint(lg);
            g2.fill(rightShape);
            if (closeBox){
                Rectangle r=getCloseRect();
                closeIcon.paintIcon(c,g,r.x,r.y);
            }
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(0));
            g2.drawRect(0,0,width-1,height-1);
        }
        public Rectangle getCloseRect(){
            if (isCloseBox()){
                return new Rectangle(getWidth()-inset.right-closeIcon.getIconWidth(),(inset.top-closeIcon.getIconHeight())/2,
                        closeIcon.getIconWidth(),closeIcon.getIconHeight());
            }
            return null;
        }
    }
    public class MAdapter extends MouseAdapter{
        private Point beginPoint=null;
        private Point prevLocation=null;
        private boolean enterCloseButton;
        private boolean isClosePressed;
        private int hitPlace=ANYWHERE;
        public MAdapter(){
            beginPoint=null;
            prevLocation=null;
            enterCloseButton=false;
            isClosePressed=false;
        }
        public void mousePressed(MouseEvent e){
            if (SwingUtilities.isRightMouseButton(e)) return;
            Rectangle r=jborder.getCloseRect();
            Point p=e.getPoint();
            hitPlace=hit(p);
            getThis().toFront();
            if (!isResizable()){
                hitPlace=ANYWHERE;
            }
            if (r !=null && r.contains(p)){
                closeIcon=JICON.CLOSE_PUSHED;
                getRootPane().repaint();
                isClosePressed=true;
                hitPlace=ANYWHERE;
                return;
            }
            if (!getTitleRect().contains(e.getPoint()) && hitPlace==ANYWHERE) return;
            beginPoint=e.getPoint();
            SwingUtilities.convertPointToScreen(beginPoint,e.getComponent());
            prevLocation=getLocation();
            e.getComponent().repaint();
            
            
        }
        public void mouseDragged(MouseEvent e){
            if (SwingUtilities.isRightMouseButton(e)) return;
            Rectangle r=jborder.getCloseRect();
            if (closeIcon==JICON.CLOSE_PUSHED){
                if (!r.contains(e.getPoint())){
                    closeIcon=JICON.CLOSE;
                    getRootPane().repaint();
                }
                return;
            }else if(isClosePressed && r.contains(e.getPoint()) && closeIcon !=JICON.CLOSE_PUSHED ){
                closeIcon=JICON.CLOSE_PUSHED;
                getRootPane().repaint();
                return;
            }
            if (beginPoint==null) return;
            Point p=e.getPoint();
            SwingUtilities.convertPointToScreen(p,e.getComponent());
            GraphicsConfiguration gc=e.getComponent().getGraphicsConfiguration();
            Dimension d=Toolkit.getDefaultToolkit().getScreenSize();
            Insets inset=Toolkit.getDefaultToolkit().getScreenInsets(gc);
            if (hitPlace==ANYWHERE){
                int barH=getRootPane().getInsets().top;
                int x=prevLocation.x+p.x-beginPoint.x;
                int y=prevLocation.y+p.y-beginPoint.y;
                if (x+getWidth()<inset.left+8) x=inset.left-getWidth()+8;
                if (x>d.width-inset.right-8) x=d.width-inset.right-8;
                if (y<inset.top-8) y=inset.top-8;
                if (y>d.height-inset.bottom-8) y=d.height-inset.bottom-8;
                setLocation(x,y);
            }else{
                int x=getX();
                int y=getY();
                int w=getWidth(),h=getHeight();
                int dx=p.x-beginPoint.x;
                int dy=p.y-beginPoint.y;
                if (hitPlace==TOP_LEFT || hitPlace==LEFT || hitPlace==BOTTOM_LEFT){                    
                    w-=dx;
                    x+=dx;
                    if (w<getMinimumSize().width){
                        dx=getWidth()-getMinimumSize().width;
                        x=getX()+dx;
                        w=getMinimumSize().width;
                    }
                 }else if (hitPlace==TOP_RIGHT || hitPlace==RIGHT || hitPlace==BOTTOM_RIGHT){
                    w+=dx;
                    if (w<=getMinimumSize().width){
                        w=getMinimumSize().width; 
                    }
                }
                if (hitPlace==TOP_LEFT || hitPlace==TOP || hitPlace==TOP_RIGHT){
                    h-=dy;
                    y+=dy;
                    if (h<getMinimumSize().height){
                        dy=getHeight()-getMinimumSize().height;
                        h=getMinimumSize().height;
                        y=getY()+dy;
                    }
                }else if (hitPlace==BOTTOM_RIGHT || hitPlace==BOTTOM || hitPlace==BOTTOM_LEFT){
                    h+=dy;
                    if (h<=getMinimumSize().height){
                        h=getMinimumSize().height;
                    }
                }
                if (x !=getX() || y != getY() || w!=getWidth() || h!=getHeight()){                  
                    if (x !=getX() || w != getWidth()){
                        beginPoint.x=p.x;
                    }
                    if (y !=getY() || h != getHeight()){
                        beginPoint.y=p.y;
                    }
                    setBounds(x,y,w,h); 
                }
                setMCursor(hitPlace);
            }
        }
        public void mouseReleased(MouseEvent e){
            if (SwingUtilities.isRightMouseButton(e)) return;
            if (closeIcon==JICON.CLOSE_PUSHED){
                closeIcon=JICON.CLOSE;
                setVisible(false);
            }
            beginPoint=null;
            prevLocation=null;
            isClosePressed=false;
        }
        public void mouseMoved(MouseEvent e){
            Rectangle r=jborder.getCloseRect();
            if (r !=null) {
                Point p=e.getPoint();
                if (r.contains(p) && !enterCloseButton){
                    enterCloseButton=true;
                    closeIcon=JICON.CLOSE_HILIGHT;
                    getRootPane().repaint();
                    return;
                }else if (!r.contains(p) && enterCloseButton){
                    enterCloseButton=false;
                    closeIcon=JICON.CLOSE;
                    getRootPane().repaint();
                    return;
                }
            }
            if (isResizable()){
                setMCursor(hit(e.getPoint()));
            }
        }
        public void mouseExited(MouseEvent e){
            setCursor(Cursor.getDefaultCursor());
        }
        private int hit(Point p){
            Rectangle r=new Rectangle();
            r.setFrame(0,0,4,4);
            if (r.contains(p)) return TOP_LEFT;
            r.setFrame(getWidth()-4,0,4,4);
            if (r.contains(p)) return TOP_RIGHT;
            r.setFrame(getWidth()-4,getHeight()-4,4,4);
            if (r.contains(p)) return BOTTOM_RIGHT;
            r.setFrame(0,getHeight()-4,4,4);
            if (r.contains(p)) return BOTTOM_LEFT;
            //
            r.setFrame(0,0,getWidth(),4);
            if (r.contains(p)) return TOP;
            r.setFrame(getWidth()-4,0,4,getHeight());
            if (r.contains(p)) return RIGHT;
            r.setFrame(0,getHeight()-4,getWidth(),4);
            if (r.contains(p)) return BOTTOM;
            r.setFrame(0,0,4,getHeight());
            if (r.contains(p)) return LEFT;
            return ANYWHERE;
        }
        private void setMCursor(int d){
            Cursor c=Cursor.getDefaultCursor();
            switch (d){
                case TOP_LEFT:c=Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);break;
                case TOP:c=Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);break;
                case TOP_RIGHT:c=Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);break;
                case RIGHT:c=Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);break;
                case BOTTOM_RIGHT:c=Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);break;
                case BOTTOM:c=Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);break;
                case BOTTOM_LEFT:c=Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR);break;
                case LEFT:c=Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);break;
            }
            if (getCursor() != c){
                setCursor(c);
            }
        }
        private Rectangle getTitleRect(){
            Insets insets=getRootPane().getInsets();
            return new Rectangle(0,0,getWidth(),insets.top);
        }
    }
}
