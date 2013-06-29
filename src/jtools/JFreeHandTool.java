/*
 * JShapeDragger.java
 *
 * Created on 2007/10/03, 16:09
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jtools;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Vector;
import javax.swing.undo.UndoableEdit;
import jtools.jcontrol.FreeHandToolPanel;
import jedit.JInsertObjectsEdit;
import jgeom.BezzierSpline;
import jgeom.JPathIterator;
import jobject.JPathObject;
import jscreen.JDragPane;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *
 * @author i002060
 */
public class JFreeHandTool extends JAbstractTool{
    protected Vector<Point2D> points=null;
    private int tolerance=4;
    //private Stroke stroke=new BasicStroke(1f);
    /** Creates a new instance of JShapeDragger */
    public JFreeHandTool(JDragPane dragPane) {
        super(dragPane);
        presentationName="フリーハンドツール";
    }
    @Override
    public void paint(Graphics2D g){
        JEnvironment env=getEnvironment();
        JRequest req=getRequest();
        if (points !=null){
            for (Point2D p:points){
                g.setColor(Color.BLACK);
                g.drawRect((int)p.getX(), (int)p.getY(), 0,0);
            }
        }
        super.paint(g);
    }
    @Override
    public void changeCursor() {
        JCursor jc=dragPane.getJCursor();
        setCursor(JCursor.PENCIL);
    }
    @Override
    public void wakeup() {
        JRequest req=getRequest();
        req.setSelectionMode(JRequest.GROUP_MODE);
        dragPane.setPaintRect(false);
        points=null;
    }
    @Override
    public void mousePressed(MouseEvent e){
        points=new Vector<Point2D>();
        Point2D p=new Point2D.Double();
        p.setLocation(e.getPoint());
        points.add(p);
    }
    @Override
    public void mouseDragged(MouseEvent e){
         Point2D prevPoint=points.lastElement();
         Point2D currentPoint=e.getPoint();
         if (prevPoint.equals(currentPoint)) return;
         Point2D p=new Point2D.Double();
         p.setLocation(currentPoint);
         points.add(p);
        dragPane.repaint();
    }
    @Override
    public void mouseReleased(MouseEvent e){
        JEnvironment env=getEnvironment();
        JRequest req=getRequest();
        Shape s=null;
        if (e.isAltDown()){
           tolerance= FreeHandToolPanel.showAsDialog(getViewer(), tolerance);
           
        }
        if (points.size()>=2){
            BezzierSpline bz=new BezzierSpline();            
            Point2D[] ps=new Point2D[points.size()];
            for (int i=0;i<points.size();i++)
                ps[i]=points.get(i);
            s=bz.getGeneralPath(ps,tolerance);
            s=env.getToAbsoluteTransform().createTransformedShape(s);
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
        points=null;
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
