/*
 * JObject.java
 *
 * Created on 2007/08/26, 14:20
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jobject;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.tree.TreeNode;

/**
 *子をもつオブジェクトの基本クラス.
 * @author TI
 */
public abstract class JObject<P extends JObject,C extends JLeaf> extends JLeaf<P> implements Cloneable{
    
    protected  Vector<C> children;
    protected static Vector<JLeaf> objectPacket=new Vector<JLeaf>();
    protected static Vector<Integer> packetType=new Vector<Integer>();
    //
    public static final int ADDING=1;
    public static final int REMOVING=2;
    //
    private static final long serialVersionUID=110l;
    /** Creates a new instance of JObject */
    public JObject() {
        children=new Vector<C>();
    }
    public boolean isEmpty(){
        return children.isEmpty();
    }
    public int size(){
        return children.size();
    }
    public int indexOf(C o){
        return children.indexOf(o);
    }
    public boolean contains(C o){
        return children.contains(o);
    }
    public C get(int i){
        return children.get(i);
    }
    public void add(C o){
        if (children.contains(o)) return;
        if(children.add(o))
            o.setParent(this);
        objectPacket.add(o);
        packetType.add(ADDING);
        JPage page=getPage();
        if (page !=null)
            page.sendPacket();
    }
    public void add(int i,C o){
        if (children.contains(o)) return;
        children.add(i,o);
        o.setParent(this);
        objectPacket.add(o);
        packetType.add(ADDING);
        JPage page=getPage();
        if (page !=null)
            page.sendPacket();
    }
    public void remove(C o){
        children.remove(o);
        objectPacket.add(o);
        packetType.add(REMOVING);
        JPage page=getPage();
        if (page !=null)
            page.sendPacket();
    }
    public void clear(){
        while(size()>0){
            remove(0);
        }
    }
    public C remove(int index){
        C ret=children.remove(index);
        if (ret != null){
            ret.setParent(null);
            JDocument doc=getDocument();
            objectPacket.add(ret);
            packetType.add(REMOVING);
        }
        JPage page=getPage();
        if (page !=null)
            page.sendPacket();
        return ret;
    }
    
    //For TreeNode
    @Override
    public Enumeration children(){
        Vector<JLeaf> reverse=new Vector<JLeaf>();
        Iterator<JLeaf> it=(Iterator<JLeaf>)children.iterator();
        while(it.hasNext()){
            reverse.add(0,it.next());
        }
        return reverse.elements();
    }
    @Override
    public boolean getAllowsChildren(){
        return true;
    }
    @Override
    public TreeNode getChildAt(int childIndex){
        return get(children.size()-childIndex-1);
    }
    @Override
    public int getChildCount(){
        return size();
    }
    @Override
    public int getIndex(TreeNode node){
        return (children.size()-indexOf((C)node)-1);
    }
    @Override
    public boolean isLeaf(){
        return false;
    }
    //
    
    //オブジェクトが所有する末端オブジェクトを要素とするVector;
    public Vector<JLeaf> getLeafs(){
        Vector<JLeaf> ret=new Vector<JLeaf>();
        for (int i=0;i<size();i++){
            Object o=get(i);
            if (o instanceof JObject){
                JObject jo=(JObject) o;
                Vector<JLeaf> vl= jo.getLeafs();
                for (int k=0;k<vl.size();k++){
                    ret.add(vl.get(k));
                }
            }else{
                ret.add((JLeaf)o);
            }
        }
        return ret;
    }
    @Override
    public  void paint(Rectangle2D clip,Graphics2D g){
        if (!isVisible()) return;
        for (int i=0;i<size();i++){
            get(i).paint(clip,g);
        }
        paintThis(clip,g);
    }
    private void readObject(java.io.ObjectInputStream in)
    throws IOException, ClassNotFoundException{
        in.defaultReadObject();
        for (int i=0;i<size();i++){
            get(i).setParent(this);
        }
    }
}
