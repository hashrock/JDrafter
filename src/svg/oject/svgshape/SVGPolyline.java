/*
 * SVGPolyline.java
 *
 * Created on 2008/09/09, 16:22
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package svg.oject.svgshape;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xml.sax.Attributes;
import svg.oject.*;

/**
 *
 * @author i002060
 */
public class SVGPolyline extends SVGShape{
    public static final String POINTS="points";
    /** Creates a new instance of SVGPolyline */
    protected SVGPolyline() {
    }
    public SVGPolyline(SVGObject parent,Attributes attr){
        super(parent);
        setAttributes(attr);
        setObjectAttributes(attr);
    }
    @Override
    public Shape createShape() {
        String s=objectAttr.get(POINTS).trim();
        String f="\\s*((\\-)?((\\d+(\\.\\d+)?)|(\\.\\d+)))";
        String units="(\\s*((pt)|(px)|(cm)|(mm)|(pc)|(em)|(ex)|(%)))?";
        String pp=f+units+"((\\s*\\,\\s*)|(\\s+))"+f+units;
        Pattern pattern=Pattern.compile(pp);
        Matcher match=pattern.matcher(s);
        int i=0;
        GeneralPath gp=new GeneralPath();
        while (match.find()){
            String[] xy=match.group().trim().split("((\\s*\\,\\s*)|(\\s+))");
            float x=toPixel(this,xy[0],HORIZONTAL,COORDS);
            float y=toPixel(this,xy[1],VERTICAL,COORDS);
            if (i==0)
                gp.moveTo(x,y);
            else
                gp.lineTo(x,y);
            i++;
        }
        return gp;
    }
    @Override
    protected void setObjectAttributes(Attributes attr) {
        String s=attr.getValue(POINTS);
        if (s !=null)
            objectAttr.put(POINTS,s);
    }
    
    @Override
    public SVGPolyline getWritableInstance(SVGObject newParent) {
        SVGPolyline result=new SVGPolyline();
        result.setAttributes(getAttributes().clone());
        result.objectAttr=(HashMap<String,String>)objectAttr.clone();
        result.viewAttribute=(HashMap<String,String>)viewAttribute.clone();
        result.parent=newParent;
        return result;
    }
    
    @Override
    public String getName() {
        return "polyline";
    }    
    @Override
    public String getXML() {
        return "";
    }
    
}
