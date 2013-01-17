/*
 * JReplaceObjectEdit.java
 *
 * Created on 2007/11/06, 13:02
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit;

import jobject.JLayoutTextObject;
import jobject.JLeaf;
import jobject.JObject;
import jobject.JPathObject;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *
 * @author i002060
 */
public class JReplaceObjectEdit extends JAbstractEdit{
    private JLeaf target;
    private JLeaf source;
    
    /**
     * Creates a new instance of JReplaceObjectEdit
     */
    public JReplaceObjectEdit(JDocumentViewer viewer,JLeaf target,JLeaf source,String pName) {
        super(viewer);
        this.target=target;
        this.source=source;
        presentationName=pName;
        redo();
    }
    public void redo(){
        super.redo();
        JEnvironment env=viewer.getEnvironment();
        JRequest req=viewer.getCurrentRequest();
        req.clear();
        JObject parent=source.getParent();
        int idx=parent.indexOf(source);
        env.addClip(source.getBounds());
        parent.remove(source);
        parent.add(idx,target);
        env.addClip(target.getBounds());
        req.add(target);
    }
    public void undo(){
        super.undo();
        JEnvironment env=viewer.getEnvironment();
        JRequest req=viewer.getCurrentRequest();
        JObject parent=target.getParent();
        int idx=parent.indexOf(target);
        env.addClip(target.getBounds());
        parent.remove(target);
        parent.add(idx,source);
        req.clear();
        req.add(source);
    }
    
}
