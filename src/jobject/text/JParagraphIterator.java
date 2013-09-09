/*
 * JParagraphIterator.java
 *
 * Created on 2007/10/22, 14:24
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jobject.text;

import java.text.AttributedCharacterIterator;
import java.util.Vector;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.StyledDocument;

/**
 *
 * @author i002060
 */
public class JParagraphIterator {
    StyledDocument document;
    int currentIndex;
    int startPosition;
    int endPosition;
    Vector<Integer> paragraphPosition;
    String text;
    /** Creates a new instance of JParagraphIterator */
    public JParagraphIterator(StyledDocument doc) {
        document=doc;
        try{
            text=doc.getText(0,doc.getLength());
        }catch(BadLocationException e){}
        paragraphPosition=new Vector<Integer>(1);
        //text+="\n";
        startPosition=0;
        endPosition=doc.getLength()-1;
        int i=startPosition;
        while(i<=endPosition){
            if (text.charAt(i)=='\n')
                paragraphPosition.add(i);
            i++;
        }      
        currentIndex=0;
    }
    //
    /**指定するテキストの範囲意を包含する段落を含むJParagraphIteratorを構築します.
     *@param doc 指定するドキュメント
     *@param st 指定するテキストの開始位置
     *@param en 指定するテキストの終了位置
     */
    public JParagraphIterator(StyledDocument doc,int st,int en){
        document=doc;
        try{
            text=doc.getText(0,doc.getLength());
        }catch(BadLocationException e){};
        while(true){
            if (--st<0){
                st=0;
                break;
            }
            if (text.charAt(st)=='\n'){
                st++;
                break;
            }
        }
        while(true){
            if (++en>text.length()-1){
                en=text.length()-1;
                break;
            }
            if (text.charAt(en)=='\n')
                break;
        }  
        paragraphPosition=new Vector<Integer>(1);
        //text+="\n";
        startPosition=st;
        endPosition=en;
        int i=startPosition;
        while(i<=endPosition){
            if (text.charAt(i)=='\n')
                paragraphPosition.add(i);
            i++;
        }
        if (paragraphPosition.isEmpty())
            paragraphPosition.add(en);
        currentIndex=0;    
    }
    public int paragraphSize(){
        return paragraphPosition.size();
    }
    public AttributedCharacterIterator current(){
        int sPos,ePos;
        if (currentIndex==0){
            sPos=startPosition;
        }else{
            sPos=paragraphPosition.get(currentIndex-1)+1;
        }
        ePos=paragraphPosition.get(currentIndex);
        return new JDocumentCharacterIterator(this,sPos,ePos+1);
    }
    public AttributedCharacterIterator first(){
        return setPosition(0);
    }
    public AttributedCharacterIterator last(){
        return setPosition(paragraphPosition.size()-1);
        
    }
    public AttributedCharacterIterator setPosition(int pos){
        if (pos<0 || pos>=paragraphPosition.size()) return null;
        currentIndex=pos;
        int spos,epos;
        
        if (currentIndex==0){
            spos=startPosition;
        }else{
            spos=paragraphPosition.get(currentIndex-1)+1;
        }
        epos=paragraphPosition.get(currentIndex);
        return new JDocumentCharacterIterator(this,spos,epos+1);
    }
    public AttributedCharacterIterator next(){
        return setPosition(currentIndex+1);
    }
    public AttributedCharacterIterator previous(){
        return setPosition(currentIndex-1);
        
    }
    public AttributeSet getParagraphAttributeSet(){
        Element elm;
        if (currentIndex==0)
            elm=document.getCharacterElement(startPosition);
        else
            elm=document.getCharacterElement(paragraphPosition.get(currentIndex-1)+1);
        return elm.getAttributes();
    }
    public StyledDocument getDocument(){
        return document;
    }
    public String getText(){
        return text;
    }
    
}
