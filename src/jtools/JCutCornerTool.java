/*
 * JCutCornerTool.java
 *
 * Created on 2007/10/05, 9:41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jtools;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.undo.CompoundEdit;
import jtools.jcontrol.CutCornerToolPanel;
import jedit.pathedit.JInsertSegmentEdit;
import jedit.pathedit.JSetSegmentEdit;
import jgeom.JIntersect;
import jgeom.JSegment;
import jgeom.JSimplePath;
import jobject.JPathObject;
import jscreen.JDragPane;
import jscreen.JRequest;

/**
 *
 * @author i002060
 */
public class JCutCornerTool extends JAbstractTool{
    Point2D currentPoint;
    //Stroke stroke=new BasicStroke(1f);
    Line2D.Double line=null;
    /**
     * Creates a new instance of JCutCornerTool
     */
    public JCutCornerTool(JDragPane dragPane) {
        super(dragPane);
        presentationName="角切り落しツール";
        wakeup();
    }
    @Override
    public void paint(Graphics2D g){
        if (currentPoint==null || dragPane.startPoint==null) {
            super.paint(g);
            return;
        }
        if (line ==null) line=new Line2D.Double();
        line.setLine(dragPane.startPoint,currentPoint);
        //g.setColor(getEnvironment().PREVIEW_COLOR);
        g.setColor(dragPane.getViewer().getDocument().getCurrentPage().getCurrentLayer().getPreviewColor());
        //g.setStroke(stroke);
        g.draw(getEnvironment().getToScreenTransform().createTransformedShape(line));
        super.paint(g);
    }
    public void mouseDragged(MouseEvent e){
        if (currentPoint==null){
            currentPoint=new Point2D.Double();
        }
        getEnvironment().getToAbsoluteTransform().transform(e.getPoint(),currentPoint);
    }
    public void mouseReleased(MouseEvent e){
        if (currentPoint !=null && dragPane.startPoint !=null){
            Vector<corner> vc=getCorners(currentPoint,dragPane.startPoint);
            if (e.isAltDown()){
                createRadius();
            }
            update(vc);
        }
        currentPoint=null;
        dragPane.repaint();
    }
    private Vector<corner> getCorners(Point2D p1,Point2D p2){
        Vector<corner> ret=new Vector<corner>();
        JRequest req=getRequest();
        for(int i=0;i<req.size();i++){
            Object o=req.get(i);
            if (!(o instanceof JPathObject)) continue;
            JPathObject jo=(JPathObject)o;
            for (int j=0;j<jo.getPath().size();j++){
                JSimplePath js=jo.getPath().get(j);
                int k1=1,k2=2;
                for (int k=0;k<js.size();k++){
                    if (k1==js.size()){
                        if (js.isLooped())
                            k1=0;
                        else
                            break;
                    }
                    if (k2==js.size()){
                        if (js.isLooped())
                            k2=0;
                        else
                            break;
                    }
                    JSegment seg1=js.get(k);
                    JSegment seg2=js.get(k1++);
                    JSegment seg3=js.get(k2++);
                    if (seg1.getControl2() !=null && seg1.getControl2().equals(seg1.getAncur())) seg1.setControl2(null);
                    if (seg2.getControl1() !=null && seg2.getAncur().equals(seg2.getControl1())) seg2.setControl1(null);
                    if (seg2.getControl2() !=null && seg2.getAncur().equals(seg2.getControl2())) seg2.setControl2(null);
                    if (seg3.getControl1() !=null && seg3.getAncur().equals(seg2.getControl1())) seg3.setControl1(null);
                    if (seg1.getControl2() !=null || seg2.getControl1() !=null || seg2.getControl2()!=null || seg3.getControl1()!=null) continue;
                    if (JIntersect.lineIntersection(p1,p2,seg1.getAncur(),seg2.getAncur()) !=null &&
                            JIntersect.lineIntersection(p1,p2,seg2.getAncur(),seg3.getAncur()) !=null){
                        ret.add(new corner(jo,js,seg1,seg2,seg3));
                    }
                }
            }
        }
        return ret;
    }
    protected boolean createRadius(){
        CutCornerToolPanel panel=new CutCornerToolPanel(this);
        return !panel.isCanceled();
        
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
            int idx=cn.jPath.indexOf(cn.seg2)+1;
            if (cEdit==null)
                cEdit=new CompoundEdit();
            cEdit.addEdit(new JSetSegmentEdit(getViewer(),cn.jPathObject,cn.seg2,
                    new Point2D.Double(length1*Math.cos(theta1)+dx
                    ,length1*Math.sin(theta1)+dy),null,null));
            JSegment insSeg=new JSegment();
            insSeg.setJoined(true);
            insSeg.setAncur(length1*Math.cos(theta2)+dx,length1*Math.sin(theta2)+dy);
            cEdit.addEdit(new JInsertSegmentEdit(getViewer(),cn.jPathObject,cn.jPath,
                    insSeg,idx,false));
        }
        if (cEdit !=null){
            cEdit.end();
            getViewer().getDocument().fireUndoEvent(cEdit);
        }
    }
    public void changeCursor() {
        setCursor(dragPane.getJCursor().CROSSHAIR);
    }
    public void wakeup() {
        JRequest req=getRequest();
        req.setSelectionMode(req.DIRECT_MODE);
        dragPane.setPaintRect(false);
        currentPoint=null;
    }
    protected class corner{
        protected JPathObject jPathObject;
        protected JSimplePath jPath;
        protected JSegment seg1,seg2,seg3;
        protected corner(JPathObject jPathObject,JSimplePath jPath,JSegment seg1,JSegment seg2,JSegment seg3){
            this.jPathObject=jPathObject;
            this.jPath=jPath;
            this.seg1=seg1;
            this.seg2=seg2;
            this.seg3=seg3;
        }
    }
}
