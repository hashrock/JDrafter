/*
 * SVGTref.java
 *
 * Created on 2008/09/25, 12:46
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package svg.svgtext;

import org.xml.sax.Attributes;
import svg.oject.SVGObject;

/**
 *
 * @author i002060
 */
public class SVGTref extends SVGTspan{
    public static final String XLINK="xlink:href";
    /** Creates a new instance of SVGTref */
    protected SVGTref() {
    }
    public SVGTref(SVGObject parent){
        super(parent);
    }
    public SVGTref(SVGObject parent,Attributes attr){
        super(parent,attr);
        String s=attr.getValue(XLINK);
        if (s !=null){
            s=s.replace("#","");
            SVGText stx=(SVGText)getRootDocument().getLink(s);
            for (int i=0;i<stx.getChildren().size();i++){
                Object o=stx.getChildren().get(i);
                if (o instanceof String){
                    addChild(o);
                }
            }
        }
    }

}
