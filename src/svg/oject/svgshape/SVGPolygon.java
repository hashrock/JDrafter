/*
 * SVGPolygon.java
 *
 * Created on 2008/09/09, 16:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package svg.oject.svgshape;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.util.HashMap;
import org.xml.sax.Attributes;
import svg.oject.*;

/**
 *
 * @author i002060
 */
public class SVGPolygon extends SVGPolyline{
    
    /** Creates a new instance of SVGPolygon */
    protected SVGPolygon() {
    }
    public SVGPolygon(SVGObject parent,Attributes attr){
        super(parent,attr);
    }
    @Override
    public Shape createShape(){
        GeneralPath gp=(GeneralPath)super.createShape();
        gp.closePath();
        return gp;
    }
    @Override
    public SVGPolygon getWritableInstance(SVGObject newParent) {
        SVGPolygon result=new SVGPolygon();
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
