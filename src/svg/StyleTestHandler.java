/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package svg;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JTextArea;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import svg.SVGElementFactory;
import svg.attribute.SVGGradient;
import svg.attribute.SVGStop;
import svg.oject.SVGAbstractGroup;
import svg.oject.SVGDocument;
import svg.oject.SVGObject;

/**
 *
 * @author takashi
 */
public class StyleTestHandler extends DefaultHandler {
    JTextArea jt=null;
    /**
     * 要素の開始タグ読み込み時
     */
    public StyleTestHandler(){
    }
    public StyleTestHandler(JTextArea jt){
        this.jt=jt;
    }
    public void startDocument(){
        jt.setText("Start parce\n");  
    }
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
         jt.setText(jt.getText()+"Element name="+qName+"\n");
         for (int i=0;i<attributes.getLength();i++){
             jt.setText(jt.getText()+"\t"+attributes.getQName(i)+"="+attributes.getValue(i)+"\n");
         }
    }
    
    /**
     * テキストデータ読み込み時
     */
    @Override
    public void characters(char[] ch, int offset, int length) {
        String attr=new String(ch,offset,length);
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
            String value=s.substring(st+1,en);
            jt.setText(jt.getText()+key+"="+value+"\n");
        }
        
    }
    
    /**
     * 要素の終了タグ読み込み時
     */
    @Override
    public void endElement(String uri, String localName, String qName) {

    }
    
    /**
     * ドキュメント終了時
     */
    @Override
    public void endDocument() {
    }
}


