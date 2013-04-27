/*
 * SVGSymbol.java
 *
 * Created on 2008/09/11, 16:26
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package svg.oject;

import java.awt.Graphics2D;
import java.util.HashMap;
import org.xml.sax.Attributes;

/**
 *
 * @author i002060
 */
public class SVGSymbol extends SVGAbstractGroup {

    /** Creates a new instance of SVGSymbol */
    private SVGSymbol() {
    }

    public SVGSymbol(SVGObject parent, Attributes attr) {
        super(parent);
        setAttributes(attr);
        setObjectAttributes(attr);
    }

    @Override
    protected void setObjectAttributes(Attributes attr) {
        setViewBox(attr);
    }

    @Override
    public void paint(Graphics2D g) {
        //do nothing;
    }

    @Override
    public SVGGroup getWritableInstance(SVGObject newParent) {
        SVGGroup result = new SVGGroup(newParent);
        result.setAttributes(getAttributes().clone());
        result.viewAttribute = (HashMap<String,String>)viewAttribute.clone();
        result.parent = newParent;
        for (SVGObject o : this) {
            SVGObject no = o.getWritableInstance(result);
            result.add(no);
        }
        return result;
    }

    @Override
    public String getName() {
        return "symbol";
    }

    @Override
    public String getXML() {
        return "";
    }
}
