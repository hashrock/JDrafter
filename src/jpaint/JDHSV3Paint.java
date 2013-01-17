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
public class JDHSV3Paint implements Paint{  
    private Rectangle2D rect;
    /**
     * Creates a new instance of JDHSV1Paint
     */
    public JDHSV3Paint(Rectangle2D rect) {
        this.rect=rect;
    }    
    public PaintContext createContext(ColorModel cm, Rectangle deviceBounds,
            Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
        return new JDHSV3PaintContext(rect,xform);
    }

    public int getTransparency() {
        return OPAQUE;
    }
}
