/*
 * BringObjectEdit.java
 *
 * Created on 2007/12/13, 10:58
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit.objectmenuedit;

import java.util.Vector;
import jedit.JAbstractEdit;
import jobject.JLeaf;
import jobject.JObject;
import jscreen.JDocumentViewer;

/**
 *
 * @author i002060
 */
public class BringObjectEdit extends JAbstractEdit{
    public static final int BRING_TO_TOP=1;
    public static final int BRING_FRONT=2;
    public static final int SEND_TO_BOTTOM=3;
    public static final int SEND_BACK=4;
    private int type;
    private int savedIndex;
    private JLeaf target;
    /**
     * Creates a new instance of BringObjectEdit
     */
    public BringObjectEdit(JDocumentViewer viewer,JLeaf target,int type) {
        super(viewer);
        this.target=target;
        this.type=type;
        this.savedIndex=target.getParent().indexOf(target);
        switch (type){
            case BRING_TO_TOP:presentationName="最前面に移動";break;
            case BRING_FRONT:presentationName="ひとつ前面に";break;
            case SEND_TO_BOTTOM:presentationName="最背面に移動";break;
            case SEND_BACK:presentationName="ひとつ背面に";break;
        }
        redo();
    }
    @Override
    public void redo(){
        canUndo=true;
        canRedo=false;
        JObject parent=target.getParent();      
        parent.remove(target);
        switch (type){
            case BRING_TO_TOP: parent.add(target);break;
            case BRING_FRONT: parent.add(Math.min(parent.size(),savedIndex+1),target);break;
            case SEND_TO_BOTTOM: parent.add(0,target);break;
            case SEND_BACK: parent.add(Math.max(0,savedIndex-1),target);break;
        }
        viewer.getEnvironment().addClip(target.getBounds());
    }
    @Override
    public void undo(){
        canUndo=false;
        canRedo=true;
        JObject parent=target.getParent();
        parent.remove(target);
        parent.add(savedIndex,target);
        viewer.getEnvironment().addClip(target.getBounds());
    }
}
