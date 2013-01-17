/*
 * JInsertSegmentEdit.java
 *
 * Created on 2007/09/20, 15:29
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit.pathedit;

import java.awt.geom.Rectangle2D;
import jedit.*;
import jgeom.JSegment;
import jgeom.JSimplePath;
import jobject.JColorable;
import jobject.JLeaf;
import jobject.JPathObject;
import jpaint.JPaint;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *
 * @author i002060
 */
public class JInsertSegmentEdit extends JAbstractEdit {
    JPathObject target;
    JSimplePath targetPath;
    JSegment insertSeg;
    boolean changeGradation;
    int indexAt;
    /** Creates a new instance of JInsertSegmentEdit */
    public JInsertSegmentEdit(JDocumentViewer viewer,JPathObject target,JSimplePath targetPath,JSegment insertSeg,int indexat,boolean changeGradation) {
        super(viewer);
        this.target=target;
        this.targetPath=targetPath;
        this.insertSeg=insertSeg;
        this.indexAt=indexat;
        this.changeGradation=changeGradation;
        presentationName=viewer.getDragPane().getDragger().presentationName();
        redo();
    }
    public void redo(){
        super.redo();
        JEnvironment env=viewer.getEnvironment();
        JRequest req=viewer.getCurrentRequest();
        env.addClip(target.getBounds());
        targetPath.add(indexAt,insertSeg);
        if (changeGradation) adjustGradient(target);
        target.updatePath();
        env.addClip(target.getBounds());
        if (!req.contains(target)) req.add(target);
        
    }
    public void undo(){
        super.undo();
        JEnvironment env=viewer.getEnvironment();
        JRequest req=viewer.getCurrentRequest();
        env.addClip(target.getBounds());
        targetPath.remove(insertSeg);
        if (changeGradation) adjustGradient(target);
        target.updatePath();
        env.addClip(target.getBounds());
        if (!req.contains(target)) req.add(target);
        
    }
    public static void adjustGradient(JLeaf jl){
        if (!(jl instanceof JColorable)) return;
        JPaint jp=jl.getFillPaint();
        if (jp==null || jp.getPaintMode()==JPaint.COLOR_MODE) return;
        Rectangle2D r=jl.getBounds();
        float sx,sy,ex,ey;
        if (jp.getPaintMode()==JPaint.LINEAR_GRADIENT_MODE){
            sx=(float)r.getX();sy=ey=(float)(r.getY()+r.getHeight()/2);
            ex=(float)(r.getX()+r.getWidth());
        }else{
            if (r.isEmpty()){
                r.setFrame(r.getX(),r.getY(),r.getWidth()+1,r.getHeight()+1);
            }
            sx=(float)r.getCenterX();sy=ey=(float)r.getCenterY();
            float mx=Math.max((float)r.getWidth()/2,(float)r.getHeight()/2);
            ex=sx+mx;
        }
        if (sx==ex && sy==ey){
            ex++;
            ey++;
        }
        try {
            jp.setGradient(jp.getPaintMode(),sx,sy,ex,ey,jp.getFracs(),jp.getColors());
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }
}
