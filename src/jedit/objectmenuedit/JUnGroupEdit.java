/*
 * JUnGroupEdit.java
 *
 * Created on 2007/09/17, 13:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit.objectmenuedit;

import jedit.*;
import jobject.JGroupObject;
import jobject.JObject;
import jscreen.JDocumentViewer;
import jscreen.JRequest;

/**
 *
 * @author TI
 */
public class JUnGroupEdit extends JAbstractEdit{
    JObject parent;
    JGroupObject saved;
    int indexat;
    
    /** Creates a new instance of JUnGroupEdit */
    public JUnGroupEdit(JDocumentViewer viewer,JGroupObject go) {
        super(viewer);
        saved=go;
        parent=go.getParent();
        indexat=parent.indexOf(go);
        presentationName="ÉOÉãÅ[Évâèú";
        redo();
    }
    public void redo(){
        super.redo();
        int idx=indexat;
        parent.remove(saved);
        JRequest req=viewer.getCurrentRequest();
        req.remove(saved);
        for(int i=0;i<saved.size();i++){
            parent.add(idx++,saved.get(i));
            req.add(saved.get(i));
        }
    }
    public void undo(){
        super.undo();
        JRequest req=viewer.getCurrentRequest();
        for (int i=saved.size()-1;i>=0;i--){
            parent.remove(saved.get(i));
            saved.get(i).setParent(saved);
            req.remove(saved.get(i));
        }
        req.add(saved);
        parent.add(indexat,saved);
    }
}
