/*
 * JPathTextPositionChangeEdit.java
 *
 * Created on 2007/11/05, 10:41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit.textobjectedit;

import jedit.*;
import jobject.JPathTextObject;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;

/**
 *
 * @author i002060
 */
public class JPathTextPositionChangeEdit extends JAbstractEdit{
    private JPathTextObject target;
    private float oldPosition;
    private float newPosition;
    /** Creates a new instance of JPathTextPositionChangeEdit */
    public JPathTextPositionChangeEdit(JDocumentViewer jv,JPathTextObject target,float newPosition) {
        super(jv);
        oldPosition=target.getStartPosition();
        this.newPosition=newPosition;
        this.target=target;
        presentationName="パステキスト開始点移動";
        redo();
        
    }
    public void redo(){
        super.redo();
        JEnvironment env=viewer.getEnvironment();
        env.addClip(target.getBounds());
        target.setStartPosition(newPosition);
        env.addClip(target.getBounds());
        target.updatePath();
    }
    public void undo(){
        super.undo();
        JEnvironment env=viewer.getEnvironment();
        env.addClip(target.getBounds());
        target.setStartPosition(oldPosition);
        env.addClip(target.getBounds());
        target.updatePath();
    }
}
