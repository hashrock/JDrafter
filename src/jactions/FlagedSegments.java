/*
 * FlagedSegments.java
 *
 * Created on 2007/09/07, 11:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jactions;

import jgeom.JSegment;
import jgeom.JSimplePath;

/**
 *
 * @author i002060
 */
public class FlagedSegments {
    private boolean[] isSelected;
    private JSimplePath path;
    public FlagedSegments(JSimplePath path){
        this.path=path;
        isSelected=new boolean[path.size()];
        for (int i=0;i<isSelected.length;i++){
            isSelected[i]=false;
        }
    }
//***ƒpƒX‚ª“r’†‚©‚çØ’f‚³‚ê‚Ä‚¢‚éê‡‚É’T¸‚·‚×‚«Å‰‚ÌIndex‚ðŽæ“¾
    public int getFirstIndex(){
        if (!hasSelected() || !isNotSelectedAll()) return 0;
        int ret=0;
        if (path.isLooped() && !isSelected(0)){
            ret=size()-1;
            while(ret-1>0 && !isSelected(ret)){
                if (isSelected(ret-1)){
                    return ret;
                }
                ret--;
            }
            return ret;
        }
        while(ret<size() && isSelected(ret)){
            ret++;
        }
        return ret;
    }
    public int getFirstIndexForCopy(){
        if (!hasSelected()|| !isNotSelectedAll()) return 0;
        if (!path.isLooped() || !isSelected(0) || !isSelected(size()-1) ||
                (isSelected(0) && !isSelected(size()-1))) return 0;
        int ret=size()-1;
        while (ret-1>0 && isSelected(ret)){
            if (!isSelected(ret-1)) return ret;
            ret--;
        }
        return ret;    
    }
    public boolean select(JSegment seg){
        int i=path.indexOf(seg);
        if (i>=0){
            isSelected[i]=true;
            return true;
        }
        return false;
    }
    public void select(int index){
        isSelected[index]=true;
    }
    public boolean isSelected(int index){
        return isSelected[index];
    }
    public boolean isSelected(JSegment seg){
        return isSelected[path.indexOf(seg)];
    }
    public boolean hasSelected(){
        for (int i=0;i<isSelected.length;i++){
            if (isSelected[i]) return true;
        }
        return false;
    } 
    public boolean isNotSelectedAll(){
        for (int i=0;i<isSelected.length;i++){
            if (!isSelected[i]) return true;
        }
        return false;
    }
    public boolean isLooped(){
        return path.isLooped();
    }
    public JSimplePath getSimplePath(){
        return path;
    }
    public JSegment get(int index){
        return path.get(index);
    }
    public int size(){
        return path.size();
    }
}
