/*
 * JCompoundPathEdit.java
 *
 * Created on 2007/12/19, 21:20
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit.effect;

import java.util.Vector;
import jedit.JAbstractEdit;
import jgeom.JComplexPath;
import jgeom.JSimplePath;
import jobject.JPathObject;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *
 * @author takashi
 */
public class JReleaceCompoundPathEdit extends JAbstractEdit{
    JPathObject target=null;
    Vector<JPathObject> createdObject=null;
    Vector<JSimplePath> savedPath=null;
    /** Creates a new instance of JCompoundPathEdit */
    public JReleaceCompoundPathEdit(JDocumentViewer viewer,JPathObject target) {
        super(viewer);
        this.target=target;
        createdObject=new Vector<JPathObject>();
        for (int i=1;i<target.getPath().size();i++){
            JPathObject cObject=new JPathObject();
            cObject.setFillPaint(target.getFillPaint().clone());
            cObject.setStrokePaint(target.getStrokePaint());
            cObject.setStroke(target.getStroke());
            cObject.setTotalRotation(target.getTotalRotation());
            cObject.setEffector(target.getEffector().clone());
            cObject.getPath().clear();
            cObject.getPath().add(target.getPath().get(i).clone());
            createdObject.add(cObject);
        }
        savedPath=new Vector<JSimplePath>();
        presentationName="複合パス解除";
        redo();
    }
    public void redo(){
        JEnvironment env=viewer.getEnvironment();
        JRequest req=viewer.getCurrentRequest();
        int idx=target.getParent().indexOf(target);
        env.addClip(target.getBounds());
        req.clear();
        req.add(target);
        savedPath.clear();
        while(target.getPath().size()>1){
            savedPath.add(target.getPath().remove(1));
        }
        for (int i=0;i<createdObject.size();i++){
            target.getParent().add(++idx,createdObject.get(i));
            req.add(createdObject.get(i));
        }
        target.updatePath();
    }
    public void undo(){
        JEnvironment env=viewer.getEnvironment();
        JRequest req=viewer.getCurrentRequest();
        req.clear();
        for (int i=0;i<savedPath.size();i++){
            target.getPath().add(savedPath.get(i));
            target.getParent().remove(createdObject.get(i));
        }
        env.addClip(target.getBounds());
        target.updatePath();
        req.add(target);
    }
}
