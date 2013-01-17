/*
 * SVGMarker.java
 *
 * Created on 2008/09/30, 10:41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package svg.oject;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import org.xml.sax.Attributes;
import svg.attribute.SVGAttributes;

/**
 *
 * @author i002060
 */
public class SVGMarker extends SVGAbstractGroup {

    public static final String MARKER_UNITS = "markerUnits";
    public static final String REF_X = "refX";
    public static final String REF_Y = "refY";
    public static final String MARKER_WIDTH = "markerWidth";
    public static final String MARKER_HEIGHT = "markerHeight";
    public static final String ORIENT = "orient";
    //
    public static final String STROKE_WIDTH = "strokeWidth";
    public static final String USER_SPACE_ON_USE = "userSpaceOnUse";
    public static final String AUTO = "auto";
    public static final String[] ATTRIBUTE_SET = new String[]{
        MARKER_UNITS,
        REF_X,
        REF_Y,
        MARKER_WIDTH,
        MARKER_HEIGHT,
        ORIENT
    };
    private HashMap<String, String> objectAttributes = null;

    /** Creates a new instance of SVGMarker */
    private SVGMarker() {
    }

    public SVGMarker(SVGObject parent) {
        super(parent);
    }

    public SVGMarker(SVGObject parent, Attributes attr) {
        super(parent);
        propertyParent=parent;
        super.setAttributes(attr);
        setObjectAttributes(attr);
        setViewBox(attr);
    }

    @Override
    protected void setObjectAttributes(Attributes attr) {
        if (objectAttributes == null) {
            objectAttributes = new HashMap<String, String>();
        }
        HashMap<String, String> a = SVGAttributes.getMapFromAttributes(attr);
        for (int i = 0; i < ATTRIBUTE_SET.length; i++) {
            if (a.containsKey(ATTRIBUTE_SET[i])) {
                objectAttributes.put(ATTRIBUTE_SET[i], a.get(ATTRIBUTE_SET[i]));
            }
        }

    }

    public String getMarkarUnits() {
        String s = objectAttributes.get(MARKER_UNITS);
        if (s == null) {
            s = STROKE_WIDTH;
        }
        return s;
    }

    @Override
    public float getViewWidth() {
        String s = objectAttributes.get(MARKER_WIDTH);
        if (s == null) {
            s = "3";
        }
        if (getMarkarUnits().equals(STROKE_WIDTH)) {
            return Float.valueOf(s) * parent.getAttributes().getStrokeWidth(parent);
        }
        return toPixel(parent, s, HORIZONTAL, LENGTH);
    }

    @Override
    public float getViewHeight() {
        String s = objectAttributes.get(MARKER_HEIGHT);
        if (s == null) {
            s = "3";
        }
        if (getMarkarUnits().equals(STROKE_WIDTH)) {
            return Float.valueOf(s) * parent.getAttributes().getStrokeWidth(parent);
        }
        return toPixel(parent, s, VERTICAL, LENGTH);
    }

    @Override
    public float getViewX() {
        return 0;
    }

    @Override
    public float getViewY() {
        return 0;
    }

    @Override
    public boolean isViewportDefined() {
        return true;
    }

    public float getRefX() {
        String s = objectAttributes.get(REF_X);
        if (s == null) {
            s = "0";
        }
        return toPixel(parent, s, HORIZONTAL, LENGTH);
    }

    public float getRefY() {
        String s = objectAttributes.get(REF_Y);
        if (s == null) {
            s = "0";
        }
        return toPixel(parent, s, VERTICAL, LENGTH);
    }

    @Override
    public Rectangle2D getCurrentViewBox() {
        if (super.isViewBoxDefined()) {
            return super.getCurrentViewBox();
        }
        return getCurrentViewport();
    }

    @Override
    public boolean isViewBoxDefined() {
        return true;
    }
    // mode 1-start mode 2-middle mode 4-end
    public void paintMarker(Graphics2D g, int mode, Shape s) {
        if (mode == 0) {
            return;
        }
        AffineTransform tx = getToPointTransform();
        g = (Graphics2D) g.create();
        PathIterator pth = s.getPathIterator(null);
        Point2D currentPoint = null, prevPoint = null;
        Point2D firstVec1 = null, firstVec2 = null;
        boolean start = false, middle = false;
        float[] coords = new float[6];
        Point2D p0 = null, p1 = null, p2 = null;
        while (!pth.isDone()) {
            int type = pth.currentSegment(coords);
            AffineTransform transform = null;
            switch (type) {
                case PathIterator.SEG_MOVETO:
                    currentPoint = new Point2D.Float(coords[0], coords[1]);
                    if (middle && (mode & 4) != 0) {
                        transform = getMarkerTransform(p0, p1, p2);
                    }
                    p0 = null;
                    p1 = currentPoint;
                    p2 = null;
                    start = true;
                    middle = false;
                    break;
                case PathIterator.SEG_LINETO:
                    currentPoint = new Point2D.Float(coords[0], coords[1]);
                    p2 = currentPoint;
                    if ((start && (mode & 1) != 0) || (middle && (mode & 2) != 0)) {
                        transform = getMarkerTransform(p0, p1, p2);
                    }
                    if (start) {
                        firstVec1 = p1;
                        firstVec2 = p2;
                    }
                    p0 = prevPoint;
                    p1 = currentPoint;
                    p2 = null;
                    start = false;
                    middle = true;
                    break;
                case PathIterator.SEG_QUADTO:
                    currentPoint = new Point2D.Float(coords[2], coords[3]);
                    p2 = new Point2D.Float(coords[0], coords[1]);
                    if ((start && (mode & 1) != 0) || (middle && (mode & 2) != 0)) {
                        transform = getMarkerTransform(p0, p1, p2);
                    }
                    if (start) {
                        firstVec1 = p1;
                        firstVec2 = p2;
                    }
                    p0 = p2;
                    p1 = currentPoint;
                    p2 = null;
                    middle = true;
                    start = false;
                    break;
                case PathIterator.SEG_CUBICTO:
                    currentPoint = new Point2D.Float(coords[4], coords[5]);
                    p2 = new Point2D.Float(coords[0], coords[1]);
                    if ((start && (mode & 1) != 0) || (middle && (mode & 2) != 0)) {
                        transform = getMarkerTransform(p0, p1, p2);
                    }
                    if (start) {
                        firstVec1 = p1;
                        firstVec2 = p2;
                    }
                    p0 = new Point2D.Float(coords[2], coords[3]);
                    p1 = currentPoint;
                    p2 = null;
                    middle = true;
                    start = false;
                    break;
                case PathIterator.SEG_CLOSE:
                    p1 = currentPoint = firstVec1;
                    p2 = firstVec2;
                    if (middle && (mode & 2) != 0) {
                        transform = getMarkerTransform(p0, p1, p2);
                    }
                    p0 = p1 = p2 = null;
                    middle = false;
                    start = false;
                    break;
            }
             if (transform != null) {
                AffineTransform stx = g.getTransform();
                g.transform(transform);
                g.transform(tx);
                g.setColor(Color.BLUE);
                for (SVGObject child : this) {
                    child.paint(g);
                }
                g.setTransform(stx);
            }
            prevPoint = currentPoint;
            pth.next();
        }
        if (middle && (mode & 4) != 0) {
            AffineTransform transform = getMarkerTransform(p0, p1, p2);
            if (transform != null) {
                AffineTransform stx = g.getTransform();
                g.transform(transform);
                g.transform(tx);
                g.setColor(Color.BLUE);
                for (SVGObject child : this) {
                    child.paint(g);
                }
                g.setTransform(stx);
            }
        }
        g.dispose();
    }

    public AffineTransform getMarkerTransform(Point2D p0, Point2D p1, Point2D p2) {
        if (p1 == null) {
            return null;
        }
        if (p0 == null && p2 == null) {
            return null;
        }
        String s = objectAttributes.get(ORIENT);
        float theta = 0;
        if (s == null || s.equals(AUTO)) {
            if (p0 == null) {
                theta = (float) Math.atan2(p2.getY() - p1.getY(), p2.getX() - p1.getX());
            } else if (p2 == null) {
                theta = (float) Math.atan2(p1.getY() - p0.getY(), p1.getX() - p0.getX());
            } else {
                float pi2 = (float) Math.PI * 2;
                float thetaA= (float) (Math.atan2(p2.getY() - p1.getY(), p2.getX() - p1.getX()));
                float thetaB=(float)(Math.atan2(p1.getY() - p0.getY(), p1.getX() - p0.getX()));
               theta=(thetaA-thetaB)/2+thetaB;
            }

        } else {
            theta = (float) Math.toRadians(Float.valueOf(s));
        }
        AffineTransform result = AffineTransform.getTranslateInstance(p1.getX(), p1.getY());
        result.rotate(theta);
        Rectangle2D vp=getCurrentViewport();
        Rectangle2D vb=getCurrentViewBox();
        double sx=vp.getWidth()/vb.getWidth();
        double sy=vp.getHeight()/vb.getHeight();
        double tx=0,ty=0;
        String pr=getPreserveAspectRatio();
        if (pr !=null && !pr.equals(NONE)){
            if (pr.contains(MEET)){
                sx=sy=Math.min(sx, sy);
            }else{
                sx=sy=Math.max(sx, sy);
            }
            double sw=vb.getWidth()*sx;
            double sh=vb.getHeight()*sy;
            
            if (pr.contains(XMID)){
                tx=(vp.getWidth()-sw)/2;
            }else if (pr.contains(XMAX)){
                tx=vp.getWidth()-sw;
            }
            if (pr.contains(YMID)){
                ty=(vp.getHeight()-sh)/2;
            }else{
                ty=vp.getHeight()-sh;
            }
        }
        tx=-getRefX()*sx-tx;
        ty=-getRefY()*sy-ty;
        result.translate(tx, ty);
        return result;
    }

    @Override
    public void paint(Graphics2D g) {
        //do nothing
    }

    @Override
    public SVGMarker getWritableInstance(SVGObject newParent) {
        SVGMarker result = new SVGMarker(newParent);
        result.setAttributes(getAttributes().clone());
        result.objectAttributes = (HashMap<String,String>)objectAttributes.clone();
        result.viewAttribute = (HashMap<String,String>)viewAttribute.clone();
        result.propertyParent=parent;
        for (SVGObject child : this) {
            result.add(child.getWritableInstance(result));
        }
        return result;
    }

    @Override
    public String getName() {
        return "marker";
    }

    @Override
    public String getXML() {
        return "";
    }
    
}
