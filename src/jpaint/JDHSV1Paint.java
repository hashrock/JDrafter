/*
 * JDHSV1Paint.java
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
 *H,S,Bいずれかの要素が遷移するPAINTです.
 *---------------------------------------------＞
 *0°　　　　　　　　　　　　　　　　　　　　　360°
 * @author i002060
 */
public class JDHSV1Paint implements Paint{  
    /**横方向に遷移するPaintを表します.*/
    public static final int VERTICAL=0;
    /**縦方向に遷移するPaintを表します。*/
    public static final int HOLIZONTAL=1;
    /**遷移する要素を表します*/
    public static final int H=0;
    public static final int S=1;
    public static final int V=2;
    private int direction;
    private int mode;
    private float[] baseColor;
    private Rectangle2D rect;
    /**
     * Creates a new instance of JDHSV1Paint
     */
    public JDHSV1Paint(int direction,int mode,Rectangle2D rect,float[] c) {
        this.direction=direction;
        this.rect=rect;
        this.mode=mode;
        this.baseColor=c;
    }    
    public PaintContext createContext(ColorModel cm, Rectangle deviceBounds,
            Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
        return new JDHSV1PaintContext(direction,mode,rect,baseColor,xform);
    }

    public int getTransparency() {
        return OPAQUE;
    }
}
