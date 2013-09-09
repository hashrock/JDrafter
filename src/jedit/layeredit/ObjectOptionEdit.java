/*
 * SetVisibleEdit.java
 *
 * Created on 2008/05/16, 22:41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit.layeredit;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.SortedMap;
import jedit.JAbstractEdit;
import jobject.JLayer;
import jobject.JLeaf;
import jobject.JObject;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *
 * @author takashi
 */
public class ObjectOptionEdit extends JAbstractEdit{
    private JLeaf target;
    private boolean newvisible,oldvisible;
    private boolean newlocked,oldlocked;
    private String newName,oldName;
    
    /** Creates a new instance of SetVisibleEdit */
    public ObjectOptionEdit(JDocumentViewer viewer,JLeaf target,boolean visible,boolean locked,String newName) {
        super(viewer);
        this.target=target;
        newvisible=visible;
        oldvisible=target.isVisible();
        newlocked=locked;
        oldlocked=target.isLocked();
        this.newName=newName;
        this.oldName=target.getName();
        presentationName="オブジェクトオプション";
        redo();
    }
    public void redo(){
        canUndo=true;
        canRedo=false;
        JRequest req=viewer.getCurrentRequest();
        JEnvironment env=viewer.getEnvironment();
        if (newvisible != oldvisible || oldlocked != newlocked)
            clearRequest(target,req);
        target.setVisible(newvisible);
        target.setLocked(newlocked);
        if (!newName.equals(oldName)){
            SortedMap map=target.getPage().getNameTable();
            map.remove(oldName);
            target.setName(newName);
            map.put(newName,target);
        }
        Rectangle2D r=target.getBounds();
        if (r!=null)
            env.addClip(target.getBounds());
    }
    public void undo(){
        canUndo=false;
        canRedo=true;
        JEnvironment env=viewer.getEnvironment();
        target.setVisible(oldvisible);
        target.setLocked(oldlocked);
        Rectangle2D r=target.getBounds();
        if (!newName.equals(oldName)){
            SortedMap map=target.getPage().getNameTable();
            map.remove(newName);
            target.setName(oldName);
            map.put(oldName,target);
        }
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
