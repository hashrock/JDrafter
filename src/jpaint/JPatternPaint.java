/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jpaint;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.util.Vector;
import jobject.JLeaf;

/**
 *
 * @author takashi
 */
public class JPatternPaint implements Paint {

    private Vector<JLeaf> patternObjects = null;
    private static final long serialVersionUID = 110l;
    private ColorModel colorModel = null;
    private Rectangle2D bounds = null;
    private Rectangle sourceBounds = null;
    private AffineTransform tx = null;
    private RenderingHints hints = null;

    public JPatternPaint(Rectangle2D clpObj, Vector<JLeaf> ptnObj) {
        patternObjects = new Vector<JLeaf>(100);
        for (JLeaf jl : ptnObj) {
            patternObjects.add(jl);
        //patternObjects.add(jl);
        }
        AffineTransform af = new AffineTransform();
        bounds = clpObj;
        af.setToTranslation(-bounds.getX(), -bounds.getY());
        bounds.setFrame(0, 0, bounds.getWidth(), bounds.getHeight());
        for (JLeaf jl : patternObjects) {
            jl.transform(af);
        }
    }
    public JPatternPaint(){
        
    }
    public Rectangle2D getClip() {
        return bounds;
    }

    public Vector<JLeaf> getPattern() {
        return patternObjects;
    }

    @Override
    public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
        colorModel = ColorModel.getRGBdefault();
        sourceBounds = xform.createTransformedShape(bounds).getBounds();
        sourceBounds = new Rectangle(0, 0, (int) (bounds.getWidth() * xform.getScaleX()), (int) (bounds.getHeight() * xform.getScaleY()));
        //
        tx = xform;
        this.hints = hints;
        return new InnerContext();
    }

    @Override
    public int getTransparency() {
        return TRANSLUCENT;
    }

    public class InnerContext implements PaintContext {

        public InnerContext() {
        }

        @Override
        public void dispose() {
        }

        @Override
        public ColorModel getColorModel() {
            return colorModel;
        }

        @Override
        public Raster getRaster(int x, int y, int w, int h) {
            BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            //ユーザー座標の原点
            Point2D origin = new Point2D.Float(0, 0);
            tx.transform(origin, origin);
            //
            Rectangle2D r = tx.createTransformedShape(bounds).getBounds2D();
            float ofsX = (float) (x - origin.getX());
            float ofsY = (float) (y - origin.getY());
            ofsX = (float) (ofsX % r.getWidth());
            if (ofsX < 0) {
                ofsX = (float) (r.getWidth() + ofsX);
            }
            ofsY = (float) (ofsY % r.getHeight());
            if (ofsY < 0) {
                ofsY = (float) (r.getHeight() + ofsY);
            }
            Graphics2D g = image.createGraphics();
            g.translate(-ofsX, -ofsY);
            g.scale(tx.getScaleX(), tx.getScaleX());
            g.setRenderingHints(hints);
            //Rectangle2D.Double clRect=new Rectangle2D.Double(0,0,bounds.getWidth(),bounds.getHeight());
            for (int vy = 0; vy < 2; vy++) {
                for (int vx = 0; vx < 2; vx++) {                   
                    for (JLeaf jl : patternObjects) {
                        jl.paint(bounds, g);
                    }
                    g.translate(bounds.getWidth(), 0);
                }
                g.translate(-bounds.getWidth() * 2, bounds.getHeight());
            }
            g.dispose();
            Rectangle ri = r.getBounds();
            int cpX = ri.width;
            WritableRaster raster = image.getRaster();
            DataBufferInt otBuffer = (DataBufferInt) raster.getDataBuffer();
            int[] out = otBuffer.getData();
            SinglePixelPackedSampleModel model = (SinglePixelPackedSampleModel) raster.getSampleModel();
            int offset = model.getOffset(0, 0);
            int stride = model.getScanlineStride();
            int srX = 0;
            int srIndex = offset;
            int dstIndex = offset + cpX;
            while (cpX < w) {
                for (int i = 0; i < h; i++) {
                    out[dstIndex + i * stride] = out[srIndex + i * stride];
                }
                dstIndex++;
                srIndex++;
                cpX++;
            }
            int cpY = ri.height;
            srIndex = offset;
            dstIndex = offset + cpY * stride;
            while (cpY < h) {
                for (int i = 0; i < w; i++) {
                    out[dstIndex + i] = out[srIndex + i];
                }
                dstIndex += stride;
                srIndex += stride;
                cpY++;
            }
            return image.getRaster();
        /*
        destRaster = colorModel.createCompatibleWritableRaster(w, h);
        DataBufferInt outBuffer = (DataBufferInt) destRaster.getDataBuffer();
        DataBufferInt inBuffer = (DataBufferInt) sourceRaster.getDataBuffer();
        int[] out = outBuffer.getData();
        int[] source = inBuffer.getData();
        SinglePixelPackedSampleModel model = (SinglePixelPackedSampleModel) destRaster.getSampleModel();
        int outOffset = model.getOffset(0, 0);
        int outStride = model.getScanlineStride();
        model = (SinglePixelPackedSampleModel) sourceRaster.getSampleModel();
        int inOffset = model.getOffset(0, 0);
        int inStride = model.getScanlineStride();
         */

        }
    }
}
