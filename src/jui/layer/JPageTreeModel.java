/*
 * JPageTreeModel.java
 *
 * Created on 2008/05/11, 6:35
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jui.layer;

import java.awt.print.Pageable;
import java.util.HashMap;
import java.util.Vector;
import jobject.JLeaf;
import jobject.JObject;
import jobject.JPage;

/**
 *
 * @author takashi
 */
public class JPageTreeModel {
    private JPage page=null;
    private Vector<JObject> expandedObject=null;
    private Vector<JLeaf> visibleRows=null;
    private Vector<JTreeModelListener> listeners;
    /** Creates a new instance of JPageTreeModel */
    public JPageTreeModel(JPage page) {
        this.page=page;
        expandedObject=new Vector<JObject>();
        visibleRows=new Vector<JLeaf>();
        listeners=new Vector<JTreeModelListener>();
        reload();
    }
    public void addTreeModelListener(JTreeModelListener l){
        if (!listeners.contains(l)){
            listeners.add(l);
        }
    }
    public void removeTreeModelLsitener(JTreeModelListener l){
        listeners.remove(l);
    }
    private void fireTreeModelEvent(){
        JTreeEvent e=new JTreeEvent(this);
        for (int i=0;i<listeners.size();i++){
            listeners.get(i).treeChanged(e);
        }
    }
    public boolean isVisible(JLeaf l){
        return visibleRows.contains(l);
    }
    public boolean isExpanded(JObject o){
        if (o ==page) return true;
        return expandedObject.contains(o);
    }
    public void setExpanded(JObject o,boolean b){
        if (o==page) return;
        if (b){
            if (expandedObject.contains(o)) return;
            expandedObject.add(o);
        }else{
            if (!expandedObject.contains(o)){
                return;
            }
            expandedObject.remove(o);
        }
        setupRows();
    }
    public void reload(){
        cleanupExpanded();
        setupRows();
        fireTreeModelEvent();
    }
    public JLeaf getJLeaForRow(int index){
        return visibleRows.get(index);
    }
    public int getRowCount(){
        return visibleRows.size();
    }
    public boolean contains(Object o){
        return visibleRows.contains(o);
    }
    public JPage getPage(){
        return page;
    }
    public int indexOf(JLeaf jl){
        return visibleRows.indexOf(jl);
    }
    public int depth(JLeaf l){
        return depth(l,0);
    }
    private int depth(JLeaf l,int dp){
        if (l==page) return dp;
        JLeaf parent=l.getParent();
        if (parent ==null) return -1;
        dp++;
        return depth(parent,dp);
    }
    
    private void cleanupExpanded(){
        /*
        Vector<JObject> vec=new Vector<JObject>();
        getDescenderObjects(page,vec);
        for (int i=0;i<expandedObject.size();i++){
            JObject o=expandedObject.get(i);
            if (!vec.contains(o)){
                expandedObject.remove(i--);
            }
        }
         **/
    }
    private void setupRows(){
        visibleRows.clear();
        getVisibleRows(page,visibleRows);
        fireTreeModelEvent();
    }
    private void getDescenderObjects(JObject o,Vector<JObject> vec){
        vec.add(o);
        for (int i=0;i<o.size();i++){
            JLeaf l=o.get(i);
            if (l instanceof JObject){
                getDescenderObjects((JObject)l,vec);
            }
        }
    }
    private void getDescenderLeafs(JLeaf l,Vector<JLeaf> vec){
        vec.add(l);
        if (l instanceof JObject){
            JObject o=(JObject)l;
            for (int i=0;i<o.size();i++){
                getDescenderLeafs(o.get(i),vec);
            }
        }
    }
    private void getVisibleRows(JLeaf l,Vector<JLeaf> vec){
        vec.add(l);
        if ((l instanceof JObject) && isExpanded((JObject)l)){
            JObject o=(JObject)l;
            for (int i=o.size()-1;i>=0;i--){
                getVisibleRows(o.get(i),vec);
            }
        }
    }
    
    
}
