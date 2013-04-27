/*
 * JDeleteObjectsEdit.java
 *
 * Created on 2007/09/06, 15:29
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit;

import java.awt.geom.Rectangle2D;
import java.util.Vector;
import jobject.JLeaf;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;

/**
 *
 * @author i002060
 */
public class JDeleteObjectsEdit extends JAbstractEdit{
    Vector<JLeaf> savedObject;
    int[] indexes;
    
    /** Creates a new instance of JDeleteObjectsEdit */
    public JDeleteObjectsEdit(JDocumentViewer view,Vector<JLeaf> objs) {
        super(view);
        savedObject=objs;        
        indexes=new int[objs.size()];
        presentationName="オブジェクトの削除";
        redo();
    }
    @Override
    public void redo(){
        super.redo();
        JEnvironment env=viewer.getEnvironment();
        for (int i=0;i<savedObject.size();i++){
            JLeaf o=savedObject.get(i);
            Rectangle2D clp=o.getBounds();
            if (clp !=null){
                env.addClip(clp);
            }
            indexes[i]=o.getParent().indexOf(o);
            o.getParent().remove(o);
        }
        viewer.getCurrentRequest().clear();
    }
    @Override
    public void undo(){
        super.undo();
        JEnvironment env=viewer.getEnvironment();
        for (int i=savedObject.size()-1;i>=0;i--){
            JLeaf o=savedObject.get(i);
            Rectangle2D clp=o.getBounds();
            if (clp !=null){
                env.addClip(clp);
            }
            o.getParent().add(indexes[i],o);
        }                   
    }
    
}
