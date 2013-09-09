/*
 * JTransformTextBoxEdit.java
 *
 * Created on 2007/10/24, 13:44
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit.textobjectedit;

import java.awt.geom.AffineTransform;
import jedit.*;
import jobject.JTextObject;
import jpaint.JPaint;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;

/**
 *
 * @author i002060
 */
public class JTransformTextBoxEdit extends JAbstractEdit{
    private JTextObject target;
    private AffineTransform transform;
    /** Creates a new instance of JTransformTextBoxEdit */
    public JTransformTextBoxEdit(JDocumentViewer viewer,JTextObject target,AffineTransform transform) {
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
        JPaint jp=target.getFillPaint();
        if (jp!=null && jp.getPaintMode()!=JPaint.COLOR_MODE){
            jp.transform(transform);
        }
        target.updatePath();
        env.addClip(target.getBounds());
    }
    public void undo(){
        super.undo();
        JEnvironment env=viewer.getEnvironment();
        env.addClip(target.getBounds());
        try{
            AffineTransform inv=transform.createInverse();
            target.addTransform(inv);
            JPaint jp=target.getFillPaint();
            if (jp !=null && jp.getPaintMode()==JPaint.COLOR_MODE){
                jp.transform(inv);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        target.updatePath();
        env.addClip(target.getBounds());
    }
    
}
