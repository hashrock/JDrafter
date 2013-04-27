/*
 * SVGUse.java
 *
 * Created on 2008/09/11, 16:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package svg.oject;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import org.xml.sax.Attributes;
import svg.attribute.SVGAttributes;

/**
 *
 * @author i002060
 */
public class SVGUse extends SVGObject {

    public static final String XLINK = "xlink:href";
    private SVGObject childObject = null;

    /** Creates a new instance of SVGUse */
    private SVGUse() {
    }

    protected SVGUse(SVGObject parent) {
        super(parent);
    }

    public SVGUse(SVGObject parent, Attributes attr) {
        super(parent);
        setAttributes(attr);
        setObjectAttributes(attr);
    }

    @Override
    protected void setObjectAttributes(Attributes attr) {
        setViewport(attr);
        if (viewAttribute.get(VIEWPORT_X)==null){
            viewAttribute.put(VIEWPORT_X,"0");
        }
        if (viewAttribute.get(VIEWPORT_Y)==null){
            viewAttribute.put(VIEWPORT_Y, "0");
        }
        if (viewAttribute.get(VIEWPORT_WIDTH)==null){
            viewAttribute.put(VIEWPORT_WIDTH, "100%");
        }
        if(viewAttribute.get(VIEWPORT_HEIGHT)==null){
            viewAttribute.put(VIEWPORT_HEIGHT,"100%");
        }
        setViewBox(attr);
        String s = attr.getValue(XLINK);
        s = s.replace("#", "").trim();
        childObject = ((SVGObject) getRootDocument().getLink(s)).getWritableInstance(this);
    }
    @Override
    public void paint(Graphics2D g) {
        AffineTransform stx = g.getTransform();
        AffineTransform tx = null;
        SVGAttributes attributes = getAttributes();
       // if (!(childObject instanceof SVGAbstractGroup)) {
            tx = getToPointTransform();
       // }
        if (attributes.getTransform(this) != null) {
            if (tx == null) {
                tx = attributes.getTransform(this);
            } else {
                tx.preConcatenate(attributes.getTransform(this));
            }
        }
        if (tx != null) {
            g.transform(tx);
        }
        Composite cmp = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, attributes.getOpacity(this)));
        childObject.paint(g);
        g.setTransform(stx);
        g.setComposite(cmp);
    }

    @Override
    public Rectangle2D getBounds() {
        return getCurrentViewport();
    }

    @Override
    public SVGObject getWritableInstance(SVGObject newParent) {
        SVGUse result = new SVGUse(newParent);
        result.setAttributes(getAttributes().clone());
        result.childObject = childObject.getWritableInstance(result);
        result.viewAttribute = viewAttribute;
        return result;
    }

    @Override
    public String getName() {
        return "use";
    }

    @Override
    public String getXML() {
        return "";
    }
}
