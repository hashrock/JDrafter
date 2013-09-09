/*
 * JReshapeTool.java
 *
 * Created on 2007/09/14, 8:44
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jtools;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import jobject.JGuidLayer;
import jobject.JLeaf;
import jscreen.JDragPane;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *
 * @author i002060
 */
public class JReshapeTool extends JGroupTool {

    private Shape selectShape = null;
    double commonTheta = -9999;
    Point2D[] reshapeHandle = null;
    Cursor[] resizeCursor = null;
    Cursor[] rotateCursor = null;
    int hitResult = -1;
    double rotation = 0;

    /**
     *
     * Creates a new instance of JReshapeTool
     */
    public JReshapeTool(JDragPane dragPane) {
        super(dragPane);
        presentationName = "選択ツール";
        reshapeHandle = new Point2D[8];
        JEnvironment env = getEnvironment();
        for (int i = 0; i < 8; i++) {
            reshapeHandle[i] = new Point2D.Double();
        }
        JCursor jc = dragPane.getJCursor();
        resizeCursor = new Cursor[]{
                    jc.NW_RESIZE, jc.N_RESIZE, jc.NE_RESIZE,
                    jc.W_RESIZE, jc.NW_RESIZE, jc.N_RESIZE,
                    jc.NE_RESIZE, jc.W_RESIZE
                };
        rotateCursor = new Cursor[]{
                    jc.ROTATE1, jc.ROTATE2, jc.ROTATE3, jc.ROTATE4,
                    jc.ROTATE5, jc.ROTATE6, jc.ROTATE7, jc.ROTATE8
                };
        hitResult = -1;
        rotation = 0;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        JEnvironment env = getEnvironment();
        JRequest req = getRequest();
        Point2D p = new Point2D.Double();
        env.getToAbsoluteTransform().transform(e.getPoint(), p);
        hitResult = hitHandle(p);
        if (hitResult == -1) {
            super.mouseMoved(e);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        JEnvironment env = getEnvironment();
        JRequest req = getRequest();
        Point2D p = new Point2D.Double();
        env.getToAbsoluteTransform().transform(e.getPoint(), p);
        hitResult = hitHandle(p);
        if (hitResult == -1) {
            super.mousePressed(e);
        }
    }

    private void setTransform(Point p, boolean isAltDown, boolean isShiftDown) {
        if (hitResult == -1) {
            return;
        }
        JEnvironment env = getEnvironment();
        JRequest req = getRequest();
        if (hitResult < 8) {
            Point2D cp = env.getAbsoluteMousePoint(p,getViewer().getCurrentPage());
            int ip = hitResult + 4;
            if (ip > 7) {
                ip -= 8;
            }
            Point2D mousePoint = new Point2D.Double(),
                    ancurPoint = new Point2D.Double(),
                    startPoint = new Point2D.Double();
            Rectangle2D rc = selectShape.getBounds2D();
            AffineTransform af = new AffineTransform();
            af.setToRotation(-commonTheta, rc.getCenterX(), rc.getCenterY());
            af.transform(cp, mousePoint);
            af.transform(reshapeHandle[hitResult], startPoint);
            af.transform(reshapeHandle[ip], ancurPoint);
            if (isAltDown) {
                ancurPoint.setLocation(rc.getCenterX(), rc.getCenterY());
            }
            double sx = (mousePoint.getX() - ancurPoint.getX()) / (startPoint.getX() - ancurPoint.getX());
            double sy = (mousePoint.getY() - ancurPoint.getY()) / (startPoint.getY() - ancurPoint.getY());
            if (isShiftDown) {
                if (hitResult == 1 || hitResult == 5) {
                    sx = sy;
                } else if (hitResult == 3 || hitResult == 7) {
                    sy = sx;
                } else {
                    double s = Math.abs(sx);
                    if (Math.abs(sy) > s) {
                        s = Math.abs(sy);
                    }
                    sx = Math.signum(sx) * s;
                    sy = Math.signum(sy) * s;
                }
            } else {
                if (hitResult == 1 || hitResult == 5) {
                    sx = 1;
                }
                if (hitResult == 3 || hitResult == 7) {
                    sy = 1;
                }
            }
            if (Math.abs(sx) < JEnvironment.MINIMUM_SCALE_RATIO) {
                sx = JEnvironment.MINIMUM_SCALE_RATIO * Math.signum(sx);
            }
            if (Math.abs(sy) < JEnvironment.MINIMUM_SCALE_RATIO) {
                sy = JEnvironment.MINIMUM_SCALE_RATIO * Math.signum(sy);
            }
            if (transform == null) {
                transform = new AffineTransform();
            }
            transform.setToRotation(commonTheta, rc.getCenterX(), rc.getCenterY());
            transform.translate(ancurPoint.getX(), ancurPoint.getY());
            transform.scale(sx, sy);
            transform.translate(-ancurPoint.getX(), -ancurPoint.getY());
            transform.rotate(-commonTheta, rc.getCenterX(), rc.getCenterY());
        } else {
            dragPane.setPaintRect(false);
            Rectangle2D r = selectShape.getBounds2D();
            Point2D cp = new Point2D.Double(r.getCenterX(), r.getCenterY());
            Point2D mp = env.getAbsoluteMousePoint(p,getViewer().getCurrentPage());
            Point2D sp = reshapeHandle[hitResult - 8];
            rotation = Math.atan2(mp.getY() - cp.getY(), mp.getX() - cp.getX()) -
                    Math.atan2(sp.getY() - cp.getY(), sp.getX() - cp.getX());
            if (isShiftDown) {
                double uAngle = Math.PI * env.getUnitAngle() / 180;
                rotation = Math.round(Math.abs(rotation) / uAngle) * uAngle;
            }
            if (transform == null) {
                transform = new AffineTransform();
            }
            transform.setToRotation(rotation, cp.getX(), cp.getY());
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        JEnvironment env = getEnvironment();
        JRequest req = getRequest();
        if (hitResult != -1) {
            dragPane.setPaintRect(false);
            setTransform(e.getPoint(), e.isAltDown(), e.isShiftDown());
            for (int i = 0; i < req.size(); i++) {
                Object o = req.get(i);
                if (o instanceof JLeaf) {
                    JLeaf lf = (JLeaf) o;
                    if (!(lf.getLayer() instanceof JGuidLayer)) {
                        lf.transform(transform, req, e.getPoint());
                    }
                }
            }
        } else {
            super.mouseDragged(e);
        }
        culcSelectShape(req);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        JRequest req = getRequest();
        JEnvironment env = getEnvironment();
        //if (rotation ==0 || transform==null){
        if (transform != null) {
            env.LAST_TRANSFORM = transform;
            env.LAST_COPY = e.isAltDown();
            env.LAST_ROTATION = rotation;
        }
        if (rotation == 0) {
            super.mouseReleased(e);
        } else {
            CompoundEdit cEdit = null;
            for (int i = 0; i < req.size(); i++) {
                Object o = req.get(i);
                if (o instanceof JLeaf) {
                    JLeaf jl = (JLeaf) o;
                    if (jl.getLayer() instanceof JGuidLayer) {
                        continue;
                    }
                    UndoableEdit edt = null;
                    edt = jl.updateRotate(env, rotation);
                    if (edt != null) {
                        if (cEdit == null) {
                            cEdit = new CompoundEdit();
                        }
                        cEdit.addEdit(edt);
                    }
                }
            }
            if (cEdit != null) {
                cEdit.end();
                getViewer().getDocument().fireUndoEvent(cEdit);
            }
        }
        rotation = 0;
        transform = null;
        hitResult = -1;
        mouseMoved(e);
        dragPane.repaint();
    }

    @Override
    public void changeCursor() {
        JRequest req = getRequest();
        JCursor jc = dragPane.getJCursor();
        if (hitResult != -1 && resizeCursor != null) {
            double rot = 4 * Math.PI + commonTheta + rotation;
            int ofs = (int) Math.round(rot * 4 / Math.PI);
            if (hitResult < 8) {
                ofs += hitResult;
                ofs %= 8;
                setCursor(resizeCursor[ofs]);
            } else {
                ofs = ofs + hitResult - 8;
                ofs %= 8;
                setCursor(rotateCursor[ofs]);
            }

        } else {
            super.changeCursor();
        }
    }

    @Override
    public void wakeup() {
        super.wakeup();
        culcSelectShape(getRequest());
    }

    private void culcSelectShape(JRequest req) {
        commonTheta = -9999;
        selectShape = null;
        for (int i = 0; i < req.size(); i++) {
            Object o = req.get(i);
            if (!(o instanceof JLeaf)) {
                continue;
            }
            JLeaf jl = (JLeaf) o;
            //
            if (jl.getLayer() instanceof JGuidLayer) {
                continue;
            //
            }
            if (commonTheta == -9999) {
                commonTheta = jl.getTotalRotation();
            } else {
                if (commonTheta != jl.getTotalRotation()) {
                    commonTheta = 0;
                    break;
                }
            }
        }
        if (commonTheta == -9999) {
            return;
        }
        Rectangle2D rc = null;
        double cx, cy;
        for (int i = 0; i < req.size(); i++) {
            if (!(req.get(i) instanceof JLeaf)) {
                continue;
            }
            JLeaf lf = (JLeaf) req.get(i);
            if (lf.getLayer() instanceof JGuidLayer) {
                continue;
            }
            if (rc == null) {
                rc = lf.getSelectionBounds();
            } else {
                rc.add(lf.getSelectionBounds());
            }
        }
        if (rc == null) {
            return;
        }
        cx = rc.getCenterX();
        cy = rc.getCenterY();
        if (commonTheta != 0) {
            rc = null;
            for (int i = 0; i < req.size(); i++) {
                if (!(req.get(i) instanceof JLeaf)) {
                    continue;
                }
                JLeaf jl = (JLeaf) req.get(i);
                if (jl.getLayer() instanceof JGuidLayer) {
                    continue;
                }
                if (rc == null) {
                    rc = jl.getOriginalSelectionBounds(cx, cy);
                } else {
                    rc.add(jl.getOriginalSelectionBounds(cx, cy));
                }
            }
        }
        JEnvironment env = getEnvironment();
        double min = JEnvironment.MINIMUM_SELECT_SIZE / env.getToScreenRatio();
        if (rc.getWidth() < min) {
            rc.setFrame(rc.getX() - (min - rc.getWidth()) / 2, rc.getY(), min, rc.getHeight());
        }
        if (rc.getHeight() < min) {
            rc.setFrame(rc.getX(), rc.getY() - (min - rc.getHeight()) / 2, rc.getWidth(), min);
        }
        if (commonTheta != 0) {
            AffineTransform af = new AffineTransform();
            af.setToRotation(commonTheta, cx, cy);
            selectShape = af.createTransformedShape(rc);
        } else {
            selectShape = rc;
        }
        putReshapeRect();
    }

    private void putReshapeRect() {
        JEnvironment env = getEnvironment();
        PathIterator pt = selectShape.getPathIterator(null);
        int i = 0;
        double x1, y1, x2, y2;
        double[] coords = new double[6];
        pt.currentSegment(coords);
        x1 = coords[0];
        y1 = coords[1];
        pt.next();

        while (!pt.isDone() && i < 8) {
            pt.currentSegment(coords);
            x2 = coords[0];
            y2 = coords[1];
            reshapeHandle[i].setLocation(x1, y1);
            reshapeHandle[i + 1].setLocation((x1 + x2) / 2, (y1 + y2) / 2);
            x1 = x2;
            y1 = y2;
            i += 2;
            pt.next();
        }
    }

    private int hitHandle(Point2D p) {
        if (selectShape == null) {
            return -1;
        }
        JEnvironment env = getEnvironment();
        //double radius=JEnvironment.OBJECT_SELECTOR_SIZE/env.getToScreenRatio();
        double radius = (JEnvironment.PATH_SELECTOR_SIZE + 1) / env.getToScreenRatio();
        Rectangle2D.Double r = new Rectangle2D.Double();
        for (int i = 0; i < 8; i++) {
            Point2D sp = reshapeHandle[i];
            r.setRect(sp.getX() - radius / 2, sp.getY() - radius / 2, radius, radius);
            if (r.contains(p)) {
                return i;
            }
            r.setRect(sp.getX() - radius, sp.getY() - radius, radius * 2, radius * 2);
            if (r.contains(p)) {
                return i + 8;
            }
        }
        return -1;
    }

    @Override
    public void paint(Graphics2D g) {
        super.paint(g);
        JRequest req = getRequest();
        JEnvironment env = getEnvironment();
        if (req.isEmpty()) {
            selectShape = null;
            return;
        }
        if (req.hitObjects.isEmpty()) {
            culcSelectShape(req);
        }
        if (selectShape == null) {
            return;
        }
        AffineTransform tr = env.getToScreenTransform();
        if (transform != null) {
            tr.concatenate(transform);
        }
        g.setColor(getViewer().getAvailableLayer().getPreviewColor());
        g.draw(tr.createTransformedShape(selectShape));
        Point2D rh = new Point2D.Double();
        //double radius=env.OBJECT_SELECTOR_SIZE/2;
        double radius = (JEnvironment.PATH_SELECTOR_SIZE + 1) / 2;
        Rectangle2D r = new Rectangle2D.Double();
        Color c = g.getColor();
        /*
        Color c=dragPane.getViewer().getDocument().getCurrentPage().getCurrentLayer().getPreviewColor();
        for (int i=0;i<req.size();i++){
        if (req.get(i) instanceof JLeaf){
        JLayer jl=((JLeaf)req.get(i)).getLayer();
        if (jl !=null){
        c=jl.getPreviewColor();
        break;
        }
        }
        }
         */
        for (int i = 0; i < reshapeHandle.length; i++) {
            tr.transform(reshapeHandle[i], rh);
            r.setFrame(rh.getX() - radius, rh.getY() - radius, radius * 2, radius * 2);
            g.setColor(Color.WHITE);
            g.fill(r);
           
            g.setColor(c);
            g.draw(r);
        }

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();
        if ((k == e.VK_ALT || k == e.VK_SHIFT) && dragPane.isDragging && hitResult != -1) {
            Point p = dragPane.getMousePosition();
            if (p != null) {
                JRequest req = getRequest();
                setTransform(p, e.isAltDown(), e.isShiftDown());
                for (int i = 0; i < req.size(); i++) {
                    Object o = req.get(i);
                    if (o instanceof JLeaf) {
                        ((JLeaf) o).transform(transform, req, null);
                    }
                }
                culcSelectShape(req);
                dragPane.repaint();
            }
            e.consume();
            return;
        }
        super.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int k = e.getKeyCode();
        if ((k == e.VK_ALT || k == e.VK_SHIFT) && dragPane.isDragging && hitResult != -1) {
            Point p = dragPane.getMousePosition();
            if (p != null) {
                JRequest req = getRequest();
                setTransform(p, e.isAltDown(), e.isShiftDown());
                for (int i = 0; i < req.size(); i++) {
                    Object o = req.get(i);
                    if (o instanceof JLeaf) {
                        ((JLeaf) o).transform(transform, req, null);
                    }
                }
                culcSelectShape(req);
                dragPane.repaint();
            }
            e.consume();
            return;
        }
        super.keyReleased(e);
    }

    @Override
    public boolean controlChangeTool() {
        return false;
    }
}
