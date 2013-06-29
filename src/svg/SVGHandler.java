/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package svg;

import java.util.Stack;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import svg.SVGElementFactory;
import svg.attribute.SVGGradient;
import svg.attribute.SVGStop;
import svg.attribute.SVGStyle;
import svg.attribute.SVGStyleElement;
import svg.oject.SVGAbstractGroup;
import svg.oject.SVGDocument;
import svg.oject.SVGObject;
import svg.svgtext.SVGText;

/**
 *
 * @author takashi
 */
public class SVGHandler extends DefaultHandler {
    
    /**
     * 要素の開始タグ読み込み時
     */
    private Stack<SVGElement> stack=new Stack<SVGElement>();
    private SVGObject currentParent=null;
    private SVGElement currentElm=null;
    private SVGDocument doc=null;
    public SVGHandler(){
    }
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        SVGElement elm=SVGElementFactory.createElement(currentParent,qName,attributes);
        if (elm instanceof SVGObject){
            if (currentParent instanceof SVGAbstractGroup){
                SVGAbstractGroup sGroup=(SVGAbstractGroup)currentParent;
                sGroup.add((SVGObject) elm);
            }
            if (currentParent instanceof SVGText){
                ((SVGText)currentParent).addChild(elm);
            }
            if (elm instanceof SVGDocument && doc==null){
                doc=(SVGDocument)elm;
            }
            currentParent=(SVGObject)elm;
        }else {
            if (elm instanceof SVGStop && currentElm instanceof SVGGradient){
                SVGStop stop=(SVGStop)elm;
                ((SVGGradient)currentElm).addStop(stop);
            }
        }
        stack.push(elm);
        currentElm=elm;
    }
    
    /**
     * テキストデータ読み込み時
     */
    @Override
    public void characters(char[] ch, int offset, int length) {
        String s=new String(ch,offset,length);
        if (currentElm instanceof SVGStyleElement){
           doc.getStyle().AddStyleAttributes(s);
        }
        if (currentElm instanceof SVGText){
            ((SVGText)currentElm).addChild(s.trim());
        }
    }
    
    
    /**
     * 要素の終了タグ読み込み時
     */
    @Override
    public void endElement(String uri, String localName, String qName) {
        stack.pop();
        if (stack.empty()){
            currentElm=null;
            currentParent=null;
        }else{
            currentElm=stack.peek();
            if (currentElm instanceof SVGObject){
                currentParent=(SVGObject)currentElm;
            }
        }
    }
    
    /**
     * ドキュメント終了時
     */
    @Override
    public void endDocument() {
    }
    public SVGDocument getDocument(){
        return doc;
    }
}


