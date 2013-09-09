/*
 * JGroupTool.java
 *
 * Created on 2007/10/04, 9:17
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
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Vector;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import jobject.JLeaf;
import jscreen.JDragPane;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *
 * @author i002060
 */
public class JGroupTool extends JAbstractTool{
    private JLeaf hoverObject;
    protected AffineTransform transform;
    private Vector savedSelection;
    private Object hitControl=null;
    /** Creates a new instance of JGroupTool */
    public JGroupTool(JDragPane dragPane) {
        super(dragPane);
        presentationName="選択ツール";
        wakeup();
    }
    @Override
    public void changeCursor() {
        JEnvironment env=getEnvironment();
        JRequest req=getRequest();
        JCursor jc=dragPane.getJCursor();
        if (hoverObject==null){
            setCursor(jc.RESHAPE);
        }else{
            if (req.contains(hoverObject)){
                if (req.isAltDown){
                    setCursor(jc.COPY_AND_MOVE);
                }else{
                    setCursor(jc.MOVE);
                }
            }else {
                if (req.hitResult==req.HIT_ANCUR){
                    setCursor(jc.RESHAPE_ON_SEGMENT);
                }else{
                    setCursor(jc.RESHAPE_ON_OBJECT);
                }
            }
        }
    }
    private void hit(Point mp){
        Point2D p=new Point2D.Double();
        JEnvironment env=getEnvironment();
        JRequest req=getRequest();
        env.getToAbsoluteTransform().transform(mp,p);
        hoverObject=null;
        for (int i=0;i<req.size();i++){
            Object o=req.get(i);
            if (o instanceof JLeaf){
                JLeaf jl=(JLeaf)o;
                req.hitResult=JRequest.HIT_NON;
                req.hitObjects.clear();
                jl.hitByPoint(env,req,p);
                if (req.hitResult==JRequest.HIT_ANCUR || req.hitResult==JRequest.HIT_OBJECT || req.hitResult==JRequest.HIT_PATH){
                    hoverObject=jl;
                    break;
                }
            }
        }
        if (hoverObject ==null){
            req.hitObjects.clear();
            req.hitResult=JRequest.HIT_NON;
            getViewer().getCurrentPage().hitByPoint(env,req,p);
            if (req.hitResult==JRequest.HIT_ANCUR || req.hitResult==JRequest.HIT_OBJECT || req.hitResult==JRequest.HIT_PATH){
                for (int i=0;i<req.hitObjects.size();i++){
                    if (req.hitObjects.get(i) instanceof JLeaf){
                        hoverObject=(JLeaf)req.hitObjects.get(i);
                        break;
                    }
                }
            }else{
                req.hitResult=JRequest.HIT_NON;
            }
        }
        req.hitObjects.clear();
        if (hoverObject  !=hitControl){
            hitControl=hoverObject;
            dragPane.repaint();
        }
    }
    @Override
    public void paint(Graphics2D g){
        if (hitControl ==null || getViewer().getCurrentRequest().contains(hitControl)){
            super.paint(g);
            return;
        }
        Shape s=((JLeaf)hitControl).getShape();
        if (s!=null){
            g.setColor(getViewer().getCurrentPage().getAvilableLayer().getPreviewColor().brighter());
            g.draw(getViewer().getEnvironment().getToScreenTransform().createTransformedShape(s));
        }
        super.paint(g);
    }
    @Override
    public void wakeup() {
        JRequest req=getRequest();
        req.setSelectionMode(req.GROUP_MODE);
        dragPane.setPaintRect(true);
        transform=null;
        savedSelection=null;
        hoverObject=null;
        Point p=dragPane.getMousePosition();
        if (p!=null){
            hit(p);
        }
        changeCursor();
        dragPane.repaint();
    }
    @Override
    public void mouseMoved(MouseEvent e){
        hit(e.getPoint());
    }
    @Override
    public void mousePressed(MouseEvent e){
        JRequest req=getRequest();
        JEnvironment env=getEnvironment();
        req.hitResult=req.HIT_NON;
        req.hitObjects.clear();
        Point2D p=new Point2D.Double();
        env.getToAbsoluteTransform().transform(e.getPoint(),p);
        getViewer().getCurrentPage().hitByPoint(env,req,p);
        if (req.hitResult==req.HIT_L_CONTROL || req.hitResult==req.HIT_R_CONTROL){
            req.hitResult=req.HIT_NON;
            req.hitObjects.clear();
        }
        savedSelection=(Vector)req.getSelectedVector().clone();
        for (int i=0;i<req.hitObjects.size();i++){
            Object o=req.hitObjects.get(i);
            if (o instanceof JLeaf){
                if (!req.contains(o))
                    req.add(o);
            }else{
                req.hitObjects.remove(i--);
            }
        }
        dragPane.repaint();
    }
    @Override
    public void mouseDragged(MouseEvent e){
        JEnvironment env=getEnvironment();
        JRequest req=getRequest();
        if (req.hitResult==req.HIT_NON ){
            if (!dragPane.isPaintRect())
                dragPane.setPaintRect(true);
            return;
        }
        req.hitResult=req.HIT_OBJECT;
        if (dragPane.isFirstDragEvent()){
            dragPane.setPaintRect(false);
            boolean contains=false;
            for (int i=0;i<req.hitObjects.size();i++){
                Object o=req.hitObjects.get(i);
                if (savedSelection.contains(o)){
                    contains=true;
                    break;
                }
            }
            if (!contains && !e.isShiftDown())
                req.clear();
            for (int i=0;i<req.hitObjects.size();i++){
                Object o=req.hitObjects.get(i);
                if (o instanceof JLeaf){
                    if (savedSelection.contains(o) && e.isShiftDown()){
                        req.remove(o);
                        req.hitResult=req.HIT_NON;
                        dragPane.setPaintRect(true);
                        return;
                    } else
                        req.add(o);
                }
            }
        }
        setTransform(e.getPoint(),e.isShiftDown());
    }
    private void setTransform(Point p,boolean isShiftDown){
        JEnvironment env=getEnvironment();
        JRequest req=getRequest();
        Point2D cp=env.getAbsoluteMousePoint(p,getViewer().getCurrentPage());
        Point2D sp=dragPane.getStartPoint();
        if (isShiftDown){
            cp=env.getShiftedMovePoint(sp,cp);
        }
        if (transform == null)
            transform=new AffineTransform();
        transform.setToTranslation(cp.getX()-sp.getX(),cp.getY()-sp.getY());
        for (int i=0;i<req.size();i++){
            Object o=req.get(i);
            if (o instanceof JLeaf){
                JLeaf lf=(JLeaf)o;
                lf.transform(transform,req,p);
            }
        }
    }
    @Override
    public void mouseReleased(MouseEvent e){
        JRequest req=getRequest();
        JEnvironment env=getEnvironment();
        if (transform==null){
            if (dragPane.getDragRect()!=null){
                getViewer().getCurrentPage().hitByRect(env,req,dragPane.getDragRect());
                for (int i=0;i<req.hitObjects.size();i++){
                    if (!(req.hitObjects.get(i) instanceof JLeaf ))
                        req.hitObjects.remove(i--);
                }
            }
            if (!e.isShiftDown()){
                boolean contains=false;
                for (int i=0;i<req.hitObjects.size();i++){
                    Object o=req.hitObjects.get(i);
                    if (savedSelection !=null && savedSelection.contains(o)){
                        contains=true;
                        break;
                    }
                }
                if (!contains || dragPane.getDragRect()!=null){
                    req.clear();
                }
            }
            for (int i=0;i<req.hitObjects.size();i++){
                Object o=req.hitObjects.get(i);
                if (!(o instanceof JLeaf)) continue;
                if (e.isShiftDown() && savedSelection.contains(o)){
                    req.remove(o);
                }else{
                    req.add(o);
                }
            }
        }else{
            CompoundEdit cEdit=null;
            for (int i=0;i<req.size();i++){
                Object o=req.get(i);
                if (o instanceof JLeaf){
                    JLeaf jl=(JLeaf)o;
                    UndoableEdit edt=null;
                    edt=jl.updateTransform(env);
                    if (edt!=null){
                        if (cEdit ==null) cEdit=new CompoundEdit();
                        cEdit.addEdit(edt);
                    }
                }
            }
            if (cEdit!=null){
                cEdit.end();
                getViewer().getDocument().fireUndoEvent(cEdit);
            }
        }
        req.hitResult=req.HIT_NON;
        req.hitObjects.clear();
        transform=null;
        savedSelection=null;
        dragPane.repaint();
        hit(e.getPoint());
        changeCursor();
    }
    private boolean isKeyPressing=false;
    @Override
    public void keyPressed(KeyEvent e){
        int k=e.getKeyCode();
        if (k==e.VK_ALT && dragPane.getCursor()==dragPane.getJCursor().MOVE){
            dragPane.setCursor(dragPane.getJCursor().COPY_AND_MOVE);
            e.consume();
            return;
        }
        if (isKeyPressing) return;
        if (k==KeyEvent.VK_SHIFT){
            isKeyPressing=true;
        }
        JRequest req=getRequest();
        if (k==e.VK_SHIFT && dragPane.isDragging() && req.hitResult != req.HIT_NON){
            Point p=dragPane.getMousePosition();
            if (p !=null){
                setTransform(p,e.isShiftDown());
                dragPane.repaint();
            }
            return;
        }
    }
    @Override
    public void keyReleased(KeyEvent e){
        int k=e.getKeyCode();
        if (k==e.VK_ALT && dragPane.getCursor()==dragPane.getJCursor().COPY_AND_MOVE){
            dragPane.setCursor(dragPane.getJCursor().MOVE);
            e.consume();
            return;
        }
        JRequest req=getRequest();
        if (k==KeyEvent.VK_SHIFT){
           isKeyPressing=false;
        }
        if (k==e.VK_SHIFT && dragPane.isDragging() && req.hitResult != req.HIT_NON){
            Point p=dragPane.getMousePosition();
            if (p !=null){
                setTransform(p,false);
                dragPane.repaint();
            }
            return;
        }
        
    }
}
