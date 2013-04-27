/*
 * JDuplicateObjectEdit.java
 *
 * Created on 2007/09/10, 10:07
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit;

import jobject.JLeaf;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *
 * @author i002060
 */
public class JDuplicateObjectEdit extends JAbstractEdit{
    private JLeaf target;
    private JLeaf duplicateObject;
    /**
     * Creates a new instance of JDuplicateObjectEdit
     */
    public JDuplicateObjectEdit(JDocumentViewer view,JLeaf target) {
        super(view);
        this.target=target;
        duplicateObject=null;
        presentationName="オブジェクトの複製";
        try{
            duplicateObject=(JLeaf)target.clone();
        }catch(CloneNotSupportedException e){
            e.printStackTrace();
        }
        redo();
    }
    public void redo(){
        super.redo();
        JEnvironment env=viewer.getEnvironment();
        JRequest req=viewer.getCurrentRequest();
        env.addClip(target.getBounds());
        int index=target.getParent().indexOf(target);
        target.getParent().add(index,duplicateObject);
    }
    public void undo(){
        super.undo();
        JEnvironment env=viewer.getEnvironment();
        env.addClip(duplicateObject.getBounds());
        target.getParent().remove(duplicateObject);
    }
}
