/*
 * JCutPathsEdit.java
 *
 * Created on 2007/09/25, 21:07
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit.pathedit;

import java.util.Vector;
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
 * @author TI
 */
public class JCutPathsEdit extends JAbstractEdit{
    Vector<JPathObject> targets;
    Vector<JSimplePath> targetPaths;
    Vector<Vector<JSimplePath>> newPaths;
    Vector<JPathObject> creatObjects;
    int[] indexes;
    /**
     * Creates a new instance of JCutPathsEdit
     */
    public JCutPathsEdit(JDocumentViewer viewer,Vector<JPathObject> targets,
            Vector<JSimplePath> targetPaths,Vector<Vector<JSimplePath>> newPaths) {
        super(viewer);
        this.targets=targets;
        this.targetPaths=targetPaths;
        this.newPaths=newPaths;
        indexes=new int[targets.size()];
        for (int i=0;i<targets.size();i++){
            JComplexPath cp=targets.get(i).getPath();
            indexes[i]=cp.indexOf(targetPaths.get(i)); 
        }
        presentationName=viewer.getDragPane().getDragger().presentationName();
        redo();
    }
    public void redo(){
        super.redo();
        JRequest req=viewer.getCurrentRequest();
        JEnvironment env=viewer.getEnvironment();
        creatObjects=null;
        req.clear();
        for (int i=0;i<targets.size();i++){
            JPathObject target=targets.get(i);
            JSimplePath targetPath=targetPaths.get(i);
            Vector<JSimplePath> newPath=newPaths.get(i);
            JComplexPath cPath=target.getPath();        
            env.addClip(target.getBounds());
            int index=indexes[i];
            //パス増?
            if (newPath.size()>1){
                //複合パス?
                if (cPath.size()>1){
                    cPath.remove(targetPath);
                    int idx=index;
                    for(int j=0;j<newPath.size();j++){
                        cPath.add(idx++,newPath.get(j));
                    }
                    req.add(target);
                }
                //単一パス;
                else{
                    JObject parent=target.getParent();
                    cPath.remove(targetPath);
                    cPath.add(newPath.get(0));
                    int idx=target.getParent().indexOf(target);
                    if (creatObjects==null)
                        creatObjects=new Vector<JPathObject>();
                    for (int j=1;j<newPath.size();j++){
                        JPathObject p=target.clone();
                        p.getPath().clear();
                        p.getPath().add(newPath.get(j));
                        parent.add(idx++,p);
                        creatObjects.add(p);
                        req.add(p);
                    }               
                }
            }else{
                cPath.remove(targetPath);
                cPath.add(index,newPath.get(0));
            }
            req.add(target);
            target.updatePath();
        }
    }
    public void undo(){
        super.undo();
        JRequest req=viewer.getCurrentRequest();
        JEnvironment env=viewer.getEnvironment();
        if (creatObjects != null){
            for (int i=0;i<creatObjects.size();i++){
                creatObjects.get(i).getParent().remove(creatObjects.get(i));
            }
        }
        for (int i=0;i<targets.size();i++){
            JPathObject target=targets.get(i);
            JSimplePath targetPath=targetPaths.get(i);
            Vector<JSimplePath> newPath=newPaths.get(i);
            int index=indexes[i];
            JComplexPath cp=target.getPath();
            for (int j=0;j<newPath.size();j++){
                cp.remove(newPath.get(j));
            }
            cp.add(index,targetPath);
            target.updatePath();
            env.addClip(target.getBounds());
            
        }
    }
    
}
