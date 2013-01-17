/*
 * JChangeGradientDirectionEdit.java
 *
 * Created on 2007/12/03, 11:25
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit.paintedit;

import java.awt.geom.Point2D;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import jedit.*;
import jobject.JLeaf;
import jpaint.JPaint;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;

/**
 *
 * @author i002060
 */
public class JChangeGradientDirectionEdit extends JAbstractEdit {
    private Point2D.Float savedSp,savedEp,newSp,newEp;
    private JLeaf target;
    /** Creates a new instance of JChangeGradientDirectionEdit */
    public JChangeGradientDirectionEdit(JDocumentViewer viewer,JLeaf target,Point2D sp,Point2D ep) {
        super(viewer);
        this.target=target;
        JPaint cp=target.getFillPaint();
        savedSp=cp.getP1();
        savedEp=cp.getP2();
        newSp=new Point2D.Float();newEp=new Point2D.Float();
        newSp.setLocation(sp);newEp.setLocation(ep);
        presentationName="グラデーションの方向と位置";
        redo();
    }
    public void redo() throws CannotRedoException{
        if (!canRedo) throw new CannotRedoException();
        canRedo=false;
        canUndo=true;
        JEnvironment env=viewer.getEnvironment();
        env.addClip(target.getBounds());
        JPaint jp=target.getFillPaint();
        try {
            jp.setGradient(jp.getPaintMode(),newSp.x,newSp.y,newEp.x,newEp.y,jp.getFracs(),jp.getColors());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }
    public void undo() throws CannotUndoException{
        if (!canUndo) throw new CannotUndoException();
        canUndo=false;
        canRedo=true;
        JEnvironment env=viewer.getEnvironment();
        env.addClip(target.getBounds());
        JPaint jp=target.getFillPaint();
        try {
            jp.setGradient(jp.getPaintMode(),savedSp.x,savedSp.y,savedEp.x,savedEp.y,jp.getFracs(),jp.getColors());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        
    }
}
