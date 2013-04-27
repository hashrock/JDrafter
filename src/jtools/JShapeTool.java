/*
 * JShapeTool.java
 *
 * Created on 2007/10/03, 16:09
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jtools;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.undo.UndoableEdit;
import jedit.JInsertObjectsEdit;
import jgeom.JPathIterator;
import jobject.JPathObject;
import jscreen.JDragPane;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *
 * @author i002060
 */
public abstract class JShapeTool extends JAbstractTool{
    protected Point2D startPoint;
    protected Point2D endPoint;
    //private Stroke stroke=new BasicStroke(1f);
    /** Creates a new instance of JShapeTool */
    public JShapeTool(JDragPane dragPane) {
        super(dragPane);
    }
    protected abstract Shape createShape(Point2D start,Point2D end,boolean isAltDown,boolean isShiftDown);
    protected abstract Shape createFromDialog(Point2D start);
    @Override
    public void paint(Graphics2D g){
        JEnvironment env=getEnvironment();
        JRequest req=getRequest();
        if (startPoint !=null && endPoint !=null){
            //g.setColor(env.PREVIEW_COLOR);
            g.setColor(dragPane.getViewer().getDocument().getCurrentPage().getCurrentLayer().getPreviewColor());
            //g.setStroke(stroke);
            Shape s=createShape(startPoint,endPoint,req.isAltDown,req.isShiftDown);
            g.draw(env.getToScreenTransform().createTransformedShape(s));
            if (req.isAltDown){
                Point p=new Point();
                env.getToScreenTransform().transform(startPoint,p);
                ImageIcon img=dragPane.JICON.CENTER;
                img.paintIcon(dragPane,g,p.x-img.getIconWidth()/2,p.y-img.getIconHeight()/2);
            }
        }
        super.paint(g);
    }
    @Override
    public void changeCursor() {
        JCursor jc=dragPane.getJCursor();
        setCursor(jc.CROSSHAIR);
    }
    @Override
    public void wakeup() {
        JRequest req=getRequest();
        req.setSelectionMode(req.GROUP_MODE);
        dragPane.setPaintRect(false);
        startPoint=endPoint=null;
    }
    @Override
    public void mousePressed(MouseEvent e){
        JEnvironment env=dragPane.getEnvironment();
        startPoint=env.getAbsoluteMousePoint(e.getPoint(),getViewer().getCurrentPage());
    }
    @Override
    public void mouseDragged(MouseEvent e){
        JEnvironment env=getEnvironment();
        JRequest req=getRequest();
        if (endPoint==null) endPoint=new Point2D.Double();
        endPoint=env.getAbsoluteMousePoint(e.getPoint(),getViewer().getCurrentPage());
        //env.getToAbsoluteTransform().transform(e.getPoint(),endPoint);
        dragPane.repaint();
    }
    @Override
    public void mouseReleased(MouseEvent e){
        JEnvironment env=getEnvironment();
        JRequest req=getRequest();
        Shape s=null;
        if (startPoint !=null && endPoint !=null && !startPoint.equals(endPoint)){
            s=createShape(startPoint,endPoint,e.isAltDown(),e.isShiftDown());
        } else{
            s=createFromDialog(startPoint);
        }
        if (s !=null){
            
            JPathIterator jpi=new JPathIterator(s.getPathIterator(null));
            JPathObject nobj=new JPathObject();
            nobj.setPath(jpi.getJPath());
            Vector objs=new Vector();
            objs.add(nobj);
            UndoableEdit anEdit= new JInsertObjectsEdit(getViewer(),objs);
            getViewer().getDocument().fireUndoEvent(anEdit);
            req.clear();
            req.add(nobj);
        }
        dragPane.repaint();
        startPoint=endPoint=null;
    }
    @Override
    public void keyPressed(KeyEvent e){
        int k=e.getKeyCode();
        if ((k==e.VK_ALT || k==e.VK_SHIFT)  && dragPane.isDragging()){
            dragPane.repaint();
            e.consume();
            return;
        }
    }
    @Override
    public void keyReleased(KeyEvent e){
        int k=e.getKeyCode();
        if ((k==e.VK_ALT || k==e.VK_SHIFT)  && dragPane.isDragging()){
            dragPane.repaint();
            e.consume();
            return;
        }
    }
    
}
