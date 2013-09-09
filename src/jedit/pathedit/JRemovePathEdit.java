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

/**
 *
 * @author i002060
 */
public class JRemovePathEdit extends JAbstractEdit {
    private JObject parent;
    private int parentIndex;
    private int pathIndex;
    private JPathObject target;
    private JSimplePath removePath;
    /** Creates a new instance of JRemovePathEdit */
    public JRemovePathEdit(JDocumentViewer viewer,JPathObject target,JSimplePath removePath) {
        super(viewer);
        this.target=target;
        this.removePath=removePath;
        parent=target.getParent();
        parentIndex=parent.indexOf(target);
        pathIndex=target.getPath().indexOf(removePath);
        presentationName="パス削除";
        redo();
    }
    public void redo(){
        super.redo();
        JEnvironment env=viewer.getEnvironment();
        env.addClip(target.getBounds());
        target.getPath().remove(removePath);
        if (target.getPath().size()==0){
            parent.remove(target);
        }
        target.updatePath();
        
        
    }
    public void undo(){
        super.undo();
        JEnvironment env=viewer.getEnvironment();
        target.getPath().add(pathIndex,removePath);
        if(target.getPath().size()==1){
            parent.add(parentIndex,target);
        }
        target.updatePath();
        env.addClip(target.getBounds());
        
    } 
    
}
