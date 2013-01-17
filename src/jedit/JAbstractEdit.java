/*
 * JAbstractEdit.java
 *
 * Created on 2007/09/04, 10:56
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit;

import java.util.Vector;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import jobject.JPage;
import jscreen.JDocumentViewer;

/**
 *
 * @author i002060
 */
public abstract class JAbstractEdit extends AbstractUndoableEdit{
    protected boolean canUndo,canRedo;
    private Vector savedSelection;
    protected JDocumentViewer viewer;
    private JPage page;
    protected String presentationName;
    /** Creates a new instance of JAbstractEdit */
    public JAbstractEdit(JDocumentViewer v) {
        viewer=v;
        savedSelection=(Vector)v.getCurrentRequest().getSelectedVector();
        canUndo=false;
        canRedo=true;
        presentationName="";
    }
    @Override
    public void undo() throws CannotUndoException{
        if (!canUndo) throw new CannotUndoException();
        viewer.getCurrentRequest().setSelectedVector((Vector)savedSelection.clone());
        canUndo=false;
        canRedo=true;
    }
    @Override
    public void redo() throws CannotRedoException{
        if (!canRedo) throw new CannotRedoException();
        viewer.getCurrentRequest().setSelectedVector((Vector)savedSelection.clone());
        
        canUndo=true;
        canRedo=false;
    }
    @Override
    public boolean canUndo(){
        return canUndo;
    }
    @Override
    public boolean canRedo(){
        return canRedo;
    }
    @Override
    public String getPresentationName(){
        return presentationName;
    }
}
