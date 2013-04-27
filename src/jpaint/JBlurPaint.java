package jpaint;
import java.awt.Color;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;

/*
 * JShadowPaint.java
 *
 * Created on 2007/11/24, 21:07
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author admin
 */
public class JBlurPaint implements Paint{
    Shape s=null;
    float  radius;
    float transparency;
    Color baseColor=null;
    /** Creates a new instance of JShadowPaint */
    public JBlurPaint(Shape s,float radius,Color baseColor,float transparency) {
        this.s=s;
        this.radius=radius;
        this.transparency=transparency;
        this.baseColor=baseColor;
    }

    public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
        return new JBlurPaintContext(s,radius,baseColor,transparency,xform);
    }

    public int getTransparency() {
        return TRANSLUCENT;
    }
    public Rectangle2D getBound2D(){
        Rectangle2D r=s.getBounds2D();
        r.setFrame(r.getX()-radius,r.getY()-radius,r.getWidth()+2*radius,r.getHeight()+2*radius);
        return r;
    }
}
