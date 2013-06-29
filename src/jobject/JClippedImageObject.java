/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jobject;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;

/**
 *
 * @author takashi
 */
public class JClippedImageObject extends JImageObject {

    private Shape clipShape;
    private static final long serialVersionUID = 110l;

    public JClippedImageObject() {
        clipShape = null;
    }

    public JClippedImageObject(JImageObject img, Point2D sp) {
        super(img.getImage(), sp);
        clipShape = null;
    }

    public JClippedImageObject(JImageObject img, Point2D sp, Shape clip) {
        super(img.getImage(), sp);
        this.totalRotation = img.totalRotation;
        this.totalTransform = img.totalTransform;
        AffineTransform tx = new AffineTransform();
        try {
            tx = totalTransform.createInverse();
        } catch (NoninvertibleTransformException e) {
        }
        clipShape = tx.createTransformedShape(clip);
    }

    public Shape getClipShape() {
        return clipShape;
    }

    @Override
    public void paintThis(Rectangle2D clip, Graphics2D g) {
        if (!clip.intersects(getBounds())) {
            return;
        }
        Graphics2D gc = (Graphics2D) g.create();
        Shape s = totalTransform.createTransformedShape(clipShape);
        ClipPaint clipPaint = new ClipPaint();
        gc.setPaint(clipPaint);
        gc.fill(s);
        gc.dispose();
    }

    @Override
    public Object clone() {
        JClippedImageObject ret = new JClippedImageObject();
        ret.imageIcon = this.imageIcon;
        ret.clipShape = this.clipShape;
        ret.boundRect = this.boundRect;
        ret.alpha = alpha;
        ret.totalTransform = (AffineTransform) totalTransform.clone();
        ret.totalRotation = totalRotation;
        return ret;
    }

    @Override
    public Shape getShape() {
        return totalTransform.createTransformedShape(clipShape);
    }

    public JImageObject createImageObject() {
        Point2D p = new Point2D.Double(boundRect.getX(), boundRect.getY());
        JImageObject ret = new JImageObject(imageIcon.getImage(), p);
        ret.alpha = alpha;
        ret.totalTransform = (AffineTransform) totalTransform.clone();
        ret.totalRotation = totalRotation;
        return ret;
    }

    public class ClipPaint implements Paint {

        private AffineTransform tx = null;
        private AffineTransform xform = null;
        private ColorModel colorModel = null;

        @Override
        public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
            colorModel = ColorModel.getRGBdefault();
            this.xform = xform;
            tx = new AffineTransform();
            try {
                tx = xform.createInverse();
            } catch (NoninvertibleTransformException e) {
            }
            return new InnerContext();
        }

        @Override
        public int getTransparency() {
            return TRANSLUCENT;
        }

        public class InnerContext implements PaintContext {

            @Override
            public void dispose() {
            }

            @Override
            public ColorModel getColorModel() {
                return colorModel;
            }

            @Override
            public Raster getRaster(int x, int y, int w, int h) {
                BufferedImage bm = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                AffineTransform atx = new AffineTransform(xform);
                Point2D origin = new Point2D.Double(x, y);
                tx.transform(origin, origin);
                AffineTransform ax = AffineTransform.getTranslateInstance(-x, -y);
                atx.preConcatenate(ax);
                Graphics2D g = bm.createGraphics();
                g.transform(atx);
                //g.scale(userBounds.getWidth()/deviceBounds.getWidth(),userBounds.getHeight()/deviceBounds.getHeight());
                Image img = imageIcon.getImage();
                g.setColor(new Color(1f, 1f, 1f, alpha));
                double sx = boundRect.getWidth() / imageIcon.getIconWidth();
                double sy = boundRect.getHeight() / imageIcon.getIconHeight();
                AffineTransform af = new AffineTransform(totalTransform);
                af.translate(boundRect.getX(), boundRect.getY());
                af.scale(sx, sy);
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g.drawImage(img, af, imageIcon.getImageObserver());
                g.dispose();
                return bm.getRaster();
            }
        }
    }
}
