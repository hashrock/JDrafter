/*
 * JEffector.java
 *
 * Created on 2007/12/23, 18:28
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jobject.effector;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import jobject.JLeaf;
import jobject.text.TextLocater;
import jpaint.JPaint;
import jpaint.JStroke;

/**
 * 描画オブジェクトのエフェクトのポリシーをあらわします。
 * @author takashi
 */
public interface JEffector extends Cloneable, Serializable {
    /**
     * 指定のShapeを描画します。
     * @param g グラフィックスコンテキスト
     * @param s 描画するShape
     * @param fillPaint 塗りつぶしのを行うPaint
     * @param border 境界を描画するPaint
     * @param stroke 境界の線種
     */
    public void paint(Graphics2D g, Shape s, JPaint fillPaint, JPaint border, JStroke stroke);
     /**
     * 指定のTextObjectを描画します。
     * @param g グラフィックスコンテキスト
     * @param s 描画するShape
     * @param fillPaint 塗りつぶしのを行うPaint
     * @param border 境界を描画するPaint
     * @param stroke 境界の線種
     */   
    public void paintText(Graphics2D g,TextLocater locater,AffineTransform tx,JPaint fillPaint,JPaint border,JStroke stroke);
    /**
     * 指定するデフォルトの描画領域を拡張し、このJEffectorの描画領域まで拡張します。
     * @param r 指定するデフォルトの描画領域
     * @return このJEffectorにより拡張された描画領域
     */
    public Rectangle2D culcBounds(Rectangle2D r,JLeaf jl);
   /**
    * このJEffectorの複製を作成します.
    * @return
    */
    public JEffector clone();
}
