/*
 * JChangeTextEdit.java
 *
 * Created on 2008/05/31, 15:01
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit.textobjectedit;

import javax.swing.undo.UndoableEdit;
import jedit.JAbstractEdit;
import jobject.JLeaf;
import jscreen.JDocumentViewer;
import jobject.JText;

/**
 *
 * @author takashi
 */
public class JChangeTextEdit extends JAbstractEdit {
    private JLeaf target;
    private UndoableEdit anEdit;
    /** Creates a new instance of JChangeTextEdit */
    public JChangeTextEdit(JDocumentViewer viewer,JLeaf target,UndoableEdit anEdit,boolean isPainting) {
        super(viewer);
        this.target=target;
        this.anEdit=anEdit;
        presentationName=anEdit.getPresentationName();
        canUndo=true;
        canRedo=false;
        if (isPainting){
            viewer.getEnvironment().addClip(target.getBounds());
            ((JText)target).updatePath();
            viewer.getEnvironment().addClip(target.getBounds());
            viewer.repaint();
        }
    }
    public void redo(){
        canUndo=true;
        canRedo=false;
        viewer.getEnvironment().addClip(target.getBounds());
        anEdit.redo();
        JText tx=(JText)target;
        tx.updatePath();
        viewer.getEnvironment().addClip(target.getBounds());
        viewer.repaint();
    }
    public void undo(){
        canRedo=true;
        canUndo=false;
        viewer.getEnvironment().addClip(target.getBounds());
        anEdit.undo();
        JText tx=(JText)target;
        tx.updatePath();
        viewer.getEnvironment().addClip(target.getBounds());
        viewer.repaint();
    }
}
