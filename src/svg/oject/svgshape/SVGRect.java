/*
 * SVGRect.java
 *
 * Created on 2008/09/09, 15:06
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package svg.oject.svgshape;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import org.xml.sax.Attributes;
import svg.oject.*;

/**
 *
 * @author i002060
 */
public class SVGRect extends SVGShape{
    public static final String X="x";
    public static final String Y="y";
    public static final String WIDTH="width";
    public static final String HEIGHT="height";
    public static final String RX="rx";
    public static final String RY="ry";
    public static final String[] ATTR_SET=new String[]{X,Y,WIDTH,HEIGHT,RX,RY};
    /** Creates a new instance of SVGRect */
    protected SVGRect() {
    }
    public SVGRect(SVGObject parent,Attributes attr){
        super(parent);
        setAttributes(attr);
        setObjectAttributes(attr);
    }
    @Override
    public Shape createShape() {
        String s=objectAttr.get(X);
        if (s==null)
            s="0";
        float x=toPixel(this,s,HORIZONTAL,COORDS);
        s=objectAttr.get(Y);
        if (s==null)
            s="0";
        float y=toPixel(this,s,VERTICAL,COORDS);
        s=objectAttr.get(WIDTH);
        float width=toPixel(this,s,HORIZONTAL,LENGTH);
        s=objectAttr.get(HEIGHT);
        float height=toPixel(this,s,VERTICAL,LENGTH);
        //
        String srx=objectAttr.get(RX);
        String sry=objectAttr.get(RY);
        if (srx==null && sry==null){
            return new Rectangle2D.Float(x,y,width,height);
        }
        if (srx ==null)
            srx=sry;
        else if (sry==null)
            sry=srx;
        float rx=toPixel(this,srx,HORIZONTAL,LENGTH);
        float ry=toPixel(this,sry,VERTICAL,LENGTH);
        return new RoundRectangle2D.Float(x,y,width,height,rx,ry);
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
    public SVGRect getWritableInstance(SVGObject newParent) {
        SVGRect result=new SVGRect();
        result.setAttributes(getAttributes().clone());
        result.objectAttr=(HashMap<String,String>)objectAttr.clone();
        result.viewAttribute=(HashMap<String,String>)viewAttribute.clone();
        result.parent=newParent;
        return result;
    }

    @Override
    public String getName() {
        return "rect";
    }

    @Override
    public String getXML() {
        return "";
    }
    
}
