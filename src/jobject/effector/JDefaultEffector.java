/*
 * JDefaultEffector.java
 *
 * Created on 2007/12/23, 18:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jobject.effector;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import jobject.JLeaf;
import jobject.text.TextLocater;
import jpaint.JPaint;
import jpaint.JStroke;

/**
 *デフォルトの塗り及び線を描画するエフェクトです。
 * @author takashi
 */
public class JDefaultEffector implements Serializable, JEffector {

    private static final long serialVersionUID = 110l;

    /**
     *JDefaultEffecterを構築します.
     */
    @Override
    public void paint(Graphics2D g, Shape s, JPaint fillPaint, JPaint border, JStroke stroke) {
        if (fillPaint != null) {
            g.setPaint(fillPaint);
            g.fill(s);
        }
        if (stroke != null && border != null) {
            g.setStroke(stroke);
            g.setPaint(border);
            g.draw(s);
        }
    }

    /**
     * 指定するデフォルトの描画領域を、このJEffectorの描画領域まで拡張します。
     * @param r 指定するデフォルトの描画領域
     * @return このJEffectorにより拡張された描画領域
     */
    @Override
    public Rectangle2D culcBounds(Rectangle2D r,JLeaf jl) {
        return r;
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
}
