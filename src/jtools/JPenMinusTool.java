/*
 * JPenMinusTool.java
 *
 * Created on 2007/10/03, 8:43
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jtools;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.undo.UndoableEdit;
import jedit.pathedit.JRemoveSegmentEdit;
import jgeom.JSegment;
import jgeom.JSimplePath;
import jobject.JPathObject;
import jscreen.JDragPane;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *
 * @author i002060
 */
public class JPenMinusTool extends JAbstractTool {

    private JPathObject tObj;
    private JSimplePath tPath;
    private JSegment tSeg;
    private JSegment hoverSeg=null;

    /** Creates a new instance of JPenMinusTool */
    public JPenMinusTool(JDragPane dragPane) {
        super(dragPane);
        presentationName = "êßå‰ì_çÌèúÉcÅ[Éã";
        getRequest().setSelectionMode(JRequest.DIRECT_MODE);
        dragPane.setPaintRect(false);
        tObj = null;
        tPath = null;
        tSeg = null;
    }
    
    @Override
    public void paint(Graphics2D g){
        if (hoverSeg==null) {
            super.paint(g);
            return;
        }
        JEnvironment env=getViewer().getEnvironment();
        JRequest req=getViewer().getCurrentRequest();
        double radius=JEnvironment.PATH_SELECTOR_SIZE*JEnvironment.HILIGHT_RATIO;
        Point2D p=new Point2D.Double();
        env.getToScreenTransform().transform(hoverSeg.getAncur(), p);
        Rectangle2D r=new Rectangle2D.Double(p.getX()-radius/2,p.getY()-radius/2,radius,radius);
        if (req.contains(hoverSeg)){
            g.setColor(getViewer().getAvailableLayer().getPreviewColor().darker());
        }else{
            g.setColor(Color.WHITE);
        }
        g.fill(r);
        g.setColor(getViewer().getAvailableLayer().getPreviewColor().darker());
        g.draw(r);
        super.paint(g);
    }

    @Override
    public void changeCursor() {
        JCursor jc = dragPane.getJCursor();
        setCursor(jc.PEN_MINUS);
    }

    @Override
    public void wakeup() {
        dragPane.setPaintRect(false);
        getRequest().setSelectionMode(JRequest.DIRECT_MODE);
        tObj = null;
        tPath = null;
        tSeg = null;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        tObj = null;
        tPath = null;
        tSeg = null;
        JRequest req = getRequest();
        JEnvironment env = getEnvironment();

        Point2D p = new Point2D.Double();
        env.getToAbsoluteTransform().transform(e.getPoint(), p);
        for (int i = 0; i < req.size(); i++) {
            Object o = req.get(i);
            if (o instanceof JPathObject) {
                JPathObject jpo = (JPathObject) o;
                req.hitObjects.clear();
                req.hitResult = JRequest.HIT_NON;
                jpo.hitByPoint(env, req, p);
                if (req.hitResult == JRequest.HIT_ANCUR) {
                    for (int j = 0; j < req.hitObjects.size(); j++) {
                        Object op = req.hitObjects.get(j);
                        if (op instanceof JPathObject) {
                            tObj = (JPathObject) op;
                        } else if (op instanceof JSimplePath) {
                            tPath = (JSimplePath) op;
                        } else if (op instanceof JSegment) {
                            tSeg = (JSegment) op;
                        }
                    }
                    break;
                }
            }
        }
        hoverSeg=null;
        if (tObj != null) {
            UndoableEdit anEdit = new JRemoveSegmentEdit(getViewer(), tObj, tSeg);
            dragPane.getViewer().getDocument().fireUndoEvent(anEdit);
            req.clear();
            if (tObj.getPath().segmentSize() > 0) {
                req.add(tObj);
            }
            dragPane.repaint();
        }
        
    }
    @Override
    public void mouseMoved(MouseEvent e){
        hit(e.getPoint());
    }
    private void hit(Point mp) {
        JRequest req = getViewer().getCurrentRequest();
        JEnvironment env = getViewer().getEnvironment();
        Point2D p = new Point2D.Double();
        env.getToAbsoluteTransform().transform(mp, p);
        JSegment hs = null;
        for (int i = 0; i < req.size(); i++) {
            Object o = req.get(i);
            if (o instanceof JPathObject) {
                JPathObject jp = (JPathObject) o;
                req.hitResult = JRequest.HIT_NON;
                req.hitObjects.clear();
                jp.hitByPoint(env, req, p);
                if (req.hitResult == JRequest.HIT_ANCUR) {
                    for (Object oj : req.hitObjects) {
                        if (oj instanceof JSegment) {
                            hs = (JSegment) oj;
                            break;
                        }
                    }
                    break;
                }
            }
        }
        if (hs != hoverSeg) {
            hoverSeg = hs;
            dragPane.repaint();
        }
    }
}
