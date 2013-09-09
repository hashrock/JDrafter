/*
 * JPolygonTool.java
 *
 * Created on 2007/10/03, 16:56
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jtools;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import jtools.jcontrol.PolygonToolPanel;
import jscreen.JDragPane;
import jscreen.JEnvironment;

/**
 *
 * @author i002060
 */
public class JPolygonTool extends JShapeTool{
    
    /** Creates a new instance of JPolygonTool */
    public JPolygonTool(JDragPane dragPane) {
        super(dragPane);
        presentationName="多角形ツール";
    }

    @Override
    protected Shape createShape(Point2D start, Point2D end, boolean isAltDown,boolean isShiftDown) {
        Point2D e=new Point2D.Double();
        e.setLocation(end);
        JEnvironment env=getEnvironment();
        if (isShiftDown){
            e=env.getShiftedMovePoint(start,e);
        }
        int vertex=getEnvironment().DEFAULT_POLYGON_VERTEX;
        return createPolygon(start,e,vertex);
    }
    public static Shape createPolygon(Point2D p0,Point2D p1,int vertex){
        Double theta=Math.PI*2/vertex;
        AffineTransform af=new AffineTransform();
        af.setToRotation(theta,p0.getX(),p0.getY());
        Point2D mp=new Point2D.Double(p1.getX(),p1.getY());
        GeneralPath ret=new GeneralPath();
        ret.moveTo(mp.getX(),mp.getY());
        for (int i=0;i<vertex-1;i++){
            af.transform(mp,mp);
            ret.lineTo(mp.getX(),mp.getY());
        }
        ret.lineTo(p1.getX(),p1.getY());
        ret.closePath();
        return ret;
        
    }
    @Override
    protected Shape createFromDialog(Point2D start) {
        Shape ret=null;
        JEnvironment env=getEnvironment();
        PolygonToolPanel panel=new PolygonToolPanel(this);
        if (!panel.isCanceled()){
            Point2D p=new Point2D.Double(start.getX()+env.DEFAULT_RADIUS,start.getY());
            ret=createPolygon(start,p,env.DEFAULT_POLYGON_VERTEX);
        }
        return ret;
    }
    
}
