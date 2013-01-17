/*
 * JStrokeOutlineEdit.java
 *
 * Created on 2007/12/26, 15:22
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit.effect;

import java.awt.Shape;
import jedit.JAbstractEdit;
import jgeom.JComplexPath;
import jgeom.JPathIterator;
import jobject.JPathObject;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *
 * @author i002060
 */
public class JStrokeOutlineEdit extends JAbstractEdit{
    JPathObject target;
    JComplexPath savedPath;
    JComplexPath newPath;
    /** Creates a new instance of JStrokeOutlineEdit */
    public JStrokeOutlineEdit(JDocumentViewer viewer,JPathObject target) {
        super(viewer);
        this.target=target;
        this.savedPath=target.getPath();
        Shape s=target.getStroke().createStrokedShape(savedPath.getShape());
        newPath=(new JPathIterator(s.getPathIterator(null))).getJPath();
        presentationName="ストロークアウトライン";
        redo();
    }
    public void redo(){
        super.redo();
        JEnvironment env=viewer.getEnvironment();
        JRequest req=viewer.getCurrentRequest();
        env.addClip(target.getBounds());
        target.setPath(newPath);
        env.addClip(target.getBounds());
        req.add(target);
    }
    public void undo(){
        super.undo();
        JEnvironment env=viewer.getEnvironment();
        JRequest req=viewer.getCurrentRequest();
        env.addClip(target.getBounds());
        target.setPath(savedPath);
        env.addClip(target.getBounds());
        req.add(target);       
    }
}
