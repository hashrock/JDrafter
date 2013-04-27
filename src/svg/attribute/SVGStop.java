/*
 * SVGStop.java
 *
 * Created on 2008/09/12, 9:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package svg.attribute;

import java.awt.Color;
import java.util.HashMap;
import org.xml.sax.Attributes;
import svg.SVGElement;

/**
 *
 * @author i002060
 */
public class SVGStop implements SVGElement{
    public static final String OFFSET="offset";
    public static final String STOP_COLOR="stop-color";
    public static final String STOP_OPACITY="stop-opacity";
    //
    private HashMap<String,String> stopAttr=new HashMap<String,String>();
    /** Creates a new instance of SVGStop */
    public SVGStop() {
    }
    public SVGStop(Attributes attr){
        setAttributes(attr);
    }
    private void setAttributes(Attributes attr){
        stopAttr.put(OFFSET,attr.getValue(OFFSET));
        stopAttr.put(STOP_COLOR,attr.getValue(STOP_COLOR));
        stopAttr.put(STOP_OPACITY,attr.getValue(STOP_OPACITY));
    }
    public float getOffset(){
        String s=stopAttr.get(OFFSET);
        String fm="((\\-)?((\\d+\\.\\d+)|(\\.\\d+)))";
        
        if (s.contains("%")){
            s=s.replace("%","");
            return Float.valueOf(s)/100f;
        }
        return Float.valueOf(s);
    }
    public Color getColor(){
       String sc=stopAttr.get(STOP_COLOR);
       String so=stopAttr.get(STOP_OPACITY);
       return SVGAttributes.getColorFromAttribute(sc,so);   
    }
    public String getXML() {
        return "";
    }
    
}
