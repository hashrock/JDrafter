/*
 * JMergePathEdit.java
 *
 * Created on 2007/09/19, 16:27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit.pathedit;

import java.awt.geom.Point2D;
import jedit.*;
import jgeom.JSegment;
import jgeom.JSimplePath;
import jobject.JPathObject;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;

/**
 *
 * @author i002060
 */
public class JMergePathEdit extends JAbstractEdit {
    JPathObject target;
    JSimplePath targetPath;
    JSegment targetSeg;
    JSimplePath insertPath;
    boolean isReverce;
    /** Creates a new instance of JMergePathEdit */
    public JMergePathEdit(JDocumentViewer view,JPathObject target,JSimplePath targetPath,JSegment targetSeg,JSimplePath insertPath) {
        super(view);
        this.target=target;
        this.targetPath=targetPath;
        this.targetSeg=targetSeg;
        this.insertPath=insertPath;
        presentationName=viewer.getDragPane().getDragger().presentationName();
        isReverce= (targetPath.indexOf(targetSeg)==targetPath.size()-1);
        redo();
    }
    public void redo(){
        super.redo();
        JEnvironment env=viewer.getEnvironment();
        env.addClip(target.getBounds());
        if (isReverce){
            int sz=targetPath.size()-1;
            for (int i=0;i<targetPath.size();i++){
                JSegment seg=targetPath.remove(sz);
                Point2D p1=seg.getControl1();
                Point2D p2=seg.getControl2();
                seg.setControl1(p2);
                seg.setControl2(p1);
                targetPath.add(i,seg);
            }
        }
        for (int i=0;i<insertPath.size();i++){
            targetPath.add(i,insertPath.get(i));
        }
        target.updatePath();
        env.addClip(target.getBounds());
        
    }
    public void undo(){
        super.undo();
        JEnvironment env=viewer.getEnvironment();
        env.addClip(target.getBounds());
        for (int i=0;i<insertPath.size();i++){
            targetPath.remove(insertPath.get(i));
        }
        if (isReverce){
            int sz=targetPath.size()-1;
            for (int i=0;i<targetPath.size();i++){
                JSegment seg=targetPath.remove(sz);
                Point2D p1=seg.getControl1();
                Point2D p2=seg.getControl2();
                seg.setControl1(p2);
                seg.setControl2(p1);
                targetPath.add(i,seg);
            }
        }
        target.updatePath();
        env.addClip(target.getBounds());
        
    }
}
