/*
 * JChangeFillPaintEdit.java
 *
 * Created on 2007/11/28, 15:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit.paintedit;


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
public class JChangeStrokePaintEdit extends JAbstractEdit{
    private JPaint saved;
    private JLeaf target;
    private JPaint newPaint;
    /**
     * Creates a new instance of JChangeFillPaintEdit
     */
    public JChangeStrokePaintEdit(JDocumentViewer viewer,JLeaf target,JPaint newPaint) {
        super(viewer);
        this.target=target;
        this.newPaint=newPaint;
        saved=target.getStrokePaint();
        presentationName="線の色";
        redo();
    }
    public void redo(){
        if (!canRedo) throw new CannotRedoException();
        canRedo=false;
        canUndo=true;
        JEnvironment env=viewer.getEnvironment();
        env.addClip(target.getBounds());
        target.setStrokePaint(newPaint);
        target.updatePath();
        env.addClip(target.getBounds());
    }
    public void undo(){
        super.undo();
        JEnvironment env=viewer.getEnvironment();
        JRequest req=viewer.getCurrentRequest();
        env.addClip(target.getBounds());
        target.setStrokePaint(saved);
        target.updatePath();
        env.addClip(target.getBounds());
    }
}
