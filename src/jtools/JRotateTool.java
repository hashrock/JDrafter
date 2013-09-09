/*
 * JRotateTool.java
 *
 * Created on 2007/10/05, 16:14
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jtools;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import jtools.jcontrol.RotateToolPanel;
import jedit.JDuplicateObjectEdit;
import jobject.JLeaf;
import jscreen.JDragPane;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *
 * @author i002060
 */
public class JRotateTool extends JScaleTool{
    private double rotation=-999;
    /** Creates a new instance of JRotateTool */
    public JRotateTool(JDragPane dragPane) {
        super(dragPane);
        presentationName="回転ツール";
    }
    protected AffineTransform createTransform(Point2D center,Point2D start ,Point2D end,boolean isShiftDown){
        JEnvironment env=getEnvironment();
        double theta0=Math.atan2(start.getY()-center.getY(),start.getX()-center.getX());
        double theta1=Math.atan2(end.getY()-center.getY(),end.getX()-center.getX());
        rotation=theta1-theta0;
        if (isShiftDown){
            double unitAngle=env.getUnitAngle()*Math.PI/180;
            rotation=unitAngle*Math.round(rotation/unitAngle);
        }
        AffineTransform ret=new AffineTransform();
        ret.setToRotation(rotation,center.getX(),center.getY());
        return ret;
    }
    protected AffineTransform createTransformFromDialog(Point2D center){
        AffineTransform ret=null;
        RotateToolPanel panel=new RotateToolPanel(this);
        JEnvironment env=getEnvironment();
        if (!panel.isCanceled()){
            ret=new AffineTransform();
            ret.setToRotation(env.DEFAULT_THETA,center.getX(),center.getY());
            rotation=env.DEFAULT_THETA;
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
                UndoableEdit anEdit=jl.updateRotate(env,rotation);
                if (anEdit !=null){
                    if (cEdit ==null) cEdit=new CompoundEdit();
                    cEdit.addEdit(anEdit);
                }
            }
        }
        if (cEdit !=null){
            env.LAST_COPY=copyFlag;
            env.LAST_ROTATION=rotation;
            env.LAST_TRANSFORM=af;
            cEdit.end();
            getViewer().getDocument().fireUndoEvent(cEdit);           
        }
        rotation=-9999;
    }
}
