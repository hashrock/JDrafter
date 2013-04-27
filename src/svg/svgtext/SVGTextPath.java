/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package svg.svgtext;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import org.xml.sax.Attributes;
import svg.oject.SVGObject;
import svg.oject.svgshape.SVGShape;

/**
 *
 * @author takashi
 */
public class SVGTextPath extends SVGTspan{
    public static final String XLINK="xlink:href";
    public static final String SPACING="spacing";
    public static final String METHOD="method";
    public static final String START_OFFSET="startOffset";
    
    public static final String ALIGN="align";
    public static final String STRETCH="stretch";
    public static final String AUTO="auto";
    public static final String EXACT="exact";
    
    private static final String[] KEY_LIST=new String[]{XLINK,SPACING,METHOD,START_OFFSET};
    public SVGTextPath(){}
    public SVGTextPath(SVGObject owner){
        super(owner);
    }
    public SVGTextPath(SVGObject owner,Attributes attr){
        super(owner,attr);
        xList=new String[]{String.valueOf(getOffset())};
    }
    @Override
    protected void setObjectAttributes(Attributes attr){
        for (int i=0;i<KEY_LIST.length;i++){
            String s=attr.getValue(KEY_LIST[i]);
            if (s!=null){
                objectAttributes.put(KEY_LIST[i], s);
            }
        }
    }
    public  String getMethod(){
        String s=objectAttributes.get(METHOD);
        if (s==null || ALIGN.equals(s)){
            return ALIGN;
        }
        return STRETCH;
    }
    public String getSpacing(){
        String s=objectAttributes.get(SPACING);
        if (s==null || EXACT.equals(s)){
            return EXACT;
        }
        return AUTO;
    }
    public Shape getLinePath(){
        String s=objectAttributes.get(XLINK);
        if (s !=null){
            s=s.replace("#", "").trim();
            SVGShape ss=(SVGShape)getRootDocument().getLink(s);
            Shape result=ss.createShape();
            AffineTransform tx=ss.getAttributes().getTransform(ss);
            if (tx !=null){
                result=tx.createTransformedShape(result);
            }
            return result;
        }
        return null;
    }
    public float getOffset(){
        String s=objectAttributes.get(START_OFFSET);
        if (s==null)
            return 0f;
        if (!s.contains("%")){
            return toPixel(this,s,HORIZONTAL,LENGTH);
        }
        s=s.replace("%", "");
        return getLocater().getLength()*Float.valueOf(s)/100f;
    }
   
    @Override
    public SVGTextLocater getLocater(){
        return new SVGTextLocater(getLinePath());
    }
    
}
