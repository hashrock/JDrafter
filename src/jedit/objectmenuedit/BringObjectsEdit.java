/*
 * BringObjectsEdit.java
 *
 * Created on 2007/12/13, 11:52
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit.objectmenuedit;

import java.util.Vector;
import javax.swing.undo.CompoundEdit;
import jedit.JAbstractEdit;
import jobject.JLeaf;
import jscreen.JDocumentViewer;
import jscreen.JRequest;

/**
 *
 * @author i002060
 */
public class BringObjectsEdit extends JAbstractEdit{
    private Vector<JLeaf> targets;
    private int mode;
    private CompoundEdit cEdit=null;
    /** Creates a new instance of BringObjectsEdit */
    public BringObjectsEdit(JDocumentViewer viewer,Vector<JLeaf> targets,int mode) {
        super(viewer);
        this.mode=mode;
        this.targets=targets;
        switch (mode){
            case BringObjectEdit.BRING_TO_TOP:presentationName="最前面に移動";break;
            case BringObjectEdit.BRING_FRONT:presentationName="ひとつ前面に";break;
            case BringObjectEdit.SEND_TO_BOTTOM:presentationName="最背面に移動";break;
            case BringObjectEdit.SEND_BACK:presentationName="ひとつ背面に";break;
        }
        redo();
    }
    public void redo(){
        canUndo=true;
        canRedo=false;
        if (cEdit==null){
            cEdit=new CompoundEdit();
            for (int i=0;i<targets.size();i++){
                cEdit.addEdit(new BringObjectEdit(viewer,targets.get(i),mode));
            }
            cEdit.end();
        }else{
            cEdit.redo();
        }
    }
    public void undo(){
        canRedo=true;
        canUndo=false;
        cEdit.undo();
        JRequest req=viewer.getCurrentRequest();
        req.clear();
        for (int i=0;i<targets.size();i++){
            req.add(targets.get(i));
        }
    }
}
