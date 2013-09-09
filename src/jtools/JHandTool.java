/*
 * JHandTool.java
 *
 * Created on 2007/12/06, 16:03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jtools;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import jscreen.JDragPane;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *
 * @author i002060
 */
public class JHandTool extends JAbstractTool{
    private boolean pressing=false;
    private Point prevPoint=null;
    /** Creates a new instance of JHandTool */
    public JHandTool(JDragPane dragPane) {
        super(dragPane);
        presentationName="ハンドツール";
    }
    
    public void changeCursor() {
        if (!pressing){
            setCursor(JEnvironment.MOUSE_CURSOR.HAND);
        }else{
            setCursor(JEnvironment.MOUSE_CURSOR.GRIP);
        }
        
    }
    public void mousePressed(MouseEvent e){
        pressing=true;
        prevPoint=e.getPoint();
    }
    public void mouseDragged(MouseEvent e){
        if (prevPoint ==null){
            prevPoint=e.getPoint();
            return;
        }
        Rectangle bounds=getViewer().getBounds();
        Rectangle viewRect=getViewer().getScroller().getViewport().getViewRect();
        Point sp=getViewer().getScroller().getViewport().getViewPosition();
        Point p=e.getPoint();
        int dx=p.x-prevPoint.x;
        int dy=p.y-prevPoint.y;
        int maxX=bounds.width-viewRect.width;
        int maxY=bounds.height-viewRect.height;
        sp.x-=dx;
        sp.y-=dy;
        if (sp.x<0){
            sp.x=0;
            prevPoint.x=e.getX();
        }
        if (sp.x>maxX){
            sp.x=maxX;
            prevPoint.x=e.getX();
        }
        if (sp.y<0){
            sp.y=0;
            prevPoint.y=e.getY();
        }
        if (sp.y>maxY){
            sp.y=maxY;
            prevPoint.y=e.getY();
        }
        getViewer().getScroller().getViewport().setViewPosition(sp);
        dragPane.isAutoScrolling=false;
        
    }
    public void mouseReleased(MouseEvent e){
        pressing=false;
    }
    
    public void wakeup() {
        JRequest req=getRequest();
        req.setSelectionMode(req.GROUP_MODE);
        dragPane.setPaintRect(false);
    }
    
}
