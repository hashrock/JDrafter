/*
 * JPathObject.java
 *
 * Created on 2007/08/28, 10:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jobject;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import jedit.JDuplicateObjectEdit;
import jedit.pathedit.JInsertPathEdit;
import jedit.JRotateObjectEdit;
import jedit.pathedit.JTransformControlEdit;
import jedit.JTransformObjectEdit;
import jedit.pathedit.JTransformPathEdit;
import jedit.pathedit.JTransformSegmentsEdit;
import jgeom.JComplexPath;
import jgeom.JSimplePath;
import jgeom.JPathIterator;
import jgeom.JSegment;
import jpaint.JPaint;
import jpaint.JStroke;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *
 * @author i002060
 */
public class JPathObject extends JLeaf<JObject> implements JColorable {

    private JComplexPath path;
    protected transient AffineTransform transform = null;//加えられたトランスフォーム
    protected transient Vector<JSimplePath> transformSimplePath = null;//トランスフォームの対象となるシンプルパス
    protected transient Vector<JSegment> transformAncur = null;//トランスフォームの対象となるセグメント
    protected transient Handle transformHandle = null;//ハンドル移動の対象となるセグメント;
    private transient boolean isFirstTime = true;
    private transient Shape drawingShape = null;
    //private transient Shape previewShape = null;
    //
    private static final long serialVersionUID = 110l;

    /**
     * Creates a new instance of JPathObject
     */
    public JPathObject() {
        path = new JComplexPath();
        fillPaint = JEnvironment.currentFill;
        if (fillPaint != null && fillPaint.getPaintMode() != JPaint.COLOR_MODE) {
            fillPaint = fillPaint.clone();
        }
        strokePaint = JEnvironment.currentBorder;
        stroke = JEnvironment.currentStroke;
    }

    public JPathObject(JPaint fillPaint, JPaint strokePaint, JStroke stroke) {
        this.fillPaint = fillPaint;
        if (this.fillPaint != null && this.fillPaint.getPaintMode() != JPaint.COLOR_MODE && this.fillPaint.getPaintMode() !=JPaint.PATTERN_MODE) {
            this.fillPaint = fillPaint.clone();
        }
        this.strokePaint = strokePaint;
        this.stroke = stroke;
        path = new JComplexPath();
    }

    public JPathObject(JComplexPath p) {
        path = p;
        fillPaint = JEnvironment.currentFill;
        if (fillPaint != null && fillPaint.getPaintMode() != JPaint.COLOR_MODE && fillPaint.getPaintMode()==JPaint.PATTERN_MODE) {
            fillPaint = fillPaint.clone();
        }
        strokePaint = JEnvironment.currentBorder;
        stroke = JEnvironment.currentStroke;
    }

    public JPathObject(Shape s) {
        path = (new JPathIterator(s.getPathIterator(null))).getJPath();
        fillPaint = JEnvironment.currentFill;
        if (fillPaint != null && fillPaint.getPaintMode() != JPaint.COLOR_MODE) {
            fillPaint = fillPaint.clone();
        }
        strokePaint = JEnvironment.currentBorder;
        stroke = JEnvironment.currentStroke;
    }

    public void setPath(JComplexPath path) {
        this.path = path;
        drawingShape = null;
    //previewShape = null;
    }

    @Override
    public Rectangle2D getSelectionBounds() {
        if (drawingShape == null) {
            drawingShape = path.getShape();
        }
        Shape s = drawingShape;
        Rectangle2D ret = new Area(s).getBounds2D();
        if (ret.isEmpty()) {
            ret = null;
            double radius = 0.00001;
            for (int i = 0; i < path.size(); i++) {
                JSimplePath sPath = path.get(i);
                for (int j = 0; j < sPath.size(); j++) {
                    JSegment sg = sPath.get(j);
                    Rectangle2D rd = new Rectangle2D.Double(sg.getAncur().getX() - radius / 2, sg.getAncur().getY() - radius / 2, radius, radius);
                    if (ret == null) {
                        ret = rd;
                    } else {
                        ret.add(rd);
                    }
                }
            }
            if (ret == null) {
                ret = new Rectangle2D.Double();
            }
        }
        return ret;
    }

    @Override
    public Rectangle2D getOriginalSelectionBounds(double x, double y) {
        AffineTransform af = new AffineTransform();
        af.setToRotation(-getTotalRotation(), x, y);
        if (drawingShape == null) {
            drawingShape = path.getShape();
        }
        Area a = new Area(af.createTransformedShape(drawingShape));
        Rectangle2D ret = a.getBounds2D();
        if (ret.isEmpty()) {
            ret = null;
            double radius = 0.00001;
            JComplexPath cPath = path.clone();
            cPath.transform(af);
            for (int i = 0; i < cPath.size(); i++) {
                JSimplePath sPath = cPath.get(i);
                for (int j = 0; j < sPath.size(); j++) {
                    JSegment sg = sPath.get(j);
                    Rectangle2D rd = new Rectangle2D.Double(sg.getAncur().getX() - radius / 2, sg.getAncur().getY() - radius / 2, radius, radius);
                    if (ret == null) {
                        ret = rd;
                    } else {
                        ret.add(rd);
                    }
                }
            }
            if (ret == null) {
                ret = new Rectangle2D.Double();
            }
        }
        return ret;
    }

    public Vector<JSegment> getTransformAncur() {
        return transformAncur;
    }

    public AffineTransform getTransform() {
        return transform;
    }

    @Override
    public void transform(AffineTransform tr, JRequest req, Point p) {
        transform = tr;
        if (!isFirstTime) {
            return;
        }
        isFirstTime = false;
        if (req.hitResult == JRequest.HIT_L_CONTROL || req.hitResult == JRequest.HIT_R_CONTROL) {
            transformSimplePath = null;
            transformAncur = null;
            transformHandle = (Handle) req.hitObjects.get(0);
        } else {
            transformHandle = null;
            if (req.hitResult == JRequest.HIT_ANCUR || req.hitResult == JRequest.HIT_PATH) {
                if (transformAncur == null) {
                    transformAncur = new Vector<JSegment>();
                }
                transformAncur.clear();
                boolean reqSegmentEmpty = true;
                for (int i = 0; i < req.size(); i++) {
                    Object o = req.get(i);
                    if (o instanceof JSegment) {
                        reqSegmentEmpty = false;
                        JSegment seg = (JSegment) o;
                        if (path.contains(seg)) {
                            transformAncur.add(seg);
                        }
                    }
                }
                if (transformAncur.isEmpty() && reqSegmentEmpty) {
                    transformAncur = null;
                    transformSimplePath = new Vector<JSimplePath>();
                    for (int i = 0; i < req.size(); i++) {
                        Object o = req.get(i);
                        if (o instanceof JSimplePath) {
                            JSimplePath spath = (JSimplePath) o;
                            if (path.contains(spath)) {
                                transformSimplePath.add(spath);
                            }
                        }
                    }
                    if (transformSimplePath.isEmpty()) {
                        transformSimplePath = null;
                    }
                } else {
                    transformSimplePath = null;
                }
            } else {
                transformAncur = null;
            }
        }
    }

    public void transformPath(AffineTransform tr) {
        path.transform(tr);
        drawingShape = null;
    //previewShape = null;
    }
    @Override
    public void transform(AffineTransform tr){
        path.transform(tr);
        if (fillPaint !=null &&(fillPaint.getPaintMode()==JPaint.LINEAR_GRADIENT_MODE || fillPaint.getPaintMode()==JPaint.RADIAL_GRADIENT_MODE)){
            fillPaint.transform(tr);
        }
        drawingShape=null;
    }
    public JComplexPath getPath() {
        return path;
    }

    public JComplexPath getTransformedPath(JRequest req) {
        if (transform == null) {
            return path;
        }
        JComplexPath ret = path.clone();
        if (transformHandle != null) {
            JSegment rseg = ret.getSegment(path.indexOf(transformHandle.target));
            if (req.isAltDown) {
                rseg.setJoined(false);
            }
            rseg.transformControl(transform, transformHandle.place);
        } else if (transformAncur != null) {
            for (int i = 0; i < transformAncur.size(); i++) {
                int idx = path.indexOf(transformAncur.get(i));
                ret.getSegment(idx).transform(transform);
            }
        } else if (transformSimplePath != null) {
            for (int i = 0; i < transformSimplePath.size(); i++) {
                int idx = path.indexOf(transformSimplePath.get(i));
                ret.get(idx).transform(transform);
            }
        } else {
            ret.transform(transform);
        }
        return ret;
    }

    @Override
    public UndoableEdit updateTransform(JEnvironment env) {
        UndoableEdit ret = null;
        JDocumentViewer viewer = getDocument().getViewer();
        JRequest req = viewer.getCurrentRequest();
        if (req.getSelectionMode() == JRequest.GROUP_MODE) {
            transformHandle = null;
            transformAncur = null;
            transformSimplePath = null;
        }
        if (transform != null && !transform.isIdentity()) {
            if (transformHandle != null) {
                JSegment aSeg = transformHandle.target.clone();
                if (req.isAltDown) {
                    aSeg.setJoined(false);
                }
                aSeg.transformControl(transform, transformHandle.place);
                ret = new JTransformControlEdit(viewer, this, transformHandle.target, aSeg);
            } else if (transformAncur != null) {
                ret = new JTransformSegmentsEdit(viewer, this, transformAncur, transform);
            } else if (transformSimplePath != null) {
                if (req.isAltDown && transform.getType() == AffineTransform.TYPE_TRANSLATION) {
                    CompoundEdit cEdit = new CompoundEdit();
                    cEdit.addEdit(new JInsertPathEdit(viewer, this, transformSimplePath));
                    cEdit.addEdit(new JTransformPathEdit(viewer, this, transformSimplePath, transform));
                    cEdit.end();
                    ret = cEdit;
                } else {
                    ret = new JTransformPathEdit(viewer, this, transformSimplePath, transform);
                }
            } else {
                if (req.isAltDown && transform.getType() == AffineTransform.TYPE_TRANSLATION) {
                    CompoundEdit cEdit = new CompoundEdit();
                    cEdit.addEdit(new JDuplicateObjectEdit(viewer, this));
                    cEdit.addEdit(new JTransformObjectEdit(viewer, this, transform));
                    cEdit.end();
                    ret = cEdit;
                } else {
                    ret = new JTransformObjectEdit(viewer, this, transform);
                }
            }
        }
        transform = null;
        transformAncur = null;
        transformSimplePath = null;
        transformHandle = null;
        isFirstTime = true;
        return ret;
    }

    public void resetTransform() {
        transform = null;
        transformAncur = null;
        transformSimplePath = null;
        isFirstTime = true;
        transformHandle = null;
    }

    @Override
    public UndoableEdit updateRotate(JEnvironment env, double rotation) {
        UndoableEdit ret = new JRotateObjectEdit(getDocument().getViewer(), this, transform, rotation);
        transform = null;
        transformAncur = null;
        transformSimplePath = null;
        transformHandle = null;
        isFirstTime = true;
        return ret;
    }

    @Override
    public int hitByPoint(JEnvironment env, JRequest req, Point2D point) {
        if (isLocked() || !isVisible()) {
            return JRequest.HIT_NON;
        }
        if (drawingShape == null) {
            drawingShape = path.getShape();
        }
        Shape s = drawingShape;
        Rectangle2D bounds = drawingShape.getBounds2D();
        if (bounds.isEmpty()) {
            double width = JEnvironment.SELECTION_STROKE_SIZE / env.getToScreenRatio();
            s = (new BasicStroke((float) width)).createStrokedShape(s);
            if (s.getBounds().isEmpty()) {
                GeneralPath gp = new GeneralPath();
                for (int i = 0; i < path.size(); i++) {
                    JSimplePath sPath = path.get(i);
                    for (int j = 0; j < sPath.size(); j++) {
                        Point2D p = sPath.get(j).getAncur();
                        bounds = new Rectangle2D.Double(p.getX() - width / 2, p.getY() - width / 2, width, width);
                        gp.append(bounds, false);
                    }
                }
                s = gp;
            }
        }
        if (req.getSelectionMode() == JRequest.GROUP_MODE) {
            if (!s.contains(point)) {
                Shape inner = drawingShape;
                BasicStroke sStroke = new BasicStroke((float) (JEnvironment.SELECTION_STROKE_SIZE / env.getToScreenRatio()));
                Shape outer = sStroke.createStrokedShape(inner);
                if (outer.contains(point)) {
                    req.hitObjects.add(this);
                    req.hitResult = JRequest.HIT_OBJECT;
                    return req.hitResult;
                }
                return JRequest.HIT_NON;
            } else if (fillPaint != null && !(getLayer() instanceof JGuidLayer)) {
                req.hitObjects.add(this);
                req.hitResult = JRequest.HIT_OBJECT;
                return req.hitResult;
            }
        }
        int ret;
        if ((ret = hitDirectly(env, req, point)) != JRequest.HIT_NON) {
            req.hitResult = ret;
            return ret;
        }
        req.hitResult = hitGroup(env, req, point);
        return req.hitResult;
    }

    private int hitGroup(JEnvironment env, JRequest req, Point2D point) {
        if (drawingShape == null) {
            drawingShape = path.getShape();
        }
        Shape inner = drawingShape;
        BasicStroke sStroke = new BasicStroke((float) (JEnvironment.SELECTION_STROKE_SIZE / env.getToScreenRatio()));
        Shape outer = sStroke.createStrokedShape(inner);
        if (outer.contains(point)) {
            req.hitObjects.add(this);
            for (int i = 0; i < path.size(); i++) {
                JSimplePath jp = path.get(i);
                Shape ot = sStroke.createStrokedShape(jp.getShape(path.getWindingRule()));
                if (ot.contains(point)) {
                    req.hitObjects.add(jp);
                }
            }
            return JRequest.HIT_PATH;
        }
        if (inner.contains(point) && fillPaint != null && !(getLayer() instanceof JGuidLayer)) {
            req.hitObjects.add(this);
            for (int i = 0; i < path.size(); i++) {
                req.hitObjects.add(path.get(i));
            }
            return JRequest.HIT_OBJECT;
        }
        return JRequest.HIT_NON;
    }

    private int hitDirectly(JEnvironment env, JRequest req, Point2D point) {
        double radius = JEnvironment.PATH_SELECTOR_SIZE * JEnvironment.HILIGHT_RATIO / env.getToScreenRatio() / 2;
        Rectangle2D.Double hitRect = new Rectangle2D.Double(point.getX() - radius, point.getY() - radius, radius * 2, radius * 2);
        //is Handle hitting ?
        for (int i = path.size() - 1; i >= 0; i--) {
            //Rectangle2D sp=path.get(i).getShape(path.getWindingRule()).getBounds2D();
            //sp.setFrame(sp.getX()-radius,sp.getY()-radius,sp.getWidth()+radius*2,sp.getHeight()+radius*2);
            //if (!sp.contains(point)) continue;
            for (int j = path.get(i).size() - 1; j >= 0; j--) {
                JSegment seg = path.get(i).get(j);
                //if (!seg.getSegmentRect(env).contains(point)) continue;
                if (req.contains(seg) && req.getSelectionMode() == JRequest.DIRECT_MODE) {
                    if (seg.getControl1() != null) {
                        if (hitRect.contains(seg.getControl1())) {
                            req.hitObjects.add(new Handle(this, seg, JSegment.CONTROL1));
                            return JRequest.HIT_L_CONTROL;
                        }
                    }
                    if (seg.getControl2() != null) {
                        if (hitRect.contains(seg.getControl2())) {
                            req.hitObjects.add(new Handle(this, seg, JSegment.CONTROL2));
                            return JRequest.HIT_R_CONTROL;
                        }
                    }
                    JSegment prev = path.prevSegment(seg);
                    if (prev != null && !req.contains(prev) && prev.getControl2() != null) {
                        if (hitRect.contains(prev.getControl2())) {
                            req.hitObjects.add(new Handle(this, prev, JSegment.CONTROL2));
                            return JRequest.HIT_R_CONTROL;
                        }
                    }
                    JSegment next = path.nextSegment(seg);
                    if (next != null && !req.contains(next) && next.getControl1() != null) {
                        if (hitRect.contains(next.getControl1())) {
                            req.hitObjects.add(new Handle(this, next, JSegment.CONTROL1));
                            return JRequest.HIT_L_CONTROL;
                        }
                    }
                }
                if (hitRect.contains(seg.getAncur())) {
                    if (req.getSelectionMode() == JRequest.DIRECT_MODE) {
                        req.hitObjects.add(seg);
                    }
                    req.hitObjects.add(path.getOwnerPath(seg));
                    req.hitObjects.add(this);
                    return JRequest.HIT_ANCUR;
                }

            }

        }
        /*
         * for (int i=0;i<path.segmentSize();i++){
         * JSegment seg=path.getSegment(i);
         * if (req.contains(seg)  && req.getSelectionMode()==req.DIRECT_MODE){
         * if (seg.getControl1() != null){
         * controlCircle.x=seg.getControl1().getX()-radius/2;
         * controlCircle.y=seg.getControl1().getY()-radius/2;
         * if (controlCircle.contains(point)){
         * req.hitObjects.add(new Handle(this,seg,seg.CONTROL1));
         * return req.HIT_L_CONTROL;
         * }
         * }
         * if (seg.getControl2()!=null){
         * controlCircle.x=seg.getControl2().getX()-radius/2;
         * controlCircle.y=seg.getControl2().getY()-radius/2;
         * if (controlCircle.contains(point)){
         * req.hitObjects.add(new Handle(this,seg,seg.CONTROL2));
         * return req.HIT_R_CONTROL;
         * }
         * }
         * JSegment prev=path.prevSegment(seg);
         * if (prev != null && !req.contains(prev) && prev.getControl2() != null ){
         * controlCircle.x=prev.getControl2().getX()-radius/2;
         * controlCircle.y=prev.getControl2().getY()-radius/2;
         * if (controlCircle.contains(point)){
         * req.hitObjects.add(new Handle(this,prev,prev.CONTROL2));
         * return req.HIT_R_CONTROL;
         * }
         * }
         * JSegment next=path.nextSegment(seg);
         * if (next != null && !req.contains(next) && next.getControl1() != null ){
         * controlCircle.x=next.getControl1().getX()-radius/2;
         * controlCircle.y=next.getControl1().getY()-radius/2;
         * if (controlCircle.contains(point)){
         * req.hitObjects.add(new Handle(this,next,next.CONTROL1));
         * return req.HIT_L_CONTROL;
         * }
         * }
         *
         * }
         * }
         */
        /*
        //is Ancur Hitting ?;
        for (int i=0;i<path.segmentSize();i++){
        JSegment seg=path.getSegment(i);
        ancurRect.x=seg.getAncur().getX()-radius/2;
        ancurRect.y=seg.getAncur().getY()-radius/2;
        if (ancurRect.contains(point)) {
        if(req.getSelectionMode()==req.DIRECT_MODE){
        req.hitObjects.add(seg);
        }
        req.hitObjects.add(path.getOwnerPath(seg));
        req.hitObjects.add(this);
        return req.HIT_ANCUR;
        }
        }
         */
        return JRequest.HIT_NON;
    }

    @Override
    public void hitByRect(JEnvironment env, JRequest req, Rectangle2D rect) {
        if (isLocked() || !isVisible()) {
            return;
        }
        if (drawingShape == null) {
            drawingShape = path.getShape();
        }
        Shape s = drawingShape;
        if (s.getBounds2D().isEmpty()) {
            double width = JEnvironment.SELECTION_STROKE_SIZE / env.getToScreenRatio();
            s = (new BasicStroke((float) width)).createStrokedShape(s);
            if (s.getBounds2D().isEmpty()) {
                GeneralPath gp = new GeneralPath();
                for (int i = 0; i < path.size(); i++) {
                    JSimplePath sp = path.get(i);
                    for (int j = 0; j < sp.size(); j++) {
                        Point2D p = sp.get(j).getAncur();
                        Rectangle2D rc = new Rectangle2D.Double(p.getX() - width / 2, p.getY() - width / 2, width, width);
                        gp.append(rc, false);
                    }
                }
                s = gp;
            }
        }
        if (!s.intersects(rect)) {
            return;
        }
        if (req.getSelectionMode() == JRequest.GROUP_MODE && fillPaint != null && !(getLayer() instanceof JGuidLayer)) {
            req.hitObjects.add(this);
            return;
        }
        for (int i = 0; i < path.segmentSize(); i++) {
            JSegment seg = path.getSegment(i);
            if (rect.contains(seg.getAncur())) {
                if (req.getSelectionMode() == JRequest.GROUP_MODE) {
                    req.hitObjects.add(this);
                    return;
                } else {
                    req.hitObjects.add(seg);
                    JSimplePath spath = path.getOwnerPath(seg);
                    if (!req.hitObjects.contains(spath)) {
                        req.hitObjects.add(path.getOwnerPath(seg));
                    }
                    if (!req.hitObjects.contains(this)) {
                        req.hitObjects.add(this);
                    }
                }

            }
        }

        BasicStroke sStroke = new BasicStroke((float) (JEnvironment.SELECTION_STROKE_SIZE / env.getToScreenRatio()));
        for (int i = 0; i < path.size(); i++) {
            Shape ss = sStroke.createStrokedShape(path.get(i).getShape(path.getWindingRule()));
            if (ss.intersects(rect)) {
                req.hitObjects.add(path.get(i));
                if (!req.hitObjects.contains(this)) {
                    req.hitObjects.add(this);
                }
            }
        }
        if (req.hitObjects.contains(this)) {
            return;
        }
        Shape inner = path.getShape();
        if (inner.intersects(rect) && fillPaint != null &&
                req.getSelectionMode() == JRequest.GROUP_MODE && !(getLayer() instanceof JGuidLayer)) {
            req.hitObjects.add(this);
        }
    }

    @Override
    public void paintThis(Rectangle2D clip, Graphics2D g) {
        if (drawingShape == null) {
            drawingShape = path.getShape();
        }
        Rectangle2D clipBounds = getBounds();
        if (drawingShape == null || clipBounds == null || clipBounds.isEmpty()) {
            return;
        }
        if (clip == null) {
            return;
        }
        if (!clip.intersects(clipBounds)) {
            return;
        }
        JLayer layer = getLayer();
        if (layer != null && layer instanceof JGuidLayer) {
            JGuidLayer jgl = (JGuidLayer) layer;
            if (jgl.isDotStyle()) {
                g.setStroke(JGuidLayer.DOT_STYLE_STROKE);
            } else {
                g.setStroke(JGuidLayer.LINE_STYLE_STROKE);
            }
            g.setColor(jgl.getGuidColor());
            Shape  s=drawingShape;
            Object key=g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            double rad=JEnvironment.PATH_SELECTOR_SIZE/2;
            JPage page=getPage();
            if (page !=null){
                JEnvironment env=page.getEnvironment();
                rad /=env.getToScreenRatio();
            }
            for (int i=0;i<path.size();i++){
                JSimplePath pth=path.get(i);
                if (pth.size()==1){
                    GeneralPath gp=new GeneralPath();
                    Point2D p=pth.get(0).getAncur();
                    gp.moveTo(p.getX()-rad, p.getY()-rad);
                    gp.lineTo(p.getX()+rad,p.getY()+ rad);
                    gp.moveTo(p.getX()-rad, p.getY()+rad);
                    gp.lineTo(p.getX()+rad, p.getY()-rad);
                    g.draw(gp);
                }
            }
            g.draw(drawingShape);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, key);
        } else {
            effector.paint(g, drawingShape, fillPaint, strokePaint, stroke);
        }
    }

    private void normalizeSelection(JRequest req) {
        if (!req.contains(this)) {
            return;
        }
        for (int i = 0; i < path.size(); i++) {
            if (req.contains(path.get(i))) {
                return;
            }
        }
        for (int i = 0; i < path.size(); i++) {
            req.add(path.get(i));
        }
    }

    private void drawSelector(Graphics2D g, PathIterator pt, int mode) {
        Rectangle drawHere = g.getClipBounds();
        float[] coords = new float[6];
        int radius = (int) JEnvironment.SELECTION_STROKE_SIZE;
        Rectangle r = new Rectangle(0, 0, radius, radius);
        while (!pt.isDone()) {
            int type = pt.currentSegment(coords);
            boolean p = true;
            if (type == PathIterator.SEG_MOVETO || type == PathIterator.SEG_LINETO) {
                r.x = (int) coords[0] - radius / 2;
                r.y = (int) coords[1] - radius / 2;

            } else if (type == PathIterator.SEG_QUADTO) {
                r.x = (int) coords[2] - radius / 2;
                r.y = (int) coords[3] - radius / 2;

            } else if (type == PathIterator.SEG_CUBICTO) {
                r.x = (int) coords[4] - radius / 2;
                r.y = (int) coords[5] - radius / 2;
            } else {
                p = false;
            }
            if (drawHere.contains(r.x, r.y)) {
                g.fill(r);
            }
            pt.next();
        }
    }

    @Override
    public void paintPreview(JEnvironment env, JRequest req, Graphics2D g) {
        AffineTransform af = env.getToScreenTransform();
        JComplexPath tPath = getTransformedPath(req);
        //Shape s=tPath.getShape();
        normalizeSelection(req);
        g.setColor(getPreviewColor());
//      g.draw(af.createTransformedShape(s));
        double radius = JEnvironment.PATH_SELECTOR_SIZE;
        Rectangle2D.Double ancurRect = new Rectangle2D.Double(0, 0, radius, radius);
        Ellipse2D.Double ctlCircle = new Ellipse2D.Double(0, 0, radius, radius);
        Point2D.Double p = new Point2D.Double(), c1 = new Point2D.Double(), c2 = new Point2D.Double();
        for (int pi = 0; pi < path.size(); pi++) {
            JSimplePath spath = path.get(pi);
            JSimplePath opath = tPath.get(pi);
            if (req.getSelectionMode() == JRequest.DIRECT_MODE && !req.contains(spath)) {
                continue;
            }
            Shape shapeToDraw = af.createTransformedShape(opath.getShape(path.getWindingRule()));
            g.draw(shapeToDraw);
            /*            if (opath.isLooped()){
            Rectangle2D or=shapeToDraw.getBounds2D();
            int cx=(int)or.getCenterX();
            int cy=(int)or.getCenterY();
            int rad=(int)(radius/2);
            g.drawLine(cx-rad,cy-rad, cx+rad, cy+rad);
            g.drawLine(cx+rad, cy-rad, cx-rad, cy+rad);
            }
             */
            //if (transform !=null) continue;
            for (int i = 0; i < spath.size(); i++) {
                JSegment seg = opath.get(i);
                JSegment oSeg = spath.get(i);
                af.transform(seg.getAncur(), p);
                ancurRect.x = p.x - radius / 2;
                ancurRect.y = p.y - radius / 2;
                if (req.getSelectionMode() == JRequest.GROUP_MODE) {
                    g.fill(ancurRect);
                } else {
                    if (req.contains(oSeg)) {
                        if (seg.getControl1() != null) {
                            af.transform(seg.getControl1(), c1);
                            ctlCircle.x = c1.x - radius / 2;
                            ctlCircle.y = c1.y - radius / 2;
                            g.drawLine((int) p.x, (int) p.y, (int) c1.x, (int) c1.y);
                            g.fill(ctlCircle);
                        }
                        if (seg.getControl2() != null) {
                            af.transform(seg.getControl2(), c2);
                            ctlCircle.x = c2.x - radius / 2;
                            ctlCircle.y = c2.y - radius / 2;
                            g.drawLine((int) p.x, (int) p.y, (int) c2.x, (int) c2.y);
                            g.fill(ctlCircle);
                        }
                        g.fill(ancurRect);
                        JSegment pSeg = tPath.prevSegment(seg);
                        if (pSeg != null && !req.contains(pSeg) && pSeg.getControl2() != null) {
                            af.transform(pSeg.getAncur(), p);
                            af.transform(pSeg.getControl2(), c1);
                            ctlCircle.x = c1.x - radius / 2;
                            ctlCircle.y = c1.y - radius / 2;
                            g.drawLine((int) p.x, (int) p.y, (int) c1.x, (int) c1.y);
                            g.fill(ctlCircle);
                            ancurRect.x = p.x - radius / 2;
                            ancurRect.y = p.y - radius / 2;
                            g.setColor(Color.WHITE);
                            g.fill(ancurRect);
                            g.setColor(getPreviewColor());
                            g.draw(ancurRect);
                        }
                        pSeg = tPath.nextSegment(seg);
                        if (pSeg != null && !req.contains(pSeg) && pSeg.getControl1() != null) {
                            af.transform(pSeg.getAncur(), p);
                            af.transform(pSeg.getControl1(), c1);
                            ctlCircle.x = c1.x - radius / 2;
                            ctlCircle.y = c1.y - radius / 2;
                            g.drawLine((int) p.x, (int) p.y, (int) c1.x, (int) c1.y);
                            g.fill(ctlCircle);
                            ancurRect.x = p.x - radius / 2;
                            ancurRect.y = p.y - radius / 2;
                            g.setColor(Color.WHITE);
                            g.fill(ancurRect);
                            g.setColor(getPreviewColor());
                            g.draw(ancurRect);
                        }
                    } else {
                        g.setColor(Color.WHITE);
                        g.fill(ancurRect);
                        g.setColor(getPreviewColor());
                        g.draw(ancurRect);
                    }
                }
            }
        }

    }

    @Override
    public JPathObject clone() {
        JPathObject ret = new JPathObject(path.clone());
        if (fillPaint != null) {
            if (fillPaint.getPaintMode()==JPaint.COLOR_MODE || fillPaint.getPaintMode()==JPaint.PATTERN_MODE)
                ret.setFillPaint(fillPaint);
            else
                ret.setFillPaint(fillPaint.clone());
        } else {
            ret.setFillPaint(null);
        }
        if (strokePaint != null) {
            ret.setStrokePaint(strokePaint);
            ret.setStroke(stroke);
        } else {
            ret.setStrokePaint(null);
            //ret.setStroke(stroke);
        }
        ret.totalRotation = totalRotation;
        ret.setEffector(getEffector().clone());
        return ret;
    }

    @Override
    public String getPrefixer() {
        return "Path";
    }

    /**トランスフォーム以外のパス編集が発生したときに呼び出される。
     *継承クラス用
     */
    @Override
    public void updatePath() {
        drawingShape = null;
    //previewShape = null;
    }

    @Override
    public Rectangle2D getBounds() {
        if (drawingShape == null) {
            drawingShape = path.getShape();
        }
        Rectangle2D ret = drawingShape.getBounds2D();
        if (strokePaint != null || getLayer() instanceof JGuidLayer) {
            float w = stroke.getWidth() / 2;
            if (stroke.getLineJoin() == BasicStroke.JOIN_MITER) {
                w += stroke.getMiterLimit() * stroke.getWidth();
            }
            ret.setFrame(ret.getX() - w, ret.getY() - w, ret.getWidth() + 2 * w, ret.getHeight() + 2 * w);
        }
        //ret=s.getBounds2D();
        if (ret.isEmpty()) {
            ret = null;
            double radius = 0.00001;
            for (int i = 0; i < path.size(); i++) {
                JSimplePath sPath = path.get(i);
                for (int j = 0; j < sPath.size(); j++) {
                    JSegment sg = sPath.get(j);
                    Rectangle2D r = new Rectangle2D.Double(sg.getAncur().getX() - radius / 2, sg.getAncur().getY() - radius / 2, radius, radius);
                    if (ret == null) {
                        ret = r;
                    } else {
                        ret.add(r);
                    }
                }
            }
            if (ret == null) {
                ret = new Rectangle2D.Double();
            }
        }
        if (getLayer() instanceof JGuidLayer){
            double rad=JEnvironment.PATH_SELECTOR_SIZE;
            ret.setRect(ret.getX()-rad/2,ret.getY()-rad/2,ret.getWidth()+rad,ret.getHeight()+rad);
        }
        return effector.culcBounds(ret,this);
    }

    @Override
    public Shape getShape() {
        if (drawingShape == null) {
            drawingShape = path.getShape();
        }
        return drawingShape;
    }

    @Override
    public boolean canBeGuide() {
        return true;
    }

    public class Handle {

        public JPathObject parent;
        public JSegment target;
        public int place;

        public Handle(JPathObject po, JSegment seg, int pl) {
            parent = po;
            target = seg;
            place = pl;
        }

        @Override
        public boolean equals(Object dist) {
            if (!(dist instanceof Handle)) {
                return false;
            }
            Handle ds = (Handle) dist;
            return (parent == ds.parent && target == ds.target && place == ds.place);
        }
    }
}
