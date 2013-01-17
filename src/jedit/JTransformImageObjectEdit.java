/*
 * JTransformTextBoxEdit.java
 *
 * Created on 2007/10/24, 13:44
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit;

import java.awt.geom.AffineTransform;
import jobject.JImageObject;
import jobject.JTextObject;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;

/**
 *
 * @author i002060
 */
public class JTransformImageObjectEdit extends JAbstractEdit{
    private JImageObject target;
    private AffineTransform transform;
    /** Creates a new instance of JTransformTextBoxEdit */
    public JTransformImageObjectEdit(JDocumentViewer viewer,JImageObject target,AffineTransform transform) {
        super(viewer);
        this.target=target;
        this.transform=transform;
        if (transform.getType()==transform.TYPE_TRANSLATION){
            presentationName="オブジェクトの移動";
        }else{
            presentationName="オブジェクトの変形";
        }
        redo();
    }
    public void redo(){
        super.redo();
        JEnvironment env=viewer.getEnvironment();
        env.addClip(target.getBounds());
        target.addTransform(transform);
        env.addClip(target.getBounds());
    }
    public void undo(){
        super.undo();
        JEnvironment env=viewer.getEnvironment();
        env.addClip(target.getBounds());
        try{
            target.addTransform(transform.createInverse());
        }catch(Exception e){
            e.printStackTrace();
        }
        env.addClip(target.getBounds());
    }
    
}
