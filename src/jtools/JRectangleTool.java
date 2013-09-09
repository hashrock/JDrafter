/*
 * JRectangleTool.java
 *
 * Created on 2007/10/03, 15:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jtools;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import jtools.jcontrol.RectangleToolPanel;
import jscreen.JDragPane;
import jscreen.JEnvironment;


/**
 *
 * @author i002060
 */
public class JRectangleTool extends JShapeTool {
    /** Creates a new instance of JRectangleTool */
    public JRectangleTool(JDragPane dragPane) {
        super(dragPane);
        presentationName="矩形ツール";
    }
    protected Shape createShape(Point2D start, Point2D end, boolean isAltDown,boolean isShiftDown) {
        Rectangle2D ret=new Rectangle2D.Double();
        Point2D e=new Point2D.Double();
        e.setLocation(end);
        if (isShiftDown){
            e=getEnvironment().getShiftedMovePoint(start,e);
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
            ret=new Rectangle2D.Double(start.getX(),start.getY(),env.DEFAULT_WIDTH,env.DEFAULT_HEIGHT);
        }
        return ret;
    }
}
