/*
 * NewLayerEdit.java
 *
 * Created on 2008/05/23, 22:55
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit.layeredit;

import java.awt.geom.Rectangle2D;
import java.util.Vector;
import jedit.JAbstractEdit;
import jobject.JLayer;
import jobject.JLeaf;
import jobject.JObject;
import jobject.JPage;
import jscreen.JDocumentViewer;
import jscreen.JRequest;

/**
 *
 * @author takashi
 */
public class NewLayerEdit extends JAbstractEdit{
    JLayer target;
    JPage parent;
    /** Creates a new instance of NewLayerEdit */
    public NewLayerEdit(JDocumentViewer viewer,JLayer target,JPage parent,String name) {
        super(viewer);
        this.target=target;
        this.parent=parent;
        presentationName=name;
        redo();
        
    }
    public void redo(){
        canUndo=true;
        canRedo=false;
        parent.add(target);
        Rectangle2D r=target.getBounds();
        if (r!=null){
            viewer.getEnvironment().addClip(r);
        }
    }
    public void undo(){
        canRedo=true;
        canUndo=false;
        JRequest req=viewer.getCurrentRequest();
        clearSelection(target,req);
        parent.remove(target);
        Rectangle2D r=target.getBounds();
        if (r!=null){
            
        }
    }
    private void clearSelection(JLeaf tg,JRequest req){
        if (tg instanceof JObject){
            JObject jo=(JObject)tg;
            for (int i=0;i<jo.size();i++){
                clearSelection(jo.get(i),req);
            }
        }
        req.remove(tg);
    }
}
