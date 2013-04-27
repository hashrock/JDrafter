/*
 * JComplexPath.java
 *
 * Created on 2007/09/03, 13:35
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jgeom;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.io.Serializable;
import java.util.Vector;

/**
 *
 * @author i002060
 */
public class JComplexPath implements Serializable,Cloneable {
    private Vector<JSimplePath> pathes;
    private int windingRule;
    /** Creates a new instance of JComplexPath */
    public JComplexPath() {
        pathes=new Vector<JSimplePath>();
        windingRule=PathIterator.WIND_EVEN_ODD;
    }
    public JComplexPath(int wRule){
        pathes=new Vector<JSimplePath>();
        windingRule=wRule;
    }
    public int getWindingRule(){
        return windingRule;
    }
    public void setWindingRuel(int w){
        this.windingRule=w;
    }
    public Shape getShape(){
        JPathIterator path=new JPathIterator(this);
        Path2D.Float gp=new Path2D.Float(windingRule);
        gp.append(path,false);
        return gp;
    }
    public void transform(AffineTransform af){
        for (int i=0;i<size();i++){
            get(i).transform(af);
        }
    }
    public boolean isEmpty(){
        return pathes.isEmpty();
    }
    public int size(){
        return pathes.size();
    }
    public int segmentSize(){
        int ret=0;
        for (int i=0;i<size();i++){
            ret+=get(i).size();
        }
        return ret;
    }
    public boolean contains(JSimplePath p){
        return pathes.contains(p);
    }
    public boolean contains(JSegment seg){
        for (int i=0;i<size();i++){
            if (get(i).contains(seg)) return true;
        }
        return false;
    }
    public int indexOf(JSimplePath p){
        return pathes.indexOf(p);
    }
    public int indexOf(JSegment seg){
        int ret=-1;
        int cidx=0;
        for (int i=0;i<size();i++){
            if ((ret=get(i).indexOf(seg)) !=-1)
                return ret+cidx;
            cidx+=get(i).size();
        }
        return -1;
    }
    public JSimplePath getOwnerPath(JSegment seg){
        for (int i=0;i<size();i++){
            if (get(i).contains(seg)) return get(i);
        }
        return null;
    }
    public JSimplePath get(int index){
        return pathes.get(index);
    }
    public JSegment getSegment(int index){
        int sar=0;
        for (int i=0;i<size();i++){
            if (sar+get(i).size()>index){
                return get(i).get(index-sar);
            }
            sar+=get(i).size();
        }
        return null;
    }
    public void add(int index,JSimplePath p){
//        if (pathes.contains(p)) return;
        pathes.add(index,p);
    }
    public boolean add(JSimplePath p){
//        if (pathes.contains(p)) return false;
        return pathes.add(p);
    }
    public boolean add(JSegment seg){
        if (isEmpty()){
           JSimplePath js=new JSimplePath();
           add(js);
           return js.add(seg);
        }else{
            JSimplePath js=get(size()-1);
            if (js.contains(seg)) return false;
            return js.add(seg);
        }
    }
    public boolean remove(JSimplePath p){
        return pathes.remove(p);
    }
    public boolean remove(JSegment seg){
        for (int i=0;i<size();i++){
            if (get(i).remove(seg)) return true;
        }
        return false;
    }
    public void clear(){
        pathes.clear();
    }
    public JSimplePath remove(int index){
        return pathes.remove(index);
    }
    public JSegment prevSegment(JSegment seg){
        for (int i=0;i<size();i++){
            if (get(i).contains(seg))
                return get(i).prevSegment(seg);
        }
        return null;
    }
    public JSegment nextSegment(JSegment seg){
        for (int i=0;i<size();i++){
            if (get(i).contains(seg))
                return get(i).nextSegment(seg);
        }
        return null;
    }
    public boolean equals(Object o){
        if (!(o instanceof JComplexPath)) return false;
        JComplexPath obj=(JComplexPath)o;
        if (obj.windingRule != windingRule) return false;
        if (obj.size()!=size()) return false;
        for (int i=0;i<size();i++){
            if (!get(i).equals(obj.get(i))) return false;
        }
        return true;
    }
    public JComplexPath clone(){
        JComplexPath ret=new JComplexPath(windingRule);
        for (int i=0;i<size();i++){
            ret.add(get(i).clone());
        }
        return ret;
    }
}
