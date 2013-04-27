/*
 * JSetEffectEdit.java
 *
 * Created on 2007/12/24, 9:11
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit.effect;

import jedit.JAbstractEdit;
import jobject.JLeaf;
import jobject.effector.JEffector;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *
 * @author takashi
 */
public class JSetEffectEdit extends JAbstractEdit{
    private JLeaf target;
    private JEffector newEffect;
    private JEffector savedEffect;
    /** Creates a new instance of JSetEffectEdit */
    public JSetEffectEdit(JDocumentViewer viewer,JLeaf target,JEffector effect ,String presentationName) {
        super(viewer);
        this.target=target;
        this.newEffect=effect;
        this.savedEffect=target.getEffector();
        this.presentationName=presentationName;
        redo();
    }
    public void redo(){
        super.redo();
        JEnvironment env=viewer.getEnvironment();
        JRequest req=viewer.getCurrentRequest();
        env.addClip(target.getBounds());
        target.setEffector(newEffect);
        env.addClip(target.getBounds());
        req.add(target);
    }
    public void undo(){
        super.undo();
        JEnvironment env=viewer.getEnvironment();
        JRequest req=viewer.getCurrentRequest();
        env.addClip(target.getBounds());
        target.setEffector(savedEffect);
        env.addClip(target.getBounds());
        req.add(target);
    }
}
