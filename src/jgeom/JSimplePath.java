/*
 * JSimplePath.java
 *
 * Created on 2007/08/25, 10:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jgeom;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

/**
 *  単一パスオブジェクトを表します.
 * @author TI
 */
public class JSimplePath implements Serializable,Cloneable{
    protected Vector<JSegment> segments;
    protected boolean looped;
    /**
     * Creates a new instance of JSimplePath
     */
    public JSimplePath() {
        segments=new Vector<JSegment>();
        looped=false;
    }
    public boolean isLooped(){
        return looped;
    }
    public void setLooped(boolean lp){
        looped=lp;
    }
    public int size(){
        return segments.size();
    }
    public boolean contains(JSegment seg){
        return segments.contains(seg);
    }
    public int indexOf(JSegment seg){
        return segments.indexOf(seg);
    }
    public JSegment get(int index){
        return segments.get(index);
    }
    public Shape getShape(int windingRule){
        JPathIterator jpi=new JPathIterator(this,windingRule);
        Path2D.Float gp=new Path2D.Float(windingRule);
        gp.append(jpi,false);
        return gp;       
    }
    public void add(int index,JSegment seg){
       segments.add(index,seg);
    }
    public boolean add(JSegment seg){
        return segments.add(seg);
    }
    public boolean remove(JSegment seg){
        boolean ret= segments.remove(seg);
        if (size()<3) looped=false;
        return ret;
    }
    public JSegment remove(int index){
        JSegment ret= segments.remove(index);
        if (size()<3) looped=false;
        return ret;
    }
    public void clear(){
        segments.clear();
    }
    public JSegment nextSegment(JSegment seg){
        int idx=indexOf(seg);
        if (idx==-1) return null;
        if (idx+1<size())
            return get(idx+1);
        if (get(0) != seg && isLooped())
            return get(0);
        return null;
    }
    public JSegment prevSegment(JSegment seg){
        int idx=indexOf(seg);
        if (idx==-1) return null;
        if (idx>0) 
            return get(idx-1);
        if (get(size()-1) != seg && isLooped()) return get(size()-1);
        return null;
    }
    public void transform(AffineTransform tr){
        for (int i=0;i<size();i++)
            get(i).transform(tr);
    }
    public JSimplePath clone(){
        JSimplePath ret=new JSimplePath();
        for (int i=0;i<segments.size();i++){
            ret.add(get(i).clone());
        }
        ret.setLooped(looped);
        return ret;
    }
    public boolean equals(Object o){
        if (!(o instanceof JSimplePath)) return false;
        JSimplePath jp=(JSimplePath)o;
        if(jp.looped != looped) return false;
        if (segments==null) {
            if (jp.segments==null) 
                return true;
            else
                return false;
        }
        if (jp.segments==null) return false;
        if(segments.size() != jp.segments.size()) return false;
        for (int i=0;i<segments.size();i++){
            if (!get(i).equals(jp.get(i))) return false;
        }
        return true;
    }
    
}
