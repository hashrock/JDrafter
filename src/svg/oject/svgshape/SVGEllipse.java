/*
 * SVGCircle.java
 *
 * Created on 2008/09/09, 15:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package svg.oject.svgshape;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;
import org.xml.sax.Attributes;
import svg.oject.*;

/**
 *
 * @author i002060
 */
public class SVGEllipse extends SVGShape{
    public static final String CX="cx";
    public static final String CY="cy";
    public static final String RX="rx";
    public static final String RY="ry";
    public static final String[] ATTR_SET=new String[]{CX,CY,RX,RY};
    
    /** Creates a new instance of SVGCircle */
    public SVGEllipse() {
    }
    public SVGEllipse(SVGObject parent,Attributes attr){
        super(parent);
        setAttributes(attr);
        setObjectAttributes(attr);
    }
    @Override
    public Shape createShape() {
        String s=objectAttr.get(CX);
        if (s==null)
            s="0";
        float cx=toPixel(this,s,HORIZONTAL,COORDS);
        s=objectAttr.get(CY);
        if (s==null)
            s="0";
        float cy=toPixel(this,s,VERTICAL,COORDS);
        s=objectAttr.get(RX);
        float rx=toPixel(this,s,HORIZONTAL,LENGTH);
        s=objectAttr.get(RY);
        float ry=toPixel(this,s,VERTICAL,LENGTH);
        return new Ellipse2D.Float(cx-rx,cy-ry,rx*2,ry*2);
    }
    @Override
    protected void setObjectAttributes(Attributes attr) {
        for (int i=0;i<ATTR_SET.length;i++){
            String s=attr.getValue(ATTR_SET[i]);
            if (s !=null)
                objectAttr.put(ATTR_SET[i], s);
        }
    }
    
    @Override
    public SVGEllipse getWritableInstance(SVGObject newParent) {
        SVGEllipse result=new SVGEllipse();
        result.setAttributes(getAttributes().clone());
        result.objectAttr=(HashMap<String,String>)objectAttr.clone();
        result.viewAttribute=(HashMap<String,String>)viewAttribute.clone();
        result.parent=newParent;
        return result;
    }
    
    @Override
    public String getName() {
        return "Ellipse";
    }
    
    @Override
    public String getXML() {
        return "";
    }
    
}
