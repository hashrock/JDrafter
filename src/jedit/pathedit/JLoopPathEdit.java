/*
 * JLoopPathEdit.java
 *
 * Created on 2007/09/19, 17:06
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit.pathedit;

import jedit.*;
import jgeom.JSimplePath;
import jobject.JPathObject;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;

/**
 *
 * @author i002060
 */
public class JLoopPathEdit extends JAbstractEdit{
    JPathObject target;
    JSimplePath targetPath;
    boolean savedLoop;
    boolean isLoop;
    /** Creates a new instance of JLoopPathEdit */
    public JLoopPathEdit(JDocumentViewer viewer,JPathObject target,JSimplePath targetPath,boolean isLoop) {
       super(viewer);
       this.target=target;
       this.targetPath=targetPath;
       savedLoop=targetPath.isLooped();
       this.isLoop=isLoop;
       presentationName=viewer.getDragPane().getDragger().presentationName();
       redo();
    }
    public void redo(){
        super.redo();
        JEnvironment env=viewer.getEnvironment();
        env.addClip(target.getBounds());
        targetPath.setLooped(isLoop);
        target.updatePath();
        env.addClip(target.getBounds());
        
    }
    public void undo(){
        super.undo();
        JEnvironment env=viewer.getEnvironment();
        env.addClip(target.getBounds());
        targetPath.setLooped(savedLoop);
        target.updatePath();
        env.addClip(target.getBounds());
        
    }
    
}
