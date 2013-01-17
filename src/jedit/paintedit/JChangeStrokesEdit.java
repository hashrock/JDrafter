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
import javax.swing.undo.CompoundEdit;
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
public class JChangeStrokesEdit extends JAbstractEdit{
    Vector<JLeaf> targets;
    JStroke newStroke;
    CompoundEdit cEdit=null;
    /**
     * Creates a new instance of JChangeStrokesEdit
     */
    public JChangeStrokesEdit(JDocumentViewer viewer,Vector<JLeaf> targets,JStroke newStroke) {
        super(viewer);
        this.targets=targets;
        this.newStroke=newStroke;
        presentationName="ê¸éÌ";
        redo();
    }
    public void redo(){
        if (!canRedo) throw new CannotRedoException();
        canRedo=false;
        canUndo=true;
        JEnvironment env=viewer.getEnvironment();
        JRequest req=viewer.getCurrentRequest();
        if (cEdit==null){
            cEdit=new CompoundEdit();
            for (int i=0;i<targets.size();i++){
                cEdit.addEdit(new JChangeStrokeEdit(viewer,targets.get(i),newStroke));
            }
            cEdit.end();
        }else{
            cEdit.redo();
        }
    }
    public void undo(){
        super.undo();
        JRequest req=viewer.getCurrentRequest();
        req.clear();
        cEdit.undo();
        for (int i=0;i<targets.size();i++){
            req.add(targets.get(i));
        }
    }
}
