/*
 * JMagnifyTool.java
 *
 * Created on 2007/12/06, 9:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jtools;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import javax.swing.JComponent;
import javax.swing.RepaintManager;
import jscreen.JDocumentViewer;
import jscreen.JDragPane;
import jscreen.JEnvironment;
import jscreen.JRequest;
import jscreen.JScroller;

/**
 *
 * @author i002060
 */
public class JMagnifyTool extends JAbstractTool{
    public static final float[] MAGNIFY_ARRAY=new float[]{
        0.06f,0.12f,0.16f,0.25f,0.33f,0.5f,0.75f,
        1.0f,1.25f,1.5f,1.75f,2f,2.4f,3f,3.5f,4f,5f,6f,8f,10f,16f,32f,64f
    };
    private boolean altDown=false;
    /** Creates a new instance of JMagnifyTool */
    public JMagnifyTool(JDragPane dragPane) {
        super(dragPane);
        presentationName="ズームツール";
    }
    @Override
    public void wakeup() {
        JRequest req=getRequest();
        req.setSelectionMode(req.GROUP_MODE);
        dragPane.setPaintRect(false);
    }
    @Override
    public void mouseReleased(MouseEvent e){
        JEnvironment env=getEnvironment();
        float  ratio=(float)env.getMagnification();
        float newRatio=ratio;
        
        if (altDown){
            newRatio=getPrevRatio(ratio);
        }else{
            newRatio=getNextRatio(ratio);
        }
        if (ratio==newRatio) return;
        JDocumentViewer viewer=getViewer();
        JScroller scroller =viewer.getScroller();
        Point2D p=new Point2D.Double();
        env.getToAbsoluteTransform().transform(e.getPoint(),p);
        env.setMagnification(newRatio);
        viewer.adjustSize();
        env.getToScreenTransform().transform(p,p);        
        Rectangle bounds=viewer.getBounds();
        Rectangle view=scroller.getViewport().getViewRect();
        int ofsX=(int)Math.min(bounds.width-view.width,p.getX()-view.width/2);
        int ofsY=(int)Math.min(bounds.height-view.height,p.getY()-view.height/2);
        ofsX=Math.max(ofsX,0);
        ofsY=Math.max(ofsY,0);
        scroller.getViewport().setViewPosition(new Point (ofsX,ofsY));
        viewer.isDraftMode=false;
        RepaintManager.currentManager(scroller).markCompletelyDirty(scroller);
        RepaintManager.currentManager(viewer).markCompletelyDirty(viewer);
        RepaintManager.currentManager(viewer).paintDirtyRegions();
    }
    @Override
    public void changeCursor() {
        JEnvironment env=getEnvironment();
        float rat=(float)env.getMagnification();
        if (altDown){
            if (hasPrev(rat)){
                setCursor(env.MOUSE_CURSOR.MAGNIFY_MINUS);
            }else{
                setCursor(env.MOUSE_CURSOR.MAGNIFY);
            }
        }else{
            if (hasNext(rat)){
                setCursor(env.MOUSE_CURSOR.MAGNIFY_PLUS);
            }else{
                setCursor(env.MOUSE_CURSOR.MAGNIFY);
            }
        }
    }
    public static float getMaxRatio(){
        return MAGNIFY_ARRAY[MAGNIFY_ARRAY.length-1];
    }
    public static float getMinimumRatio(){
        return MAGNIFY_ARRAY[0];
    }
    public static float getNearRatio(float f){
        float ret=getMaxRatio();
        for (int i=0;i<MAGNIFY_ARRAY.length;i++){
            if (f<=MAGNIFY_ARRAY[i]){
                ret=MAGNIFY_ARRAY[i];
                break;
            }
        }
        return ret;
    }
    public static int getNearIndex(float f){
        int ret=MAGNIFY_ARRAY.length-1;
        for (int i=0;i<MAGNIFY_ARRAY.length;i++){
            if (f<=MAGNIFY_ARRAY[i]){
                ret=i;
                break;
            }
        }
        return ret;
    }
    public static boolean hasPrev(float f){
        return (getNearIndex(f)!=0);
    }
    public static boolean hasNext(float f){
        return (getNearIndex(f) !=MAGNIFY_ARRAY.length-1);
    }
    public static float getNextRatio(float f){
        if (hasNext(f)){
            return MAGNIFY_ARRAY[getNearIndex(f)+1];
        }
        return f;
    }
    public static float getPrevRatio(float f){
        if (hasPrev(f)){
            return MAGNIFY_ARRAY[getNearIndex(f)-1];
        }
        return f;
    }
    
    @Override
    public void keyPressed(KeyEvent e){
        if (e.getKeyCode()==e.VK_ALT){
            altDown=true;
            changeCursor();
            e.consume();
            
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode()==e.VK_ALT){
            altDown=false;
            changeCursor();
            e.consume();
        }
    }
    
    
}
