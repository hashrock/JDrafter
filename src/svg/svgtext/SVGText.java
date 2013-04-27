/*
 * SVGText.java
 *
 * Created on 2008/09/22, 14:33
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package svg.svgtext;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Vector;
import org.xml.sax.Attributes;
import svg.attribute.SVGAttributes;
import svg.oject.SVGObject;

/**
 *
 * @author i002060
 */
public class SVGText extends SVGObject {

    public static final String X = "x";
    public static final String Y = "y";
    public static final String DX = "dx";
    public static final String DY = "dy";
    public static final String ROTATE = "rotate";
    public static final String TEXT_LENGTH = "textLength";
    public static final String LENGTH_ADJUST = "lengthAdjust";
    //
    public static final String SPACING = "spacing";
    public static final String SPACING_AND_GLYPHS = "spacingAndGlyphs";
    //
    public static float CURRENT_X = 0,  CURRENT_Y = 0;
    //
    protected static final String[] KEYSET = new String[]{X, Y, DX, DY, ROTATE, TEXT_LENGTH, LENGTH_ADJUST};
    protected HashMap<String, String> objectAttributes;
    protected Vector<Object> children;
    protected String[] xList = null;
    private int xIndex = 0;
    protected String[] yList = null;
    private int yIndex = 0;
    protected String[] dxList = null;
    private int dxIndex = 0;
    protected String[] dyList = null;
    private int dyIndex = 0;
    protected float[] rotateList = null;
    private int rotateIndex = 0;
    protected Shape cShape = null;
    protected Vector<TextCoords> cTextCoords = null;
    protected Paint cFill = null;
    protected Paint cStroke = null;
    protected BasicStroke cBasicStroke = null;

    /** Creates a new instance of SVGText */
    protected SVGText() {
        objectAttributes = new HashMap<String, String>();
        children = new Vector<Object>();
    }

    public SVGText(SVGObject parent) {
        super(parent);
        objectAttributes = new HashMap<String, String>();
        children = new Vector<Object>();
    }

    public SVGText(SVGObject parent, Attributes attr) {
        super(parent);
        objectAttributes = new HashMap<String, String>();
        children = new Vector<Object>();
        setAttributes(attr);
        setObjectAttributes(attr);
    }

    @Override
    protected void setObjectAttributes(Attributes attr) {
        for (int i = 0; i < KEYSET.length; i++) {
            String s = attr.getValue(KEYSET[i]);
            if (s != null) {
                objectAttributes.put(KEYSET[i], s);
            }
        }
        String s = attr.getValue(X);
        String regex = "(\\s+)|(\\s*\\,\\s*)";
        if (s != null) {
            s = s.trim();
            xList = s.split(regex);
        }
        s = attr.getValue(Y);
        if (s != null) {
            s = s.trim();
            yList = s.split(regex);
        }
        s = attr.getValue(DX);
        if (s != null) {
            dxList = s.split(regex);
        }
        s = attr.getValue(DY);
        if (s != null) {
            dyList = s.split(regex);
        }
        s = attr.getValue(ROTATE);
        if (s != null) {
            String[] rl = s.split(regex);
            rotateList = new float[rl.length];
            for (int i = 0; i < rotateList.length; i++) {
                rotateList[i] = Float.valueOf(rl[i]);
            }
        }
    }

    @Override
    public SVGObject getWritableInstance(SVGObject newParent) {
        SVGText result = new SVGTspan(newParent);
        result.setAttributes(getAttributes().clone());
        result.children = (Vector<Object>) children.clone();
        result.objectAttributes = (HashMap<String,String>)objectAttributes.clone();
        result.xList = (String[])xList.clone();
        result.yList = (String[])yList.clone();
        result.dxList = (String[])dxList.clone();
        result.dyList = (String[])dyList.clone();
        result.rotateList = (float[])rotateList.clone();
        return result;
    }

    @Override
    public String getName() {
        return "text";
    }

    @Override
    public String getXML() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void compile() {
        super.compile();

        //
        top();
        //
        for (int i = 0; i < children.size(); i++) {
            Object o = children.get(i);
            if (o instanceof String) {
                if (cTextCoords == null) {
                    cTextCoords = getTextCoords(this, (String) o);
                } else {
                    cTextCoords.addAll(getTextCoords(this, (String) o));
                }
            } else if (o instanceof SVGText) {
                ((SVGText) o).compile();
            }
        }
        GeneralPath gp = null;
        if (cTextCoords != null) {
            gp = new GeneralPath();
            for (int i = 0; i < cTextCoords.size(); i++) {
                gp.append(cTextCoords.get(i).getShape(), false);
            }
        }
        cShape = gp;

        SVGAttributes attributes = getAttributes();
        cFill = attributes.getFill(this, this);
        cStroke = attributes.getStroke(this, this);
        cBasicStroke = attributes.getBasicStroke(this);
        cTextCoords = null;
    }

    @Override
    public void paint(Graphics2D g) {

        if (!isCompiled()) {
            compile();
        }
        g = (Graphics2D) g.create();
        AffineTransform tx = objectTransform;
        if (tx != null) {
            g.transform(tx);
        }
        Composite cmp = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getAttributes().getOpacity(this)));
        if (cShape != null) {
            if (cFill != null) {
                g.setPaint(cFill);
                g.fill(cShape);
            }
            if (cStroke != null) {
                g.setPaint(cStroke);
                g.setStroke(cBasicStroke);
                g.draw(cShape);
            }
        }
        for (int i = 0; i < children.size(); i++) {
            Object o = children.get(i);
            if (o instanceof SVGObject) {
                ((SVGObject) o).paint(g);
            }
        }
        g.dispose();
    }

    @Override
    public Rectangle2D getBounds() {
        if (!isCompiled()) {
            compile();
        }
        if (cShape == null) {
            return new Rectangle2D.Float();
        }
        return cShape.getBounds2D();
    }

    public void addChild(Object o) {
        if (!children.contains(o)) {
            children.add(o);
        }
    }

    public void removeChild(Object o) {
        children.remove(o);
    }

    public SVGText getAnscester(SVGObject obj) {
        return null;
    }
    //
    public boolean xHasNext() {
        if (xList != null && xIndex < xList.length) {
            return true;
        } else {
            SVGText a = getAnscester(this);
            if (a != null) {
                return a.xHasNext();
            }
            return false;
        }
    }

    public boolean yHasNext() {
        if (yList != null && yIndex < yList.length) {
            return true;
        } else {
            SVGText a = getAnscester(this);
            if (a != null) {
                return a.yHasNext();
            }
            return false;
        }
    }

    public boolean dxHasNext() {
        if (dxList != null && dxIndex < dxList.length) {
            return true;
        } else {
            SVGText a = getAnscester(this);
            if (a != null) {
                return a.dxHasNext();
            }
            return false;
        }
    }

    public boolean dyHasNext() {
        if (dyList != null && dyIndex < dyList.length) {
            return true;
        } else {
            SVGText a = getAnscester(this);
            if (a != null) {
                return a.dyHasNext();
            }
            return false;
        }
    }

    public boolean rotateHasNext() {
        if (rotateList != null && rotateIndex < rotateList.length) {
            return true;
        } else {
            SVGText a = getAnscester(this);
            if (a != null) {
                return a.rotateHasNext();
            }
            return false;
        }
    }
    //
    public float nextX() {
        float result = CURRENT_X;
        if (xList != null && xIndex < xList.length) {
            result = toPixel(this, xList[xIndex++], HORIZONTAL, COORDS);
        } else {
            SVGText a = getAnscester(this);
            if (a != null) {
                result = a.nextX();
            }
        }
        return result;
    }
    //
    public float nextXbyLength() {
        float result = CURRENT_X;
        if (xList != null && xIndex < xList.length) {
            result = toPixel(this, xList[xIndex++], HORIZONTAL, LENGTH);
        } else {
            SVGText a = getAnscester(this);
            if (a != null) {
                result = a.nextXbyLength();
            }
        }
        return result;
    }

    public float nextY() {
        float result = CURRENT_Y;
        if (yList != null && yIndex < yList.length) {
            result = toPixel(this, yList[yIndex++], VERTICAL, COORDS);
        } else {
            SVGText a = getAnscester(this);
            if (a != null) {
                result = a.nextY();
            }
        }
        return result;
    }

    public float nextYbyLength() {
        float result = CURRENT_Y;
        if (yList != null && yIndex < yList.length) {
            result = toPixel(this, yList[yIndex++], VERTICAL, LENGTH);
        } else {
            SVGText a = getAnscester(this);
            if (a != null) {
                result = a.nextYbyLength();
            }
        }
        return result;
    }

    public float nextDx() {
        float result = 0;
        if (dxList != null && dxIndex < dxList.length) {
            result = toPixel(this, dxList[dxIndex++], HORIZONTAL, LENGTH);
        } else {
            SVGText a = getAnscester(this);
            if (a != null) {
                result = a.nextDx();
            }
        }
        return result;
    }

    public float nextDy() {
        float result = 0;
        if (dyList != null && dyIndex < dyList.length) {
            result = toPixel(this, dyList[dyIndex++], VERTICAL, LENGTH);
        } else {
            SVGText a = getAnscester(this);
            if (a != null) {
                result = a.nextDy();
            }
        }
        return result;
    }

    public float nextRotate() {
        float result = 0;
        if (rotateList != null && rotateIndex < rotateList.length) {
            result = Float.valueOf(rotateList[rotateIndex++]);
        } else {
            SVGText a = getAnscester(this);
            if (a != null) {
                result = a.nextRotate();
            }
        }
        return result;
    }
    //
    public void top() {
        xIndex = yIndex = dxIndex = dyIndex = rotateIndex = 0;
        CURRENT_X = CURRENT_Y = 0;
    }

    public boolean hasNext() {
        return xHasNext() || yHasNext() || dxHasNext() || dyHasNext() || rotateHasNext();
    }

    public Vector<Object> getChildren() {
        return children;
    }

    public Vector<TextCoords> getTextCoords(SVGText obj, String st) {
        int i = 0;
        SVGTextLocater locater = getLocater();
        Vector<TextCoords> result = new Vector<TextCoords>();
        Font font = obj.getAttributes().getFont(obj);
        while (i < st.length()) {
            float x = 0,  y = 0;
            if (locater == null) {
                x = nextX();
                y = nextY();
            } else {
                x = nextXbyLength();
                y = nextYbyLength();
            }
            float dx = nextDx();
            float dy = nextDy();
            float rotate = nextRotate();
            int length = 1;
            if (!hasNext() && locater == null) {
                length = st.length() - i;
            }
            TextLayout tLayout = new TextLayout(st.substring(i, i + length), font, GRAPHICS.getFontRenderContext());
            if (locater == null) {
                result.add(new TextCoords(x + dx, y + dy, rotate, null, tLayout));
                CURRENT_X = x + dx + tLayout.getAdvance();
                CURRENT_Y = y + dy;
            } else {
                AffineTransform atf = locater.getTransform(x + dx);
                if (atf != null) {
                    result.add(new TextCoords(0, CURRENT_Y + dy, rotate, atf, tLayout));
                }
                CURRENT_X = x + dx + tLayout.getAdvance();
                CURRENT_Y += dy;
            }
            i += length;
        }
        return result;
    }

    public SVGTextLocater getLocater() {
        return null;
    }

    public class TextCoords {

        public float currentX;
        public float currentY;
        public float currentRotate;
        public TextLayout layout;
        public AffineTransform transform;

        public TextCoords() {
            this(0, 0, 0, null, null);
        }

        public TextCoords(float cx, float cy, float rotate, AffineTransform transform, TextLayout ly) {
            currentX = cx;
            currentY = cy;
            this.transform = transform;
            currentRotate = rotate;
            layout = ly;
        }

        public Shape getShape() {
            if (layout == null) {
                return null;
            }
            AffineTransform tx = AffineTransform.getTranslateInstance(currentX, currentY);
            tx.rotate(currentRotate * Math.PI / 180f);
            if (transform != null) {
                tx.preConcatenate(transform);
            }
            return layout.getOutline(tx);
        }
    }
}
