/*
 * SVGGroup.java
 *
 * Created on 2008/09/11, 13:25
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package svg.oject;

import java.util.HashMap;
import org.xml.sax.Attributes;

/**
 *
 * @author i002060
 */
public class SVGGroup extends SVGAbstractGroup{
    
    /** Creates a new instance of SVGGroup */
    protected SVGGroup() {
    }
    protected SVGGroup(SVGObject parent){
       super(parent);
    }
    public SVGGroup(SVGObject parent,Attributes attr){
        super(parent);
        setAttributes(attr);
    }
    @Override
    protected void setObjectAttributes(Attributes attr) {
        //do nothing;
    }
    
    @Override
    public SVGGroup getWritableInstance(SVGObject newParent) {
        SVGGroup result=new SVGGroup();
        result.setAttributes(getAttributes().clone());
        result.viewAttribute=(HashMap<String,String>)viewAttribute.clone();
        result.parent=newParent;
        //result.id=id;
        for (SVGObject o:this){
            SVGObject no=o.getWritableInstance(result);
            result.add(no);
        }
        return result;
    }
    
    @Override
    public String getName() {
        return "g";
    }
    
    @Override
    public String getXML() {
        return "";
    }
    
}
