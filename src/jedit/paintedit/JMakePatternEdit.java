/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jedit.paintedit;

import javax.swing.undo.CannotRedoException;
import jedit.JAbstractEdit;
import jpaint.JPaint;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;

/**
 *
 * @author takashi
 */
public class JMakePatternEdit extends JAbstractEdit {

    JPaint paint;

    public JMakePatternEdit(JDocumentViewer viewer, JPaint p) {
        super(viewer);
        this.paint = p;
        presentationName = "パターン作成";
        redo();
    }

    @Override
    public void redo() {
        if (!canRedo) {
            throw new CannotRedoException();
        }
        canRedo = false;
        canUndo = true;
        JEnvironment.SAVED_PATTERN=paint;
        
    }

    @Override
    public void undo() {
        super.undo();
        JEnvironment.SAVED_PATTERN=null;
    }
}
