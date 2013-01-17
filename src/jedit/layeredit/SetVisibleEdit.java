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
public class SetVisibleEdit extends JAbstractEdit{
    private JLeaf target;
    private boolean visible;
    /** Creates a new instance of SetVisibleEdit */
    public SetVisibleEdit(JDocumentViewer viewer,JLeaf target,boolean visible) {
        super(viewer);
        this.target=target;
        this.visible=visible;
        if (visible){
            presentationName="オブジェクの表示";
        }else{
            presentationName="オブジェクトの非表示";
        }
        redo();
    }
    public void redo(){
        canUndo=true;
        canRedo=false;
        JRequest req=viewer.getCurrentRequest();
        JEnvironment env=viewer.getEnvironment();
        clearRequest(target,req);
        target.setVisible(visible);
        Rectangle2D r=target.getBounds();
        if (r!=null)
            env.addClip(target.getBounds());
    }
    public void undo(){
        canUndo=false;
        canRedo=true;
        JEnvironment env=viewer.getEnvironment();
        target.setVisible(!visible);
        Rectangle2D r=target.getBounds();
        if (r!=null)
            env.addClip(target.getBounds());
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
