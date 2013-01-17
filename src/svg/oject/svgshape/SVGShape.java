/*
 * SVGShape.java
 *
 * Created on 2008/09/09, 14:02
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package svg.oject.svgshape;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import svg.attribute.SVGAttributes;
import svg.oject.*;

/**
 *
 * @author i002060
 */
public abstract class SVGShape extends SVGObject {
    
    protected HashMap<String, String> objectAttr = new HashMap<String, String>();
    protected Shape cShape=null;
    protected Paint cFill=null;
    protected Paint cStroke=null;
    protected BasicStroke cBasicStroke=null;
    
    /** Creates a new instance of SVGShape */
    protected SVGShape() {
    }
    
    protected SVGShape(SVGObject parent) {
        super(parent);
    }
    
    @Override
    public void compile(){
        super.compile();
        cShape=createShape();
        SVGAttributes attributes=getAttributes();
        cFill=attributes.getFill(this,this);
        cStroke=attributes.getStroke(this,this);
        cBasicStroke=attributes.getBasicStroke(this);
    }
    @Override
    public void paint(Graphics2D g) {
        if (!isCompiled()){
            compile();
        }
        g=(Graphics2D)g.create();
        SVGAttributes attributes = getAttributes();
        Paint fill = cFill;
        Paint stroke = cStroke;
        BasicStroke bStroke = cBasicStroke;
        AffineTransform tx = null;
        if (objectTransform !=null){
            tx=new AffineTransform(objectTransform);
        }
        Shape s = cShape;
        if (s == null) {
            return;
        }
        GeneralPath pth = new GeneralPath(s);
        pth.setWindingRule(attributes.getFillRule(this));
        if (tx != null) {
            g.transform(tx);
        }
        Composite cmp = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, attributes.getOpacity(this)));
        if (fill != null) {
            g.setPaint(fill);
            g.fill(pth);
        }
        if (stroke != null) {
            g.setPaint(stroke);
            g.setStroke(bStroke);
            g.draw(pth);
            if (!(parent instanceof SVGMarker))
                paintMaker(g,pth);
        }
        g.dispose();
    }
    public void paintMaker(Graphics2D g,Shape s){
        
    }
    @Override
    public Rectangle2D getBounds() {
        if (!compiled)
            compile();
        return cShape.getBounds2D();
    }
    public Shape getShape(){
        if (!isCompiled()){
            cShape=createShape();
        }
        return cShape;
    }
    public abstract Shape createShape();
}
