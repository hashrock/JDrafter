/*
 * SVGLine.java
 *
 * Created on 2008/09/09, 16:05
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package svg.oject.svgshape;

import java.awt.Shape;
import java.awt.geom.Line2D;
import java.util.HashMap;
import org.xml.sax.Attributes;
import svg.oject.*;

/**
 *
 * @author i002060
 */
public class SVGLine extends SVGShape{
    public static final String X1="x1";
    public static final String Y1="y1";
    public static final String X2="x2";
    public static final String Y2="y2";
    public static final String[] ATTR_SET=new String[]{X1,Y1,X2,Y2};
    /** Creates a new instance of SVGLine */
    protected SVGLine() {
    }
    public SVGLine(SVGObject parent,Attributes attr){
        super(parent);
        setAttributes(attr);
        setObjectAttributes(attr);
    }
    @Override
    public Shape createShape() {
        String s=objectAttr.get(X1);
        if (s==null){
            s="0";
        }
        float x1=toPixel(this,s,HORIZONTAL,COORDS);
        s=objectAttr.get(Y1);
        if (s==null)
            s="0";
        float y1=toPixel(this,s,VERTICAL,COORDS);
        s=objectAttr.get(X2);
        if (s==null)
            s="0";
        float x2=toPixel(this,s,HORIZONTAL,COORDS);
        s=objectAttr.get(Y2);
        if (s==null)
            s="0";
        float y2=toPixel(this,s,VERTICAL,COORDS);
        return new Line2D.Float(x1,y1,x2,y2);
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
    public SVGLine getWritableInstance(SVGObject newParent) {
        SVGLine result=new SVGLine();
        result.setAttributes(getAttributes().clone());
        result.objectAttr=(HashMap<String,String>)objectAttr.clone();
        result.viewAttribute=(HashMap<String,String>)viewAttribute.clone();
        result.parent=newParent;
        return result;
    }
    
    @Override
    public String getName() {
        return "line";
    }
    
    @Override
    public String getXML() {
        return "";
    }
    
}
