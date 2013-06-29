/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package svg.oject;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import org.xml.sax.Attributes;
import svg.SVGElement;
import svg.attribute.SVGAttributes;

/**
 *
 * @author takashi
 */
public abstract class SVGObject implements SVGElement {

    public static final float PIX = 1f; //72f / Toolkit.getDefaultToolkit().getScreenResolution();
    public static final float POINT = Toolkit.getDefaultToolkit().getScreenResolution() / 72f;
    public static final float IN = POINT * 72;
    public static final float MM = IN / 25.4f;
    public static final float CM = MM * 10;
    public static final float PC = POINT * 12;
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    public static final int BOTH = 2;
    public static final int LENGTH = 0;
    public static final int COORDS = 1;
    public static final String VIEWPORT_X = "x";
    public static final String VIEWPORT_Y = "y";
    public static final String VIEWPORT_WIDTH = "width";
    public static final String VIEWPORT_HEIGHT = "height";
    public static final String VIEWBOX = "viewBox";
    public static final String ASPECT_RATIO = "preserveAspectRatio";
    //
    public static final String NONE = "none";
    public static final String DEFER = "defer";
    public static final String MEET = "meet";
    public static final String SLICE = "slice";
    public static final String XMIN = "xMin";
    public static final String XMID = "xMid";
    public static final String XMAX = "xMax";
    public static final String YMIN = "YMin";
    public static final String YMID = "YMid";
    public static final String YMAX = "YMax";    //
    protected HashMap<String, String> viewAttribute;
    protected SVGObject parent;
    protected SVGObject propertyParent=null;
    protected AffineTransform viewTransform = null;
    protected AffineTransform objectTransform = null;
    protected boolean compiled = false;
    private SVGAttributes attributes;
    protected String id;
    private static BufferedImage img = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);
    public static final Graphics2D GRAPHICS = createGraphics();

    private static Graphics2D createGraphics() {
        Graphics2D ga = img.createGraphics();
        ga.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ga.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        return ga;
    }

    protected SVGObject() {
        propertyParent=parent = null;
        attributes = null;
        viewAttribute = new HashMap<String, String>();
        id = null;
    }

    protected SVGObject(SVGObject parent) {
        attributes = null;
        viewAttribute = new HashMap<String, String>();
        propertyParent=this.parent = parent;
        id = null;
    }

    public void setAttributes(Attributes attr) {
        attributes = new SVGAttributes(this, attr);
        String s = attr.getValue("id");
        if (s != null) {
            id = s.trim();
            getRootDocument().addLink(id, this);
        }

    }

    public void setAttributes(SVGAttributes attr) {
        attributes = attr;
    }

    public SVGAttributes getAttributes() {
        return attributes;
    }

    public void setParent(SVGObject parent) {
        this.parent = parent;
    }
    
    public void setPropertyParent(SVGObject parent){
        propertyParent=parent;
    }
    
    public void setViewport(Attributes attr) {
        String[] cv=new String[]{VIEWPORT_X,VIEWPORT_Y,VIEWPORT_WIDTH,VIEWPORT_HEIGHT};
        String s;
        for (int i=0;i<cv.length;i++){
            s=attr.getValue(cv[i]);
            if (s !=null)
                viewAttribute.put(cv[i], s);
        }
    }

    public void setViewBox(Attributes attr) {
        String s=attr.getValue(VIEWBOX);
        if (s !=null)
            viewAttribute.put(VIEWBOX, s);
        s=attr.getValue(ASPECT_RATIO);
        if (s!=null)
            viewAttribute.put(ASPECT_RATIO, attr.getValue(ASPECT_RATIO));
    }

    public String getPreserveAspectRatio() {
        String s = viewAttribute.get(ASPECT_RATIO);
        if (s == null) {
            return null;
        }
        if (s.contains(DEFER)) {
            if (parent != null) {
                return parent.getPreserveAspectRatio();
            }
            return null;
        }
        return s;
    }

    public float getViewX() {
        String s = viewAttribute.get(VIEWPORT_X);
        if (s == null) {
            if (!isViewportDefined() && parent != null) {
                return parent.getViewX();
            }
            s = "0";
        }
        return toPixel(this.getParent(), s, HORIZONTAL, COORDS);
    }

    public float getViewY() {
        String s = viewAttribute.get(VIEWPORT_Y);
        if (s == null) {
            if (!isViewportDefined() && parent != null) {
                return parent.getViewY();
            }
            s = "0";
        }
        return toPixel(this.getParent(), s, VERTICAL, COORDS);
    }

    public float getViewWidth() {
        String s = viewAttribute.get(VIEWPORT_WIDTH);
        if (s == null) {
            if (!isViewportDefined() && parent != null) {
                return parent.getViewWidth();
            }
            s = "100%";
        }
        return toPixel(this.getParent(), s, HORIZONTAL, LENGTH);
    }

    public float getViewHeight() {
        String s = viewAttribute.get(VIEWPORT_HEIGHT);
        if (s == null) {
            if (!isViewportDefined() && parent != null) {
                return parent.getViewHeight();
            }
            s = "100%";
        }
        return toPixel(this.getParent(), s, VERTICAL, LENGTH);
    }

    public boolean isViewBoxDefined() {
        return !(viewAttribute.get(VIEWBOX) == null);
    }

    public boolean isViewportDefined() {
        return !(viewAttribute.get(VIEWPORT_X) == null && viewAttribute.get(VIEWPORT_Y) == null && viewAttribute.get(VIEWPORT_WIDTH) == null && viewAttribute.get(VIEWPORT_HEIGHT) == null);
    }

    public Rectangle2D getCurrentViewport() {
        return new Rectangle2D.Float(getViewX(), getViewY(), getViewWidth(), getViewHeight());
    }

    public Rectangle2D getCurrentViewBox() {
        String s = viewAttribute.get(VIEWBOX);
        if (s == null) {
            if (parent != null) {
                return parent.getCurrentViewBox();
            }
            return new Rectangle2D.Float(getViewX(), getViewY(), getViewWidth(), getViewHeight());
        }
        s = s.trim();
        String[] params = s.trim().split("(\\s+)|(\\,)");
        float x = Float.valueOf(params[0]);
        float y = Float.valueOf(params[1]);
        float width = Float.valueOf(params[2]);
        float height = Float.valueOf(params[3]);
        return new Rectangle2D.Float(x, y, width, height);
    }

    public SVGObject getParent() {
        return parent;
    }
    
    public SVGObject getPropertyParent(){
        if (propertyParent==null){
            return getParent();
        }
        return propertyParent;
    }

    public SVGDocument getRootDocument() {
        if (parent == null) {
            return null;
        }
        return parent.getRootDocument();
    }


    public AffineTransform getToPointTransform() {
        AffineTransform ret = new AffineTransform();
        Rectangle2D vBox = getCurrentViewBox();
        Rectangle2D vp = getCurrentViewport();
        double sx = vp.getWidth() / vBox.getWidth();
        double sy = vp.getHeight() / vBox.getHeight();
        double tx = 0, ty = 0;
        String pr = getPreserveAspectRatio();
        if (pr==null){
            pr=XMID+YMID+" "+MEET;
        }
        if (pr != null && !pr.equals(NONE)) {
            if (pr.contains(SLICE)) {
                sx = sy = Math.max(sx, sy);
            } else {
                sx = sy = Math.min(sx, sy);
            }
            double wx = vBox.getWidth() * sx;
            double wy = vBox.getHeight() * sy;
            if (pr.contains(XMID)) {
                tx = (vp.getWidth() - wx) / 2;
            } else if (pr.contains(XMAX)) {
                tx = vp.getWidth() - wx;
            }
            if (pr.contains(YMID)) {
                ty = (vp.getHeight() - wy) / 2;
            } else if (pr.contains(YMAX)) {
                ty = vp.getHeight() - wy;
            }
        }
        //ret.translate(tx, ty);
        ret.translate(vp.getX()+tx, vp.getY()+ty);
        ret.scale(sx, sy);
        ret.translate(-vBox.getX(), -vBox.getY());
        return ret;
    }

    public String getId() {
        return id;
    }

    public void resetCompile() {
        compiled = false;
    }

    public void compile() {
        objectTransform = attributes.getTransform(this);
        viewTransform = getToPointTransform();
        compiled = true;
    }

    public boolean isCompiled() {
        return compiled;
    }

    /**
     * public float toPoint(String para,int direction, int tp){
     * return toPoint(this,para,direction,tp );
     * }*/
    public static float toPixel(SVGObject obj, String para, int direction, int type) {
        String fDef = "(\\-?((\\d+(\\.\\d+)?)|(0?\\.(\\d+))))";
        //Rectangle2D viewport=null;
        Rectangle2D viewport = new Rectangle2D.Float(0, 0, 800, 600);
        if (obj != null) {
            viewport = obj.getCurrentViewport();
        }
        //Rectangle2D viewBox=null;
        Rectangle2D viewBox = new Rectangle2D.Float(0, 0, 800, 600);
        if (obj != null) {
            viewBox = obj.getCurrentViewBox();
        }
        para = para.trim();
        if (para.matches(fDef + "\\s*pt")) {
            return Float.valueOf(para.replace("pt", "")) * POINT;
        } else if (para.matches(fDef + "\\s*mm")) {
            return Float.valueOf(para.replace("mm", "")) * MM;
        } else if (para.matches(fDef + "\\s*cm")) {
            return Float.valueOf(para.replace("cm", "")) * CM;
        } else if (para.matches(fDef + "\\s*pc")) {
            return Float.valueOf(para.replace("pc", "")) * PC;
        } else if (para.matches(fDef + "\\s*in")) {
            return Float.valueOf(para.replace("in", "")) * IN;
        } else if (para.matches(fDef + "\\s*em")) {
            if (obj != null) {
                float f = obj.getAttributes().getFontSize(obj);
                return f * Float.valueOf(para.replace("em", ""));
            }
            return Float.valueOf(para.replace("em", "")) * 12 * POINT;
        } else if (para.matches(fDef + "\\s*ex")) {
            return Float.valueOf(para.replace("ex", "")) * 12 * POINT;
        } else if (para.matches(fDef + "\\s*%")) {
            float f = Float.valueOf(para.replace("%", "")) / 100f;
            float sw = (float) viewBox.getWidth();
            float sh = (float) viewBox.getHeight();
            if (type == LENGTH) {
                if (direction == HORIZONTAL) {
                    return f * sw;
                } else if (direction == VERTICAL) {
                    return f * sh;
                } else {
                    return (float) (Math.sqrt(sw * sw + sh * sh) / Math.sqrt(2)) * f;
                }
            }
            if (direction == HORIZONTAL) {
                return f * sw;
            } else {
                return f * sh;
            }
        } else {
            return Float.valueOf(para.replace("px", ""));

        }
    }

    protected abstract void setObjectAttributes(Attributes attr);

    public abstract void paint(Graphics2D g);

    public abstract Rectangle2D getBounds();

    public abstract SVGObject getWritableInstance(SVGObject newParent);

    public abstract String getName();
}
