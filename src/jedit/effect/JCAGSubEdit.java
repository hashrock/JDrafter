/*
 * JCAGSubAction.java
 *
 * Created on 2007/12/19, 19:40
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit.effect;

import java.awt.geom.Area;
import java.util.Vector;
import jedit.JAbstractEdit;
import jgeom.JComplexPath;
import jgeom.JPathIterator;
import jobject.JPathObject;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *
 * @author takashi
 */
public class JCAGSubEdit extends JAbstractEdit{
    private Vector<JPathObject> targets=null;
    private Vector<JComplexPath> savedPath=null;
    private Vector<JComplexPath> createdPath=null;
    /** Creates a new instance of JCAGSubAction */
    public JCAGSubEdit(JDocumentViewer viewer,Vector<JPathObject> targets) {
        super(viewer);
        this.targets=(Vector<JPathObject>)targets.clone();
        savedPath=new Vector<JComplexPath>();
        for (int i=0;i<targets.size()-1;i++){
            savedPath.add(targets.get(i).getPath());
        }
        presentationName="·(Œ^”²‚«)";
        Area a=new Area(targets.get(targets.size()-1).getPath().getShape());
        createdPath=new Vector<JComplexPath>();
        for (int i=0;i<savedPath.size();i++){
            Area b=new Area(savedPath.get(i).getShape());
            b.subtract(a);
            JPathIterator pt=new JPathIterator(b.getPathIterator(null));
            pt.normalize();
            createdPath.add(pt.getJPath());
        }
        redo();
        
    }
    public void redo(){
        canUndo=true;
        canRedo=false;
        JEnvironment env=viewer.getEnvironment();
        JRequest req=viewer.getCurrentRequest();
        req.clear();
        for (int i=0;i<targets.size()-1;i++){
            env.addClip(targets.get(i).getBounds());
            targets.get(i).setPath(createdPath.get(i));
            req.add(targets.get(i));
        }
    }
    public void undo(){
        canUndo=false;
        canRedo=true;
        JEnvironment env=viewer.getEnvironment();
        JRequest req=viewer.getCurrentRequest();
        req.clear();
        for (int i=0;i<targets.size()-1;i++){
            env.addClip(targets.get(i).getBounds());
            targets.get(i).setPath(savedPath.get(i));
            req.add(targets.get(i));
        }
    }
}
