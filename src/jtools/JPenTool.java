/*
 * JPenTool.java
 *
 * Created on 2007/09/18, 17:35
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jtools;

import jactions.JUndoRedoEvent;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import jedit.JInsertObjectsEdit;
import jedit.pathedit.JInsertSegmentEdit;
import jedit.pathedit.JLoopPathEdit;
import jedit.pathedit.JMergePathEdit;
import jedit.pathedit.JRemovePathEdit;
import jedit.pathedit.JRemoveSegmentEdit;
import jedit.pathedit.JReversePathEdit;
import jedit.pathedit.JSetSegmentEdit;
import jgeom.JIntersect;
import jgeom.JPathIterator;
import jgeom.JSegment;
import jgeom.JSimplePath;
import jobject.JLeaf;
import jobject.JPathObject;
import jscreen.JDragPane;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *
 * @author TI
 */
public class JPenTool extends JAbstractTool {

    private final int NOP = 0;
    private final int ADDMODE = 1;
    private final int ADDOBJECTMODE = 2;
    private final int LOOPMODE = 3;
    private final int JOINMODE = 4;
    private final int PICKUPMODE = 5;
    private final int REMOVEMODE = 6;
    private final int ANCURMODE = 7;
    private final int INSERTMODE = 8;
    private JPathObject creatingObject,  tObject;
    private JSimplePath creatingPath,  tPath;
    private JSegment creatingSeg,  tSeg;
    private JSegment savedSeg;
    private JSimplePath previewPath;
    private CompoundEdit cEdit;
    private int mode;
    private Object hoveringObject = null;
    private Object hObject = null;
    private boolean controlChangeTool=true;
    //private Stroke stroke=new BasicStroke(1f);
    /** Creates a new instance of JPenTool */
    public JPenTool(JDragPane dragPane) {
        super(dragPane);
        presentationName = "ÉyÉìÉcÅ[Éã";
        creatingObject = tObject = null;
        creatingPath = tPath = null;
        creatingSeg = tSeg = null;
        previewPath = null;
        cEdit = null;
        dragPane.getCurrentRequest().setSelectionMode(JRequest.DIRECT_MODE);
        dragPane.setPaintRect(false);

    }

    private void paintSpecial(Graphics2D g) {
        if (hoveringObject != null && !dragPane.isDragging()) {
            if (hoveringObject instanceof Point2D) {
                Point2D pa = (Point2D) hoveringObject;
                Point2D p = new Point2D.Double();
                getViewer().getEnvironment().getToScreenTransform().transform(pa, p);
                double radius = JEnvironment.PATH_SELECTOR_SIZE;
                g.setColor(getViewer().getAvailableLayer().getPreviewColor().darker());
                Line2D.Double line = new Line2D.Double(p.getX() - radius, p.getY() - radius,
                        p.getX() + radius, p.getY() + radius);
                g.draw(line);
                line.setLine(p.getX() - radius, p.getY() + radius, p.getX() + radius,
                        p.getY() - radius);
                g.draw(line);
            } else if (hoveringObject instanceof JSegment) {
                JSegment seg = (JSegment) hoveringObject;
                JEnvironment env = getViewer().getEnvironment();
                JRequest req = getViewer().getCurrentRequest();
                double radius = JEnvironment.PATH_SELECTOR_SIZE * JEnvironment.HILIGHT_RATIO;
                Point2D p = new Point2D.Double();
                env.getToScreenTransform().transform(seg.getAncur(), p);
                Rectangle2D r = new Rectangle2D.Double(p.getX() - radius / 2, p.getY() - radius / 2, radius, radius);
                if (req.contains(seg)) {
                    g.setColor(getViewer().getAvailableLayer().getPreviewColor().darker());
                } else {
                    g.setColor(Color.WHITE);
                }
                g.fill(r);
                g.setColor(getViewer().getAvailableLayer().getPreviewColor().darker());
                g.draw(r);
            }
        }
    }

    @Override
    public void paint(Graphics2D g) {
        boolean crnull = false;

        if (previewPath == null) {
            if (creatingPath == null) {
                super.paint(g);
                return;
            }
            crnull = true;
            previewPath = creatingPath;
        }
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
        paintSpecial(g);
        super.paint(g);
    }

    private JSimplePath createJoinPath(JSimplePath targetPath, JSegment targetSeg, JSimplePath sourcePath) {
        int idx = targetPath.indexOf(targetSeg);
        JSimplePath ret = new JSimplePath();
        ret.setLooped(false);
        if (sourcePath != null) {
            for (int i = 0; i < sourcePath.size(); i++) {
                ret.add(sourcePath.get(i));
            }
        }
        for (int i = 0; i < targetPath.size(); i++) {
            ret.add(targetPath.get(i));
        }
        return ret;
    }

    private int getMode(MouseEvent e) {

        JRequest req = dragPane.getViewer().getCurrentRequest();
        JEnvironment env = dragPane.getEnvironment();
        tObject = null;
        tPath = null;
        tSeg = null;
        req.hitObjects.clear();
        req.hitResult = JRequest.HIT_NON;
        Point2D p = new Point2D.Double();
        int smode = NOP;
        env.getToAbsoluteTransform().transform(e.getPoint(), p);
        hObject = null;
        if (creatingObject != null) {
            creatingObject.hitByPoint(env, req, p);
        }
        if (req.hitResult != JRequest.HIT_ANCUR) {
            req.hitObjects.clear();
            req.hitResult = JRequest.HIT_NON;
            getViewer().getCurrentPage().hitByPoint(env, req, p);
        }
        if (req.hitResult == JRequest.HIT_ANCUR) {
            for (int i = 0; i < req.hitObjects.size(); i++) {
                Object o = req.hitObjects.get(i);
                if (o instanceof JPathObject) {
                    tObject = (JPathObject) o;
                } else if (o instanceof JSimplePath) {
                    tPath = (JSimplePath) o;
                } else if (o instanceof JSegment) {
                    tSeg = (JSegment) o;
                }
            }
            hObject = tSeg;
            //loop;
            int idx = tPath.indexOf(tSeg);
            if (idx == 0 && creatingPath == tPath && tPath.size() > 2 && !tPath.isLooped()) {
                if (e.isAltDown()) {
                    smode = ANCURMODE;
                } else {
                    smode = LOOPMODE;
                }
            } else if ((idx == 0 || idx == tPath.size() - 1) && !tPath.isLooped() && creatingPath != tPath) {
                if (creatingPath != null) {
                    smode = JOINMODE;
                } else {
                    smode = PICKUPMODE;
                }
            } else if (tPath == creatingPath) {
                if (e.isAltDown()) {
                    smode = ANCURMODE;
                } else {
                    smode = REMOVEMODE;
                }

            } else {
                req.hitObjects.clear();
                req.hitResult = JRequest.HIT_NON;
            }
        }
        if (req.hitResult != JRequest.HIT_ANCUR) {
            if (creatingObject != null) {
                Stroke str = new BasicStroke((float) (JEnvironment.SELECTION_STROKE_SIZE / env.getToScreenRatio()));
                Shape s = str.createStrokedShape(creatingPath.getShape(creatingObject.getPath().getWindingRule()));
                if (s.contains(p)) {
                    smode = INSERTMODE;
                    hObject = getIntersection(e.getPoint(), creatingPath);
                } else {
                    smode = ADDMODE;
                }
            } else {
                smode = ADDOBJECTMODE;
            }
        }
        req.hitObjects.clear();
        req.hitResult = JRequest.HIT_NON;

        return smode;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (!dragPane.isDragging) {
            mode = getMode(e);
            if (hoveringObject != hObject) {
                hoveringObject = hObject;
                dragPane.repaint();
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        JRequest req = dragPane.getViewer().getCurrentRequest();
        JEnvironment env = dragPane.getEnvironment();
        if (e.getClickCount() == 2) {
            creatingObject = null;
            creatingPath = null;
            creatingSeg = null;
            savedSeg = null;
            return;
        }
        cEdit = null;
        Point2D p = new Point2D.Double();
        env.getToAbsoluteTransform().transform(e.getPoint(), p);
        mode = getMode(e);
        switch (mode) {
            case LOOPMODE:
                previewPath = creatingPath.clone();
                previewPath.setLooped(true);
                creatingSeg = previewPath.get(0);
                creatingSeg.setControl1(null);
                creatingSeg.setControl2(null);
                savedSeg = creatingPath.get(0);
                break;
            case JOINMODE:
            case PICKUPMODE:
                int idx = tPath.indexOf(tSeg);
                if (idx == 0) {
                    if (cEdit == null) {
                        cEdit = new CompoundEdit();
                    }
                    cEdit.addEdit(new JReversePathEdit(getViewer(), tObject, tPath));
                }
                previewPath = createJoinPath(tPath, tSeg, creatingPath);
                int i = previewPath.indexOf(tSeg);
                savedSeg = tSeg;
                creatingSeg = tSeg.clone();
                //creatingSeg.setControl1(null);
                //creatingSeg.setControl2(null);
                previewPath.remove(tSeg);
                previewPath.add(i, creatingSeg);
                if (creatingObject == null) {
                    creatingObject = tObject;
                    creatingPath = tPath;
                }
                break;
            case REMOVEMODE:
                req.add(creatingObject);
                req.add(tPath);
                req.add(tSeg);
                UndoableEdit anEdit = new JRemoveSegmentEdit(getViewer(), creatingObject, tSeg);
                getViewer().getDocument().fireUndoEvent(anEdit);
                previewPath = tPath;
                savedSeg = null;
                creatingSeg = null;
                if (creatingObject.getPath().size() == 0) {
                    creatingObject = null;
                    creatingPath = null;
                    previewPath = null;
                }
                break;
            case INSERTMODE:
                JSegment seg0,
                 seg1,
                 seg2;
                JSimplePath addedPath;
                double radius = env.PATH_SELECTOR_SIZE / env.getToScreenRatio() / 2;
                Point2D p0 = new Point2D.Double(),
                 p1 = new Point2D.Double();
                p0.setLocation(p.getX() - radius, p.getY() - radius);
                p1.setLocation(p.getX() + radius, p.getY() + radius);
                addedPath = creatingPath.clone();
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
                CompoundEdit edt = new CompoundEdit();
                edt.addEdit(new JSetSegmentEdit(getViewer(), creatingObject, creatingPath.get(k - 1), seg0));
                if (k != addedPath.size() - 1) {
                    edt.addEdit(new JSetSegmentEdit(getViewer(), creatingObject, creatingPath.get(k), seg2));
                } else {
                    edt.addEdit(new JSetSegmentEdit(getViewer(), creatingObject, creatingPath.get(0), seg2));
                }
                edt.addEdit(new JInsertSegmentEdit(getViewer(), creatingObject, creatingPath, seg1, k, true));
                edt.end();
                getViewer().getDocument().fireUndoEvent(edt);
                creatingSeg = seg1;
                break;
            case ANCURMODE:
                previewPath = creatingPath.clone();
                int ix = creatingPath.indexOf(tSeg);
                savedSeg = tSeg;
                creatingSeg = previewPath.get(ix);
                break;
            default:
                if (creatingObject == null) {
                    creatingObject = new JPathObject();
                    creatingPath = new JSimplePath();
                    creatingObject.getPath().add(creatingPath);
                }
                creatingSeg = new JSegment();
                p = env.getAbsoluteMousePoint(e.getPoint(),getViewer().getCurrentPage());
                if (e.isShiftDown() && creatingPath.size() > 0) {
                    p = env.getShiftedMovePoint(creatingPath.get(creatingPath.size() - 1).getAncur(), p);

                }
                creatingSeg.setAncur(p);
                creatingSeg.setControl1(null);
                creatingSeg.setControl2(null);
                creatingSeg.setJoined(true);
                savedSeg = null;
                previewPath = creatingPath.clone();
                previewPath.add(creatingSeg);
                break;
        }
        req.clear();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        JRequest req = dragPane.getViewer().getCurrentRequest();
        JEnvironment env = dragPane.getEnvironment();
        Point2D p = new Point2D.Double();
        env.getToAbsoluteTransform().transform(e.getPoint(), p);
        if (creatingSeg != null && mode != INSERTMODE) {
            if (e.isShiftDown()) {
                p = env.getShiftedMovePoint(creatingSeg.getAncur(), p);
            }
            //creatingSeg.setControl1(p);
            Point2D ap = creatingSeg.getAncur();
            double dx = p.getX() - ap.getX();
            double dy = p.getY() - ap.getY();
            

            creatingSeg.setControl2(p);
            if (!e.isAltDown()) {
                if (e.isControlDown()) {
                    controlChangeTool=false;
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
                creatingSeg.setJoined(true);
            } else {
                creatingSeg.setJoined(false);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getClickCount() == 2) {
            return;
        }
        JRequest req = dragPane.getCurrentRequest();

        switch (mode) {
            case LOOPMODE:
                req.add(creatingObject);
                req.add(creatingPath);
                req.add(savedSeg);
                if (cEdit == null) {
                    cEdit = new CompoundEdit();
                }
                cEdit.addEdit(new JLoopPathEdit(getViewer(), creatingObject, creatingPath, true));
                cEdit.addEdit(new JSetSegmentEdit(getViewer(), creatingObject, savedSeg, creatingSeg.getAncur(), creatingSeg.getControl1(), creatingSeg.getControl2()));

                creatingObject = null;
                creatingPath = null;
                creatingSeg = null;
                savedSeg = null;
                break;
            case JOINMODE:
                if (cEdit == null) {
                    cEdit = new CompoundEdit();
                }
                cEdit.addEdit(new JRemovePathEdit(getViewer(), creatingObject, creatingPath));
                cEdit.addEdit(new JMergePathEdit(getViewer(), tObject, tPath, tSeg, creatingPath));
            case PICKUPMODE:
                if (cEdit == null) {
                    cEdit = new CompoundEdit();
                }
                cEdit.addEdit(new JSetSegmentEdit(getViewer(), tObject, savedSeg, creatingSeg.getAncur(), creatingSeg.getControl1(), creatingSeg.getControl2()));
                creatingObject = tObject;
                creatingPath = tPath;
                creatingSeg = tSeg;
                savedSeg = null;
                break;
            case ADDOBJECTMODE:
                if (cEdit == null) {
                    cEdit = new CompoundEdit();
                }
                Vector<JLeaf> objects = new Vector<JLeaf>();
                objects.add(creatingObject);
                cEdit.addEdit(new JInsertObjectsEdit(getViewer(), objects));
            case ADDMODE:
                if (cEdit == null) {
                    cEdit = new CompoundEdit();
                }
                cEdit.addEdit(new JInsertSegmentEdit(getViewer(), creatingObject, creatingPath, creatingSeg, creatingPath.size(), true));
                break;
            case INSERTMODE:
                break;
            default:
                if (creatingSeg != null) {
                    if (cEdit == null) {
                        cEdit = new CompoundEdit();
                    }
                    req.add(creatingObject);
                    req.add(creatingPath);
                    req.add(savedSeg);
                    cEdit.addEdit(new JSetSegmentEdit(getViewer(), creatingObject, savedSeg, creatingSeg));
                    creatingSeg = savedSeg;
                }
                break;
        }
        if (creatingObject != null) {
            req.add(creatingObject);
        }
        if (cEdit != null) {
            cEdit.end();
            getViewer().getDocument().fireUndoEvent(cEdit);
        }
        mode = NOP;
        previewPath = null;
        cEdit = null;
        dragPane.repaint();
    }

    @Override
    public void changeCursor() {
        JCursor jc = dragPane.getJCursor();
        if (dragPane.isDragging() && mode != REMOVEMODE) {
            setCursor(jc.MOVE);
        } else {
            switch (mode) {
                case NOP:
                case ADDMODE:
                    setCursor(jc.PEN);
                    break;
                case ADDOBJECTMODE:
                    setCursor(jc.PEN_DUM);
                    break;
                case LOOPMODE:
                    setCursor(jc.PEN_LINK);
                    break;
                case JOINMODE:
                case PICKUPMODE:
                    setCursor(jc.PEN_JOIN);
                    break;
                case REMOVEMODE:
                    setCursor(jc.PEN_MINUS);
                    break;
                case ANCURMODE:
                    setCursor(jc.PEN_ANCUR);
                    break;
                case INSERTMODE:
                    setCursor(jc.PEN_PLUS);
                    break;
                default:
                    setCursor(jc.PEN);
                    break;
            }
        }
    }

    @Override
    public void wakeup() {
        creatingObject = tObject = null;
        creatingPath = tPath = null;
        creatingSeg = tSeg = null;
        previewPath = null;
        cEdit = null;
        /*
        if (creatingObject !=null){
        if (creatingObject.getParent()==null || !creatingObject.getParent().contains(creatingObject)
        || !getRequest().contains(creatingObject)) {
        creatingObject=tObject=null;
        creatingPath=tPath=null;
        creatingSeg=tSeg=null;
        previewPath=null;
        cEdit=null;
        }
        }
         */
        dragPane.repaint();
        dragPane.getCurrentRequest().setSelectionMode(JRequest.DIRECT_MODE);
        dragPane.setPaintRect(false);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == e.VK_ENTER) {
            if (!dragPane.isDragging()) {
                JRequest req = dragPane.getCurrentRequest();
                if (creatingObject != null) {
                    req.add(creatingObject);
                    req.add(creatingSeg);
                    if (savedSeg != null) {
                        req.add(savedSeg);
                    }
                }
                creatingObject = null;
                creatingPath = null;
                creatingSeg = null;
                savedSeg = null;
                dragPane.repaint();
                e.consume();
            }
        }
        if (e.getKeyCode() == e.VK_ALT) {
            Point p = dragPane.getMousePosition();
            if (p == null) {
                return;
            }
            MouseEvent em = new MouseEvent(dragPane, MouseEvent.MOUSE_MOVED,
                    e.getWhen(), e.getModifiers(), p.x, p.y, 0, false, 0);
            mouseMoved(em);
            e.consume();

        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode()==KeyEvent.VK_CONTROL && !controlChangeTool){
            controlChangeTool=true;
        }
        if (e.getKeyCode() == e.VK_ALT && !dragPane.isDragging()) {
            Point p = dragPane.getMousePosition();
            if (p == null) {
                return;
            }
            MouseEvent em = new MouseEvent(dragPane, MouseEvent.MOUSE_MOVED,
                    e.getWhen(), e.getModifiers(), p.x, p.y, 0, false, 0);
            mouseMoved(em);
            e.consume();

        }
    }

    @Override
    public void undoRedoEventHappened(JUndoRedoEvent e) {
        if (creatingObject != null) {
            if (creatingObject.getParent() == null || !creatingObject.getParent().contains(creatingObject) ||
                    !getViewer().getCurrentPage().getCurrentLayer().contains(creatingObject) || !getViewer().getCurrentPage().getCurrentLayer().isVisible() || getViewer().getCurrentPage().getCurrentLayer().isLocked()) {
                creatingObject = tObject = null;
                creatingPath = tPath = null;
                creatingSeg = tSeg = null;
                previewPath = null;
                cEdit = null;
                dragPane.repaint();
            } else if (creatingPath.isLooped()) {
                creatingObject = tObject = null;
                creatingPath = tPath = null;
                creatingSeg = tSeg = null;
                previewPath = null;
                cEdit = null;
                dragPane.repaint();
                creatingSeg = tSeg = null;
                previewPath = null;
                cEdit = null;
                dragPane.repaint();
            }

        }
    }
    @Override
    public boolean controlChangeTool(){
        return controlChangeTool;
    }
    private Point2D getIntersection(Point mp, JSimplePath pth) {
        JRequest req = getViewer().getCurrentRequest();
        JEnvironment env = getEnvironment();
        Point2D p = new Point2D.Double();
        env.getToAbsoluteTransform().transform(mp, p);
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
}
