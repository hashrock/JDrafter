/*
 * JJoinPathEdit.java
 *
 * Created on 2007/12/20, 14:16
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit.effect;

import java.util.Vector;
import javax.swing.undo.CompoundEdit;
import jedit.JAbstractEdit;
import jedit.JDeleteObjectsEdit;
import jedit.pathedit.JLoopPathEdit;
import jedit.pathedit.JMergePathEdit;
import jedit.pathedit.JRemovePathEdit;
import jedit.pathedit.JReversePathEdit;
import jgeom.JSegment;
import jgeom.JSimplePath;
import jobject.JLeaf;
import jobject.JPathObject;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *
 * @author i002060
 */
public class JJoinPathEdit extends JAbstractEdit{
    private JPathObject sourceObj=null;
    private JSimplePath sourcePath=null;
    private JSegment sourceSeg=null;
    private JPathObject targetObj=null;
    private JSimplePath targetPath=null;
    private JSegment targetSeg=null;
    private CompoundEdit cEdit=null;
    /** Creates a new instance of JJoinPathEdit */
    public JJoinPathEdit(JDocumentViewer viewer,JPathObject targetObj,JSegment targetSeg,JPathObject sourceObj,JSegment sourceSeg) {
        super(viewer);
        this.targetObj=targetObj;
        this.targetPath=targetObj.getPath().getOwnerPath(targetSeg);
        this.targetSeg=targetSeg;
        this.sourceObj=sourceObj;
        this.sourcePath=sourceObj.getPath().getOwnerPath(sourceSeg);
        this.sourceSeg=sourceSeg;
        presentationName="ƒpƒX‚Ì˜AŒ‹";
        redo();
    }
    public void redo(){
        canRedo=false;
        canUndo=true;
        JEnvironment env=viewer.getEnvironment();
        JRequest req=viewer.getCurrentRequest();
        if (cEdit==null){
            cEdit=new CompoundEdit();
            Vector<JLeaf> objects=new Vector<JLeaf>();
            objects.add(sourceObj);
            if (sourceObj==targetObj){
                if (sourcePath==targetPath){
                    cEdit.addEdit(new JLoopPathEdit(viewer,targetObj,targetPath,true));
                }else{
                    if (sourcePath.indexOf(sourceSeg)==0){
                        cEdit.addEdit(new JReversePathEdit(viewer,sourceObj,sourcePath));
                    }
                    cEdit.addEdit(new JRemovePathEdit(viewer,targetObj,sourcePath));
                    
                    cEdit.addEdit(new JMergePathEdit(viewer,targetObj,targetPath,targetSeg,sourcePath));
                }
            }else{
                if (sourcePath.indexOf(sourceSeg)==0){
                    cEdit.addEdit(new JReversePathEdit(viewer,sourceObj,sourcePath));
                }
                cEdit.addEdit(new JDeleteObjectsEdit(viewer,objects));
                cEdit.addEdit(new JMergePathEdit(viewer,targetObj,targetPath,targetSeg,sourcePath));
            }
            cEdit.end();
        }else{
            cEdit.redo();
        }
        req.clear();
        req.add(targetObj);
    }
    public void undo(){
        canUndo=false;
        canRedo=true;
        JEnvironment env=viewer.getEnvironment();
        JRequest req=viewer.getCurrentRequest();
        cEdit.undo();
        req.clear();
        req.add(sourceObj);
        req.add(sourcePath);
        req.add(sourceSeg);
        req.add(targetObj);
        req.add(targetPath);
        req.add(targetSeg);
        
    }
    
}
