/*
 * JBevelTool.java
 *
 * Created on 2007/10/04, 21:49
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jtools;

import java.awt.Shape;
import java.awt.geom.Point2D;
import jtools.jcontrol.BevelToolPanel;
import jgeom.BevelRect;
import jscreen.JDragPane;
import jscreen.JEnvironment;

/**
 *
 * @author TI
 */
public class JBevelTool extends JShapeTool{
    
    /** Creates a new instance of JBevelTool */
    public JBevelTool(JDragPane dragPane) {
        super(dragPane);
        presentationName="べベル矩形ツール";
    }
    
    protected Shape createShape(Point2D start, Point2D end, boolean isAltDown, boolean isShiftDown) {
        BevelRect ret=new BevelRect();
        JEnvironment env=getEnvironment();
        ret.archeight=env.DEFAULT_BEVEL_RADIUS;
        ret.arcwidth=env.DEFAULT_BEVEL_RADIUS;
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
            ret=new BevelRect(start.getX(),start.getY(),env.DEFAULT_WIDTH,env.DEFAULT_HEIGHT,env.DEFAULT_BEVEL_RADIUS,env.DEFAULT_BEVEL_RADIUS);
        }
        return ret;
    }
    
}
