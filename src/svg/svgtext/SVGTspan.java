/*
 * SVGTspan.java
 *
 * Created on 2008/09/25, 10:53
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package svg.svgtext;

import java.util.HashMap;
import java.util.Vector;
import org.xml.sax.Attributes;
import svg.attribute.SVGAttributes;
import svg.oject.SVGObject;

/**
 *
 * @author i002060
 */
public class SVGTspan extends SVGText{
    
    /** Creates a new instance of SVGTspan */
    public SVGTspan() {
    }
    public SVGTspan(SVGObject parent){
        super(parent);
    }
    public SVGTspan(SVGObject parent,Attributes attr){
        super(parent);
        objectAttributes=new HashMap<String,String>();
        children=new Vector<Object>();
        setAttributes(attr);
        setObjectAttributes(attr);
    }
    @Override
    public void top(){
        float cx=CURRENT_X,cy=CURRENT_Y;
        super.top();
        CURRENT_X=cx;
        CURRENT_Y=cy;
    }
    @Override
    public SVGText getAnscester(SVGObject o){
        SVGObject parent=o.getParent();
        if (parent==null)
            return null;
        if (o instanceof SVGText){
            return (SVGText)parent;
        }
        return getAnscester(o);
    }
    @Override
    public SVGTextLocater getLocater(){
        SVGText o=getAnscester(this);
        if (o !=null){
            return o.getLocater();
        }
        return null;
    }
    @Override
    public String getName(){
        return "tspan";
    }
}
