/*
 * TextRow.java
 *
 * Created on 2008/05/27, 14:46
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jobject.text;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ConcurrentSkipListMap;
import javax.swing.text.StyleConstants;

/**
 *àÍçsÇÃTextLayoutÇï\ÇµÇ‹Ç∑.
 * @author i002060
 */
public class TextRow {
   // private int rowIndex=-1;
    private float offsetY=0;
    int alignment=StyleConstants.ALIGN_LEFT;
    ConcurrentSkipListMap<Integer,LocatedTextLayout> sMap=null;
    private TextLocater locater=null;
    private float rightIndent=0;
    /** Creates a new instance of TextRow */
    public TextRow(float ofsY,int align) {
        offsetY=ofsY;
        alignment=align;
        sMap=new ConcurrentSkipListMap<Integer,LocatedTextLayout>();
        
    }
    public void setLocater(TextLocater locater){
        this.locater=locater;
    }
    public TextLocater getLocater(){
        return locater;
    }
    public void add(LocatedTextLayout loc){
        sMap.put(loc.getTextPosition(),loc);
        loc.setTextRow(this);
    }
    public int getStartPosition(){
        return sMap.firstKey();
    }
    public void setStartPosition(int p){
        Vector<LocatedTextLayout> vec=new Vector<LocatedTextLayout>(sMap.values());
        sMap.clear();
        for(LocatedTextLayout lc:vec){
            lc.setTextPosition(p);
            sMap.put(p,lc);
            p+=lc.getTextLayout().getCharacterCount();
        }
    }
    public int getCharacterSize(){
        int ret=0;
       for(LocatedTextLayout lc:sMap.values()){
           ret+=lc.getTextLayout().getCharacterCount();
       }
       return ret;
    }
    public int getEndPosition(){
        return sMap.lastKey()+sMap.lastEntry().getValue().getTextLayout().getCharacterCount()-1;
    }
    public  float getOffsetY(){
        return offsetY;
    }
    public void setOffsetY(float y){
        offsetY=y;
    }
    //User only for DefaultLocater
    public void setRightIndent(float ri){
        rightIndent=ri;
    }
    public float getRightIndent(){
        return rightIndent;
    }
    public float getWidth(){
       LocatedTextLayout last=sMap.lastEntry().getValue();
       return last.getOffsetX()+last.getTextLayout().getAdvance()+rightIndent;
    }
    public float getHeight(){
        return getDescent()+getAcent();
    }
    public float getDescent(){
        float  ret=0;
        for (LocatedTextLayout tl:sMap.values()){
            if (tl.getTextLayout().getDescent()>ret)
                ret=tl.getTextLayout().getDescent();
        }
        return ret;
    }
    public float getAcent(){
        float  ret=0;
        for (LocatedTextLayout tl:sMap.values()){
            if (tl.getTextLayout().getAscent()>ret)
                ret=tl.getTextLayout().getAscent();
        }
        return ret;
    }
    public int getAlignment(){
        return alignment;
    }
    public int size(){
        return sMap.size();
    }
    public LocatedTextLayout first(){
        return sMap.firstEntry().getValue();
    }
    public int firstIndex(){
        return first().getTextPosition();
    }
    public LocatedTextLayout last(){
        return sMap.lastEntry().getValue();
    }
    public int lastIndex(){
        return last().getTextPosition()+last().getTextLayout().getCharacterCount()-1;
    }
    public LocatedTextLayout getForPosition(int pos){
        return sMap.floorEntry(pos).getValue();
    }
    public Iterator<LocatedTextLayout> iterator(){
        return sMap.values().iterator();
    }
    public void draw(Graphics2D g){
        Iterator<LocatedTextLayout> it=sMap.values().iterator();
        while(it.hasNext()){
            it.next().draw(g);
        }
    }
    public void drawBaseLine(Graphics2D g){
        LocatedTextLayout bg=sMap.firstEntry().getValue();
        LocatedTextLayout eg=sMap.lastEntry().getValue();
        Point2D.Float sp=bg.getLocation();
        float ex=eg.getOffsetX()+eg.getTextLayout().getVisibleAdvance();
        Line2D.Float l=new Line2D.Float(sp.x,sp.y,ex,sp.y);
        g.draw(l);
    }
    
}
