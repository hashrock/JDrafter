/*
 * JCutterTool.java
 *
 * Created on 2007/09/25, 11:44
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jtools;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Vector;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import jedit.pathedit.JCutPathEdit;
import jedit.pathedit.JCutPathsEdit;
import jgeom.JComplexPath;
import jgeom.JIntersect;
import jgeom.JSimplePath;
import jobject.JPathObject;
import jscreen.JDragPane;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *
 * @author i002060
 */
public class JCutterTool extends JAbstractTool{
    Line2D line;
    //Stroke stroke=new BasicStroke(1f);
    /** Creates a new instance of JCutterTool */
    public JCutterTool(JDragPane dpane) {
        super(dpane);
        dragPane.setPaintRect(false);
        presentationName="カッターツール";
        getRequest().setSelectionMode(JRequest.DIRECT_MODE);
        line=null;
    }
    public void paint(Graphics2D g){
        JEnvironment env=getEnvironment();
        if (dragPane.isDragging() && line !=null){
           // g.setStroke(stroke);
            g.draw(env.getToScreenTransform().createTransformedShape(line));
        }
        super.paint(g);
        
    }
    public void mouseMoved(MouseEvent e){
        JEnvironment env=getEnvironment();
        JRequest req=getRequest();
        Point2D p=new Point2D.Double();
        env.getToAbsoluteTransform().transform(e.getPoint(),p);
        req.hitObjects.clear();
        req.hitResult=req.HIT_NON;
        getViewer().getCurrentPage().hitByPoint(env,req,p);
    }
    public void mouseDragged(MouseEvent e){
        Point2D p=new Point2D.Double();
        JEnvironment env=getEnvironment();
        env.getToAbsoluteTransform().transform(e.getPoint(),p);
        if (line==null) line=new Line2D.Double();
        line.setLine(dragPane.getStartPoint(),p);
    }
    public void mouseReleased(MouseEvent e){
        JEnvironment env=getEnvironment();
        JRequest req=getRequest();
        Point2D p=new Point2D.Double();
        env.getToAbsoluteTransform().transform(e.getPoint(),p);
        Vector vec=(Vector)req.getSelectedVector().clone();
        Vector<JPathObject> targets=new Vector<JPathObject>();
        Vector<JSimplePath> targetPath=new Vector<JSimplePath>();
        Vector<Vector<JSimplePath>> results=new Vector<Vector<JSimplePath>>();
        for (int i=0;i<vec.size();i++){
            Object o=vec.get(i);
            if (o instanceof JPathObject){
                JPathObject jp=(JPathObject)o;
                JComplexPath cpath=jp.getPath();
                for (int j=0;j<cpath.size();j++){
                    Vector<JSimplePath> result=JIntersect.cutPath(dragPane.getStartPoint(),p,cpath.get(j));
                    if (result.size()>1 || result.get(0).size() != cpath.get(j).size()){
                        targets.add(jp);
                        targetPath.add(cpath.get(j));
                        results.add(result);
                    }
                }
            }
        }
        if (!targets.isEmpty()){
            UndoableEdit anEdit=new JCutPathsEdit(getViewer(),targets,targetPath,results);
            getViewer().getDocument().fireUndoEvent(anEdit);
        }
        dragPane.repaint();
    }
    public void changeCursor() {
        JRequest req=getRequest();
        JCursor jc=dragPane.getJCursor();
        if (req.hitResult==req.HIT_OBJECT){
            setCursor(jc.CUTTER_ON_OBJECT);
        }else{
            setCursor(jc.CUTTER);
        }    
    }
    public void wakeup(){
        dragPane.setPaintRect(false);
        getRequest().setSelectionMode(JRequest.DIRECT_MODE);
        line=null;
    }
    
}
