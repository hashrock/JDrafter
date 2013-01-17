/*
 * JPaint.java
 *
 * Created on 2007/08/15, 8:25
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jpaint;

import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.io.IOException;
import java.io.Serializable;
import java.util.Vector;
import jobject.JLeaf;
import jobject.JPathObject;

/**
 *JPaintクラスは、単一の色、線形化されたグラデーション又は放射状のグラデーション
 * で塗りつぶす手段を提供します。
 * @author TI
 */
public class JPaint implements Serializable, Paint, Cloneable {

    /**単一色による塗りを表します.*/
    public static final int COLOR_MODE = 0;
    /**線形グラデーションによる塗りを表します。*/
    public static final int LINEAR_GRADIENT_MODE = 1;
    /**放射状グラデーションによる塗りを表します.*/
    public static final int RADIAL_GRADIENT_MODE = 2;
    /**パターンによる塗りを表します.*/    
    public static final int PATTERN_MODE = 3;
    private int paintMode = COLOR_MODE;
    private Color color = null;
    private transient MultipleGradientPaint gradient = null;
    private transient JPatternPaint patternPaint = null;
    private float startX = 0,  startY = 0,  endX = 0,  endY = 0;
    private float[] fractions = null;
    private Color[] colors = null;
    private Rectangle2D.Float clipObject = null;
    private Vector<JLeaf> patternObjects = null;
    private static final long serialVersionUID = 110l;

    /**カラーペイントモード、塗り色WhiteでJPaintを構築します。
     */
    public JPaint() {
        this(Color.WHITE);
    }

    /**指定したColorからJPaintを構築します.
     *@param c 指定するColor;
     */
    public JPaint(Color c) {
        setPaintColor(c);
    }

    /**指定したMultipleGradientPaintからJPaintを構築します.
     *@param mg 指定するMultipleGradientPaint*/
    public JPaint(MultipleGradientPaint mg) throws Exception {
        float sx, sy, ex, ey;
        if (mg instanceof LinearGradientPaint) {
            LinearGradientPaint lg = (LinearGradientPaint) mg;
            int pmode = LINEAR_GRADIENT_MODE;
            Point2D sp = lg.getStartPoint();
            Point2D ep = lg.getEndPoint();
            setGradient(pmode, (float) sp.getX(), (float) sp.getY(), (float) ep.getX(), (float) ep.getY(), lg.getFractions(), lg.getColors());            
        } else {
            RadialGradientPaint rd = (RadialGradientPaint) mg;
            int pmode = RADIAL_GRADIENT_MODE;
            Point2D sp = rd.getFocusPoint();
            float rad = rd.getRadius();
            setGradient(pmode, (float) sp.getX(), (float) sp.getY(), (float) sp.getX() + rad, (float) sp.getY(), mg.getFractions(), mg.getColors());
        }
        
    }

    /**指定したパラメータでLinear又はRadialのグラデーションを返すJPaintを構築します。
     *@param pMode LINEAR_GRADIENT_MODE又はRADIAL_GRADIENT_MODE
     *@param sX 開始点のX座標
     *@param sY 開始点のY座標
     *@param eX 終了点のX座標
     *@param eY 終了店のY座標
     *@param fracs 0.0 ? 1.0 の範囲の数値。 グラデーションでの色分布を指定する
     *@param cols 各小数値に対応する色の配列
     */
    public JPaint(int pMode,
            float sX,
            float sY,
            float eX,
            float eY,
            float[] fracs,
            Color[] cols) throws Exception {
        setGradient(pMode, sX, sY, eX, eY, fracs, cols);        
    }

    /**
     * 指定したクリップ及びパターンでパタンの塗りを返すJPaintオブジェクトを構築します。
     * @param clip クリップエリアを示すJPathObject;
     * @param pattern 塗りのパターン
     */    
    public JPaint(Rectangle2D clip, Vector<JLeaf> pt) {
        setPattern(clip, pt);
    }
    public JPaint(JPatternPaint ppt){
        this.patternPaint=ppt;
        paintMode=PATTERN_MODE;
    }
    public void setPattern(Rectangle2D clip, Vector<JLeaf> pt) {
        patternPaint = new JPatternPaint(clip, pt);
        clipObject = new Rectangle2D.Float((float)clip.getX(),(float)clip.getY(),(float)clip.getWidth(),(float)clip.getHeight());
        patternObjects = new Vector<JLeaf>();
        for (JLeaf jl : pt) {
            patternObjects.add(jl);
        }
        paintMode = PATTERN_MODE;
    }

    public Rectangle2D getClip() {
        if (patternPaint == null) {
            return null;
        }
        return patternPaint.getClip();
    }

    public Vector<JLeaf> getPatternObjcets() {
        if (patternPaint == null) {
            return null;
        }
        return patternPaint.getPattern();
    }

    public JPatternPaint getPatternPaint() {
        return patternPaint;
    }

    /**指定したColorを塗り色に設定します。
     *@param c 指定する塗り色
     */
    public void setPaintColor(Color c) {
        color = c;
        paintMode = COLOR_MODE;
    }

    /**指定したパラメータでLinear又はRadialのグラデーションをJPaintに設定します。
     *@param pMode LINEAR_GRADIENT_MODE又はRADIAL_GRADIENT_MODE
     *@param sX 開始点のX座標
     *@param sY 開始点のY座標
     *@param eX 終了点のX座標
     *@param eY 終了店のY座標
     *@param fracs 0.0 ~ 1.0 の範囲の数値。 グラデーションでの色分布を指定する
     *@param cols 各小数値に対応する色の配列
     */
    public void setGradient(
            int pMode,
            float sX,
            float sY,
            float eX,
            float eY,
            float[] fracs,
            Color[] cols) throws Exception {
        if (pMode != LINEAR_GRADIENT_MODE && pMode != RADIAL_GRADIENT_MODE) {
            throw new Exception("PaintMode must be LINEAR_GRADIENT_MODE or RADIAL_GRADIENT_MODE)");            
        }
        startX = sX;
        startY = sY;
        endX = eX;
        endY = eY;
        fractions = fracs;
        colors = cols;
        if (pMode == LINEAR_GRADIENT_MODE) {
            
            gradient = new LinearGradientPaint(startX, startY, endX, endY, fractions, colors, LinearGradientPaint.CycleMethod.NO_CYCLE);
        } else {
            float dx = endX - startX;
            float dy = endY - startY;
            float radius = (float) (Math.sqrt(dx * dx + dy * dy));
            gradient = new RadialGradientPaint(startX, startY, radius, fractions, colors, RadialGradientPaint.CycleMethod.NO_CYCLE);
        }
        paintMode = pMode;
        this.color = null;
    }

    /**グラデーション制御位置を要素とする配列のコピーを返します
     *@return グラデーション制御位置を要素とする配列
     */
    public float[] getFracs() {
        float[] ret = new float[fractions.length];
        for (int i = 0; i < fractions.length; i++) {
            ret[i] = fractions[i];
        }
        return ret;
    }

    /**制御位置上のカラーを表す配列のコピーを返します.
     *@return 制御位置上のカラーを要素とする配列
     */
    public Color[] getColors() {
        Color[] ret = new Color[colors.length];
        for (int i = 0; i < colors.length; i++) {
            ret[i] = colors[i];
        }
        return ret;
    }

    /**現在の塗り色を返します.
     *@return 現在の塗り色
     */
    public Color getColor() {
        return color;
    }

    /**
     * このJPaintがグラデーションの場合、有効なMultipleGradientPaintを返します。
     * @return　有効なMultipleGradientPaint,有効なGradientPaintがない場合null
     */
    public MultipleGradientPaint getGradient() {
        return gradient;
    }

    /**グラデーションの開始位置および終了位置の座標を要素とする配列を返します。
     *@return グラデーションの開始位置及び終了位置を要素とする配列
     * 配列要素の内容は次のとおりです。<br>
     *[0] 開始位置のX座標<br>
     *[1] 開始位置のY座標<br>
     *[2] 終了位置のX座標<br>
     *[3] 終了位置のY座標
     */
    public float[] gradientPoints() {
        return new float[]{
                    startX, startY, endX, endY
                };
    }

    /**現在のペイントモードを返します
     *@return 現在のペイントモード.LINEAR_GRADIENT_MODE又はRADIAL_GRADIENT_MODE
     */
    public int getPaintMode() {
        return paintMode;
    }
    public Paint getPaint(){
        if (paintMode==COLOR_MODE)
            return color;
        else if (paintMode==PATTERN_MODE)
            return patternPaint;
        else
            return gradient;
    }
    @Override
    public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
        if (paintMode == COLOR_MODE) {
            return color.createContext(cm, deviceBounds, userBounds, xform, hints);
        }else if(paintMode==LINEAR_GRADIENT_MODE || paintMode==RADIAL_GRADIENT_MODE){
        return gradient.createContext(cm, deviceBounds, userBounds, xform, hints);
        }else if(paintMode==PATTERN_MODE){
            return patternPaint.createContext(cm, deviceBounds, userBounds, xform, hints);
        }
        return null;
    }    
    @Override
    public int getTransparency() {
        if (paintMode == COLOR_MODE) {
            return color.getTransparency();
        }else if (paintMode== PATTERN_MODE){
            return patternPaint.getTransparency();
        }
        return gradient.getTransparency();
    }

    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException, Exception {
        gradient = null;
        patternPaint=null;
        in.defaultReadObject();
        if (paintMode == LINEAR_GRADIENT_MODE || paintMode==RADIAL_GRADIENT_MODE) {
            setGradient(paintMode, startX, startY, endX, endY, fractions, colors);
        }else if (paintMode==PATTERN_MODE){
            setPattern(clipObject,patternObjects);
        }
    }

    /**
     * 指定されたJPaintがこのJPaintと等しい場合にtrueを返します.
     * @param jp 指定するJPaint
     * @return 指定されたJPaintとこのJPaintが等しい場合true,それ以外false;
     */
    public boolean equals(JPaint jp) {
        if (jp == null) {
            return false;
        }
        if (this==jp)
            return true;
        if (paintMode != jp.paintMode) {
            return false;
        }
        if (paintMode == COLOR_MODE) {
            if (color == null) {
                if (jp.color != null) {
                    return false;
                }
            } else {
                return (color.equals(jp.color));
            }
        } else  if (paintMode==LINEAR_GRADIENT_MODE || paintMode==RADIAL_GRADIENT_MODE){
            if (fractions.length != jp.fractions.length) {
                return false;
            }
            for (int i = 0; i < fractions.length; i++) {
                if (fractions[i] != jp.fractions[i]) {
                    return false;
                }
                if (!colors[i].equals(jp.colors[i])) {
                    return false;
                }
            }
        } else if (paintMode==PATTERN_MODE){
            if (clipObject.equals(jp.clipObject)){
                if (patternObjects.size() !=jp.patternObjects.size()) 
                    return false;
                for (int i=0;i<patternObjects.size();i++){
                    if (!patternObjects.get(i).equals(jp.getPatternObjcets().get(i))){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * JPaintに指定するAffine変換を加えます。
     * @param tx 指定するAffineTransform
     */
    public void transform(AffineTransform tx) {
        if (paintMode == COLOR_MODE || paintMode==PATTERN_MODE) {
            return;
        }
        Point2D.Double p1 = new Point2D.Double(startX, startY);
        Point2D.Double p2 = new Point2D.Double(endX, endY);
        tx.transform(p1, p1);
        tx.transform(p2, p2);
        try {
            setGradient(paintMode, (float) p1.x, (float) p1.y, (float) p2.x, (float) p2.y, fractions, colors);
        } catch (Exception e) {
        }
    }

    /**
     * グラデーションの開始位置を返します.
     * @return グラデーションの開始位置
     */
    public Point2D.Float getP1() {
        return new Point2D.Float(startX, startY);
    }

    /**
     * グラデーションの終了位置を返します.
     * @return グラデーションの終了位置
     */
    public Point2D.Float getP2() {
        return new Point2D.Float(endX, endY);
    }

    @Override
    public JPaint clone() {
        JPaint ret = new JPaint();
        if (this.paintMode == COLOR_MODE) {
            ret.setPaintColor(this.color);
        } else  if (paintMode==LINEAR_GRADIENT_MODE || paintMode==RADIAL_GRADIENT_MODE){
            Color[] cols = getColors();
            float[] frs = getFracs();
            try {
                ret.setGradient(paintMode, startX, startY, endX, endY, frs, cols);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }else {
           ret.clipObject=(Rectangle2D.Float)clipObject.clone();
            ret.patternObjects=patternObjects;
            ret.paintMode=paintMode;
            ret.patternPaint=new JPatternPaint(ret.clipObject,ret.patternObjects);
        }
        return ret;
    }
}
