/*
 * JSetSegmentEdit.java
 *
 * Created on 2007/09/19, 16:02
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit.pathedit;

import java.awt.geom.Point2D;
import jedit.*;
import jgeom.JSegment;
import jobject.JPathObject;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;

/**
 *
 * @author i002060
 */
public class JSetSegmentEdit extends JAbstractEdit{
    JPathObject target;
    JSegment targetSeg;
    JSegment savedSeg;
    Point2D ancur,ctl1,ctl2;
    /** Creates a new instance of JSetSegmentEdit */
    public JSetSegmentEdit(JDocumentViewer view,JPathObject target,JSegment seg,Point2D ancur,Point2D ctl1,Point2D ctl2) {
        super(view);
        this.target=target;
        this.targetSeg=seg;
        this.savedSeg=seg.clone();
        this.ancur=ancur;
        this.ctl1=ctl1;
        this.ctl2=ctl2;
        presentationName=viewer.getDragPane().getDragger().presentationName();
        redo();
    }
    public JSetSegmentEdit(JDocumentViewer view,JPathObject target,JSegment seg,JSegment sSeg){
        super(view);
        this.target=target;
        this.targetSeg=seg;
        this.savedSeg=seg.clone();
        this.ancur=sSeg.getAncur();
        this.ctl1=sSeg.getControl1();
        this.ctl2=sSeg.getControl2();
        presentationName=viewer.getDragPane().getDragger().presentationName();
        redo();
    }
    public void redo(){
        super.redo();
        JEnvironment env=viewer.getEnvironment();
        env.addClip(target.getBounds());
        targetSeg.setAncur(ancur);
        targetSeg.setControl1(ctl1);
        targetSeg.setControl2(ctl2);
         target.updatePath();
        env.addClip(target.getBounds());
       
    }
    public void undo(){
        super.undo();
        JEnvironment env=viewer.getEnvironment();
        env.addClip(target.getBounds());
        targetSeg.setAncur(savedSeg.getAncur());
        targetSeg.setControl1(savedSeg.getControl1());
        targetSeg.setControl2(savedSeg.getControl2());
        target.updatePath();
        env.addClip(target.getBounds());
        
    }
}
