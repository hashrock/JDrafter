/*
 * JRemoveSegmentEdit.java
 *
 * Created on 2007/09/19, 14:47
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit.pathedit;

import jedit.*;
import jgeom.JSegment;
import jgeom.JSimplePath;
import jobject.JObject;
import jobject.JPathObject;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;

/**
 *
 * @author i002060
 */
public class JRemoveSegmentEdit extends JAbstractEdit{
    private JObject targetOwner;
    private JPathObject target;
    private JSegment segment;
    private JSimplePath ownerPath;
    private boolean isLooped;
    private int objectIndex;
    private int pathIndex;
    private int segIndex;
    /** Creates a new instance of JRemoveSegmentEdit */
    public JRemoveSegmentEdit(JDocumentViewer view,JPathObject target,JSegment seg) {
        super(view);
        targetOwner=target.getParent();
        this.target=target;
        this.segment=seg;
        this.ownerPath=target.getPath().getOwnerPath(seg);
        isLooped=ownerPath.isLooped();
        objectIndex=targetOwner.indexOf(target);
        pathIndex=target.getPath().indexOf(ownerPath);
        segIndex=ownerPath.indexOf(segment);
        presentationName=viewer.getDragPane().getDragger().presentationName();
        redo();
    }
    public void redo(){
        super.redo();
        JEnvironment env=viewer.getEnvironment();
        env.addClip(target.getBounds());
        ownerPath.remove(segment);
        if (ownerPath.size()==0){
            target.getPath().remove(ownerPath);
            if (target.getPath().size()==0){
                targetOwner.remove(target);
            }
        }
        if (ownerPath.size()<3){
            ownerPath.setLooped(false);
        }
        target.updatePath();
    }
    public void undo(){
        super.undo();
        JEnvironment env=viewer.getEnvironment();
        ownerPath.add(segIndex,segment);
        ownerPath.setLooped(isLooped);
        if (ownerPath.size()==1){
            target.getPath().add(pathIndex,ownerPath);
            if (target.getPath().size()==1){
                targetOwner.add(objectIndex,target);
            }
        }
        target.updatePath();
        env.addClip(target.getBounds());
        
    }
    
    
}
