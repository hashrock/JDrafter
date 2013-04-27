/*
 * JLineTool.java
 *
 * Created on 2007/10/03, 14:01
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jtools;

import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import jtools.jcontrol.LineToolPanel;
import jscreen.JDragPane;
import jscreen.JEnvironment;

/**
 *
 * @author i002060
 */
public class JLineTool extends JShapeTool{
    /** Creates a new instance of JLineTool */
    public JLineTool(JDragPane dragPane) {
        super(dragPane);
        presentationName="ÉâÉCÉìÉcÅ[Éã";
    }

    protected Shape createShape(Point2D start, Point2D end, boolean isAltDown,boolean isShiftDown) {
        Line2D ret;
        Point2D e=new Point2D.Double();
        e.setLocation(end);
        JEnvironment env=getEnvironment();
        if (isShiftDown){
            e=env.getShiftedMovePoint(start,e);
        }
        if (isAltDown){
            ret=new Line2D.Double(e.getX(),e.getY(),start.getX()*2-e.getX(),start.getY()*2-e.getY());
        }else{
            ret=new Line2D.Double(start,e);
        }
        return ret;
    }

    protected Shape createFromDialog(Point2D start) {
        Shape ret=null;
        JEnvironment env=getEnvironment();
        LineToolPanel panel=new LineToolPanel(this);
        if (!panel.isCanceled()){
            Point2D.Double p=new Point2D.Double();
            p.x=env.DEFAULT_RADIUS*Math.cos(-env.DEFAULT_ANGLE)+start.getX();
            p.y=env.DEFAULT_RADIUS*Math.sin(-env.DEFAULT_ANGLE)+start.getY();
            ret=new Line2D.Double(start,p);
        }
        return ret;
    }
}
