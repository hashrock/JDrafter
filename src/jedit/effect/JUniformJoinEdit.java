/*
 * JUniformJoinEdit.java
 *
 * Created on 2007/12/21, 9:23
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit.effect;

import java.awt.geom.Point2D;
import javax.swing.undo.CompoundEdit;
import jedit.JAbstractEdit;
import jedit.pathedit.JRemovePathEdit;
import jedit.pathedit.JReplacePathEdit;
import jgeom.JSegment;
import jgeom.JSimplePath;
import jobject.JPathObject;
import jscreen.JDocumentViewer;
import jscreen.JRequest;

/**
 *
 * @author i002060
 */
public class JUniformJoinEdit extends JAbstractEdit{
    private JPathObject sourceObj=null;
    private JSimplePath sourcePath=null;
    private JSegment sourceSeg=null;
    private JPathObject targetObj=null;
    private JSimplePath targetPath=null;
    private JSegment targetSeg=null;
    private JSimplePath createdPath=null;
    private CompoundEdit cEdit=null;
    
    /** Creates a new instance of JUniformJoinEdit */
    public JUniformJoinEdit(JDocumentViewer viewer,JPathObject targetObj,JSegment targetSeg,
            JPathObject sourceObj,JSegment sourceSeg) {
        super(viewer);
        this.targetObj=targetObj;
        this.targetPath=targetObj.getPath().getOwnerPath(targetSeg);
        this.targetSeg=targetSeg;
        this.sourceObj=sourceObj;
        this.sourcePath=sourceObj.getPath().getOwnerPath(sourceSeg);
        this.sourceSeg=sourceSeg;
        createdPath=makePath();
        presentationName="•½‹Ï‰»‚µ‚Ä˜AŒ‹";
        redo();
        
    }
    public void redo(){
        canUndo=true;
        canRedo=false;
        JRequest req=viewer.getCurrentRequest();
        if (cEdit==null){
            cEdit=new CompoundEdit();
            if (targetPath !=sourcePath){
                cEdit.addEdit(new JRemovePathEdit(viewer,sourceObj,sourcePath));
            }
            cEdit.addEdit(new JReplacePathEdit(viewer,targetObj,targetPath,createdPath));
            
            cEdit.end();
        }else{
            cEdit.redo();
        }
        req.clear();
        req.add(targetObj);
        if (sourceObj.getParent() !=null && sourceObj.getParent().contains(sourceObj))
            req.add(sourceObj);
    }
    public void undo(){
        canRedo=true;
        canUndo=false;
        JRequest req=viewer.getCurrentRequest();
        cEdit.undo();
        req.clear();
        req.add(targetObj);
        req.add(targetPath);
        req.add(targetSeg);
        req.add(sourceObj);
        req.add(sourcePath);
        req.add(sourceSeg);
    }
    private JSimplePath makePath(){
        JSimplePath ret=targetPath.clone();
        JSegment seg1=null,seg2=null;
        if (targetPath==sourcePath){
            seg1=ret.get(0);
            seg2=ret.get(ret.size()-1);
        }else{
            if (targetPath.indexOf(targetSeg)==0){
                for (int i=0;i<ret.size();i++){
                    JSegment sg=ret.remove(ret.size()-1);
                    Point2D p1=sg.getControl1();
                    Point2D p2=sg.getControl2();
                    sg.setControl1(p2);sg.setControl2(p1);
                    ret.add(i,sg);
                }
            }
            seg1=ret.get(ret.size()-1);
            JSimplePath sp=sourcePath.clone();
            if (sourcePath.indexOf(sourceSeg) !=0){
                for (int i=0;i<sp.size();i++){
                    JSegment sg=sp.remove(sp.size()-1);
                    Point2D p1=sg.getControl1();
                    Point2D p2=sg.getControl2();
                    sg.setControl1(p2);
                    sg.setControl2(p1);
                    sp.add(i,sg);
                }
            }
            for (int i=0;i<sp.size();i++){
                ret.add(sp.get(i));
            }
            seg2=sp.get(0);
        }
        segNormalize(seg1);
        segNormalize(seg2);
        segUniform(seg1,seg2);
        ret.remove(seg2);
        if (targetPath == sourcePath){
            ret.setLooped(true);
        }
        return ret;
    }
    private void segNormalize(JSegment seg){
        if (seg.getControl1()==null){
            seg.setControl1(seg.getAncur());
        }
        if (seg.getControl2()==null){
            seg.setControl2(seg.getAncur());
        }
    }
    private void segUniform(JSegment seg1,JSegment seg2){
        Point2D ap1=seg1.getAncur();
        Point2D ap2=seg2.getAncur();
        double dx=(ap2.getX()-ap1.getX())/2;
        double dy=(ap2.getY()-ap1.getY())/2;
        Point2D cp1=seg1.getControl1();
        Point2D cp2=seg2.getControl1();
        double dpx=((cp1.getX()-ap1.getX())+(cp2.getX()-ap2.getX()))/2;
        double dpy=((cp1.getY()-ap1.getY())+(cp2.getY()-ap2.getY()))/2;
        seg1.setControl1(ap1.getX()+dpx,ap1.getY()+dpy);
        seg2.setControl1(ap2.getX()+dpx,ap2.getY()+dpy);
        cp1=seg1.getControl2();
        cp2=seg2.getControl2();
        dpx=((cp1.getX()-ap1.getX())+(cp2.getX()-ap2.getX()))/2;
        dpy=((cp1.getY()-ap1.getY())+(cp2.getY()-ap2.getY()))/2;
        seg1.setControl2(ap1.getX()+dpx,ap1.getY()+dpy);
        seg2.setControl2(ap2.getX()+dpx,ap2.getY()+dpy);
        moveSeg(seg1,dx,dy);
        moveSeg(seg2,-dx,-dy);
        if (seg1.getAncur().equals(seg1.getControl1())) seg1.setControl1(null);
        if (seg1.getAncur().equals(seg1.getControl2())) seg1.setControl2(null);
        if (seg2.getAncur().equals(seg2.getControl1())) seg2.setControl1(null);
        if (seg2.getAncur().equals(seg2.getControl2())) seg2.setControl2(null);
    }
    public void moveSeg(JSegment seg,double dx,double dy){
        Point2D p=seg.getAncur();
        seg.setAncur(p.getX()+dx,p.getY()+dy);
        p=seg.getControl1();
        seg.setControl1(p.getX()+dx,p.getY()+dy);
        p=seg.getControl2();
        seg.setControl2(p.getX()+dx,p.getY()+dy);
    }
}
