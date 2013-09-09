/*
 * JDropShadowEffector.java
 *
 * Created on 2007/12/24, 17:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jobject.effector;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import jobject.JLeaf;
import jobject.text.TextLocater;
import jpaint.JBlurPaint;
import jpaint.JPaint;
import jscreen.JEnvironment;
import jpaint.JStroke;

/**
 *オブジェクトに影をつけるエフェクトの処理を処理します。
 * @author takashi
 */
public class JDropShadowEffector implements JEffector {

    private float radius = 4f;
    private double offsetX = 4;
    private double offsetY = 4;
    private Color color = null;
    private transient Shape savedShape = null;
    private transient BufferedImage buffer = null;
    private transient AffineTransform tx = null;
    //
    private static final long serialVersionUID = 110l;

    /**
     * 指定するパラメータでJDropShadowEffecterを構築します.
     * @param radius 影をぼかす半径.
     * @param ofsX 影のオフセット位置のX座標
     * @param ofsY 影のオフセット位置のY座標
     * @param color 影の色
     */
    public JDropShadowEffector(float radius, double ofsX, double ofsY, Color color) {
        this.radius = radius;
        offsetX = ofsX;
        offsetY = ofsY;
        this.color = color;
    }

    /**
     * エフェクトを描画し、描画結果をイメージに保持します.
     * @param g 描画対象となるグラフィックスコンテキスト
     * @param s 描画するShape
     * @param fillPaint 塗りつぶしのPaint
     * @param border 境界のPaint
     * @param stroke 境界の線種
     */
    private void paintEffect(Graphics2D g, Shape s, JPaint fillPaint, JPaint border, JStroke stroke) {
        if (fillPaint != null || border != null) {
            if (!shapeEqual(s ,savedShape) || buffer == null || tx == null) {
                AffineTransform af = new AffineTransform();
                af.setToTranslation(offsetX, offsetY);
                float alpha = (color.getAlpha() / 255f);
                Area shadowShape = new Area();
                if (fillPaint != null) {
                    shadowShape.add(new Area(af.createTransformedShape(s)));
                }
                if (border != null) {
                    shadowShape.add(new Area(af.createTransformedShape(stroke.createStrokedShape(s))));
                }
                JBlurPaint jbp = new JBlurPaint(shadowShape, radius, color, alpha);
                Rectangle2D br = shadowShape.getBounds2D();
                br.setFrame(br.getX() - radius, br.getY() - radius, br.getWidth() + radius * 2, br.getHeight() + radius * 2);
                double ratio = JEnvironment.screenDPI / 72;
                buffer = new BufferedImage((int) (br.getWidth() * ratio), (int) (br.getHeight() * ratio), BufferedImage.TYPE_INT_ARGB);
                Graphics2D gb = buffer.createGraphics();
                tx = new AffineTransform();
                tx.setToScale(ratio, ratio);
                tx.translate(-br.getX(), -br.getY());
                gb.setTransform(tx);
                gb.setPaint(jbp);
                gb.fill(br);
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
        }
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
        paintEffect(g, s, fillPaint, border, stroke);
        if (fillPaint != null) {
            g.setPaint(fillPaint);
            g.fill(s);
        }
        if (border != null) {
            g.setPaint(border);
            g.setStroke(stroke);
            g.draw(s);
        }

    }
    public static boolean shapeEqual(Shape s1,Shape s2){
        if (s1==s2) return true;
        if (s1==null || s2==null) return false;
        PathIterator p1=s1.getPathIterator(null);
        PathIterator p2=s2.getPathIterator(null);
        float[] coords1=new float[6];
        float[] coords2=new float[6];
        while (!p1.isDone()){
            if (p2.isDone()) return false;
            int type1=p1.currentSegment(coords1);
            int type2=p2.currentSegment(coords2);
            if (type1 != type2) return false;
            for (int i=0;i<getSize(type1);i++){
                if (Math.abs(coords1[i]-coords2[i])>0.0001) return false;
            }
            p1.next();
            p2.next();
        }
        return true;
    }
    private static int getSize(int type){
        switch (type){
            case PathIterator.SEG_MOVETO:
            case PathIterator.SEG_LINETO:
                return 2;
            case PathIterator.SEG_QUADTO:
                return 4;
            case PathIterator.SEG_CUBICTO:
                return 6;
        }
        return 0;
    }
    /**
     * 指定するデフォルトの描画領域を拡張し、このJEffectorの描画領域まで拡張します。
     * @param r 指定するデフォルトの描画領域
     * @return このJEffectorにより拡張された描画領域
     */
    @Override
    public void paintText(Graphics2D g, TextLocater locater, AffineTransform tx, JPaint fillPaint, JPaint border, JStroke stroke) {
        if (tx == null) {
            tx = new AffineTransform();
        }
        Shape s = locater.getOutlineShape(tx);
        paintEffect(g, s, fillPaint, border, stroke);
        
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
        if (border != null) {
            g.setPaint(border);
            g.setStroke(stroke);
            g.setPaint(border);
            g.draw(s);
        }
    }

    @Override
    public Rectangle2D culcBounds(Rectangle2D r,JLeaf jl) {
        AffineTransform at = new AffineTransform();
        at.setToTranslation(offsetX, offsetY);
        Rectangle2D rx = at.createTransformedShape(r).getBounds2D();
        rx.add(r);
        rx.setFrame(rx.getX() - radius, rx.getY() - radius, rx.getWidth() + (radius) * 2, rx.getHeight() + (radius) * 2);
        return rx;
    }

    /**
     * このJEffectorの複製を作成します.
     * @return
     */
    @Override
    public JEffector clone() {
        JEffector ret = null;
        try {
            ret = (JEffector) super.clone();
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace();
        }
        return ret;
    }

}
