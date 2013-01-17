/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jedit;

import java.awt.geom.Rectangle2D;
import jobject.JLeaf;
import jobject.JObject;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;

/**
 *
 * @author takashi
 */
public class JDeleteObjectEdit extends JAbstractEdit {

    JLeaf savedObject;
    JObject savedParent;
    int index;

    /** Creates a new instance of JDeleteObjectsEdit */
    public JDeleteObjectEdit(JDocumentViewer view, JLeaf objs,String pName) {
        super(view);
        savedObject = objs;
        presentationName = pName;
        redo();
    }

    @Override
    public void redo() {
        super.redo();
        JEnvironment env = viewer.getEnvironment();
        Rectangle2D clp = savedObject.getBounds();
        if (clp != null) {
            env.addClip(clp);
        }
        index = savedObject.getParent().indexOf(savedObject);
        savedParent = savedObject.getParent();
        savedObject.getParent().remove(savedObject);
        viewer.getCurrentRequest().remove(savedObject);
    }

    @Override
    public void undo() {
        super.undo();
        JEnvironment env = viewer.getEnvironment();
        Rectangle2D clp=null;
        clp=savedObject.getBounds();
        if (clp != null) {
            env.addClip(clp);
        }
        savedParent.add(index,savedObject);
    }
}
