/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jobject.effector;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;
import jgeom.JComplexPath;
import jgeom.JSimplePath;
import jobject.JGroupObject;
import jobject.JLeaf;
import jobject.JObject;
import jobject.JPathObject;
import jobject.text.TextLocater;
import jpaint.JPaint;
import jpaint.JStroke;

/**
 *
 * @author takashi
 */
public class JArrowEffect implements JEffector {

    private Shape start = null,  end = null;
    private float startRatio = 1f,  endRatio = 1f;
    public static int START = 1;
    public static int END = 2;
    public static int BOTH = 3;

    public JArrowEffect(Shape st, Shape en, float stRat, float enRat) {
        start = st;
        end = en;
        startRatio = stRat;
        endRatio = enRat;
    }

    @Override
    public void paint(Graphics2D g, Shape s, JPaint fillPaint, JPaint border, JStroke stroke) {

        if (fillPaint != null) {
            g.setPaint(fillPaint);
            g.fill(s);
        }
        if (border == null || stroke == null || stroke.getWidth() == 0) {
            return;
        }
        g.setPaint(border);
        BasicStroke str = new BasicStroke(stroke.getWidth(), BasicStroke.CAP_BUTT, stroke.getLineJoin(), stroke.getMiterLimit(), stroke.getDashArray(), stroke.getDashPhase());
        g.setStroke(str);
        g.draw(s);
        if (start != null) {
            Vector<Shape> shapes = getArrows(start, s, START, stroke.getWidth() * startRatio);
            for (Shape sp : shapes) {
                g.fill(sp);
            }
        }
        if (end != null) {
            Vector<Shape> shapes = getArrows(end, s, END, stroke.getWidth() * endRatio);
            for (Shape sp : shapes) {
                g.fill(sp);
            }
        }

    }

    public static Vector<Shape> getArrows(Shape arrow, Shape dist, int direction, double width) {
        Vector<Shape> rt = null;
        PathIterator it = dist.getPathIterator(null);
        double[] coords = new double[6];
        boolean started = false;
        Point2D start = null, p0 = null, vec1 = null, vec2 = null, p;
        AffineTransform tx = null;
        while (!it.isDone()) {
            int type = it.currentSegment(coords);
            tx = null;
            switch (type) {
                case PathIterator.SEG_MOVETO:
                    if (p0 != null && vec1 != null && vec2 != null && direction == END) {
                        tx = getArrowTransform(p0, vec1, vec2, width);
                    }
                    started = true;
                    p0 = start = vec1 = new Point2D.Double(coords[0], coords[1]);
                    vec2 = null;
                    break;
                case PathIterator.SEG_LINETO:
                    p = new Point2D.Double(coords[0], coords[1]);
                    if (started && direction == START) {
                        tx = getArrowTransform(p0, p, vec1, width);
                    }
                    vec2 = p0;
                    p0 = vec1 = p;
                    started = false;
                    break;
                case PathIterator.SEG_CUBICTO:
                    p = new Point2D.Double(coords[0], coords[1]);
                    if (started && direction == START) {
                        tx = getArrowTransform(p0, p, vec1, width);
                    }
                    vec2 = new Point2D.Double(coords[2], coords[3]);
                    p0 = vec1 = new Point2D.Double(coords[4], coords[5]);
                    started = false;
                    break;
                case PathIterator.SEG_QUADTO:
                    p = new Point2D.Double(coords[0], coords[1]);
                    if (started && direction == START) {
                        tx = getArrowTransform(p0, p, vec1, width);
                    }
                    vec2 = p;
                    vec1 = p0 = new Point2D.Double(coords[2], coords[3]);
                    started = false;
                    break;
            }
            if (tx != null) {
                if (rt == null) {
                    rt = new Vector<Shape>();
                }
                rt.add(tx.createTransformedShape(arrow));
            }
            it.next();
        }

        if (p0 != null && vec1 != null && vec2 != null && direction==END) {
            if (rt == null) {
                rt = new Vector<Shape>();
            }
            tx = getArrowTransform(p0, vec2, vec1, width);
            rt.add(tx.createTransformedShape(arrow));
        }
        return rt;
    }

    public Shape getStartShape() {
        return start;
    }

    public Shape getEndShape() {
        return end;
    }

    public float getStartRatio() {
        return startRatio;
    }

    public float getEndRatio() {
        return endRatio;
    }

    /**
     *
     * @return
     */
    private static AffineTransform getArrowTransform(Point2D p0, Point2D vec1, Point2D vec2, double width) {
        if (p0 == null || vec1 == null || vec2 == null) {
            return null;
        }
        AffineTransform tx = new AffineTransform();
        tx.setToTranslation(p0.getX(), p0.getY());
        tx.scale(width, width);
        tx.rotate(vec1.getX() - vec2.getX(), vec1.getY() - vec2.getY());
        return tx;
    }

    /***
     * 与えられたグループオブジェクトから、正規化された矢印の形状を返す。
     * @param gs 矢印として与えられた形状
     * @return　正規化された形状
     */
    public static Shape createNormalShape(JGroupObject gs, double ratio) {
        //基準点を探す.
        Point2D cp = null;
        outer:
        for (JLeaf jl : gs.getLeafs()) {
            if (jl instanceof JPathObject) {
                JPathObject jp = (JPathObject) jl;
                JComplexPath jcp = jp.getPath();
                for (int i = 0; i < jcp.size(); i++) {
                    JSimplePath jsp = jcp.get(i);
                    if (jsp.size() == 1) {
                        cp = jsp.get(0).getAncur();
                        break outer;
                    }
                }
            }
        }
        //正規化
        GeneralPath gp = new GeneralPath();
        createGroupShape(gp, gs);
        //
        Rectangle2D bd = gp.getBounds2D();
        if (bd == null || bd.isEmpty()) {
            return null;
        }
        if (cp == null) {
            cp = new Point2D.Double(bd.getCenterX(), bd.getCenterY());
        }
        double rat = ratio * 2 / bd.getWidth();
        AffineTransform tx = new AffineTransform();
        tx.setToScale(rat, rat);
        tx.rotate(-Math.PI / 2);
        tx.translate(-cp.getX(), -cp.getY());
        return tx.createTransformedShape(gp);
    }

    private static void createGroupShape(GeneralPath gp, JLeaf jl) {
        if (jl instanceof JObject) {
            JObject jo = (JObject) jl;
            for (int i = 0; i < jo.size(); i++) {
                createGroupShape(gp, jo.get(i));
            }
        } else {
            Shape s = jl.getShape();
            if (s != null) {
                gp.append(s, false);
            }
        }
    }

    @Override
    public void paintText(Graphics2D g, TextLocater locater, AffineTransform tx, JPaint fillPaint, JPaint border, JStroke stroke) {
        if (locater == null) {
            return;
        }
        if (tx == null) {
            tx = new AffineTransform();
        }
        Shape s = locater.getOutlineShape(tx);

        if (fillPaint != null) {
            Graphics2D gg = (Graphics2D) g.create();
            //paint(g,s,fillPaint,border,stroke);
            AffineTransform rev = new AffineTransform();
            try {
                rev = tx.createInverse();
            } catch (NoninvertibleTransformException ex) {
            }
            gg.transform(tx);
            boolean isGrad = false;
            if (fillPaint.getPaintMode() == JPaint.LINEAR_GRADIENT_MODE || fillPaint.getPaintMode() == JPaint.RADIAL_GRADIENT_MODE) {
                fillPaint.transform(rev);
                isGrad = true;
            }
            gg.setPaint(fillPaint);
            locater.draw(gg);
            gg.dispose();
            if (isGrad) {
                fillPaint.transform(tx);
            }
        }
        if (border != null && stroke != null) {
            g.setStroke(stroke);
            g.setPaint(border);
            g.draw(s);
        }
    }

    @Override
    public Rectangle2D culcBounds(Rectangle2D r, JLeaf jl) {
        if (jl.getStrokePaint() == null || jl.getStroke() == null || jl.getStroke().getWidth() == 0) {
            return r;
        }
        double ml = 0;
        double w = jl.getStroke().getWidth();
        if (start != null) {
            Rectangle2D rc = start.getBounds2D();
            ml = Math.max(rc.getWidth(), rc.getHeight()) * w*startRatio;
        }
        if (end != null) {
            Rectangle2D rc = end.getBounds2D();
            ml = Math.max(ml, rc.getWidth() * w*startRatio);
            ml = Math.max(ml, rc.getHeight() * w*startRatio);
        }
        ml /= 2;
        r.setFrame(r.getX() - ml, r.getY() - ml, r.getWidth() + ml * 2, r.getHeight() + ml * 2);
        return r;
    }

    @Override
    public JEffector clone() {
        return new JArrowEffect(start, end, startRatio, endRatio);
    }
}
