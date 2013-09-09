/*
 * JAncurTool.java
 *
 * Created on 2007/10/03, 11:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jtools;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.undo.CompoundEdit;
import jedit.pathedit.JSetSegmentEdit;
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
public class JAncurTool extends JAbstractTool {

    JPathObject tObj;
    JSimplePath tPath;
    JSimplePath previewPath;
    JSegment tSeg;
    JSegment creatingSeg;
    JSegment hoverSeg = null;
    
    private boolean controlChangeTool=true;
    //Stroke stroke=new BasicStroke(1f);
    /** Creates a new instance of JAncurTool */
    public JAncurTool(JDragPane dragpane) {
        super(dragpane);
        presentationName = "方向点の切り替えツール";
        wakeup();
    }

    @Override
    public void changeCursor() {
        JCursor jc = dragPane.getJCursor();
        if (creatingSeg != null) {
            setCursor(jc.MOVE);
        } else {
            setCursor(jc.ANCUR);
        }

    }

    @Override
    public void wakeup() {
        dragPane.getCurrentRequest().setSelectionMode(JRequest.DIRECT_MODE);
        dragPane.setPaintRect(false);
        tObj = null;
        tPath = previewPath = null;
        tSeg = creatingSeg = null;
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

    @Override
    public void paint(Graphics2D g) {
        boolean crnull = false;
        if (previewPath == null) {
            if (hoverSeg != null) {
                JEnvironment env = getViewer().getEnvironment();
                JRequest req = getViewer().getCurrentRequest();
                Point2D p = new Point2D.Double();
                env.getToScreenTransform().transform(hoverSeg.getAncur(), p);
                double radius = JEnvironment.PATH_SELECTOR_SIZE * JEnvironment.HILIGHT_RATIO;
                Rectangle2D r = new Rectangle2D.Double(p.getX() - radius / 2, p.getY() - radius / 2, radius, radius);
                if (req.contains(hoverSeg)) {
                    g.setColor(getViewer().getAvailableLayer().getPreviewColor().darker());
                } else {
                    g.setColor(Color.WHITE);
                }
                g.fill(r);
                g.setColor(getViewer().getAvailableLayer().getPreviewColor().darker());
                g.draw(r);
            }
            super.paint(g);
            return;
        }
        hoverSeg = null;
        JEnvironment env = getEnvironment();
        Shape s = previewPath.getShape(JPathIterator.WIND_NON_ZERO);
        AffineTransform af = env.getToScreenTransform();
        //g.setColor(env.PREVIEW_COLOR);
        g.setColor(dragPane.getViewer().getDocument().getCurrentPage().getCurrentLayer().getPreviewColor());
        //g.setStroke(stroke);
        g.draw(af.createTransformedShape(s));
        Point2D.Double pd = new Point2D.Double();
        Point2D.Double pd1 = new Point2D.Double();
        double radius = env.PATH_SELECTOR_SIZE;
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
        JEnvironment env = getEnvironment();
        JRequest req = dragPane.getCurrentRequest();
        tObj = null;
        tPath = null;
        previewPath = null;
        tSeg = null;
        creatingSeg = null;
        Point2D p = new Point2D.Double();
        env.getToAbsoluteTransform().transform(e.getPoint(), p);
//        Point2D p=env.getAbsoluteMousePoint(e.getPoint());
        for (int i = 0; i < req.size(); i++) {
            Object o = req.get(i);
            if (o instanceof JPathObject) {
                JPathObject jpo = (JPathObject) o;
                req.hitObjects.clear();
                req.hitResult = JRequest.HIT_NON;
                jpo.hitByPoint(env, req, p);
                if (req.hitResult == JRequest.HIT_ANCUR) {
                    for (int j = 0; j < req.hitObjects.size(); j++) {
                        Object oo = req.hitObjects.get(j);
                        if (oo instanceof JPathObject) {
                            tObj = (JPathObject) oo;
                        } else if (oo instanceof JSimplePath) {
                            tPath = (JSimplePath) oo;
                        } else if (oo instanceof JSegment) {
                            tSeg = (JSegment) oo;
                        }
                    }
                    break;
                }
            }
        }
        if (tObj != null) {
            previewPath = tPath.clone();
            int idx = tPath.indexOf(tSeg);
            creatingSeg = previewPath.get(idx);
            creatingSeg.setControl1(null);
            creatingSeg.setControl2(null);
            req.clear();
            dragPane.repaint();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (creatingSeg == null) {
            return;
        }
        JRequest req = dragPane.getViewer().getCurrentRequest();
        JEnvironment env = dragPane.getEnvironment();
//        Point2D p=new Point2D.Double();
//        env.getToAbsoluteTransform().transform(e.getPoint(),p);
        Point2D p = env.getAbsoluteMousePoint(e.getPoint(),getViewer().getCurrentPage());
        if (e.isShiftDown()) {
            p = env.getShiftedMovePoint(creatingSeg.getAncur(), p);
        }
        //creatingSeg.setControl1(p);
        Point2D ap = creatingSeg.getAncur();
        double dx = p.getX() - ap.getX();
        double dy = p.getY() - ap.getY();
        if (!e.isAltDown()) {
            if (e.isControlDown()) {
                controlChangeTool = false;
                double dst = 0;
                if (creatingSeg.getControl1() != null) {
                    double sdx = creatingSeg.getControl1().getX() - creatingSeg.getAncur().getX();
                    double sdy = creatingSeg.getControl1().getY() - creatingSeg.getAncur().getY();
                    dst = Math.sqrt(sdx * sdx + sdy * sdy);
                    double dstA = Math.sqrt(dx * dx + dy * dy);
                    dx = dst * (dx / dstA);
                    dy = dst * (dy / dstA);
                }
            }
            Point2D cp = new Point2D.Double(ap.getX() - dx, ap.getY() - dy);
            creatingSeg.setControl1(cp);
        }
        creatingSeg.setControl2(p);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        JRequest req = dragPane.getViewer().getCurrentRequest();
        JEnvironment env = dragPane.getEnvironment();
        if (creatingSeg != null) {
            CompoundEdit cEdit = new CompoundEdit();
            req.add(tObj);
            req.add(tPath);
            req.add(tSeg);
            tSeg.setJoined(true);
            cEdit.addEdit(new JSetSegmentEdit(getViewer(), tObj, tSeg, creatingSeg));
            cEdit.end();
            getViewer().getDocument().fireUndoEvent(cEdit);
        }
        tSeg = null;
        tObj = null;
        tPath = null;
        creatingSeg = null;
        previewPath = null;
        dragPane.repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        hit(e.getPoint());
    }
    @Override
    public void keyReleased(KeyEvent e){
        if (e.getKeyCode()==KeyEvent.VK_CONTROL && !controlChangeTool){
            controlChangeTool=true;
        }
    }
    @Override
    public boolean controlChangeTool(){
        return controlChangeTool;
    }
}
