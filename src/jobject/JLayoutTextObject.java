/*
 * JLayoutTextObject.java
 *
 * Created on 2007/10/29, 10:32
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jobject;

import jobject.text.InnerShapeTextLocater;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.undo.UndoableEdit;
import jgeom.JPathIterator;
import jpaint.JPaint;
import jpaint.JStroke;
import jscreen.JEnvironment;
import jscreen.JRequest;
import jobject.JText;
import jobject.text.TextLocater;

/**
 *
 * @author i002060
 */
public class JLayoutTextObject extends JPathObject implements JText, JColorable {

    private DefaultStyledDocument document;
    private transient GeneralPath lineShape = null;
//    private transient GeneralPath previewShape=null;
    private transient TextLocater locater = null;
    private transient Shape savedShape = null;
    private static final long serialVersionUID=110l;
    /** Creates a new instance of JLayoutTextObject */
    public JLayoutTextObject() {
        super(JEnvironment.currentTextFill, JEnvironment.currentTextBorder, JEnvironment.currentTextStroke);
        document = new DefaultStyledDocument();
    }

    public JLayoutTextObject(JPaint fillpaint, JPaint strokepaint, JStroke stroke) {
        super(fillpaint, strokepaint, stroke);
        document = new DefaultStyledDocument();
    }

    /**constructer for debug;*/
    public JLayoutTextObject(String s) {
        this();
        JPathIterator it = new JPathIterator(new Ellipse2D.Double(100, 100, 100, 100).getPathIterator(null));
        this.setPath(it.getJPath());
        SimpleAttributeSet attr = new SimpleAttributeSet();
        try {
            document.insertString(0, s, attr);
        } catch (Exception ex) {
        }
    }

    @Override
    public void paintThis(Rectangle2D clip, Graphics2D g) {
        if (!clip.intersects(getBounds())) {
            return;
        }
        if (lineShape == null || savedShape == null) {
            lineShape = createLineShape(getPath().getShape(), document, null, false);
            Rectangle2D r = this.getSelectionBounds();
            AffineTransform af = new AffineTransform();
            af.setToTranslation(r.getX(), r.getY());
            savedShape = af.createTransformedShape(lineShape);
        }
        AffineTransform af = new AffineTransform();
        Rectangle2D r = (new Area(getPath().getShape())).getBounds2D();
        af.translate(r.getX(), r.getY());
        effector.paintText(g, locater, af, fillPaint, strokePaint, stroke);
        //effector.paint(g, savedShape, fillPaint, strokePaint, stroke);
    }

    @Override
    public void paintPreview(JEnvironment env, JRequest req, Graphics2D g) {
        super.paintPreview(env, req, g);
        AffineTransform tr = getTransform();
//        if (tr==null || tr.isIdentity()) return;
        Shape s = getTransformedPath(req).getShape();
        if (locater == null) {
            locater = new InnerShapeTextLocater(document, s,null);
        }
        AffineTransform af = env.getToScreenTransform();
        Rectangle2D r = (new Area(s)).getBounds2D();
        af.translate(r.getX(), r.getY());
        Graphics2D tg = (Graphics2D) g.create();
        tg.transform(af);
        locater.drawBaseLine(tg);
        if (tr != null && !tr.isIdentity()) {
            locater.draw(tg);
        }
        tg.dispose();
    }

    @Override
    public UndoableEdit updateTransform(JEnvironment env) {
        lineShape = null;
        savedShape = null;
        return super.updateTransform(env);
    }

    @Override
    public UndoableEdit updateRotate(JEnvironment env, double rotation) {
        lineShape = null;
        savedShape = null;
        return super.updateRotate(env, rotation);
    }

    @Override
    public int hitByPoint(JEnvironment env, JRequest req, Point2D p) {
        if (isLocked() || !isVisible()) {
            return JRequest.HIT_NON;
        }
        int ret = super.hitByPoint(env, req, p);
        if (ret == JRequest.HIT_OBJECT) {
            ret = req.hitResult = JRequest.HIT_NON;
            req.hitObjects.clear();
        }
        if (lineShape==null && document.getLength()>0)
            lineShape = createLineShape(getPath().getShape(), document, null, false);
        if (ret == JRequest.HIT_NON) {
            double rad = env.getToScreenRatio();
            Rectangle2D r = new Rectangle2D.Double(p.getX() - rad, p.getY() - rad, rad * 2, rad * 2);
            if (lineShape != null) {
                Rectangle2D rb = this.getSelectionBounds();
                AffineTransform af = new AffineTransform();
                af.setToTranslation(rb.getX(), rb.getY());
                Shape s = af.createTransformedShape(lineShape);
                if (s.intersects(r)) {
                    ret = req.hitResult = JRequest.HIT_OBJECT;
                    req.hitObjects.add(this);
                }
            }
        }
        return ret;
    }

    @Override
    public void hitByRect(JEnvironment env, JRequest req, Rectangle2D rect) {
        if (isLocked() || !isVisible()) {
            return;
        }
        BasicStroke sStroke = new BasicStroke((float) (JEnvironment.SELECTION_STROKE_SIZE / env.getToScreenRatio()));
        for (int i = 0; i < getPath().size(); i++) {
            Shape ss = sStroke.createStrokedShape(getPath().get(i).getShape(getPath().getWindingRule()));
            if (ss.intersects(rect)) {
                req.hitObjects.add(getPath().get(i));
                if (!req.hitObjects.contains(this)) {
                    req.hitObjects.add(this);
                }
            }
        }
        if (req.hitObjects.contains(this)) {
            return;
        }
        if (savedShape != null && savedShape.intersects(rect)) {
            req.hitObjects.add(this);
        }
    }

    @Override
    public void transform(AffineTransform tr, JRequest req, Point p) {
        super.transform(tr, req, p);
        locater = null;
    // previewShape=null;
    }

    @Override
    public DefaultStyledDocument getStyledDocument() {
        return document;
    }

    @Override
    public DefaultStyledDocument getCloneStyledDocument() {
        return JTextObject.cloneDocument(document);
    }

    @Override
    public void setStyledDocument(DefaultStyledDocument doc) {
        document = doc;
        lineShape = null;
        //
        locater = null;
    //previewShape=null;
    }

    @Override
    public void updatePath() {
        lineShape = null;
        locater = null;
        //previewShape=null;
        savedShape = null;
        super.updatePath();
    }

    public GeneralPath createLineShape(Shape outlineShape, DefaultStyledDocument doc, FontRenderContext frc, boolean createBaseLine) {
        GeneralPath ret = new GeneralPath();
        if (doc.getLength() == 0) {
            return ret;
        }
        locater = createLocater(frc);
        return locater.getOutlineShape();
    }

    @Override
    public JPathObject clone() {
        JPaint fp = null,sp  = null;
        if (fillPaint != null) {
            fp = fillPaint.clone();
        }
        if (strokePaint != null) {
            sp = strokePaint.clone();
        }
        JLayoutTextObject ret = new JLayoutTextObject(fp, sp, stroke);
        ret.setPath(getPath().clone());
        ret.document = JTextObject.cloneDocument(document);
        ret.setEffector(getEffector());
        ret.updatePath();
        return ret;
    }

    @Override
    public Shape getShape() {
        if (lineShape == null) {
            lineShape = createLineShape(getPath().getShape(), document, null, false);
            Rectangle2D r = this.getSelectionBounds();
            AffineTransform af = new AffineTransform();
            af.setToTranslation(r.getX(), r.getY());
            savedShape = af.createTransformedShape(lineShape);
        }
        return savedShape;
    }

    @Override
    public void textUpdate(JEnvironment env) {
        env.addClip(getBounds());
        updatePath();
        env.addClip(getBounds());
    }

    @Override
    public AffineTransform getTotalTransform() {
        Rectangle2D r = this.getSelectionBounds();
        AffineTransform af = new AffineTransform();
        af.setToTranslation(r.getX(), r.getY());
        return af;
    }

    @Override
    public TextLocater createLocater(FontRenderContext frc) {
        if (frc==null){
            frc=new FontRenderContext(null,true,true);
        }
        return new InnerShapeTextLocater(getStyledDocument(), getPath().getShape(),frc);
    }

    @Override
    public Shape getLayoutShape() {
        return getPath().getShape();
    }

    public class LayoutIterator {

        private Area layoutArea;
        private float leftMergin;
        private float rightMergin;
        private int currentIndex = 0;
        Vector<LayoutLine> layoutLines = null;

        public LayoutIterator(Shape s, float leftMergin, float rightMergn) {
            GeneralPath gn = new GeneralPath();
            gn.append(s.getPathIterator(null, 0.01), false);
            layoutArea = new Area(gn);
            Rectangle2D rc = layoutArea.getBounds2D();
            AffineTransform af = new AffineTransform();
            af.setToTranslation(-rc.getX(), -rc.getY());
            layoutArea.transform(af);
            this.leftMergin = leftMergin;
            this.rightMergin = rightMergn;
        }

        public LayoutLine moveFirst(float verticalPos, float height) {
            layoutLines = createLayoutLines(verticalPos, height);
            currentIndex = 0;
            return current();
        }

        public LayoutLine current() {
            if (layoutLines == null || layoutLines.size() <= currentIndex) {
                return null;
            } else {
                return layoutLines.get(currentIndex);
            }
        }

        public LayoutLine next() {
            currentIndex++;
            return current();
        }

        private Vector<LayoutLine> createLayoutLines(float verticalPos, float height) {
            Vector<LayoutLine> ret = new Vector<LayoutLine>();
            Rectangle2D rb = layoutArea.getBounds2D();
            //if (verticalPos>rb.getHeight()) return ret;
            rb.setFrame(rb.getX(), verticalPos, rb.getWidth(), height);
            Area rArea = new Area(rb);
            rArea.intersect(layoutArea);
            if (rArea.isEmpty()) {
                return ret;
            }
            Vector<Double> px = new Vector<Double>(1);
            PathIterator path = rArea.getPathIterator(null, 0.1);
            double[] coords = new double[6];
            while (!path.isDone()) {
                int type = path.currentSegment(coords);
                if (type == PathIterator.SEG_LINETO || type == PathIterator.SEG_MOVETO) {
                    boolean sf = false;
                    for (int i = 0; i < px.size(); i++) {
                        if (px.get(i) == coords[0]) {
                            sf = true;
                            break;
                        }
                        if (px.get(i) > coords[0]) {
                            px.add(i, coords[0]);
                            sf = true;
                            break;
                        }
                    }
                    if (!sf) {
                        px.add(coords[0]);
                    }
                }
                path.next();
            }
            Rectangle2D.Double rc = new Rectangle2D.Double();
            float mv = 0.01f;
            for (int i = 0; i < px.size() - 1; i++) {
                rc.setFrame(px.get(i) + mv, rb.getY() + mv, px.get(i + 1) - px.get(i) - 2 * mv, height - 2 * mv);
                if (rArea.contains(rc)) {
                    float lx = px.get(i).floatValue() + leftMergin;
                    float lw = px.get(i + 1).floatValue() - lx - rightMergin;
                    if (lw > 0) {
                        ret.add(new LayoutLine(lx, lw));
                    }
                }
            }
            return ret;
        }
    }

    public class LayoutLine {

        float x;
        float width;

        public LayoutLine(float x, float width) {
            this.x = x;
            this.width = width;
        }

        public float getMax() {
            return x + width;
        }
    }
}
