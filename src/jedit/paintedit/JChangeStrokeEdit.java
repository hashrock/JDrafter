/*
 * JChangeStrokesEdit.java
 *
 * Created on 2007/12/05, 9:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit.paintedit;

import java.util.Vector;
import javax.swing.undo.CannotRedoException;
import jedit.*;
import jobject.JLeaf;
import jpaint.JStroke;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *
 * @author i002060
 */
public class JChangeStrokeEdit extends JAbstractEdit{
    private JLeaf target;
    private JStroke savedStroke;
    private JStroke newStroke;
    /**
     * Creates a new instance of JChangeStrokesEdit
     */
    public JChangeStrokeEdit(JDocumentViewer viewer,JLeaf target,JStroke newStroke) {
        super(viewer);
        this.target=target;
        this.newStroke=newStroke;
        savedStroke=target.getStroke();
        presentationName="線種";
        redo();
    }
    public void redo(){
        if (!canRedo) throw new CannotRedoException();
        canRedo=false;
        canUndo=true;
        JEnvironment env=viewer.getEnvironment();
        JRequest req=viewer.getCurrentRequest();        
        env.addClip(target.getBounds());
        target.setStroke(newStroke);
        target.updatePath();
        env.addClip(target.getBounds());        
    }
    public void undo(){
        super.undo();
        JEnvironment env=viewer.getEnvironment();
        JRequest req=viewer.getCurrentRequest();
        env.addClip(target.getBounds());
        target.setStroke(savedStroke);
        target.updatePath();
        env.addClip(target.getBounds());
    }
}
