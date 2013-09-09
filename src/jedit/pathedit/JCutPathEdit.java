/*
 * JCutPathEdit.java
 *
 * Created on 2007/09/25, 14:28
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit.pathedit;

import java.util.Vector;
import java.util.jar.Pack200;
import jedit.*;
import jgeom.JComplexPath;
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
public class JCutPathEdit extends JAbstractEdit{
    private JPathObject target;
    private JSimplePath targetPath;
    private Vector<JSimplePath> newPath;
    private Vector<JPathObject> createdObjects;
    private int index;
    /** Creates a new instance of JCutPathEdit */
    public JCutPathEdit(JDocumentViewer viewer,JPathObject target,JSimplePath targetPath,Vector<JSimplePath> newPath) {
        super(viewer);
        this.target=target;
        this.targetPath=targetPath;
        this.newPath=newPath;
        index=target.getPath().indexOf(targetPath);
        createdObjects=null;
        presentationName=viewer.getDragPane().getDragger().presentationName();
        redo();
    }
    public void redo(){
        super.redo();
        JRequest req=viewer.getCurrentRequest();
        JEnvironment env=viewer.getEnvironment();
        JComplexPath cPath=target.getPath();
        req.clear();
        env.addClip(target.getBounds());
        //パス増?
        if (newPath.size()>1){
            //複合パス?
            if (cPath.size()>0){
                cPath.remove(targetPath);
                int idx=index;
                for(int i=0;i<newPath.size();i++){
                    cPath.add(idx++,newPath.get(i));
                }
                createdObjects=null;
                req.add(target);
            }
            //単一パス;
            else{
                JObject parent=target.getParent();
                cPath.remove(targetPath);
                cPath.add(newPath.get(0));
                int idx=target.getParent().indexOf(target);
                createdObjects=new Vector<JPathObject>();
                for (int i=1;i<newPath.size();i++){
                    JPathObject p=target.clone();
                    p.getPath().clear();
                    p.getPath().add(newPath.get(i));
                    parent.add(idx++,p);
                    createdObjects.add(p);
                    req.add(p);
                }               
            }
        }else{
            cPath.remove(targetPath);
            cPath.add(index,newPath.get(0));
            req.add(target);
        }
        target.updatePath();
    }
    public void undo(){
        super.undo();
        JRequest req=viewer.getCurrentRequest();
        JEnvironment env=viewer.getEnvironment();
        if (createdObjects != null){
            JObject parent=target.getParent();
            for (int i=0;i<createdObjects.size();i++){
                parent.remove(createdObjects.get(i));
            }
            target.getPath().clear();
            target.getPath().add(targetPath);
        }else{
            for (int i=0;i<newPath.size();i++){
                target.getPath().remove(newPath.get(i));
            }
            target.getPath().add(index,targetPath);
        }
        target.updatePath();
        env.addClip(target.getBounds());
        
    }
}
