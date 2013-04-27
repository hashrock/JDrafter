/*
 * BringToIndexEdit.java
 *
 * Created on 2008/05/09, 21:30
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
import jscreen.JRequest;

/**
 *
 * @author takashi
 */
public class BringToIndexEdit extends JAbstractEdit{
    private JObject savedParent,newParent;
    private int savedIndex,newIndex;
    private JLeaf target;
    private JObject savedAnscester;
    private int savedAnscesterIndex;
    
    /** Creates a new instance of BringToIndexEdit */
    public BringToIndexEdit(JDocumentViewer viewer,JLeaf target,JObject newParent,int newIndex) {
        super(viewer);
        this.target=target;
        this.newParent=newParent;
        savedIndex=target.getParent().indexOf(target);
        this.newIndex=newIndex;
        savedParent=target.getParent();
        if (savedParent==newParent && newIndex>savedIndex){
            this.newIndex--;
        }
        presentationName="ŠK‘w‚Ì•ÏX";
        savedAnscester=getSavedAnscester(target,newParent);
        if (savedAnscester !=null){
            savedAnscesterIndex=savedAnscester.getParent().indexOf(savedAnscester);
        }
        redo();
    }
    public void redo(){
        canUndo=true;
        canRedo=false;
        //JRequest req=viewer.getCurrentRequest();
        //req.clear();
        if (savedAnscester !=null){
            savedAnscester.getParent().remove(savedAnscester);
        }
        savedParent.remove(target);
        if (newIndex !=-1 && newIndex<newParent.size())
            newParent.add(newIndex,target);
        else
            newParent.add(target);
        //req.add(target);
        Rectangle2D bounds=target.getBounds();
        if (bounds !=null)
            viewer.getEnvironment().addClip(bounds);
    }
    public void undo(){
        canUndo=false;
        canRedo=true;
        JRequest req=viewer.getCurrentRequest();
        newParent.remove(target);
        savedParent.add(savedIndex,target);
        if (savedAnscester!=null){
            savedAnscester.getParent().add(savedAnscesterIndex,savedAnscester);
        }
        Rectangle2D bounds=target.getBounds();
        if (bounds !=null)
            viewer.getEnvironment().addClip(bounds);
    }
    private JObject getSavedAnscester(JLeaf thisLeaf,JObject newparent){
        JObject ret=null;
        JObject parent=thisLeaf.getParent();
        while (parent.size()==1 && parent instanceof JObject){
            if (newParent==parent) break;
            ret=parent;
            parent=parent.getParent();
        }
        return ret;
    }
}
