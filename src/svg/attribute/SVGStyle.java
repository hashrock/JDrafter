/*
 * SVGStyle.java
 *
 * Created on 2008/09/19, 9:56
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package svg.attribute;

import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import svg.SVGElement;
import svg.oject.SVGDocument;
import svg.oject.SVGObject;

/**
 *
 * @author i002060
 */
public class SVGStyle {
    private HashMap<String,String> styleMap=null;
    private SVGDocument document=null;
    /** Creates a new instance of SVGStyle */
    private SVGStyle() {
        styleMap=new HashMap<String,String>();
    }
    public SVGStyle(SVGDocument doc){
        document=doc;
        styleMap=new HashMap<String,String>();
    }
    public void AddStyleAttributes(String attr){
        attr=attr.replaceAll("[\\t\\n]"," ").trim();
        attr=attr.replaceAll("\\/\\*([^\\*\\/]*)\\*\\/","").trim();
        String regex="([#\\.]?)([\\D][[^\\{]]+)\\s*\\{([^\\}])+\\}";
        Pattern ptn=Pattern.compile(regex);
        Matcher match=ptn.matcher(attr);
        while (match.find()){
            String s=match.group();
            int st=s.indexOf('{');
            int en=s.indexOf('}');
            String key=s.substring(0,st).trim();
            String value=s.substring(st+1,en).trim();
            styleMap.put(key,value);
        }
        Iterator<SVGElement> itr=document.linkIterator();
        while (itr.hasNext()){
            SVGElement elm=itr.next();
            if (elm instanceof SVGObject){
                SVGObject obj=(SVGObject)elm;
                obj.getAttributes().addStyleAttributes(this,obj);
            }
        }
    }
    public HashMap<String,String> getMap(String key){
        HashMap<String,String> result=new HashMap<String,String>();
        String s=styleMap.get(key);
        if (s != null) {
            s=s.replaceAll("((/\\*)([^*/])+\\*/)", "").trim();
            s=s.replaceAll("[\\t\\n]","").trim();
            String[] sp1 = s.split("\\s*;\\s*");
            for (int i = 0; i < sp1.length; i++) {
                String[] sp2 = sp1[i].trim().split(":");
                result.put(sp2[0].trim(), sp2[1].trim());
            }
        }
        
        return result;
    }   
}
