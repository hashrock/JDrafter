/*
 * JBasicTool.java
 *
 * Created on 2007/08/29, 16:34
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
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;
import javax.swing.SwingUtilities;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import jedit.pathedit.JSetSegmentEdit;
import jgeom.JSegment;
import jgeom.JSimplePath;
import jobject.JLeaf;
import jobject.JPathObject;
import jscreen.JDragPane;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *
 * @author i002060
 */
public class JBasicTool extends JAbstractTool {

    /** Creates a new instance of JBasicTool */
    private Vector savedSelection;
    private AffineTransform transform;
    private boolean isPressing = false;
    private Object hitControl = null;
    private Object hitParent = null;

    public JBasicTool(JDragPane dragPane) {
        super(dragPane);
        presentationName = "ダイレクト選択ツール";
        wakeup();
    }

    private void hit(Point2D p) {
        JRequest req = getRequest();
        JEnvironment env = getEnvironment();
        req.hitResult = JRequest.HIT_NON;
        req.hitObjects.clear();
        Object hitCtl = null;
        Object hitPrt = null;
        for (int i = 0; i < req.size(); i++) {
            Object o = req.get(i);
            if (o instanceof JLeaf) {
                JLeaf jl = (JLeaf) o;
                int rs = jl.hitByPoint(env, req, p);
                for (Object obj : req.hitObjects) {
                    if (rs == JRequest.HIT_ANCUR) {
                        if (obj instanceof JSegment) {
                            hitCtl = obj;
                        } else if (obj instanceof JSimplePath) {
                            hitPrt = obj;
                        }
                    }
                    if (rs == JRequest.HIT_L_CONTROL || rs == JRequest.HIT_R_CONTROL) {
                        if (obj instanceof JPathObject.Handle) {
                            hitCtl = obj;
                        } else if (obj instanceof JSimplePath) {
                            hitPrt = obj;
                        }
                    }
                    if (rs == JRequest.HIT_PATH) {
                        if (obj instanceof JSimplePath) {
                            hitCtl = obj;
                            hitPrt = obj;
                            break;
                        }
                    }
                    if (rs == JRequest.HIT_OBJECT) {
                        break;
                    }
                }
                if (hitCtl != null) {
                    break;
                }
            }
            req.hitObjects.clear();
            req.hitResult = JRequest.HIT_NON;
        }

        //選択中のオブジェクトにヒットしなかった場合
        if (req.hitResult == JRequest.HIT_NON) {
            getViewer().getCurrentPage().hitByPoint(env, req, p);
            if (req.hitResult != JRequest.HIT_NON) {
                int rs = req.hitResult;
                for (Object obj : req.hitObjects) {
                    if (rs == JRequest.HIT_ANCUR) {
                        if (obj instanceof JSegment) {
                            hitCtl = obj;
                        } else if (obj instanceof JSimplePath) {
                            hitPrt = obj;
                        }
                    } else if (rs == JRequest.HIT_L_CONTROL || rs == JRequest.HIT_R_CONTROL) {
                        if (obj instanceof JPathObject.Handle) {
                            hitCtl = obj;
                        } else if (obj instanceof JSimplePath) {
                            hitPrt = obj;
                        }
                    } else if (rs == JRequest.HIT_PATH) {
                        if (obj instanceof JSimplePath) {
                            hitCtl = obj;
                            hitPrt = obj;
                            break;
                        }
                    } else if (rs == JRequest.HIT_OBJECT) {
                        if (obj instanceof JLeaf) {
                            hitCtl = obj;
                            hitPrt = obj;
                            break;
                        }
                    }
                }
            }
        }
        if (hitCtl != hitControl) {
            hitControl = hitCtl;
            hitParent = hitPrt;
            dragPane.repaint();
        }
    }

    @Override
    public void paint(Graphics2D g) {
        if (hitControl == null || transform != null) {
            super.paint(g);
            return;
        }
        double radius = JEnvironment.PATH_SELECTOR_SIZE * JEnvironment.HILIGHT_RATIO / getViewer().getEnvironment().getToScreenRatio();
        Point2D p = null;
        Shape s = null;
        boolean fillWhite = false;

        if (hitControl instanceof JSegment) {
            p = ((JSegment) hitControl).getAncur();
            s = new Rectangle2D.Double(p.getX() - radius / 2, p.getY() - radius / 2, radius, radius);
            if (!getViewer().getCurrentRequest().contains(hitControl)) {
                fillWhite = true;
            }
        } else if (hitControl instanceof JPathObject.Handle) {
            JPathObject.Handle h = (JPathObject.Handle) hitControl;
            if (h.place == JSegment.CONTROL1) {
                p = h.target.getControl1();
            } else if (h.place == JSegment.CONTROL2) {
                p = h.target.getControl2();
            }
            if (p != null) {
                s = new Ellipse2D.Double(p.getX() - radius / 2, p.getY() - radius / 2, radius, radius);
            }
        }
        if (!getViewer().getCurrentRequest().contains(hitParent)) {
            Shape os = null;
            if (hitParent instanceof JSimplePath) {
                os = ((JSimplePath) hitParent).getShape(PathIterator.WIND_EVEN_ODD);
            } else if (hitParent instanceof JLeaf) {
                os = ((JLeaf) hitParent).getShape();
            }
            if (os != null) {
                g.setColor(getViewer().getAvailableLayer().getPreviewColor().brighter());
                g.draw(getEnvironment().getToScreenTransform().createTransformedShape(os));
            }
        }
        if (s == null) {
            super.paint(g);
            return;
        }
        if (transform != null) {
            s = transform.createTransformedShape(s);
        }
        Color c = getViewer().getAvailableLayer().getPreviewColor().darker();
        if (fillWhite) {
            g.setColor(Color.WHITE);
        } else {
            g.setColor(c);
        }
        s = getViewer().getEnvironment().getToScreenTransform().createTransformedShape(s);
        g.fill(s);
        g.setColor(c);
        g.draw(s);
        super.paint(g);
    }

    @Override
    public void changeCursor() {
        JRequest req = getRequest();
        JCursor jc = dragPane.getJCursor();
        if (req.hitResult == JRequest.HIT_OBJECT) {
            if (isPressing) {
                if (req.isAltDown) {
                    setCursor(JCursor.COPY_AND_MOVE);
                } else {
                    setCursor(JCursor.MOVE);
                }
            } else {
                setCursor(JCursor.DIRECT_ON_OBJECT);
            }
            return;
        }
        if (req.hitResult == JRequest.HIT_ANCUR || req.hitResult == JRequest.HIT_L_CONTROL || req.hitResult == JRequest.HIT_R_CONTROL) {
            if (isPressing) {
                setCursor(JCursor.DIRECT_MOVE);
            } else {
                setCursor(JCursor.DIRECT_ON_SEGMENT);
            }
            return;
        }
        if (req.hitResult == JRequest.HIT_PATH) {
            if (isPressing) {
                if (req.isAltDown) {
                    setCursor(JCursor.DIRECT_MOVE_COPY);
                } else {
                    setCursor(JCursor.DIRECT_MOVE);
                }
            } else {
                setCursor(JCursor.DIRECT_ON_PATH);
            }
            return;
        }
        setCursor(JCursor.DIRECT);
    }

    @Override
    public void wakeup() {
        savedSelection = null;
        transform = null;
        getRequest().setSelectionMode(JRequest.DIRECT_MODE);
        dragPane.setPaintRect(true);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        JRequest req = getRequest();
        JEnvironment env = getEnvironment();
        Point2D p = new Point2D.Double();
        env.getToAbsoluteTransform().transform(e.getPoint(), p);
        hit(p);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            return;
        }
        JRequest req = getRequest();
        JEnvironment env = getEnvironment();
        req.hitObjects.clear();
        savedSelection = null;
        transform = null;
        req.hitResult = JRequest.HIT_NON;
        Point2D p = new Point2D.Double();
        env.getToAbsoluteTransform().transform(e.getPoint(), p);
//        Point2D p=env.getAbsoluteMousePoint(e.getPoint());
        hit(p);
        savedSelection = (Vector) req.getSelectedVector().clone();
        if (req.hitResult == JRequest.HIT_OBJECT || req.hitResult == JRequest.HIT_PATH ||
                req.hitResult == JRequest.HIT_ANCUR) {
            for (int i = 0; i < req.hitObjects.size(); i++) {
                Object o = req.hitObjects.get(i);
                req.add(o);
                if (o instanceof JSegment) {
                    JSegment seg = (JSegment) o;
                    if (req.hitResult == JRequest.HIT_ANCUR) {
                        dragPane.startPoint = seg.getAncur();
                    }
                } else if (o instanceof JPathObject.Handle) {
                    JPathObject.Handle hd = (JPathObject.Handle) o;
                    if (hd.place == JSegment.CONTROL1) {
                        dragPane.startPoint = hd.target.getControl1();
                    } else {
                        dragPane.startPoint = hd.target.getControl2();
                    }
                }
            }
        }
        isPressing = true;
        dragPane.repaint();
    }
    //ポイントの延長線上の点を取得
    private Point2D getLinePoint(Point2D cp) {
        JRequest req = getRequest();
        JSimplePath spath = null;
        JSegment seg = null;
        for (int i = 0; i < req.hitObjects.size(); i++) {
            if (req.hitObjects.get(i) instanceof JSimplePath) {
                spath = (JSimplePath) req.hitObjects.get(i);
                if (seg != null) {
                    break;
                }
            }
            if (req.hitObjects.get(i) instanceof JSegment) {
                seg = (JSegment) req.hitObjects.get(i);
                if (spath != null) {
                    break;
                }
            }
        }
        if (seg == null || spath == null || spath.isLooped() || spath.size() < 2) {
            return cp;
        }
        int idx = spath.indexOf(seg);
        Point2D.Double p0 = null, p1 = null, p2 = null,p3=null;
        if (idx == 0) {
            JSegment preSeg = spath.get(1);
            p0 = new Point2D.Double(preSeg.getAncur().getX(), preSeg.getAncur().getY());
            p1 = new Point2D.Double(seg.getAncur().getX(), seg.getAncur().getY());
        } else if (idx == spath.size() - 1) {
            JSegment preSeg = spath.get(idx - 1);
            p0 = new Point2D.Double(preSeg.getAncur().getX(), preSeg.getAncur().getY());
            p1 = new Point2D.Double(seg.getAncur().getX(), seg.getAncur().getY());
        }else{
            return cp;
        }
        p2=new Point2D.Double(p1.x-p0.x,p1.y-p0.y);
        p3=new Point2D.Double(cp.getX()-p0.x,cp.getY()-p0.y);
        double vr=p2.distance(0,0);
        p2.x /=vr;
        p2.y/=vr;
        double dst=p3.distance(0,0);
        p3.x/=dst;
        p3.y/=dst;
        double inr=p2.x*p3.x+p2.y*p3.y;
        p0.x +=p2.x*inr*dst;
        p0.y+=p2.y*inr*dst;
        return p0;
    }

    private void transformObjects(Point p, boolean isShiftDown, boolean isControlDown) {
        JRequest req = getRequest();
        JEnvironment env = getEnvironment();
        Point2D cp = env.getAbsoluteMousePoint(p,getViewer().getCurrentPage());
        Point2D sp = dragPane.getStartPoint();
        //アンカーが端点でかつコントロールキーが押されている場合直前の点からの延長線上へ固定
        if (req.hitResult == JRequest.HIT_ANCUR && isControlDown) {
            cp=getLinePoint(cp);
        }
        if (isShiftDown) {
            if (req.hitResult == JRequest.HIT_L_CONTROL || req.hitResult == JRequest.HIT_R_CONTROL) {
                for (int i = 0; i < req.hitObjects.size(); i++) {
                    Object op = req.hitObjects.get(i);
                    if (op instanceof JPathObject.Handle) {
                        JPathObject.Handle hn = (JPathObject.Handle) op;
                        cp = env.getShiftedMovePoint(hn.target.getAncur(), cp);
                        break;
                    }
                }
            } else {
                cp = env.getShiftedMovePoint(sp, cp);
            }
        }
        transform = new AffineTransform();
        transform.setToTranslation(cp.getX() - sp.getX(), cp.getY() - sp.getY());
        //選択Objectのふるい落とし
        if (dragPane.isFirstDragEvent() && !isShiftDown && req.hitResult != JRequest.HIT_L_CONTROL && req.hitResult != JRequest.HIT_R_CONTROL) {
            boolean contain = false;
            for (int i = 0; i < req.hitObjects.size(); i++) {
                Object o = req.hitObjects.get(i);
                if (req.hitResult == JRequest.HIT_OBJECT && !(o instanceof JLeaf)) {
                    continue;
                }
                if (req.hitResult == JRequest.HIT_PATH && !(o instanceof JSimplePath)) {
                    continue;
                }
                if (req.hitResult == JRequest.HIT_ANCUR && !(o instanceof JSegment)) {
                    continue;
                }
                if (contain = savedSelection.contains(o)) {
                    break;
                }
            }
            if (!contain) {
                req.clear();
            }
            for (int i = 0; i < req.hitObjects.size(); i++) {
                req.add(req.hitObjects.get(i));
            }
        }
        if (req.hitResult == JRequest.HIT_L_CONTROL || req.hitResult == JRequest.HIT_R_CONTROL) {
            for (int i = 0; i < req.hitObjects.size(); i++) {
                Object o = req.hitObjects.get(i);
                if (o instanceof JPathObject.Handle) {
                    JPathObject.Handle hn = (JPathObject.Handle) o;
                    //CTLキーダウンの場合
                    if (isControlDown) {
                        Point2D dp = new Point2D.Double(), p0, p1, p2;
                        env.getToAbsoluteTransform().transform(p, dp);
                        p0 = hn.target.getAncur();
                        p1 = hn.target.getControl1();
                        if (hn.place == JSegment.CONTROL2) {
                            p1 = hn.target.getControl2();
                        }
                        p2 = new Point2D.Double(p1.getX() - p0.getX(), p1.getY() - p0.getY());
                        Point2D p3 = new Point2D.Double(dp.getX() - p0.getX(), dp.getY() - p0.getY());
                        double dst = p2.distance(0, 0);
                        p2.setLocation(p2.getX() / dst, p2.getY() / dst);
                        dst = p3.distance(0, 0);
                        p3.setLocation(p3.getX() / dst, p3.getY() / dst);

                        double ist = p2.getX() * p3.getX() + p2.getY() * p3.getY();

                        dst = ist * dst;
                        p2.setLocation(p2.getX() * dst, p2.getY() * dst);

                        transform.setToTranslation(p2.getX() + p0.getX() - p1.getX(), p2.getY() + p0.getY() - p1.getY());
                    }
                    hn.parent.transform(transform, req, p);
                }
            }
        } else {
            for (int i = 0; i < req.size(); i++) {
                Object o = req.get(i);
                if (o instanceof JLeaf) {
                    ((JLeaf) o).transform(transform, req, p);
                }
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        JRequest req = getRequest();
        JEnvironment env = getEnvironment();
        if (req.hitResult == JRequest.HIT_NON) {
            return;
        }
        dragPane.setPaintRect(false);
        transformObjects(e.getPoint(), e.isShiftDown(), e.isControlDown());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            return;
        }
        JRequest req = getRequest();
        JEnvironment env = getEnvironment();
        //移動有
        if (transform != null) {
            CompoundEdit cEdit = null;
            if (req.hitResult == JRequest.HIT_L_CONTROL || req.hitResult == JRequest.HIT_R_CONTROL) {
                for (int i = 0; i < req.hitObjects.size(); i++) {
                    Object o = req.hitObjects.get(i);
                    if (o instanceof JPathObject.Handle) {
                        if (cEdit == null) {
                            cEdit = new CompoundEdit();
                        }
                        JPathObject.Handle jh = (JPathObject.Handle) o;
                        cEdit.addEdit(jh.parent.updateTransform(env));
                    }
                }
            } else {
                for (int i = 0; i < req.size(); i++) {
                    Object o = req.get(i);
                    if (o instanceof JLeaf) {
                        UndoableEdit anEdit = ((JLeaf) o).updateTransform(env);
                        if (anEdit != null) {
                            if (cEdit == null) {
                                cEdit = new CompoundEdit();
                            }
                            cEdit.addEdit(anEdit);
                        }
                    }
                }

            }
            if (cEdit != null) {
                JEnvironment.LAST_ROTATION = 0;
                JEnvironment.LAST_TRANSFORM = transform;
                JEnvironment.LAST_COPY = e.isAltDown();
                cEdit.end();
                getViewer().getDocument().fireUndoEvent(cEdit);


            }
        } //移動なし
        else if (req.hitResult != JRequest.HIT_L_CONTROL && req.hitResult != JRequest.HIT_R_CONTROL) {
            if (dragPane.getDragRect() != null) {
                req.hitObjects.clear();
                dragPane.getViewer().getCurrentPage().hitByRect(env, req, dragPane.getDragRect());
                req.hitResult = JRequest.HIT_ANCUR;
            }
            boolean contain = false;
            if (!e.isShiftDown()) {
                for (int i = 0; i < req.hitObjects.size(); i++) {
                    Object o = req.hitObjects.get(i);
                    if (req.hitResult == JRequest.HIT_OBJECT && !(o instanceof JLeaf)) {
                        continue;
                    }
                    if (req.hitResult == JRequest.HIT_PATH && !(o instanceof JSimplePath)) {
                        continue;
                    }
                    if (req.hitResult == JRequest.HIT_ANCUR && !(o instanceof JSegment)) {
                        continue;
                    }
                    if (contain = savedSelection.contains(o)) {
                        break;
                    }
                }
                if ((!contain || dragPane.getDragRect() != null)) {
                    req.clear();
                }
            }
            for (int i = 0; i < req.hitObjects.size(); i++) {
                Object o = req.hitObjects.get(i);
                if (savedSelection != null && savedSelection.contains(o) && e.isShiftDown()) {
                    if (req.hitResult == JRequest.HIT_OBJECT && o instanceof JLeaf ||
                            req.hitResult == JRequest.HIT_PATH && o instanceof JSimplePath ||
                            req.hitResult == JRequest.HIT_ANCUR && o instanceof JSegment) {
                        req.remove(o);
                    }
                } else {
                    req.add(o);
                }
            }
        } else {
            if (e.isControlDown()) {
                for (int i = 0; i < req.hitObjects.size(); i++) {
                    Object o = req.hitObjects.get(i);
                    if (o instanceof JPathObject.Handle) {
                        JPathObject.Handle hdl = (JPathObject.Handle) o;
                        hdl.parent.resetTransform();
                        Point2D an = hdl.target.getAncur();
                        Point2D c1 = hdl.target.getControl1();
                        Point2D c2 = hdl.target.getControl2();
                        if (req.hitResult == JRequest.HIT_L_CONTROL) {
                            c1 = null;
                        } else {
                            c2 = null;
                        }
                        getViewer().getDocument().
                                fireUndoEvent(new JSetSegmentEdit(getViewer(), hdl.parent, hdl.target, an, c1, c2));
                        break;
                    }
                }
            }
        }
        isPressing = false;
        transform = null;
        savedSelection = null;
        dragPane.setPaintRect(true);
        dragPane.repaint();

    }
    private boolean keyPressing = false;

    @Override
    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();
        JRequest req = getRequest();
        if (keyPressing) {
            return;
        }
        if ((k == KeyEvent.VK_SHIFT || k == KeyEvent.VK_ALT) && isPressing &&
                (req.hitResult == JRequest.HIT_ANCUR || req.hitResult == JRequest.HIT_OBJECT || req.hitResult == JRequest.HIT_PATH)) {

            Point p = dragPane.getMousePosition();
            if (p != null) {
                transformObjects(p, e.isShiftDown(), e.isControlDown());
                dragPane.repaint();
                e.consume();
            }
            keyPressing = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int k = e.getKeyCode();
        JRequest req = getRequest();
        if ((k == KeyEvent.VK_SHIFT || k == KeyEvent.VK_ALT) && isPressing &&
                (req.hitResult == JRequest.HIT_ANCUR || req.hitResult == JRequest.HIT_OBJECT || req.hitResult == JRequest.HIT_PATH)) {
            Point p = dragPane.getMousePosition();
            if (p != null) {
                transformObjects(p, e.isShiftDown(), e.isControlDown());
                dragPane.repaint();
                e.consume();
            }
            keyPressing = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_ALT && isPressing || req.hitResult != JRequest.HIT_NON) {
            e.consume();
        }
    }
    @Override
    public boolean controlChangeTool(){
        return false;
    }
}
