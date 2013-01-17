/*
 * JCompoundPathEdit.java
 *
 * Created on 2007/12/20, 9:12
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit.effect;

import java.awt.geom.PathIterator;
import java.util.Vector;
import jedit.JAbstractEdit;
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
public class JCompoundPathEdit extends JAbstractEdit{
    Vector<JPathObject> targets=null;
    Vector<JSimplePath> addingPath=null;
    Vector<Integer> indexes=null;
    Vector<JObject> parents=null;
    /** Creates a new instance of JCompoundPathEdit */
    public JCompoundPathEdit(JDocumentViewer viewer,Vector<JPathObject> targets) {
        super(viewer);
        this.targets=(Vector<JPathObject>)targets.clone();
        JCAGAddEdit.sort(this.targets);
        addingPath=new Vector<JSimplePath>();
        indexes=new Vector<Integer>();
        parents=new Vector<JObject>();
        for (int i=1;i<targets.size();i++){
            parents.add(targets.get(i).getParent());
            indexes.add(targets.get(i).getParent().indexOf(targets.get(i)));
            for (int j=0;j<targets.get(i).getPath().size();j++){
                addingPath.add(targets.get(i).getPath().get(j).clone());
            }
        }
        presentationName="•¡‡ƒpƒXì¬";
        redo();
    }
    public void redo(){
        canUndo=true;
        canRedo=false;
        JEnvironment env=viewer.getEnvironment();
        JRequest req=viewer.getCurrentRequest();
        req.clear();
        env.addClip(targets.get(0).getBounds());
        for (int i=1;i<targets.size();i++){
            env.addClip(targets.get(i).getBounds());
            targets.get(i).getParent().remove(targets.get(i));
        }
        for (int i=0;i<addingPath.size();i++){
            targets.get(0).getPath().add(addingPath.get(i));
        }
        targets.get(0).getPath().setWindingRuel(PathIterator.WIND_EVEN_ODD);
        targets.get(0).updatePath();
        req.add(targets.get(0));
    }
    public void undo(){
        canUndo=true;
        canRedo=false;
        JEnvironment env=viewer.getEnvironment();
        JRequest req=viewer.getCurrentRequest();
        req.clear();
        env.addClip(targets.get(0).getBounds());
        for (int i=1;i<targets.size();i++){
            parents.get(i-1).add(indexes.get(i-1),targets.get(i));
            req.add(targets.get(i));
            env.addClip(targets.get(i).getBounds());
            targets.get(i).updatePath();
        }
        for (int i=0;i<addingPath.size();i++){
            targets.get(0).getPath().remove(addingPath.get(i));
        }
        targets.get(0).updatePath();
        env.addClip(targets.get(0).getBounds());
        req.add(targets.get(0));
    }
}
