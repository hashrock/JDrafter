/*
 * JRotateObjectEdit.java
 *
 * Created on 2007/09/15, 16:57
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import jobject.JPathObject;
import jpaint.JPaint;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;

/**
 *
 * @author TI
 */
public class JRotateObjectEdit extends JAbstractEdit {
    private JPathObject target;
    private AffineTransform transform;
    private double rotation;
    /** Creates a new instance of JRotateObjectEdit */
    public JRotateObjectEdit(JDocumentViewer v,JPathObject target,AffineTransform transform,double rot) {
        super(v);
        this.target=target;
        this.transform=transform;
        rotation=rot;
        presentationName="オブジェクトの回転";
        redo();
    }
    public void redo(){
        super.redo();
        JEnvironment env=viewer.getEnvironment();
        env.addClip(target.getBounds());
        target.transformPath(transform);
        env.addClip(target.getBounds());
        target.addTotalRotate(rotation);
        JPaint jp=target.getFillPaint();
        if (jp != null && jp.getPaintMode()!=JPaint.COLOR_MODE){
            jp.transform(transform);
        }
        target.updatePath();
    }
    public void undo(){
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
        JPaint jp=target.getFillPaint();
        if (jp != null && jp.getPaintMode()!=JPaint.COLOR_MODE){
            jp.transform(inv);
        }
        env.addClip(target.getBounds());
        target.addTotalRotate(-rotation);
        target.updatePath();
    }
    
}