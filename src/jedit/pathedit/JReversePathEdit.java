/*
 * JReversePathEdit.java
 *
 * Created on 2007/09/21, 8:26
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
public class JReversePathEdit extends JAbstractEdit{
    private JPathObject target;
    private JSimplePath targetPath;
    /** Creates a new instance of JReversePathEdit */
    public JReversePathEdit(JDocumentViewer view,JPathObject target,JSimplePath targetPath) {
        super(view);
        this.target=target;
        this.targetPath=targetPath;
        presentationName="ƒpƒX‹t“]";
        redo();
    }
    public void redo(){
        super.redo();
        JEnvironment env=viewer.getEnvironment();
        env.addClip(target.getBounds());
        Point2D p1,p2;
        int ix=targetPath.size()-1;
        for (int i=0;i<targetPath.size();i++){
            JSegment seg=targetPath.remove(ix);
            p1=seg.getControl1();
            p2=seg.getControl2();
            seg.setControl1(p2);
            seg.setControl2(p1);
            targetPath.add(i,seg);
        }
        target.updatePath();
    }
    public void undo(){
        super.undo();
                super.redo();
        JEnvironment env=viewer.getEnvironment();
        env.addClip(target.getBounds());
        Point2D p1,p2;
        int ix=targetPath.size()-1;
        for (int i=0;i<targetPath.size();i++){
            JSegment seg=targetPath.remove(ix);
            p1=seg.getControl1();
            p2=seg.getControl2();
            seg.setControl1(p2);
            seg.setControl2(p1);
            targetPath.add(i,seg);
        } 
        target.updatePath();
    }
}
