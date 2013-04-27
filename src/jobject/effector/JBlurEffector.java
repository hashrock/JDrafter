/*
 * JBlurEffector.java
 *
 * Created on 2007/12/24, 8:32
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jobject.effector;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.Kernel;
import java.io.Serializable;
import jobject.JLeaf;
import jobject.text.TextLocater;
import jpaint.JBlurPaint;
import jpaint.JPaint;
import jscreen.JEnvironment;
import jpaint.JStroke;

/**
 *オブジェクトの境界をぼかすEffectorです。
 * @author takashi
 */
public class JBlurEffector implements JEffector, Serializable {

    private float radius = 4f;
    private transient Shape savedShape = null;
    private transient BufferedImage buffer = null;
    private transient AffineTransform tx = null;
    private transient JPaint savedPaint = null;
    private static final long serialVersionUID = 110l;

    /** Creates a new instance of JBlurEffector */
    /**
     * デフォルトの属性で、JBlurEffectorを構築します.
     */
    public JBlurEffector() {
    }

    /**
     * 指定するぼかし半径の属性を持つJBlurEffectorを構築します.
     * @param radius ぼかしの半径
     */
    public JBlurEffector(float radius) {
        this.radius = radius;
    }

    /**
     * ぼかしの半径を取得します.
     * @return
     */
    public float getRadius() {
        return radius;
    }

    public Paint getFillPaint(Shape s, JPaint fillPaint) {
        if (fillPaint == null) {
            return null;
        }
        Color cf = null;
        if (fillPaint.getPaintMode() == JPaint.COLOR_MODE) {
            cf = fillPaint.getColor();
        } else {
            cf = Color.BLACK;
        }
        float alpha = cf.getAlpha() / 255f;
        return new JBlurPaint(s, radius, cf, alpha);
    }

    public Paint getStrokePaint(Shape s, JPaint strokePaint) {
        return null;
    }

    /**
     * エフェクトを描画します.
     * @param g 描画対象となるグラフィックスコンテキスト
     * @param s 描画するShape
     * @param fillPaint 塗りつぶしのPaint
     * @param border 境界のPaint
     * @param stroke 境界の線種
     */
    @Override
    public void paint(Graphics2D g, Shape s, JPaint fillPaint, JPaint border, JStroke stroke) {
        if (fillPaint != null || border != null) {
            if (!JDropShadowEffector.shapeEqual(s, savedShape) || buffer == null || tx == null || savedPaint != fillPaint) {
                Color cf = null;
                if (fillPaint != null) {
                    if (fillPaint.getPaintMode() == JPaint.COLOR_MODE) {
                        cf = fillPaint.getColor();
                    } else if (fillPaint.getPaintMode() == JPaint.PATTERN_MODE) {
                        cf = Color.BLACK;
                    } else {
                        cf = fillPaint.getColors()[0];
                    }
                }
                Color sf = null;
                Shape os = null;
                if (border != null) {
                    sf = border.getColor();
                    os = stroke.createStrokedShape(s);
                }
                Rectangle2D br = s.getBounds2D();
                if (os != null) {
                    br = os.getBounds2D();
                }
                br.setFrame(br.getX() - radius, br.getY() - radius, br.getWidth() + radius * 2, br.getHeight() + radius * 2);
                double ratio = JEnvironment.screenDPI / 72;
                buffer = new BufferedImage((int) (br.getWidth() * ratio), (int) (br.getHeight() * ratio), BufferedImage.TYPE_INT_ARGB);
                Graphics2D gb = buffer.createGraphics();
                tx = new AffineTransform();
                tx.setToScale(ratio, ratio);
                tx.translate(-br.getX(), -br.getY());
                gb.setTransform(tx);
                JBlurPaint jbp = null;
                if (cf != null) {
                    float alpha = cf.getAlpha() / 255f;
                    jbp = new JBlurPaint(s, radius, cf, alpha);
                    gb.setPaint(jbp);
                    gb.fill(br);
                }
                if (sf != null) {
                    float alpha = sf.getAlpha() / 255f;
                    jbp = new JBlurPaint(os, radius, sf, alpha);
                    gb.setPaint(jbp);
                    gb.fill(br);
                }
                try {
                    tx.invert();
                } catch (NoninvertibleTransformException ex) {
                    ex.printStackTrace();
                }
            }
            BufferedImageOp bop = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
            g.drawImage(buffer, bop, 0, 0);
            //g.drawImage(buffer, tx, null);
            savedShape = s;
            savedPaint = fillPaint;
        }
    }

    public void paint1(Graphics2D g, Shape s, JPaint fillPaint, JPaint border, JStroke stroke) {
        if (fillPaint != null || border != null) {
            if (!JDropShadowEffector.shapeEqual(s, savedShape) || buffer == null || tx == null || savedPaint != fillPaint) {
                Color sf = null;
                Shape os = null;
                if (border != null) {
                    sf = border.getColor();
                    os = stroke.createStrokedShape(s);
                }
                Rectangle2D br = s.getBounds2D();
                if (os != null) {
                    br = os.getBounds2D();
                }
                br.setFrame(br.getX() - radius, br.getY() - radius, br.getWidth() + radius * 2, br.getHeight() + radius * 2);
                double ratio = JEnvironment.screenDPI / 72;
                buffer = new BufferedImage((int) (br.getWidth() * ratio), (int) (br.getHeight() * ratio), BufferedImage.TYPE_INT_ARGB);
                Graphics2D gb = buffer.createGraphics();
                gb.setColor(new Color(255,255,255,0));
                gb.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());
                tx = new AffineTransform();
                tx.setToScale(ratio, ratio);
                tx.translate(-br.getX(), -br.getY());
                gb.setTransform(tx);
                //gb.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (fillPaint != null) {
                    gb.setPaint(fillPaint);
                    gb.fill(s);
                }
                if (border != null && stroke != null) {
                    gb.setPaint(border);
                    gb.setStroke(stroke);
                    gb.draw(s);
                }

                int size = (int) (radius * radius);
                float[] data = new float[size];
                float weight = 1f / size;
                for (int i = 0; i < size; i++) {
                    data[i] = weight;
                }
                Kernel kernel = new Kernel((int) radius, (int) radius, data);
                ConvolveOp cop = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
                gb.dispose();
                BufferedImage buffer2 = new BufferedImage(buffer.getWidth(), buffer.getHeight(), BufferedImage.TYPE_INT_ARGB);
                cop.filter(buffer, buffer2);
                buffer = null;
                buffer = buffer2;
                buffer2 = null;
                gb.dispose();
                try {
                    tx.invert();
                } catch (NoninvertibleTransformException ex) {
                    ex.printStackTrace();
                }
            }
            BufferedImageOp bop = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
            g.drawImage(buffer, bop, 0, 0);
            savedShape = s;
            savedPaint = fillPaint;
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
        Paint p = fillPaint;
        if (p != null) {
            paint(g, s, fillPaint, border, stroke);
        //Graphics2D gg=(Graphics2D)g.create();
        //gg.setPaint(p);
        //gg.transform(tx);
        //locater.draw(gg);
        //gg.dispose();
        }
        if (border != null && stroke != null) {
            g.setPaint(border);
            g.setStroke(stroke);
            g.draw(s);
        }
    }

    /**
     * 指定するデフォルトの描画領域を拡張し、このJEffectorの描画領域まで拡張します。
     * @param r 指定するデフォルトの描画領域
     * @return このJEffectorにより拡張された描画領域
     */
    @Override
    public Rectangle2D culcBounds(Rectangle2D r,JLeaf jl) {
        r.setFrame(r.getX() - radius, r.getY() - radius, r.getWidth() + radius * 2, r.getHeight() + radius * 2);
        return r;
    }

    /**
     * このJEffectorの複製を作成します.
     * @return
     */
    @Override
    public JEffector clone() {
        try {
            return (JEffector) super.clone();
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
