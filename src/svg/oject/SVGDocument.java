/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package svg.oject;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Iterator;
import org.xml.sax.Attributes;
import svg.SVGElement;
import svg.attribute.SVGAttributes;
import svg.attribute.SVGStyle;

/**
 *
 * @author takashi
 */
public class SVGDocument extends SVGAbstractGroup{
    private HashMap<String,SVGElement> linkMap;
    private SVGStyle svgStyle;
    protected SVGDocument(){
        linkMap=new HashMap<String,SVGElement>();
        svgStyle=new SVGStyle(this);
        
    }
    public SVGDocument(SVGObject parent,Attributes attr){
        super(parent);      
        linkMap=new HashMap<String,SVGElement>();
        svgStyle=new SVGStyle(this);
        setAttributes(attr);
        setObjectAttributes(attr);
    }
    @Override
    public void setObjectAttributes(Attributes attr){
        setViewBox(attr);
        setViewport(attr);
    }
    @Override
    public Rectangle2D getBounds() {
        return getCurrentViewport();
    }
    @Override
    public SVGDocument getRootDocument(){
        if (parent==null)
            return this;
        return parent.getRootDocument();
    }
    public void addLink(String id,SVGElement elm){
        linkMap.put(id, elm);
    }
    public SVGElement getLink(String id){
        return linkMap.get(id);
    }
    @Override
    public String getXML() {
        return "";
    }
    @Override
    public String getName(){
        return "svg";
    }
    public SVGStyle getStyle(){
        return svgStyle;
    }
    @Override
    public SVGDocument getWritableInstance(SVGObject newParent) {
        SVGDocument result=new SVGDocument();
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
    public Iterator<SVGElement> linkIterator(){
        return linkMap.values().iterator();
    }
}
