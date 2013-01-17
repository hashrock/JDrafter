/*
 * JReflectTool.java
 *
 * Created on 2007/10/06, 10:52
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jtools;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import jtools.jcontrol.ReflectToolPanel;
import jedit.JDuplicateObjectEdit;
import jobject.JLeaf;
import jscreen.JDragPane;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *
 * @author TI
 */
public class JReflectTool extends JScaleTool{
    Line2D.Double line=null;
    Point2D ep=null;
    double theta=0;
    /**
     * Creates a new instance of JReflectTool
     */
    public JReflectTool(JDragPane dragPane) {
        super(dragPane);
        presentationName="ëŒèÃà⁄ìÆÉcÅ[Éã";
    }
    public void paint(Graphics2D g){
        super.paint(g);
        if (currentPoint !=null){
            if (line == null) line=new Line2D.Double();
            line.setLine(ep.getX(),ep.getY(),2*centerPoint.getX()-ep.getX(),2*centerPoint.getY()-ep.getY());
            g.draw(getEnvironment().getToScreenTransform().createTransformedShape(line));
        }
    }
    protected AffineTransform createTransform(Point2D center,Point2D start ,Point2D end,boolean isShiftDown){
        if (ep==null) ep=new Point2D.Double();
        ep.setLocation(end);
        if (isShiftDown)
            ep=getEnvironment().getShiftedMovePoint(center,ep);
        theta=Math.atan2(ep.getY()-center.getY(),ep.getX()-center.getX());
        AffineTransform ret=new AffineTransform();
        ret.setToTranslation(center.getX(),center.getY());
        ret.rotate(theta);
        ret.scale(1,-1);
        ret.rotate(-theta);
        ret.translate(-center.getX(),-center.getY());
        return ret;
    }
    protected AffineTransform createTransformFromDialog(Point2D center){
        AffineTransform ret=null;
        ReflectToolPanel panel=new ReflectToolPanel(this);
        JEnvironment env=getEnvironment();
        if (!panel.isCanceled()){
            ret=new AffineTransform();
            ret.setToTranslation(center.getX(),center.getY());
            ret.rotate(env.DEFAULT_REFLECT_AXIS);
            ret.scale(1,-1);
            ret.rotate(-env.DEFAULT_REFLECT_AXIS);
            ret.translate(-center.getX(),-center.getY());
            copyFlag=panel.isCopy();
        }
        return ret;
    }
    protected void updateObjects(AffineTransform af,Point P){
        JRequest req=getRequest();
        JEnvironment env=getEnvironment();
        CompoundEdit cEdit=null;
        for (int i=0;i<req.size();i++){
            Object o=req.get(i);
            if (o instanceof JLeaf){
                JLeaf jl=(JLeaf)o;
                if (copyFlag){
                    if (cEdit ==null) cEdit=new CompoundEdit();
                    cEdit.addEdit(new JDuplicateObjectEdit(getViewer(),jl));
                }
                jl.transform(af,req,P);
                UndoableEdit anEdit=jl.updateRotate(env,env.DEFAULT_REFLECT_AXIS);
                if (anEdit !=null){
                    if (cEdit ==null) cEdit=new CompoundEdit();
                    cEdit.addEdit(anEdit);
                }
            }
        }
        if (cEdit !=null){
            env.LAST_COPY=copyFlag;
            env.LAST_ROTATION=env.DEFAULT_REFLECT_AXIS;
            env.LAST_TRANSFORM=af;
            cEdit.end();
            getViewer().getDocument().fireUndoEvent(cEdit);
        }
    }
}
