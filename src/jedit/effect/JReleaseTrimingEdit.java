/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jedit.effect;

import jedit.JAbstractEdit;
import jobject.JClippedImageObject;
import jobject.JImageObject;
import jobject.JObject;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *
 * @author takashi
 */
public class JReleaseTrimingEdit extends JAbstractEdit {

    private JImageObject imageObject;
    private JClippedImageObject clipObject;

    public JReleaseTrimingEdit(JDocumentViewer viewer, JClippedImageObject img) {
        super(viewer);
        clipObject = img;
        imageObject = clipObject.createImageObject();
        presentationName="ÉgÉäÉ~ÉìÉOâèú";
        redo();
    }

    @Override
    public void redo() {
        super.redo();
        JRequest req = viewer.getCurrentRequest();
        JEnvironment env = viewer.getEnvironment();
        JObject parent = clipObject.getParent();
        int idx = parent.indexOf(clipObject);
        env.addClip(clipObject.getBounds());
        req.remove(clipObject);
        parent.remove(clipObject);
        parent.add(idx, imageObject);
        env.addClip(imageObject.getBounds());
        req.add(imageObject);
    }

    @Override
    public void undo() {
        super.undo();
        JRequest req = viewer.getCurrentRequest();
        JEnvironment env = viewer.getEnvironment();
        JObject parent = imageObject.getParent();
        int idx = parent.indexOf(imageObject);
        env.addClip(imageObject.getBounds());
        req.remove(imageObject);
        parent.remove(imageObject);
        parent.add(idx, clipObject);
        env.addClip(clipObject.getBounds());
        req.add(clipObject);
    }
}
