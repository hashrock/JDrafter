/*
 * JRemovePathEdit.java
 *
 * Created on 2007/09/19, 16:54
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit.pathedit;

import jedit.*;
import jgeom.JSimplePath;
import jobject.JObject;
import jobject.JPathObject;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *
 * @author i002060
 */
public class JReplacePathEdit extends JAbstractEdit {
    private JObject parent;
    private int pathIndex;
    private JPathObject target;
    private JSimplePath removePath;
    private JSimplePath newPath;
    /** Creates a new instance of JRemovePathEdit */
    public JReplacePathEdit(JDocumentViewer viewer,JPathObject target,JSimplePath removePath,JSimplePath newPath) {
        super(viewer);
        this.target=target;
        this.removePath=removePath;
        parent=target.getParent();
        pathIndex=target.getPath().indexOf(removePath);
        this.newPath=newPath;
        presentationName="パス編集";
        redo();
    }
    public void redo(){
        super.redo();
        JEnvironment env=viewer.getEnvironment();
        env.addClip(target.getBounds());
        target.getPath().remove(removePath);
        target.getPath().add(pathIndex,newPath);
        target.updatePath();
        env.addClip(target.getBounds());
        
        JRequest req= viewer.getCurrentRequest();
        req.add(target);
        
    }
    public void undo(){
        super.undo();
        JEnvironment env=viewer.getEnvironment();
        env.addClip(target.getBounds());
        target.getPath().remove(newPath);
        target.getPath().add(pathIndex,removePath);
        target.updatePath();
        env.addClip(target.getBounds());
        
        
    }
    
}
