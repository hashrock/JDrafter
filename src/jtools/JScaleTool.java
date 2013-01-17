/*
 * JScaleTool.java
 *
 * Created on 2007/10/05, 14:53
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jtools;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.ImageIcon;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import jtools.jcontrol.ScaleToolPanel;
import jedit.JDuplicateObjectEdit;
import jobject.JLeaf;
import jscreen.JDragPane;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *
 * @author i002060
 */
public class JScaleTool extends JAbstractTool{
    protected Point2D currentPoint;
    protected Point2D centerPoint;
    protected boolean copyFlag=false;
    private boolean movingCenter;
    /** Creates a new instance of JScaleTool */
    public JScaleTool(JDragPane dragPane) {
        super(dragPane);
        presentationName="スケールツール";
        wakeup();
    }
    public void paint(Graphics2D g){
        JEnvironment env=getEnvironment();
        if (centerPoint!=null){
            Point p=new Point();
            env.getToScreenTransform().transform(centerPoint,p);
            ImageIcon img=dragPane.JICON.CENTER;
            int rx=img.getIconWidth()/2;
            int ry=img.getIconHeight()/2;
            img.paintIcon(dragPane,g,p.x-rx,p.y-ry);
        }
        super.paint(g);
    }
    public void mousePressed(MouseEvent e){
        Point p=new Point();
        getEnvironment().getToScreenTransform().transform(centerPoint,p);
        int w=dragPane.JICON.CENTER.getIconWidth();
        int h=dragPane.JICON.CENTER.getIconHeight();
        Rectangle r=new Rectangle(p.x-w/2,p.y-h/2,w,h);
        if (r.contains(e.getPoint())) movingCenter=true;
    }
    public void mouseDragged(MouseEvent e){
        if (movingCenter){
            getEnvironment().getToAbsoluteTransform().transform(e.getPoint(),centerPoint);
            dragPane.repaint();
            return;
        }
        if (currentPoint ==null)
            currentPoint=new Point2D.Double();
        JEnvironment env=getEnvironment();
        JRequest req=getRequest();
        env.getToAbsoluteTransform().transform(e.getPoint(),currentPoint);
        AffineTransform af=createTransform(centerPoint,dragPane.startPoint,currentPoint,e.isShiftDown());
        if (af!=null){
            transformObjects(af);
            dragPane.repaint();
        }
    }
    public void mouseReleased(MouseEvent e){
        JEnvironment env=getEnvironment();
        if (movingCenter){
            movingCenter=false;
            return;
        }
        JRequest req=getRequest();
        copyFlag=false;
        AffineTransform af=null;
        if (currentPoint == null) {
            if (e.isAltDown()){
                af=createTransformFromDialog(centerPoint);
            } else{
                env.getToAbsoluteTransform().transform(e.getPoint(),centerPoint);
            }
            
        }else{
            af=createTransform(centerPoint,dragPane.startPoint,currentPoint,e.isShiftDown());
        }
        if (af !=null){
            updateObjects(af,e.getPoint());
        }
        copyFlag=false;
        currentPoint=null;
        dragPane.repaint();
    }
    protected void updateObjects(AffineTransform  af,Point mp){
        JRequest req=getRequest();
        JEnvironment env=getEnvironment();
        CompoundEdit cEdit=null;
        for (int i=0;i<req.size();i++){
            Object o=req.get(i);
            if (o instanceof JLeaf){
                JLeaf jl=(JLeaf)o;
                if (copyFlag) {
                    if (cEdit ==null) cEdit=new CompoundEdit();
                    cEdit.addEdit(new JDuplicateObjectEdit(getViewer(),jl));
                }
                jl.transform(af,req,mp);
                UndoableEdit anEdit=jl.updateTransform(env);
                if (anEdit !=null){
                    if (cEdit ==null) cEdit=new CompoundEdit();
                    cEdit.addEdit(anEdit);
                }
            }
        }
        if (cEdit !=null){
            env.LAST_COPY=copyFlag;
            env.LAST_ROTATION=0;
            env.LAST_TRANSFORM=af;
            cEdit.end();
            getViewer().getDocument().fireUndoEvent(cEdit);
            
        }
    }
    protected void transformObjects(AffineTransform af){
        JRequest req=getRequest();
        for (int i=0;i<req.size();i++){
            Object o=req.get(i);
            if (o instanceof JLeaf){
                ((JLeaf)o).transform(af,req,null);
            }
        }
    }
    protected AffineTransform createTransform(Point2D center,Point2D start ,Point2D end,boolean isShiftDown){
        if (center.equals(start)) return null;
        JEnvironment env=getEnvironment();
        double dx=start.getX()-center.getX();
        double dy=start.getY()-center.getY();
        double dx1=end.getX()-center.getX();
        double dy1=end.getY()-center.getY();
        double sx;
        if (dx==0){
            sx=1;
        }else{
            sx=dx1/dx;
            if (Math.abs(sx)<env.MINIMUM_SCALE_RATIO){
                sx=Math.signum(sx)*env.MINIMUM_SCALE_RATIO;
            }
        }
        double sy;
        if (dy==0){
            sy=1;
        }else{
            sy=dy1/dy;
            if (Math.abs(sy)<env.MINIMUM_SCALE_RATIO){
                sy=Math.signum(sy)*env.MINIMUM_SCALE_RATIO;
            }
        }
        if (isShiftDown){
            double ratio=Math.sqrt(dx1*dx1+dy1*dy1)/Math.sqrt(dx*dx+dy*dy);
            if (ratio<env.MINIMUM_SCALE_RATIO) ratio=env.MINIMUM_SCALE_RATIO;
            sx=sy=ratio;
        }
        AffineTransform ret=new AffineTransform();
        ret.setToTranslation(center.getX(),center.getY());
        ret.scale(sx,sy);
        ret.translate(-center.getX(),-center.getY());
        return ret;
    }
    protected AffineTransform createTransformFromDialog(Point2D center){
        AffineTransform ret=null;
        ScaleToolPanel panel=new ScaleToolPanel(this);
        JEnvironment env=getEnvironment();
        if (!panel.isCanceled()){
            ret=new AffineTransform();
            ret.setToTranslation(center.getX(),center.getY());
            ret.scale(env.DEFAULT_SCALE_X,env.DEFAULT_SCALE_Y);
            ret.translate(-center.getX(),-center.getY());
            copyFlag=panel.isCopy();
        }
        return ret;
    }
    public void changeCursor() {
        JCursor jc=dragPane.getJCursor();
        if (currentPoint !=null){
            setCursor(jc.MOVE);
        }else{
            setCursor(jc.CROSSHAIR);
        }
    }
    public void wakeup() {
        dragPane.setPaintRect(false);
        JRequest req=getRequest();
        req.setSelectionMode(JRequest.GROUP_MODE);
        currentPoint=null;
        centerPoint=null;
        Rectangle2D br=null;
        for (int i=0;i<req.size();i++){
            Object o=req.get(i);
            if (o instanceof JLeaf){
                JLeaf jl=(JLeaf)o;
                Rectangle2D r=jl.getSelectionBounds();
                if (r==null) continue;
                if (br==null){
                    br=r;
                }else{
                    br.add(r);
                }
            }
            
        }
        if (br==null){
            br=getEnvironment().getToAbsoluteTransform().createTransformedShape(dragPane.getVisibleRect()).getBounds2D();
        }
        centerPoint=new Point2D.Double(br.getCenterX(),br.getCenterY());
        movingCenter=false;
    }
    public void keyPressed(KeyEvent e){
        int k=e.getKeyCode();
        if (k==e.VK_SHIFT && currentPoint !=null){
            AffineTransform af=createTransform(centerPoint,dragPane.startPoint,currentPoint,e.isShiftDown());
            if (af !=null){
                transformObjects(af);
                dragPane.repaint();
            }
            e.consume();
            return;
        }
        super.keyPressed(e);
    }
    public void keyReleased(KeyEvent e){
        int k=e.getKeyCode();
        if (k==e.VK_SHIFT && currentPoint !=null){
            AffineTransform af=createTransform(centerPoint,dragPane.startPoint,currentPoint,e.isShiftDown());
            if (af !=null){
                transformObjects(af);
                dragPane.repaint();
            }
            e.consume();
            return;
        }
        super.keyReleased(e);
    }
}
