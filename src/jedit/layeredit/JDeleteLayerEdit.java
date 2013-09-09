/*
 * JDeleteLayerEdit.java
 *
 * Created on 2008/05/24, 20:02
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit.layeredit;

import java.awt.geom.Rectangle2D;
import jedit.JAbstractEdit;
import jobject.JLayer;
import jobject.JLeaf;
import jobject.JObject;
import jobject.JPage;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *
 * @author takashi
 */
public class JDeleteLayerEdit extends JAbstractEdit{
    private JLayer target;
    private JPage parent;
    private int idx; 
    /** Creates a new instance of JDeleteLayerEdit */
    public JDeleteLayerEdit(JDocumentViewer viewer,JLayer target) {
        super(viewer);
        this.target=target;
        this.parent=target.getParent();
        idx=parent.indexOf(target);
        presentationName="レイヤー削除";
        redo();
    }
    public void redo(){
        canUndo=true;
        canRedo=false;
        JRequest req=viewer.getCurrentRequest();
        JEnvironment env=viewer.getEnvironment();
        clearSelection(target,req);
        parent.remove(target);
        Rectangle2D r=target.getBounds();
        if (r!=null)
            env.addClip(r);
        
    }
    public void undo(){
        canUndo=false;
        canRedo=true;
        JRequest req=viewer.getCurrentRequest();
        JEnvironment env=viewer.getEnvironment();
        parent.add(idx,target);
        Rectangle2D r=target.getBounds();
        if(r!=null)
            env.addClip(r);
    }
    private void clearSelection(JLeaf leaf,JRequest req){
        if (leaf instanceof JObject){
           JObject jo=(JObject)leaf;
           for (int i=0;i<jo.size();i++){
               clearSelection(jo.get(i),req);
           }
        }
        req.remove(leaf);
        
    }
}
