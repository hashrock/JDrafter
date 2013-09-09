/*
 * JStarTool.java
 *
 * Created on 2007/10/04, 20:11
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jtools;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import jtools.jcontrol.StarToolPanel;
import jscreen.JDragPane;
import jscreen.JEnvironment;

/**
 *
 * @author TI
 */
public class JStarTool extends JShapeTool{
    
    /** Creates a new instance of JStarTool */
    public JStarTool(JDragPane dragPane) {
        super(dragPane);
        presentationName="星型ツール";
    }
    
    @Override
    protected Shape createShape(Point2D start, Point2D end, boolean isAltDown, boolean isShiftDown) {
        Point2D e=new Point2D.Double();
        e.setLocation(end);
        if (isShiftDown){
            e=getEnvironment().getShiftedMovePoint(start,e);
        }
        JEnvironment env=getEnvironment();
        return createShape(start,e,env.DEFAULT_STAR_RADIUS_RATIO,env.DEFAULT_STAR_VERTEX);
    }
    public static Shape createShape(Point2D start,Point2D end,double ratio,int vertex){
        double theta=2*Math.PI/vertex;
        AffineTransform af=new AffineTransform();
        af.setToTranslation(start.getX(),start.getY());
        af.scale(ratio,ratio);
        af.translate(-start.getX(),-start.getY());
        Point2D lp=new Point2D.Double(end.getX(),end.getY());
        Point2D sp=new Point2D.Double();
        af.transform(lp,sp);
        af.setToRotation(theta/2,start.getX(),start.getY());
        af.transform(sp,sp);
        af.setToRotation(theta,start.getX(),start.getY());
        GeneralPath ret=new GeneralPath();
        ret.moveTo(lp.getX(),lp.getY());
        ret.lineTo(sp.getX(),sp.getY());
        for (int i=0;i<vertex-1;i++){
            af.transform(sp,sp);
            af.transform(lp,lp);
            ret.lineTo(lp.getX(),lp.getY());
            ret.lineTo(sp.getX(),sp.getY());
        }
        ret.lineTo(end.getX(),end.getY());
        ret.closePath();
        return ret;
    }
    @Override
    protected Shape createFromDialog(Point2D start) {
        Shape ret=null;
        JEnvironment env=getEnvironment();
        StarToolPanel panel=new StarToolPanel(this);
        if (!panel.isCanceled()){
            Point2D p=new Point2D.Double(start.getX()+env.DEFAULT_RADIUS,start.getY());
            ret=createShape(start,p,env.DEFAULT_STAR_RADIUS_RATIO,env.DEFAULT_STAR_VERTEX);
        }
        return ret;
    }
    
}
