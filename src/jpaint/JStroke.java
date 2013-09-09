/*
 * JStroke.java
 *
 * Created on 2007/08/16, 20:14
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jpaint;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Stroke;
import java.io.IOException;
import java.io.Serializable;

/**
 *直列化可能なstrokeです
 * @author TI
 */
public class JStroke implements Stroke,Serializable{
    private float width=1;
    private int cap=BasicStroke.CAP_SQUARE;
    private int join=BasicStroke.JOIN_MITER;
    private float miterLimit=10.0f;
    private float[] dash=null;
    private float dash_phase=0;
    private transient BasicStroke stroke=null;
    private static final long serialVersionUID=110l;
    /**すべての属性にデフォルト値を使って新しい JStroke を構築します。
     * デフォルトの属性は、実線の幅 1.0、CAP_SQUARE、JOIN_MITER、
     * トリミング制限値 10.0 です。 
     */
    public JStroke() {
        width=1;
        cap=BasicStroke.CAP_SQUARE;
        join=BasicStroke.JOIN_MITER;
        miterLimit=10.0f;
        dash=null;
        dash_phase=0;
        stroke=new BasicStroke(width,cap,join,miterLimit,dash,dash_phase);
    }
    /**指定された属性を持つ新しい BasicStroke を構築します]
     *@param w この BasicStroke の幅。値は 0.0f 以上でなければならない。幅が 0.0f に設定されている場合、 ストロークは対象のデバイス上のもっとも細いラインとして描画される。 また、このときアンチエイリアス設定が使用される
     *@param cp - BasicStroke の両端の装飾
     *@param jn - 輪郭線セグメントの接合部の装飾
     *@param mLimit - 接合トリミングの制限値。miterlimit は 1.0f 以上でなければならない
     *@param dsh - 破線パターンを表す配列
     *@param dphase - 破線パターン開始位置のオフセット 
     */
    public JStroke(float w,int cp,int jn,float mLimit,float[] dsh,float dphase){
        width=w;
        cap=cp;
        join=jn;
        miterLimit=mLimit;
        dash=dsh;
        dash_phase=dphase;
        stroke=new BasicStroke(width,cap,join,miterLimit,dash,dash_phase);
    }
    /**指定されたBasicStrokeの属性をコピーしたJStrokeを構築します*/
    public JStroke(BasicStroke stroke){
        this(stroke.getLineWidth(),stroke.getEndCap(),stroke.getLineJoin(),
                stroke.getMiterLimit(),stroke.getDashArray(),stroke.getDashPhase());
    }
    /**線幅を返します.*/
    public float getWidth(){
        return width;
    }
    /**線の両端の装飾を返します.*/
    public int getEndCap(){
        return cap;
    }
    /**輪郭線セグメントの接合部の装飾を返します.*/
    public int getLineJoin(){
        return join;
    }
    /**接合トリミングの制限値を返します.*/
    public float getMiterLimit(){
        return miterLimit;
    }
    /**破線パターンを表す配列のコピーを返します.*/
    public float[] getDashArray(){
        if (dash==null)
            return null;
        return dash.clone();
    }
    /**破線パターン開始位置のオフセットを返します.*/
    public float getDashPhase(){
        return dash_phase;
    }
    /**
     *指定された Shape をストロークで描画した輪郭を表す内部を持つ Shape を返します.
     * @param p  ストロークで描画される Shape の境界 
     */
    public Shape createStrokedShape(Shape p) {
        return stroke.createStrokedShape(p);
    }
    /**現在のストロークを返します.*/
    public BasicStroke getStroke(){
        return stroke;
    }
    public boolean equals(JStroke tg){
        if (tg==null) return false;
        if (width !=tg.width) return false;
        if(cap !=tg.cap) return false;
        if (join != tg.join) return false;
        if (join == BasicStroke.JOIN_MITER && miterLimit !=tg.miterLimit) return false;
        if (dash != null){
            if (tg.dash==null) return false;
            if (dash.length != tg.dash.length) return false;
            for (int i=0;i<dash.length;i++){
                if (dash[i] !=tg.dash[i]) return false;
            }
            if (dash_phase != tg.dash_phase) return false;
        }else{
            if (tg.dash !=null) return false;
        }
        return true;
    }
    public JStroke clone(){
        return new JStroke(width,cap,join,miterLimit,getDashArray(),dash_phase);
    }
    private void readObject(java.io.ObjectInputStream in)
     throws IOException, ClassNotFoundException
     {
         stroke=null;
         in.defaultReadObject();
         stroke=new BasicStroke(width,cap,join,miterLimit,dash,dash_phase);
     }
    
}
