/*
 * JRoundCornerTool.java
 *
 * Created on 2007/10/05, 11:45
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jtools;

import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.undo.CompoundEdit;
import jedit.pathedit.JDeleteSegmentEdit;
import jedit.pathedit.JInsertSegmentEdit;
import jedit.pathedit.JRemoveSegmentEdit;
import jedit.pathedit.JSetSegmentEdit;
import jgeom.JPathIterator;
import jgeom.JSimplePath;
import jscreen.JDragPane;

/**
 *
 * @author i002060
 */
public class JRoundCornerTool extends JCutCornerTool{
    
    /** Creates a new instance of JRoundCornerTool */
    public JRoundCornerTool(JDragPane dragPane) {
        super(dragPane);
        presentationName="äpä€ÇﬂÉcÅ[Éã";
    }
    protected void update(Vector<corner> vc){
        double defaultRadius=getEnvironment().DEFAULT_CUTCORNER_RADIUS;
        CompoundEdit cEdit=null;
        for (int i=0;i<vc.size();i++){
            corner cn=vc.get(i);
            Point2D p1=cn.seg1.getAncur();
            Point2D p2=cn.seg2.getAncur();
            Point2D p3=cn.seg3.getAncur();
            double dx=p2.getX();
            double dy=p2.getY();
            double theta1=Math.atan2(p1.getY()-dy,p1.getX()-dx);
            double theta2=Math.atan2(p3.getY()-dy,p3.getX()-dx);
            double theta=(theta2-theta1)/2;
            if (theta==0 || theta==Math.PI){
                return;
            }
            double length=Math.abs(defaultRadius/Math.sin(theta));
            double length1=Math.abs(length*Math.cos(theta));
            Point2D.Double pa1=new Point2D.Double(length1*Math.cos(theta1)+dx,length1*Math.sin(theta1)+dy);
            Point2D.Double pa2=new Point2D.Double(length1*Math.cos(theta2)+dx,length1*Math.sin(theta2)+dy);
            double aTheta=theta+theta1;
            Point2D.Double cp=new Point2D.Double(length*Math.cos(aTheta)+dx,length*Math.sin(aTheta)+dy);
            GeneralPath include=new GeneralPath();
            include.moveTo(p1.getX(),p1.getY());
            include.lineTo(p2.getX(),p2.getY());
            include.lineTo(p3.getX(),p3.getY());
            include.closePath();
            boolean aflag=false;
            if (!include.contains(cp)){
                aTheta+=Math.PI;
                cp.setLocation(length*Math.cos(aTheta)+dx,length*Math.sin(aTheta)+dy);
                if (!include.contains(cp)){
                    JOptionPane.showMessageDialog(getViewer().getScroller(),
                            "ï”ÇÃí∑Ç≥Ç…î‰Ç◊îºåaÇÃílÇ™ëÂÇ´Ç∑Ç¨Ç‹Ç∑.","JDraw",JOptionPane.YES_OPTION);
                    return;
                }
                aflag=true;
            }
            //
            double startAngle=-Math.atan2(pa1.y-cp.y,pa1.x-cp.x)*180/Math.PI;
            if (startAngle<0) startAngle+=360;
            double endAngle=-Math.atan2(pa2.y-cp.y,pa2.x-cp.x)*180/Math.PI;
            if (endAngle<0) endAngle+=360;
            aTheta=endAngle-startAngle;
            if (aTheta<-180)
                aTheta=360+aTheta;
            if (aTheta>180)
                aTheta=aTheta-360;
            Arc2D.Double arc;
            arc=new Arc2D.Double(cp.x-defaultRadius,cp.y-defaultRadius,
                    defaultRadius*2,defaultRadius*2,startAngle,aTheta,Arc2D.OPEN);
            JPathIterator jpi=new JPathIterator(arc.getPathIterator(null));
            JSimplePath jsp=jpi.getJPath().get(0);
            if (cEdit==null) cEdit=new CompoundEdit();
            int idx=cn.jPath.indexOf(cn.seg2);
            for (int j=0;j<jsp.size();j++){
                cEdit.addEdit(new JInsertSegmentEdit(getViewer(),cn.jPathObject,
                        cn.jPath,jsp.get(j),idx++,false));
            }
            cEdit.addEdit(new JRemoveSegmentEdit(getViewer(),cn.jPathObject,cn.seg2));
        }
        if (cEdit !=null){
            cEdit.end();
            getViewer().getDocument().fireUndoEvent(cEdit);
        }
    }
    
}
