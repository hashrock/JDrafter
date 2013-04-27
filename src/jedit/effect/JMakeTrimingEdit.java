/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jedit.effect;

import jedit.JAbstractEdit;
import jobject.JClippedImageObject;
import jobject.JImageObject;
import jobject.JObject;
import jobject.JPathObject;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *
 * @author takashi
 */
public class JMakeTrimingEdit extends JAbstractEdit {

    private JClippedImageObject clipObject;
    private JImageObject imageObject;

    public JMakeTrimingEdit(JDocumentViewer viewer, JImageObject img, JPathObject path) {
        super(viewer);
        imageObject = img;
        clipObject = new JClippedImageObject(img, img.getOriginalPoint(), path.getShape());
        presentationName="ÉgÉäÉ~ÉìÉOçÏê¨";
        redo();
    }

    @Override
    public void redo() {
        super.redo();
        JObject parent = imageObject.getParent();
        int index = parent.indexOf(imageObject);
        JEnvironment env = viewer.getEnvironment();
        JRequest req = viewer.getCurrentRequest();
        env.addClip(imageObject.getBounds());
        parent.remove(imageObject);
        req.remove(imageObject);
        parent.add(index, clipObject);
        env.addClip(clipObject.getBounds());
        req.add(clipObject);
    }

    @Override
    public void undo() {
        super.undo();
        JEnvironment env = viewer.getEnvironment();
        JRequest req = viewer.getCurrentRequest();
        JObject parent=clipObject.getParent();
        int idx=parent.indexOf(clipObject);
        env.addClip(clipObject.getBounds());
        parent.remove(clipObject);
        req.remove(clipObject);
        env.addClip(imageObject.getBounds());
        parent.add(idx,imageObject);
        req.add(imageObject);
    }
}
