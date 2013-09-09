/*
 * JLayerItem.java
 *
 * Created on 2007/12/27, 9:41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jui.layer;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JLabel;
import jobject.JLeaf;
import jobject.JObject;

/**
 *
 * @author i002060
 * ┌────────────────────┐<br>
 * │目|鍵│(Preview) 名称                   │<br>
 * └────────────────────┘<br>
 *
 */
public class JLayerItem {
    public static final int HEIGHT=16;
    public static final int WIDTH=400;
    private JLeaf leaf;
    private Map<JLeaf,JLayerItem> map;
    private Object parent=null;
    private boolean isOpen=false;
    /** Creates a new instance of JLayerItem */
    public JLayerItem(JLeaf leaf,Object parent) {
        map=new HashMap<JLeaf,JLayerItem>();
        this.parent=parent;
        this.leaf=leaf;
        if (leaf instanceof JObject){
            setupMap();
        }
    }
    public boolean hasChildren(){
        return !map.isEmpty();
    }    
    private void setupMap(){
        if (!hasChildren()) return;
        JObject jb=(JObject)leaf;
        Iterator<JLeaf> it=map.keySet().iterator();
        while(it.hasNext()){
            JLeaf l=it.next();
            if (!jb.contains(l)) map.remove(l);
        }
        for (int i=0;i<jb.size();i++){
            if (!map.containsKey(jb.get(i)))
                map.put(jb.get(i),new JLayerItem(jb.get(i),this));
        }
    }
    public int getX(){
        return 0;
    }
    public int getY(){
        if (parent instanceof JComponent){
            return 0;
        }
        JLayerItem p=(JLayerItem)parent;
        return (p.getY()+HEIGHT*(p.indexOf(this)+1));
    }
    public int getHeight(){
        int ret=HEIGHT;
        if (hasChildren() && isOpen){
            setupMap();
            Iterator<JLayerItem> it=map.values().iterator();
            while (it.hasNext()){
                ret+=it.next().getHeight();
            }
        }
        return ret;
    }
    public int getWidth(){
        return WIDTH;
    }
    public int indexOf(JLayerItem li){
       if (!hasChildren()) return -1;
       return ((JObject)leaf).indexOf(li.leaf);
    }
    public JLeaf getLeaf(){
        return leaf;
    }
    public Object getParent(){
        return parent;
    }
    public JLayerItem hit(Point p){
        Rectangle r=new Rectangle(getX(),getY(),WIDTH,HEIGHT);
        if (r.contains(p)) return this;
        if (hasChildren()){
            setupMap();
            JObject jo=(JObject)leaf;
            for (int i=0;i<jo.size();i++){
                JLayerItem ret=map.get(jo.get(i)).hit(p);
                if (ret !=null) return ret;
            }
        }
        return null;
    }
    public void paint(Graphics g){
       Graphics2D g2=(Graphics2D)g;
       setupMap();
       
    }
    
}
