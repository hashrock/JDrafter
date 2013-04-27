/*
 * SVGElementFactory.java
 *
 * Created on 2008/09/12, 10:21
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package svg;

import org.xml.sax.Attributes;
import svg.attribute.SVGGradient;
import svg.attribute.SVGStop;
import svg.attribute.SVGStyleElement;
import svg.oject.SVGDefs;
import svg.oject.SVGDocument;
import svg.oject.SVGGroup;
import svg.oject.SVGMarker;
import svg.oject.SVGObject;
import svg.oject.SVGSymbol;
import svg.oject.SVGUse;
import svg.oject.svgshape.SVGCircle;
import svg.oject.svgshape.SVGEllipse;
import svg.oject.svgshape.SVGLine;
import svg.oject.svgshape.SVGPath;
import svg.oject.svgshape.SVGPolygon;
import svg.oject.svgshape.SVGPolyline;
import svg.oject.svgshape.SVGRect;
import svg.svgtext.SVGText;
import svg.svgtext.SVGTextPath;
import svg.svgtext.SVGTref;
import svg.svgtext.SVGTspan;

/**
 *
 * @author i002060
 */
public class SVGElementFactory {
    public static final String SVG_GROUP="g";
    public static final String SVG_DEFS="defs";
    public static final String SVG_SYMBOL="symbol";
    public static final String SVG_USE="use";
    public static final String SVG_DOCUMENT="svg";
    public static final String SVG_LINEAR_GRADIENT="linearGradient";
    public static final String SVG_RADIAL_GRADIENT="radialGradient";
    public static final String SVG_STOP="stop";
    public static final String SVG_STYLE="style";
    //
    public static final String SVG_TEXT="text";
    public static final String SVG_TSPAN="tspan";
    public static final String SVG_TREF="tref";
    public static final String SVG_TEXTPATH="textPath";
    //
    public static final String SVG_CIRCLE="circle";
    public static final String SVG_ELLIPSE="ellipse";
    public static final String SVG_LINE="line";
    public static final String SVG_PATH="path";
    public static final String SVG_POLYGON="polygon";
    public static final String SVG_POLYLINE="polyline";
    public static final String SVG_RECT="rect";
    
    public static final String SVG_MARKER="marker";
    /** Creates a new instance of SVGElementFactory */
    private SVGElementFactory() {
    }
    public static SVGElement createElement(SVGObject owner,String qname,Attributes attr){
        SVGElement result=null;
        if (qname.equals(SVG_GROUP)){
            result= new SVGGroup(owner,attr);
        }else if (qname.equals(SVG_DEFS)){
            result= new SVGDefs(owner,attr);
        }else if(qname.equals(SVG_SYMBOL)){
            result=new SVGSymbol(owner,attr);
        }else if (qname.equals(SVG_USE)){
            result=new SVGUse(owner,attr);
        }else if (qname.equals(SVG_DOCUMENT)){
            result=new SVGDocument(owner,attr);
        }else if (qname.equals(SVG_LINEAR_GRADIENT)){
            result=new SVGGradient(owner.getRootDocument(),SVGGradient.LINEAR,attr);
        }else if (qname.equals(SVG_RADIAL_GRADIENT)){
            result=new SVGGradient(owner.getRootDocument(),SVGGradient.RADIAL,attr);
        }else if (qname.equals(SVG_STOP)){
            result=new SVGStop(attr);
        }else if (qname.equals(SVG_CIRCLE)){
            result=new SVGCircle(owner,attr);
        }else if (qname.equals(SVG_ELLIPSE)){
            result=new SVGEllipse(owner,attr);
        }else if (qname.equals(SVG_LINE)){
            result=new SVGLine(owner,attr);
        }else if (qname.equals(SVG_PATH)){
            result=new SVGPath(owner,attr);
        }else if (qname.equals(SVG_POLYGON)){
            result=new SVGPolygon(owner,attr);
        }else if (qname.equals(SVG_POLYLINE)){
            result=new SVGPolyline(owner,attr);
        }else if (qname.equals(SVG_RECT)){
            result=new SVGRect(owner,attr);
        }else if (qname.equals(SVG_STYLE)){
            result=new SVGStyleElement();
        } else if (qname.equals(SVG_TEXT)){
            result=new SVGText(owner,attr);
        }else if (qname.equals(SVG_TSPAN)){
            result=new SVGTspan(owner,attr);
        }else if (qname.equals(SVG_TREF)){
            result=new SVGTref(owner,attr);
        }else if (qname.equals(SVG_TEXTPATH)){
            result=new SVGTextPath(owner,attr);
        }else if (qname.equals(SVG_MARKER)){
            result=new SVGMarker(owner,attr);
        }else{
            result=new UnKnownElement();
        }
        return result;
    }
}
