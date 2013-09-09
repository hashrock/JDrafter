/*
 * JTextOutlineEdit.java
 *
 * Created on 2007/12/26, 11:28
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit.effect;

import jedit.JAbstractEdit;
import jgeom.JPathIterator;
import jobject.JLeaf;
import jobject.JObject;
import jobject.JPathObject;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;
import jscreen.JRequest;
import jobject.JText;

/**
 *
 * @author i002060
 */
public class JTextOutlineEdit extends JAbstractEdit{
    private JText target=null;
    private JPathObject createdObject=null;
    /** Creates a new instance of JTextOutlineEdit */
    public JTextOutlineEdit(JDocumentViewer viewer,JText target) {
        super(viewer);
        presentationName="テキストアウトライン";
        this.target=target;
        JLeaf tg=(JLeaf)target;
        createdObject=new JPathObject(tg.getShape());
        createdObject.setFillPaint(tg.getFillPaint());
        createdObject.setStrokePaint(tg.getStrokePaint());
        createdObject.setEffector(tg.getEffector().clone());
        redo();
    }
    @Override
    public void redo(){
        super.redo();
        JEnvironment env=viewer.getEnvironment();
        JRequest req=viewer.getCurrentRequest();
        JLeaf tg=(JLeaf)target;
        JObject parent=tg.getParent();
        int idx=parent.indexOf(tg);
        env.addClip(tg.getBounds());
        parent.remove(tg);
        req.remove(tg);
        parent.add(idx,createdObject);
        env.addClip(createdObject.getBounds());
        req.add(createdObject);
    }
    @Override
    public void undo(){
        super.undo();
        JEnvironment env=viewer.getEnvironment();
        JRequest req=viewer.getCurrentRequest();
        JLeaf tg=(JLeaf)target;
        JObject parent=createdObject.getParent();
        int idx=parent.indexOf(createdObject);
        env.addClip(createdObject.getBounds());
        parent.remove(createdObject);
        req.remove(createdObject);
        parent.add(idx,tg);
        env.addClip(tg.getBounds());
        req.add(tg);        
    }
    
}
