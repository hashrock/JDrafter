/*
 * JFlagedPath.java
 *
 * Created on 2007/09/25, 17:01
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jgeom;

import java.util.Vector;

/**
 *
 * @author i002060
 */
public class JFlagedPath extends JSimplePath{
    public Vector<Boolean> flags;
    /**
     * Creates a new instance of JFlagedPath
     */
    public JFlagedPath() {
        flags=new Vector<Boolean>();
    }
    public JFlagedPath(JSimplePath sp){
        this();
        for (int i=0;i<sp.size();i++){
            add(sp.get(i).clone());
        }
        this.setLooped(sp.isLooped());
    }
    public void add(int index,JSegment seg){
       segments.add(index,seg);
       flags.add(index,Boolean.FALSE);
    }
    public boolean add(JSegment seg){
        flags.add(Boolean.FALSE);
        return segments.add(seg);
        
    }
    public boolean remove(JSegment seg){
        int idx =indexOf(seg);
        flags.remove(idx);
        boolean ret= segments.remove(seg);
        if (size()<3) looped=false;
        return ret;
    }
    public JSegment remove(int index){
        flags.remove(index);
        JSegment ret= segments.remove(index);
        if (size()<3) looped=false;
        return ret;
    }
    public void clear(){
        super.clear();
        flags.clear();
    }
    public boolean isSelected(JSegment seg){
        int idx=indexOf(seg);
        if (idx !=-1 )
            return flags.get(idx).booleanValue();
        return false;
    }
    public boolean isSelected(int idx){
        return flags.get(idx).booleanValue();
    }
    public void setSelected(JSegment seg,boolean selected){
        int idx=indexOf(seg);
        if (idx !=-1){
            if (selected)
                flags.set(idx,Boolean.TRUE);
            else
                flags.set(idx,Boolean.FALSE);
        }
    }
    public void setSelected(int idx,boolean selected){
        if (selected){
            flags.set(idx,Boolean.TRUE);
        }else{
            flags.set(idx,Boolean.FALSE);
        }
    }
    public void clearSelection(){
        for (int i=0;i<flags.size();i++){
            flags.set(i,Boolean.FALSE);
        }
    }
    public void selectAll(){
        for (int i=0;i<flags.size();i++){
            flags.set(i,Boolean.TRUE);
        }
    }
    public int getSelectionSize(){
        int ret=0;
        for (int i=0;i<flags.size();i++){
            if(flags.get(i).booleanValue()){
                ret++;
            }
        }
        return ret;
    }
    public int firstSelectionIndex(){
        int ret=0;
        for (int i=0;i<flags.size();i++){
            if (flags.get(i).booleanValue())
                return i;
        }
        return -1;
    }
    public JSimplePath getSimplePath(){
        JSimplePath ret=new JSimplePath();
        for (int i=0;i<size();i++){
            ret.add(get(i));
        }
        ret.setLooped(isLooped());
        return ret;
    }
}
