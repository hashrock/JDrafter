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
import javax.swing.undo.CompoundEdit;
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
public class JChangeStrokePaintsEdit extends JAbstractEdit{
    private Vector<JLeaf> targets;
    private JPaint newPaint;
    private CompoundEdit cEdit=null;
    /**
     * Creates a new instance of JChangeFillPaintEdit
     */
    public JChangeStrokePaintsEdit(JDocumentViewer viewer,Vector<JLeaf> targets,JPaint newPaint) {
        super(viewer);
        this.targets=targets;
        this.newPaint=newPaint;
        cEdit=null;
        presentationName="線の色";
        redo();
    }
    public void redo(){
        if (!canRedo) throw new CannotRedoException();
        canRedo=false;
        canUndo=true;
        if (cEdit==null){
            cEdit=new CompoundEdit();
            for (int i=0;i<targets.size();i++){
                cEdit.addEdit(new JChangeStrokePaintEdit(viewer,targets.get(i),newPaint));
            }
            cEdit.end();
        }else{
            cEdit.redo();
        }
    }
    public void undo(){
        super.undo();
        JEnvironment env=viewer.getEnvironment();
        JRequest req=viewer.getCurrentRequest();
        cEdit.undo();
        req.clear();
        for (int i=0;i<targets.size();i++){
            req.add(targets.get(i));
        }
    }
}
