/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package svg.attribute;

import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import org.xml.sax.Attributes;
import svg.oject.SVGDocument;
import svg.oject.SVGObject;

/**
 *
 * @author takashi
 */
public class SVGGradient implements SVGPaint {

    public static final String LINEAR = "linearGradient";
    public static final String RADIAL = "radialGradient";
    //keys
    public static final String UNITS = "gradientUnits";
    public static final String TRANSFORM = "transform";
    public static final String X1 = "x1";
    public static final String Y1 = "y1";
    public static final String X2 = "x2";
    public static final String Y2 = "y2";
    //
    public static final String CX = "cx";
    public static final String CY = "cy";
    public static final String R = "r";
    public static final String FX = "fx";
    public static final String FY = "fy";
    //
    public static final String METHOD = "spreadMethod";
    public static final String XLINK = "xlink:href";
    public static final String ID = "id";
    //
    public static final String[] KEY_SET=new String[]{
        UNITS,
        TRANSFORM,
        X1,
        Y1,
        X2,
        Y2,
        CX,
        CY,
        R,
        FX,
        FY,
        METHOD,
        XLINK        
    };
    //values
    public static final String USER_SPACE = "userSpaceOnUse";
    public static final String OBJECT_BOUNDS = "objectBoundingBox";
    public static final String METHOD_PAD = "pad";
    public static final String METHOD_REFLECT = "reflect";
    public static final String METHOD_REPEAT = "repeat";    //map
    private HashMap<String, String> attrMap;
    private String mode;
    //owner document
    private String id;
    //fractions
    private ConcurrentSkipListMap<Float, Color> fractions;

    private SVGGradient() {
        mode = LINEAR;
        fractions = new ConcurrentSkipListMap<Float, Color>();
        attrMap = new HashMap<String, String>();
        id = null;
    }

    public SVGGradient(SVGDocument owner, String mode, Attributes attr) {
        this.mode = mode;
        fractions = new ConcurrentSkipListMap<Float, Color>();
        attrMap = new HashMap<String, String>();
        setAttributes(attr);
        id = attr.getValue(ID);
        if (id != null) {
            owner.addLink(id, this);
        }
    }

    public void setAttributes(Attributes attr) {
        HashMap<String, String> map = SVGAttributes.getMapFromAttributes(attr);
        for (int i=0;i<KEY_SET.length;i++){
            if(map.containsKey(KEY_SET[i])){
                attrMap.put(KEY_SET[i],map.get(KEY_SET[i]));
            }
        }
    }

    private float getPersent(String s) {
        String regex = ".+\\%$";
        if (s.matches(regex)) {
            s.replace("%", "");
            return Float.valueOf(s) / 100;
        }
        return Float.valueOf(s);
    }

    private float getCoords(SVGObject obj, String s, int direction, int type) {
        String regex = ".+\\%$";
        String units = getUnits(obj);
        if (!s.matches(regex) || units.equals(USER_SPACE)) {
            return SVGObject.toPixel(obj, s, direction, type);
        }
        Rectangle2D r = obj.getBounds();
        if (direction == SVGObject.HORIZONTAL) {
            if (type == SVGObject.COORDS) {
                return (float) (r.getX() + r.getWidth() * getPersent(s));
            } else {
                return (float) (r.getWidth() * getPersent(s));
            }
        } else {
            if (type == SVGObject.COORDS) {
                return (float) (r.getY() + r.getHeight() * getPersent(s));
            } else {
                return (float) (r.getHeight() * getPersent(s));
            }
        }
    }

    /*    public void addStop(Attributes attr) {
    float f = getPersent(attr.getValue("offset"));
    Color c = SVGAttributes.getColorFromAttribute(attr.getValue("stop-color"), "stop-opacity");
    fractions.put(f, c);
    }*/
    public void addStop(SVGStop stop) {
        fractions.put(stop.getOffset(), stop.getColor());
    }

    private SVGGradient getLink(SVGObject obj) {
        String s = (String) attrMap.get(XLINK);
        if (s != null) {
            return (SVGGradient) (obj.getRootDocument().getLink(s));
        }
        return null;
    }

    protected float[] getFractions(SVGObject obj) {
        SVGGradient link = getLink(obj);
        if (link != null && fractions.isEmpty()) {
            return link.getFractions(obj);
        }
        float[] result = new float[fractions.size()];
        int i = 0;
        for (Float f : fractions.keySet()) {
            result[i++] = f;
        }
        return result;
    }

    protected Color[] getColors(SVGObject obj) {
        SVGGradient link = getLink(obj);
        if (link != null && fractions.isEmpty()) {
            return link.getColors(obj);
        }
        float[] fracs = getFractions(obj);
        Color[] result = new Color[fracs.length];
        for (int i = 0; i < fracs.length; i++) {
            result[i] = (Color) fractions.get(fracs[i]);
        }
        return result;
    }

    protected String getUnits(SVGObject obj) {
        SVGGradient link = getLink(obj);
        String s = attrMap.get(UNITS);
        if (s == null) {
            if (link != null) {
                return link.getUnits(obj);
            }
            return OBJECT_BOUNDS;
        }
        return s;
    }

    protected float getCx(SVGObject obj) {
        SVGGradient link = getLink(obj);
        String s = attrMap.get(CX);
        if (s == null) {
            if (link != null) {
                return link.getCx(obj);
            }
            s = "50%";
        }
        if (getUnits(obj).equals(OBJECT_BOUNDS)) {
            Rectangle2D r = obj.getBounds();
            s = s.trim();
            float f = 0;
            if (s.contains("%")) {
                s = s.replace("%", "");
                f = Float.valueOf(s) / 100;
            } else {
                f = Float.valueOf(s);
            }
            return (float) (r.getX() + r.getWidth() * f);
        }
        return getCoords(obj, s, SVGObject.HORIZONTAL, SVGObject.COORDS);
    }

    protected float getCy(SVGObject obj) {
        SVGGradient link = getLink(obj);
        String s = attrMap.get(CY);
        if (s == null) {
            if (link != null) {
                return link.getCy(obj);
            }
            s = "50%";
        }
        if (getUnits(obj).equals(OBJECT_BOUNDS)) {
            Rectangle2D r = obj.getBounds();
            s = s.trim();
            float f = 0;
            if (s.contains("%")) {
                s = s.replace("%", "");
                f = Float.valueOf(s) / 100;
            } else {
                f = Float.valueOf(s);
            }
            return (float) (r.getY() + r.getHeight() * f);
        }
        return getCoords(obj, s, SVGObject.VERTICAL, SVGObject.COORDS);
    }

    protected float getFx(SVGObject obj) {
        SVGGradient link = getLink(obj);
        String s = attrMap.get(FX);
        if (s == null) {
            if (link != null) {
                return link.getFx(obj);
            }
            return getCx(obj);
        }
        if (getUnits(obj).equals(OBJECT_BOUNDS)) {
            Rectangle2D r = obj.getBounds();
            s = s.trim();
            float f = 0;
            if (s.contains("%")) {
                s = s.replace("%", "");
                f = Float.valueOf(s) / 100;
            } else {
                f = Float.valueOf(s);
            }
            return (float) (r.getX() + r.getWidth() * f);
        }
        return getCoords(obj, s, SVGObject.HORIZONTAL, SVGObject.COORDS);
    }

    protected float getFy(SVGObject obj) {
        SVGGradient link = getLink(obj);
        String s = attrMap.get(FY);
        if (s == null) {
            if (link != null) {
                return link.getFy(obj);
            }
            return getCy(obj);
        }
        if (getUnits(obj).equals(OBJECT_BOUNDS)) {
            Rectangle2D r = obj.getBounds();
            s = s.trim();
            float f = 0;
            if (s.contains("%")) {
                s = s.replace("%", "");
                f = Float.valueOf(s) / 100;
            } else {
                f = Float.valueOf(s);
            }
            return (float) (r.getY() + r.getHeight() * f);
        }
        return getCoords(obj, s, SVGObject.VERTICAL, SVGObject.COORDS);
    }

    protected float getR(SVGObject obj) {
        SVGGradient link = getLink(obj);
        String s = attrMap.get(R);
        if (s == null) {
            if (link != null) {
                return link.getR(obj);
            }
            s = "50%";
        }
        if (getUnits(obj).equals(OBJECT_BOUNDS)) {
            Rectangle2D r = obj.getBounds();
            s = s.trim();
            float f = 0;
            if (s.contains("%")) {
                s = s.replace("%", "");
                f = Float.valueOf(s) / 100;
            } else {
                f = Float.valueOf(s);
            }
            return (float) (r.getWidth() * f);
        }
        return getCoords(obj, s, SVGObject.HORIZONTAL, SVGObject.LENGTH);
    }

    protected float getX1(SVGObject obj) {
        SVGGradient link = getLink(obj);
        String s = attrMap.get(X1);
        if (s == null) {
            if (link != null) {
                return link.getX1(obj);
            }
            s = "0%";
        }
        if (getUnits(obj).equals(OBJECT_BOUNDS)) {
            Rectangle2D r = obj.getBounds();
            s = s.trim();
            float f = 0;
            if (s.contains("%")) {
                s = s.replace("%", "");
                f = Float.valueOf(s) / 100;
            } else {
                f = Float.valueOf(s);
            }
            return (float) (r.getX() + r.getWidth() * f);
        }
        return getCoords(obj, s, SVGObject.HORIZONTAL, SVGObject.COORDS);
    }

    protected float getY1(SVGObject obj) {
        SVGGradient link = getLink(obj);
        String s = attrMap.get(Y1);
        if (s == null) {
            if (link != null) {
                return link.getY1(obj);
            }
            s = "0%";
        }
        if (getUnits(obj).equals(OBJECT_BOUNDS)) {
            Rectangle2D r = obj.getBounds();
            s = s.trim();
            float f = 0;
            if (s.contains("%")) {
                s = s.replace("%", "");
                f = Float.valueOf(s) / 100;
            } else {
                f = Float.valueOf(s);
            }
            return (float) (r.getY() + r.getHeight() * f);
        }
        return getCoords(obj, s, SVGObject.VERTICAL, SVGObject.COORDS);
    }

    protected float getX2(SVGObject obj) {
        SVGGradient link = getLink(obj);
        String s = attrMap.get(X2);
        if (s == null) {
            if (link != null) {
                return link.getX2(obj);
            }
            s = "100%";
        }
        if (getUnits(obj).equals(OBJECT_BOUNDS)) {
            Rectangle2D r = obj.getBounds();
            s = s.trim();
            float f = 0;
            if (s.contains("%")) {
                s = s.replace("%", "");
                f = Float.valueOf(s) / 100;
            } else {
                f = Float.valueOf(s);
            }
            return (float) (r.getX() + r.getWidth() * f);
        }
        return getCoords(obj, s, SVGObject.HORIZONTAL, SVGObject.COORDS);
    }

    protected float getY2(SVGObject obj) {
        SVGGradient link = getLink(obj);
        String s = attrMap.get(Y2);
        if (s == null) {
            if (link != null) {
                return link.getY2(obj);
            }
            s = "0%";
        }
        if (getUnits(obj).equals(OBJECT_BOUNDS)) {
            Rectangle2D r = obj.getBounds();
            s = s.trim();
            float f = 0;
            if (s.contains("%")) {
                s = s.replace("%", "");
                f = Float.valueOf(s) / 100;
            } else {
                f = Float.valueOf(s);
            }
            return (float) (r.getY() + r.getHeight() * f);
        }
        return getCoords(obj, s, SVGObject.VERTICAL, SVGObject.COORDS);
    }

    protected CycleMethod getMethod(SVGObject obj) {
        SVGGradient link = getLink(obj);
        String s = attrMap.get(METHOD);
        if (s == null) {
            if (link != null) {
                return link.getMethod(obj);
            } else {
                return CycleMethod.NO_CYCLE;
            }
        }
        s = s.trim();
        if (s.equals(METHOD_REFLECT)) {
            return CycleMethod.REFLECT;
        } else if (s.equals(METHOD_REPEAT)) {
            return CycleMethod.REPEAT;
        }
        return CycleMethod.NO_CYCLE;
    }

    protected AffineTransform getTransform(SVGObject obj) {
        SVGGradient link = getLink(obj);
        String s = attrMap.get(TRANSFORM);
        if (s == null) {
            if (link != null) {
                return link.getTransform(obj);
            }
            return null;
        }
        return SVGAttributes.getTransformFromAttribute(s, obj);
    }

    @Override
    public Paint getPaint(SVGObject obj) {
        float[] fracs = getFractions(obj);
        Color[] colors = getColors(obj);
        AffineTransform tx = getTransform(obj);
        if (mode.equals(RADIAL)) {
            Point2D cp = new Point2D.Float(getCx(obj), getCy(obj));
            Point2D fp = new Point2D.Float(getFx(obj), getFy(obj));
            float r = getR(obj);
            if (tx != null) {
                tx.transform(cp, cp);
                tx.transform(fp, fp);
                r = (float) tx.getScaleX() * r;
            }
            return new RadialGradientPaint(cp, r, fp, fracs, colors, getMethod(obj));
        } else {
            Point2D p1 = new Point2D.Float(getX1(obj), getY1(obj));
            Point2D p2 = new Point2D.Float(getX2(obj), getY2(obj));
            if (tx != null) {
                tx.transform(p1, p1);
                tx.transform(p2, p2);
            }
            return new LinearGradientPaint(p1, p2, fracs, colors, getMethod(obj));
        }
    }

    @Override
    public void setPaint(Paint paint) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getXML() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
