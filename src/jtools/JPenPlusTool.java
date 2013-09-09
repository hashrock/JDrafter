/*
 * JPenPlusTool.java
 *
 * Created on 2007/10/02, 14:42
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jtools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.undo.CompoundEdit;
import jedit.pathedit.JInsertSegmentEdit;
import jedit.pathedit.JSetSegmentEdit;
import jgeom.JIntersect;
import jgeom.JPathIterator;
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
public class JPenPlusTool extends JAbstractTool {

    private JPathObject tObj;
    private JSimplePath tPath;
    private JSegment tSeg;
    private JSimplePath previewPath;
    private JSegment creatingSeg;
    private Point2D intersection = null;
    private CompoundEdit cEdit = null;
    
    private boolean controlChangeTool=true;
    /** Creates a new instance of JPenPlusTool */
    public JPenPlusTool(JDragPane dragPane) {
        super(dragPane);
        presentationName = "制御点追加ツール";
        dragPane.getCurrentRequest().setSelectionMode(JRequest.DIRECT_MODE);
        dragPane.setPaintRect(false);
        tObj = null;
        tPath = null;
        tSeg = null;
        previewPath = null;
        creatingSeg = null;
    }

    @Override
    public void paint(Graphics2D g) {
        boolean crnull = false;
        if (previewPath == null) {
            if (intersection != null) {
                Point2D p = new Point2D.Double();
                getViewer().getEnvironment().getToScreenTransform().transform(intersection, p);
                double radius = JEnvironment.PATH_SELECTOR_SIZE;
                g.setColor(getViewer().getAvailableLayer().getPreviewColor().darker());
                Line2D.Double line = new Line2D.Double(p.getX() - radius, p.getY() - radius,
                        p.getX() + radius, p.getY() + radius);
                g.draw(line);
                line.setLine(p.getX() - radius, p.getY() + radius, p.getX() + radius,
                        p.getY() - radius);
                g.draw(line);
            }
            super.paint(g);
            return;
        }
        JEnvironment env = getEnvironment();
        Shape s = previewPath.getShape(JPathIterator.WIND_NON_ZERO);
        AffineTransform af = env.getToScreenTransform();
        //g.setColor(env.PREVIEW_COLOR);
        g.setColor(dragPane.getViewer().getDocument().getCurrentPage().getCurrentLayer().getPreviewColor());
        //g.setStroke(new BasicStroke(0));
        g.draw(af.createTransformedShape(s));
        Point2D.Double pd = new Point2D.Double();
        Point2D.Double pd1 = new Point2D.Double();
        double radius = JEnvironment.PATH_SELECTOR_SIZE;
        Rectangle2D.Double rd = new Rectangle2D.Double(0, 0, radius, radius);
        Ellipse2D.Double ed = new Ellipse2D.Double(0, 0, radius, radius);
        for (int i = 0; i < previewPath.size(); i++) {
            af.transform(previewPath.get(i).getAncur(), pd);
            rd.x = pd.x - radius / 2;
            rd.y = pd.y - radius / 2;
            g.setColor(Color.WHITE);
            g.fill(rd);
            //g.setColor(env.PREVIEW_COLOR);
            g.setColor(dragPane.getViewer().getDocument().getCurrentPage().getCurrentLayer().getPreviewColor());
            g.draw(rd);
            if (previewPath.get(i) == creatingSeg) {
                if (creatingSeg.getControl1() != null) {
                    af.transform(creatingSeg.getControl1(), pd1);
                    g.drawLine((int) pd.x, (int) pd.y, (int) pd1.x, (int) pd1.y);
                    ed.x = pd1.x - radius / 2;
                    ed.y = pd1.y - radius / 2;
                    g.fill(ed);
                }
                if (creatingSeg.getControl2() != null) {
                    af.transform(creatingSeg.getControl2(), pd1);
                    g.drawLine((int) pd.x, (int) pd.y, (int) pd1.x, (int) pd1.y);
                    ed.x = pd1.x - radius / 2;
                    ed.y = pd1.y - radius / 2;
                    g.fill(ed);
                }
                g.fill(rd);
                JSegment nex = previewPath.nextSegment(creatingSeg);
                if (nex != null) {
                    if (nex.getControl1() != null) {
                        af.transform(nex.getAncur(), pd);
                        af.transform(nex.getControl1(), pd1);
                        g.drawLine((int) pd.x, (int) pd.y, (int) pd1.x, (int) pd1.y);
                        ed.x = pd1.x - radius / 2;
                        ed.y = pd1.y - radius / 2;
                        g.fill(ed);
                    }
                }
                nex = previewPath.prevSegment(creatingSeg);
                if (nex != null) {
                    if (nex.getControl2() != null) {
                        af.transform(nex.getAncur(), pd);
                        af.transform(nex.getControl2(), pd1);
                        g.drawLine((int) pd.x, (int) pd.y, (int) pd1.x, (int) pd1.y);
                        ed.x = pd1.x - radius / 2;
                        ed.y = pd1.y - radius / 2;
                        g.fill(ed);
                    }
                    rd.x = pd.x - radius / 2;
                    rd.y = pd.y - radius / 2;
                    g.setColor(Color.WHITE);
                    g.fill(rd);
                    //g.setColor(env.PREVIEW_COLOR);
                    g.setColor(dragPane.getViewer().getDocument().getCurrentPage().getCurrentLayer().getPreviewColor());
                    g.draw(rd);
                }

            }
        }
        if (crnull) {
            previewPath = null;
        }
        super.paint(g);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        intersection = null;
        JRequest req = dragPane.getViewer().getCurrentRequest();
        JEnvironment env = dragPane.getEnvironment();
        Point2D p = new Point2D.Double();
        env.getToAbsoluteTransform().transform(e.getPoint(), p);
        tObj = null;
        tPath = null;
        tSeg = null;
        for (int i = 0; i < req.size(); i++) {
            Object o = req.get(i);
            if (o instanceof JPathObject) {
                JPathObject jp = (JPathObject) o;
                req.hitObjects.clear();
                req.hitResult = req.HIT_NON;
                jp.hitByPoint(env, req, p);
                if (req.hitResult == req.HIT_PATH) {
                    for (int j = 0; j < req.hitObjects.size(); j++) {
                        Object oj = req.hitObjects.get(j);
                        if (oj instanceof JPathObject) {
                            tObj = (JPathObject) oj;
                        } else if (oj instanceof JSimplePath) {
                            tPath = (JSimplePath) oj;
                        }
                    }
                    break;
                }
            }
        }
        if (tObj != null) {
            JSegment seg0, seg1, seg2;
            JSimplePath addedPath;
            double radius = JEnvironment.PATH_SELECTOR_SIZE * JEnvironment.HILIGHT_RATIO / env.getToScreenRatio() / 2;
            Point2D p0 = new Point2D.Double(), p1 = new Point2D.Double();
            p0.setLocation(p.getX() - radius, p.getY() - radius);
            p1.setLocation(p.getX() + radius, p.getY() + radius);
            addedPath = tPath.clone();
            int k = JIntersect.addPath(p0, p1, addedPath);
            if (k == -1) {
                p0.setLocation(p.getX() - radius, p.getY() + radius);
                p1.setLocation(p.getX() + radius, p.getY() - radius);
                k = JIntersect.addPath(p0, p1, addedPath);
            }
            seg0 = seg1 = seg2 = null;
            seg0 = addedPath.get(k - 1);
            seg1 = addedPath.get(k);
            if (k == addedPath.size() - 1) {
                seg2 = addedPath.get(0);
            } else {
                seg2 = addedPath.get(k + 1);
            }
            cEdit = new CompoundEdit();
            cEdit.addEdit(new JSetSegmentEdit(getViewer(), tObj, tPath.get(k - 1), seg0));
            if (k != addedPath.size() - 1) {
                cEdit.addEdit(new JSetSegmentEdit(getViewer(), tObj, tPath.get(k), seg2));
            } else {
                cEdit.addEdit(new JSetSegmentEdit(getViewer(), tObj, tPath.get(0), seg2));
            }
            cEdit.addEdit(new JInsertSegmentEdit(getViewer(), tObj, tPath, seg1, k, false));
            req.clear();
            tSeg = tPath.get(k);
            previewPath = tPath.clone();
            creatingSeg = previewPath.get(k);
            dragPane.repaint();
        }
    }

    private Point2D getIntersection(Point mp) {
        JRequest req = getViewer().getCurrentRequest();
        JEnvironment env = getEnvironment();
        Point2D p = new Point2D.Double();
        env.getToAbsoluteTransform().transform(mp, p);
        JSimplePath pth = null;
        for (int i = 0; i < req.size(); i++) {
            Object o = req.get(i);
            if (o instanceof JPathObject) {
                JPathObject jp = (JPathObject) o;
                req.hitObjects.clear();
                req.hitResult = JRequest.HIT_NON;
                jp.hitByPoint(env, req, p);
                if (req.hitResult == JRequest.HIT_PATH) {
                    for (Object oj : req.hitObjects) {
                        if (oj instanceof JSimplePath) {
                            pth = (JSimplePath) oj;
                            break;
                        }
                    }
                    break;
                }
            }
        }
        if (pth != null) {
            JSimplePath addedPath = pth.clone();
            Point2D p0 = new Point2D.Double(), p1 = new Point2D.Double();
            double radius = JEnvironment.PATH_SELECTOR_SIZE * JEnvironment.HILIGHT_RATIO / env.getToScreenRatio() / 2;
            p0.setLocation(p.getX() - radius, p.getY() - radius);
            p1.setLocation(p.getX() + radius, p.getY() + radius);
            int k = JIntersect.addPath(p0, p1, addedPath);
            if (k == -1) {
                p0.setLocation(p.getX() - radius, p.getY() + radius);
                p1.setLocation(p.getX() + radius, p.getY() - radius);
                k = JIntersect.addPath(p0, p1, addedPath);
            }
            if (k != -1) {
                return addedPath.get(k).getAncur();
            }
        }
        return null;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (creatingSeg == null) {
            return;
        }
        
        JRequest req = dragPane.getViewer().getCurrentRequest();
        JEnvironment env = dragPane.getEnvironment();
        Point2D p = new Point2D.Double();
        env.getToAbsoluteTransform().transform(e.getPoint(), p);
        if (e.isShiftDown()) {
            p = env.getShiftedMovePoint(creatingSeg.getAncur(), p);
        }
        Point2D ap = creatingSeg.getAncur();
        double dx = p.getX() - ap.getX();
        double dy = p.getY() - ap.getY();
        if (!e.isAltDown()) {
            if (e.isControlDown()){
                double dst=0;
                controlChangeTool=false;
                 if (creatingSeg.getControl1() !=null){
                     double sdx=creatingSeg.getControl1().getX()-creatingSeg.getAncur().getX();
                     double sdy=creatingSeg.getControl1().getY()-creatingSeg.getAncur().getY();    
                     dst=Math.sqrt(sdx*sdx+sdy*sdy);
                     double dstA=Math.sqrt(dx*dx+dy*dy);
                     dx=dst*(dx/dstA);
                     dy=dst*(dy/dstA);
                 }
            }
            Point2D cp = new Point2D.Double(ap.getX() - dx, ap.getY() - dy);
            creatingSeg.setControl1(cp);
        }
        creatingSeg.setControl2(p);
    }
    @Override
    public void keyReleased(KeyEvent e){
        if (e.getKeyCode()==KeyEvent.VK_CONTROL && !controlChangeTool){
            controlChangeTool=true;
        }
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        JRequest req = dragPane.getViewer().getCurrentRequest();
        JEnvironment env = dragPane.getEnvironment();
        if (creatingSeg != null) {
            req.add(tObj);
            req.add(tPath);
            req.add(tSeg);
            cEdit.addEdit(new JSetSegmentEdit(getViewer(), tObj, tSeg, creatingSeg));
            tSeg.setJoined(tSeg.canJoin());
        }
        if (cEdit != null) {
            cEdit.end();
            getViewer().getDocument().fireUndoEvent(cEdit);
        }
        cEdit=null;
        tSeg = null;
        tObj = null;
        tPath = null;
        creatingSeg = null;
        previewPath = null;
        dragPane.repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Point2D p = getIntersection(e.getPoint());
        if (p != intersection) {
            intersection = p;
            dragPane.repaint();
        }
    }

    @Override
    public void changeCursor() {
        JCursor jc = dragPane.getJCursor();
        if (creatingSeg != null) {
            setCursor(jc.MOVE);
        } else {
            setCursor(jc.PEN_PLUS);
        }
    }

    @Override
    public void wakeup() {
        dragPane.getCurrentRequest().setSelectionMode(JRequest.DIRECT_MODE);
        dragPane.setPaintRect(false);
    }
    @Override
    public boolean controlChangeTool(){
        return controlChangeTool;
    }
}
