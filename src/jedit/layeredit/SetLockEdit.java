/*
 * SetVisibleEdit.java
 *
 * Created on 2008/05/16, 22:41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit.layeredit;

import java.awt.geom.Rectangle2D;
import jedit.JAbstractEdit;
import jobject.JLeaf;
import jobject.JObject;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *
 * @author takashi
 */
public class SetLockEdit extends JAbstractEdit{
    private JLeaf target;
    private boolean locked;
    /** Creates a new instance of SetVisibleEdit */
    public SetLockEdit(JDocumentViewer viewer,JLeaf target,boolean lock) {
        super(viewer);
        this.target=target;
        this.locked=lock;
        if (lock){
            presentationName="オブジェクをロック";
        }else{
            presentationName="オブジェクトのロック解除";
        }
        redo();
    }
    public void redo(){
        canUndo=true;
        canRedo=false;
        JRequest req=viewer.getCurrentRequest();
        JEnvironment env=viewer.getEnvironment();
        clearRequest(target,req);
        target.setLocked(locked);
        Rectangle2D r=target.getBounds();
    }
    public void undo(){
        canUndo=false;
        canRedo=true;
        JEnvironment env=viewer.getEnvironment();
        target.setLocked(!locked);
    }
    
    private void clearRequest(JLeaf tg,JRequest req){
        if (tg instanceof JObject){
            JObject jo=(JObject)tg;
            for (int i=0;i<jo.size();i++){
                clearRequest(jo.get(i),req);
            }
        }
        req.remove(tg);
    }
    
}
