/*
 * JInsertPathEdit.java
 *
 * Created on 2007/09/10, 9:35
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit.pathedit;

import java.awt.geom.AffineTransform;
import java.util.Vector;
import jedit.*;
import jgeom.JComplexPath;
import jgeom.JSimplePath;
import jobject.JPathObject;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *
 * @author i002060
 */
public class JInsertPathEdit extends JAbstractEdit {
    JPathObject target;
    Vector<JSimplePath> paths;
    Vector<JSimplePath> clonepaths;
    /** Creates a new instance of JInsertPathEdit 
     * if Param"index" =-1 insert into lastIndex;
     */
    public JInsertPathEdit(JDocumentViewer v,JPathObject target,Vector<JSimplePath> paths) {
        super(v);
        this.target=target;
        this.paths=(Vector<JSimplePath>)paths.clone();
        clonepaths=new Vector<JSimplePath>();
        for (int i=0;i<paths.size();i++){
            
            clonepaths.add(paths.get(i).clone());
        }
        presentationName=viewer.getDragPane().getDragger().presentationName();
        redo();
    }
    public void redo(){
        super.redo();
        JEnvironment env=viewer.getEnvironment();
        JRequest req=viewer.getCurrentRequest();
        env.addClip(target.getBounds());
        JComplexPath cpath=target.getPath();
        for (int i=0;i<clonepaths.size();i++){
            int idx=cpath.indexOf(paths.get(i));
             cpath.add(idx,clonepaths.get(i));
        }
         target.updatePath();
        env.addClip(target.getBounds());
       
    }
    public void undo(){
        super.undo();
        JEnvironment env=viewer.getEnvironment();
        env.addClip(target.getBounds());
        JComplexPath cpath=target.getPath();
        for (int i=0;i<clonepaths.size();i++){
            cpath.remove(clonepaths.get(i));
        }
        target.updatePath();
        env.addClip(target.getBounds());
        
    }
}
