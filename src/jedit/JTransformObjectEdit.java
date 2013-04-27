/*
 * JTransformObjectEdit.java
 *
 * Created on 2007/09/03, 15:06
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import jobject.JPathObject;
import jpaint.JPaint;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;

/**
 *
 * @author i002060
 */
public class JTransformObjectEdit extends JAbstractEdit{
    private JPathObject target;
    private AffineTransform transform;
    /** Creates a new instance of JTransformObjectEdit */
    public JTransformObjectEdit(JDocumentViewer v,JPathObject target,AffineTransform transform) {
        super(v);
        this.target=target;
        this.transform=(AffineTransform)transform.clone();
        if (transform.getType()==transform.TYPE_TRANSLATION){
            presentationName="オブジェクトの移動";
        }else{
            presentationName="オブジェクトの変形";
        }
        redo();
    }
    public void redo() throws CannotRedoException{
        super.redo();
        JEnvironment env=viewer.getEnvironment();
        env.addClip(target.getBounds());
        target.transformPath(transform);
        JPaint pf=target.getFillPaint();
        if (pf !=null && pf.getPaintMode() !=JPaint.COLOR_MODE){
            pf.transform(transform);
        }
        env.addClip(target.getBounds());
        target.updatePath();
    }
    public void undo() throws CannotUndoException{
        super.undo();
        JEnvironment env=viewer.getEnvironment();
        env.addClip(target.getBounds());
        AffineTransform inv=null;
        try{
            inv=transform.createInverse();
        }catch(NoninvertibleTransformException e){
            return;
        }
        target.transformPath(inv);
        JPaint pf=target.getFillPaint();
        if (pf !=null && pf.getPaintMode() !=JPaint.COLOR_MODE){
            pf.transform(inv);
        }
        env.addClip(target.getBounds());
        target.updatePath();
    }
    
}
