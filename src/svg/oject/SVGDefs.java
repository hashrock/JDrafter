/*
 * SVGDefs.java
 *
 * Created on 2008/09/11, 15:17
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package svg.oject;

import java.awt.Graphics2D;
import org.xml.sax.Attributes;
import svg.attribute.SVGAttributes;

/**
 *
 * @author i002060
 */
public class SVGDefs extends SVGAbstractGroup{
    
    /** Creates a new instance of SVGDefs */
    private SVGDefs() {
    }
    public SVGDefs(SVGObject owner,Attributes attr){
        super(owner);
        setAttributes(attr);
    }
    @Override
    protected void setObjectAttributes(Attributes attr) {
        //do nothing;
    }
    @Override
    public void paint(Graphics2D g){
        //do nothing;
    }
    @Override
    public SVGObject getWritableInstance(SVGObject newParent) {
        return null;
    }
    
    @Override
    public String getName() {
        return "defs";
    }
    
    @Override
    public String getXML() {
        return "";
    }
    
}
