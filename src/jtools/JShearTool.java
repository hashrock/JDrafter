/*
 * JShearTool.java
 *
 * Created on 2007/10/06, 8:49
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jtools;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import jtools.jcontrol.SheerToolPanel;
import jscreen.JDragPane;
import jscreen.JEnvironment;

/**
 *
 * @author TI
 */
public class JShearTool extends JScaleTool{
    
    /** Creates a new instance of JShearTool */
    public JShearTool(JDragPane dragPane) {
        super(dragPane);
        presentationName="シアリングツール";
    }
    protected AffineTransform createTransform(Point2D center,Point2D start ,Point2D end,boolean isShiftDown){
        if (center.equals(start)) return null;
        JEnvironment env=getEnvironment();
        Point2D ep=new Point2D.Double(end.getX(),end.getY());
        if (isShiftDown){
            ep=env.getShiftedMovePoint(start,ep);
        }
        double dx=start.getX()-center.getX();
        double dy=start.getY()-center.getY();
        double dx1=ep.getX()-center.getX();
        double dy1=ep.getY()-center.getY();
        double sx,sy;
        if (dy==0){
            sx=0;
        }else{
            sx=(dx1-dx)/dy;
        }
        if (dx==0){
            sy=0;
        }else{
            sy=(dy1-dy)/dx;
        }
        AffineTransform ret=new AffineTransform();
        ret.setToTranslation(center.getX(),center.getY());
        ret.shear(sx,sy);
        ret.translate(-center.getX(),-center.getY());
        return ret;
    }
    protected AffineTransform createTransformFromDialog(Point2D center){
        AffineTransform ret=null;
        SheerToolPanel panel=new SheerToolPanel(this);
        JEnvironment env=getEnvironment();
        if (!panel.isCanceled()){
            ret=new AffineTransform();
            ret.setToTranslation(center.getX(),center.getY());
            ret.shear(env.DEFAULT_SHEER_X,env.DEFAULT_SHEER_Y);
            ret.translate(-center.getX(),-center.getY());
            copyFlag=panel.isCopy();
        }
        return ret;
    }
    
}
