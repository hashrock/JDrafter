/*
 * JGradientTool.java
 *
 * Created on 2007/12/03, 10:23
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jtools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.undo.CompoundEdit;
import jedit.paintedit.JChangeGradientDirectionEdit;
import jobject.JColorable;
import jobject.JLeaf;
import jobject.JObject;
import jpaint.JPaint;
import jscreen.JDragPane;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *
 * @author i002060
 */
public class JGradientTool extends JAbstractTool {
    static final BasicStroke bStroke=new BasicStroke(1f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,10f,
            new float[]{2f,1f},0f);
    protected Point2D startPoint=null;
    protected Point2D endPoint=null;
    /** Creates a new instance of JGradientTool */
    public JGradientTool(JDragPane dragPane) {
        super(dragPane);
        presentationName="グラデーションの方向と位置";
    }
    
    @Override
    public void changeCursor() {
        JCursor jc=dragPane.getJCursor();
        setCursor(jc.CROSSHAIR);
    }
    @Override
    public void paint(Graphics2D g){
        if (startPoint==null) {
            super.paint(g);
            return;
        }
        ImageIcon icn=JEnvironment.ICONS.CENTER;
        AffineTransform tx=getEnvironment().getToScreenTransform();
        Point2D p1=tx.transform(startPoint,null);
        icn.paintIcon(dragPane,g,(int)p1.getX()-icn.getIconWidth()/2,(int)p1.getY()-icn.getIconHeight()/2);
        if (endPoint==null) {
            super.paint(g);
            return;
        }
        Point2D p2=tx.transform(endPoint,null);
        g.setColor(Color.BLACK);
        g.setStroke(bStroke);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawLine((int)p1.getX(),(int)p1.getY(),(int)p2.getX(),(int)p2.getY());
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_DEFAULT);
        super.paint(g);
    }
    @Override
    public void wakeup() {
        JRequest req=getRequest();
        req.setSelectionMode(req.GROUP_MODE);
        dragPane.setPaintRect(false);
        startPoint=endPoint=null;
    }
    @Override
    public void mousePressed(MouseEvent e){
        startPoint=new Point2D.Double();
        AffineTransform tx=getEnvironment().getToAbsoluteTransform();
        tx.transform(e.getPoint(),startPoint);
        endPoint=null;
    }
    @Override
    public void mouseDragged(MouseEvent e){
        if (endPoint==null){
            endPoint=new Point2D.Double();
        }
        endPoint.setLocation(e.getPoint());
        getEnvironment().getToAbsoluteTransform().transform(endPoint,endPoint);
    }
    @Override
    public void mouseReleased(MouseEvent e){
        if (startPoint !=null && endPoint !=null){
            updateGradient();
        }
        startPoint=endPoint=null;
        dragPane.repaint();
    }
    private void updateGradient(){
        JRequest req=getViewer().getCurrentRequest();
        CompoundEdit cEdit=null;
        for (int i=0;i<req.size();i++){
            Object o=req.get(i);
            if (!(o instanceof JLeaf) || !(o instanceof JColorable)) continue;
            Vector<JLeaf> leafs;
            if (o instanceof JObject){
                JObject jo=(JObject)o;
                leafs=jo.getLeafs();
            }else{
                leafs=new Vector<JLeaf>();
                leafs.add((JLeaf)o);
            }
            for (int j=0;j<leafs.size();j++){
                JLeaf jl=leafs.get(j);
                JPaint jp=jl.getFillPaint();
                if (jp ==null || jp.getPaintMode()==JPaint.COLOR_MODE) continue;
                if (cEdit==null)
                    cEdit=new CompoundEdit();
                cEdit.addEdit(new JChangeGradientDirectionEdit(getViewer(),jl,startPoint,endPoint));
            }
        }
        if (cEdit !=null){
            cEdit.end();
            getViewer().getDocument().fireUndoEvent(cEdit);
        }
    }
    
}
