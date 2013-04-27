/*
 * JEllipseTool.java
 *
 * Created on 2007/10/03, 16:30
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jtools;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import jtools.jcontrol.RectangleToolPanel;
import jscreen.JDragPane;
import jscreen.JEnvironment;

/**
 *
 * @author i002060
 */
public class JEllipseTool extends JShapeTool{
    
    /** Creates a new instance of JEllipseTool */
    public JEllipseTool(JDragPane dragPane) {
        super(dragPane);
        presentationName="ë»â~ÉcÅ[Éã";
    }
    
    protected Shape createShape(Point2D start, Point2D end, boolean isAltDown,boolean isShiftDown) {
        Ellipse2D ret=new Ellipse2D.Double();
        Point2D e=new Point2D.Double();
        e.setLocation(end);
        JEnvironment env=getEnvironment();
        if (isShiftDown){
            e=env.getShiftedMovePoint(start,e);
        }
        if (isAltDown){
            ret.setFrameFromCenter(start,e);
        }else{
            ret.setFrameFromDiagonal(start,e);
        }
        return ret;
    }
    
    protected Shape createFromDialog(Point2D start) {
        Shape ret=null;
        JEnvironment env=getEnvironment();
        RectangleToolPanel panel=new RectangleToolPanel(this);
        if (!panel.isCanceled()){
            ret=new Ellipse2D.Double(start.getX(),start.getY(),env.DEFAULT_WIDTH,env.DEFAULT_HEIGHT);
        }
        return ret;
    }
    
}
