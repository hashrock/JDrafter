/*
 * JRoundRectTool.java
 *
 * Created on 2007/10/03, 16:47
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jtools;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import jtools.jcontrol.BevelToolPanel;
import jscreen.JDragPane;
import jscreen.JEnvironment;

/**
 *
 * @author i002060
 */
public class JRoundRectTool extends JShapeTool{
    
    /** Creates a new instance of JRoundRectTool */
    public JRoundRectTool(JDragPane dragPane) {
        super(dragPane);
        presentationName="äpä€ãÈå`ÉcÅ[Éã";
    }
    
    protected Shape createShape(Point2D start, Point2D end, boolean isAltDown,boolean isShiftDown) {
        RoundRectangle2D.Double ret=new RoundRectangle2D.Double();
        JEnvironment env=getEnvironment();
        ret.archeight=env.DEFAULT_ROUNDRECT_RADIUS;
        ret.arcwidth=env.DEFAULT_ROUNDRECT_RADIUS;
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
        BevelToolPanel panel=new BevelToolPanel(this);
        if (!panel.isCanceled()){
            ret=new RoundRectangle2D.Double(start.getX(),start.getY(),env.DEFAULT_WIDTH,env.DEFAULT_HEIGHT,env.DEFAULT_BEVEL_RADIUS,env.DEFAULT_BEVEL_RADIUS);
        }
        return ret;
    }
    
}
