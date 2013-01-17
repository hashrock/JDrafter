/*
 * JInsertObjectsEdit.java
 *
 * Created on 2007/09/06, 16:46
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit;

import java.awt.geom.Rectangle2D;
import java.util.Vector;
import jobject.JLayer;
import jobject.JLeaf;
import jpaint.JPaint;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *
 * @author i002060
 */
public class JInsertObjectsEdit extends JAbstractEdit{
    Vector<JLeaf> insertObjects;
    
    /**
     * Creates a new instance of JInsertObjectsEdit
     */
    public JInsertObjectsEdit(JDocumentViewer view,Vector<JLeaf> objs) {
        super(view);
        insertObjects=objs;
        presentationName=view.getDragPane().getDragger().presentationName();
        redo();
    }
    @Override
    public void redo(){
        super.redo();
        JEnvironment env=viewer.getEnvironment();
        JRequest req=viewer.getCurrentRequest();
        JLayer layer=viewer.getAvailableLayer();
        req.clear();
        for (int i=0;i<insertObjects.size();i++){
            Rectangle2D r=insertObjects.get(i).getBounds();
            if (r!=null){
                env.addClip(r);
                adjustPaint(insertObjects.get(i));
            }
            layer.add(insertObjects.get(i));
            req.add(insertObjects.get(i));
        }
    }
    @Override
    public void undo(){
        super.undo();
        JRequest req=viewer.getCurrentRequest();
        JEnvironment env=viewer.getEnvironment();
        for (int i=0;i<insertObjects.size();i++){
            JLeaf lf=insertObjects.get(i);
            Rectangle2D r=lf.getBounds();
            if (r!=null){
                env.addClip(r);
            }
            lf.getParent().remove(lf);
            req.remove(lf);
        }
    }
    private void adjustPaint(JLeaf o){
        JPaint jp=o.getFillPaint();
        if (jp==null || jp.getPaintMode()==JPaint.COLOR_MODE || jp.getPaintMode()==JPaint.PATTERN_MODE) return;
        Rectangle2D r=o.getSelectionBounds();
        float sx,sy,ex,ey;
        if (jp.getPaintMode()==JPaint.LINEAR_GRADIENT_MODE){
            sx=(float)r.getX();sy=ey=(float)(r.getY()+r.getHeight()/2);
            ex=(float)(r.getX()+r.getWidth());
        }else{
            sx=(float)r.getCenterX();sy=ey=(float)r.getCenterY();
            ex=(float)(r.getX()+Math.max(r.getWidth(),r.getHeight()));
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
