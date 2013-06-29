/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jedit;

import java.awt.geom.Rectangle2D;
import jobject.JLayer;
import jobject.JLeaf;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *
 * @author takashi
 */
public class JInsertObjectEdit extends JAbstractEdit {

    private JLeaf savedObj;
    private JLayer savedLayer;

    public JInsertObjectEdit(JDocumentViewer view, JLeaf obj, JLayer layer,String pName) {
        super(view);
        savedObj = obj;
        savedLayer = layer;
        presentationName = pName;
        redo();
    }

    @Override
    public void redo() {
        super.redo();
        JEnvironment env = viewer.getEnvironment();
        JRequest req = viewer.getCurrentRequest();
        req.clear();
        savedLayer.add(savedObj);
        Rectangle2D r = savedObj.getBounds();
        if (r != null) {
            env.addClip(r);
        }
    }

    @Override
    public void undo() {
        super.undo();
        JRequest req = viewer.getCurrentRequest();
        JEnvironment env = viewer.getEnvironment();
        Rectangle2D r =savedObj.getBounds();
        req.remove(savedObj);
        if (r != null) {
            env.addClip(r);
        }
        savedLayer.remove(savedObj);
    }
}
