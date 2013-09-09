/*
 * JTransformControlEdit.java
 *
 * Created on 2007/09/03, 15:20
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit.pathedit;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;
import jedit.*;
import jgeom.JSegment;
import jobject.JPathObject;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *
 * @author i002060
 */
public class JTransformControlEdit extends JAbstractEdit{
    private JSegment target;
    private JPathObject obj;
    private Point2D cAnc,cCtl1,cCtl2,oAnc,oCtl1,oCtl2;
    private boolean newJoin,oldJoin;

    /** Creates a new instance of JTransformControlEdit */
    public JTransformControlEdit(JDocumentViewer v,JPathObject obj,JSegment target,JSegment newSeg) {
        super(v);
        this.obj=obj;
        this.target=target;
        oAnc=target.getAncur();
        oCtl1=target.getControl1();
        oCtl2=target.getControl2();
        cAnc=newSeg.getAncur();
        cCtl1=newSeg.getControl1();
        cCtl2=newSeg.getControl2();
        oldJoin=target.isJoined();
        newJoin=newSeg.isJoined();
        presentationName="パスの編集";
        redo();
    }
    public void redo() throws CannotRedoException{
        super.redo();
        JEnvironment env=viewer.getEnvironment();
        Rectangle2D rc=obj.getBounds();
        env.addClip(rc);
        target.setControl1(cCtl1);
        target.setControl2(cCtl2);
        target.setJoined(newJoin);
        obj.updatePath();
        env.addClip(obj.getBounds());
        
    }
    public void undo() throws CannotUndoException{
        super.undo();
        JEnvironment env=viewer.getEnvironment();
        env.addClip(obj.getBounds());
        target.setControl1(oCtl1);
        target.setControl2(oCtl2);
        target.setJoined(oldJoin);
        obj.updatePath();
        env.addClip(obj.getBounds());
        
    }
    
}
