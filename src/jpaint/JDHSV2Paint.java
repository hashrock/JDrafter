/*
 * JDHuePaint.java
 *
 * Created on 2007/02/13, 13:27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jpaint;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

/**
 *Saturation(彩度）　Brightness(明度)　選択用のPaintです。
 *＾100%
 *|明
 *|度
 *|
 *|　　　　　　　　彩度
 *-----------------------------------＞
 *0°　　　　　　　　　　　　　　　　 100%
 * @author i002060
 */
public class JDHSV2Paint implements Paint{
    /**横軸に彩度、縦軸に明度をプロットするPaintです*/
    public static final int SB_MODE=0;
    /**横軸に色相、縦軸に明度をプロットします.*/
    public static final int HB_MODE=1;
    /**横軸に色相、縦軸に彩度をプロットします.*/
    public static final int HS_MODE=3;
    private Rectangle2D rect;
    private Color baseColor;
    private int mode;
    
    /**
     * Creates a new instance of JDHuePaint
     */
    public JDHSV2Paint(Color c,Rectangle2D rect,int mode) {
        this.baseColor=c;
        this.rect=rect;
        this.mode=mode;
    }    
    public PaintContext createContext(ColorModel cm, Rectangle deviceBounds,
            Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
        return new JDHSV2PaintContext(baseColor,rect,mode,xform);
    }

    public int getTransparency() {
        return OPAQUE;
    }
}
