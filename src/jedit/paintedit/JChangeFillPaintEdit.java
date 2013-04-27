/*
 * JChangeFillPaintsEdit.java
 *
 * Created on 2007/11/28, 15:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit.paintedit;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;
import javax.swing.undo.CannotRedoException;
import jedit.*;
import jobject.JLeaf;
import jpaint.JPaint;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *
 * @author i002060
 */
public class JChangeFillPaintEdit extends JAbstractEdit{
    private JLeaf target;
    private JPaint savedPaint;
    private JPaint newPaint;
    /**
     * Creates a new instance of JChangeFillPaintsEdit
     */
    public JChangeFillPaintEdit(JDocumentViewer viewer,JLeaf target,JPaint newPaint) {
        super(viewer);
        this.target=target;
        this.newPaint=newPaint;
        savedPaint=target.getFillPaint();
        presentationName="ìhÇËÇÃïœçX";
        redo();
    }
    public void redo(){
        if (!canRedo) throw new CannotRedoException();
        canRedo=false;
        canUndo=true;
        JEnvironment env=viewer.getEnvironment();
        setPaint(target,newPaint);
        target.updatePath();
        env.addClip(target.getBounds());
    }
    public void undo(){
        super.undo();
        JEnvironment env=viewer.getEnvironment();
        JRequest req=viewer.getCurrentRequest();
        target.setFillPaint(savedPaint);
        target.updatePath();
        env.addClip(target.getBounds());
    }
    private void setPaint(JLeaf tg,JPaint jp){
        boolean firstGrad=true;
        if (jp==null || jp.getPaintMode()==JPaint.COLOR_MODE){
            tg.setFillPaint(jp);
        }else if (jp.getPaintMode()==JPaint.PATTERN_MODE){
            tg.setFillPaint(jp);
        }else{
            Rectangle2D r=tg.getSelectionBounds();
            float sx,sy,ex,ey;
            JPaint cp=tg.getFillPaint();
            if (cp !=null && cp.getPaintMode()!=JPaint.COLOR_MODE && cp.getPaintMode()==jp.getPaintMode()){
                firstGrad=false;
                Point2D.Float p=cp.getP1();
                sx=p.x;sy=p.y;
                p=cp.getP2();
                ex=p.x;ey=p.y;
            }else if (jp.getPaintMode()==JPaint.LINEAR_GRADIENT_MODE){
                sx=(float)r.getX();sy=ey=(float)(r.getY()+r.getHeight()/2);
                ex=(float)(r.getX()+r.getWidth());
            }else{
                sx=(float)r.getCenterX();sy=ey=(float)r.getCenterY();
                ex=(float)(r.getX()+Math.max(r.getWidth(),r.getHeight()));
            }
            JPaint np=null;
            if(sx==ex && sy==ey){
                ex++;
                ey++;
            }
            try {
                 np=new JPaint(jp.getPaintMode(),sx,sy,ex,ey,jp.getFracs(),jp.getColors());
            } catch (Exception e) {
                e.printStackTrace();
            }
            AffineTransform tx=new AffineTransform();
            tx.setToRotation(tg.getTotalRotation(),r.getCenterX(),r.getCenterY());
            if (firstGrad)
                np.transform(tx);           
            tg.setFillPaint(np);
        }
    }
}
