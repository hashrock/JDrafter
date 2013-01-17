/*
 * JDeleteSegmentEdit.java
 *
 * Created on 2007/09/05, 21:54
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit.pathedit;

import jactions.JPathDeleteSet;
import java.util.Vector;
import jedit.*;
import jgeom.JSegment;
import jobject.JLeaf;
import jobject.JPathObject;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *
 * @author TI
 */
public class JDeleteSegmentEdit extends JAbstractEdit {
    private Vector<JPathDeleteSet> jEdits;
    /** Creates a new instance of JDeleteSegmentEdit */
    public JDeleteSegmentEdit(JDocumentViewer view,Vector<JPathDeleteSet> e) {
        super(view);
        presentationName="çÌèú";
        jEdits=e;
        redo();
    }
    public void redo(){
        super.redo();
        JEnvironment env=viewer.getEnvironment();
        JRequest req=viewer.getCurrentRequest();
        req.clear();
        for (int i=0;i<jEdits.size();i++){
            env.addClip(jEdits.get(i).getOriginal().getBounds());
            jEdits.get(i).doDelete(viewer.getCurrentRequest());
            Vector<JLeaf> robj=jEdits.get(i).getResults();
            if (robj==null) continue;
            for (int j=0;j<robj.size();j++){
                req.add(robj.get(j));
            }
            
        }
        
    }
    public void undo(){
        super.undo();
        JEnvironment env=viewer.getEnvironment();
        for (int i=jEdits.size()-1;i>=0;i--){
            JLeaf jl=jEdits.get(i).getOriginal();
            env.addClip(jl.getBounds());
            jEdits.get(i).doRestore(viewer.getCurrentRequest());
        }
    }
}
