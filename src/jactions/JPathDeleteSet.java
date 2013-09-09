/*
 * JPathDeleteSet.java
 *
 * Created on 2007/09/06, 9:08
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jactions;

import java.util.Vector;
import jgeom.JComplexPath;
import jgeom.JSegment;
import jgeom.JSimplePath;
import jobject.JLayoutTextObject;
import jobject.JLeaf;
import jobject.JObject;
import jobject.JPathObject;
import jscreen.JRequest;
import org.omg.PortableServer.AdapterActivator;

/**
 * パス,セグメント削除及び削除によるオブジェクトの作成をカプセル化します。
 * @author i002060
 */
public class JPathDeleteSet {
    public static final int OBJECT_MODE=1;
    public static final int PATH_MODE=2;
    public static final int SEGMENT_MODE=3;
    private JLeaf leaf;
    private Vector<JSimplePath> spaths;
    private Vector<JSegment> segments;
    private int mode;
    private int index;
    private Vector<JLeaf> deleteResult;
    /**
     * Creates a new instance of JPathDeleteSet
     */
    public JPathDeleteSet(JLeaf leaf,Vector<JSimplePath> tp,Vector<JSegment> ts,int mode) {
        this.leaf=leaf;
        spaths=tp;
        segments=ts;
        this.mode=mode;
        deleteResult=createDeleteResults();
    }
    public void doDelete(JRequest req){
        index=leaf.getParent().indexOf(leaf);
        leaf.getParent().remove(leaf);
        if (deleteResult==null || deleteResult.isEmpty()) return;
        for (int i=0;i<deleteResult.size();i++){
            leaf.getParent().add(index,deleteResult.get(i));
        }
        if (leaf instanceof JPathObject){
            ((JPathObject)leaf).updatePath();
        }
    }
    public void doRestore(JRequest req){
        if (!(deleteResult == null || deleteResult.isEmpty())){
            for (int i=0;i<deleteResult.size();i++){
                leaf.getParent().remove(deleteResult.get(i));
            }
        }
        leaf.getParent().add(index,leaf);
        if (leaf instanceof JPathObject){
            ((JPathObject)leaf).updatePath();
        }
    }
    public JLeaf getOriginal(){
        return leaf;
    }
    public Vector<JLeaf> getResults(){
        return deleteResult;
    }
    private Vector<JLeaf> createDeleteResults(){
        Vector<JLeaf> ret=null;
        if (mode==OBJECT_MODE)
            return ret;
        JPathObject jp=(JPathObject)leaf;
        if (mode==PATH_MODE){
            if (jp.getPath().size()==spaths.size()) return ret;
            JPathObject jr;
            if (leaf instanceof JLayoutTextObject){
                JLayoutTextObject sObj=(JLayoutTextObject)leaf;
                JLayoutTextObject rObj=new JLayoutTextObject(jp.getFillPaint(),jp.getStrokePaint(),jp.getStroke());
                rObj.setStyledDocument(sObj.getStyledDocument());
                jr=rObj;
            }else{
                jr=new JPathObject(jp.getFillPaint(),jp.getStrokePaint(),jp.getStroke());
            }
            jr.getPath().setWindingRuel(jp.getPath().getWindingRule());
            for (int i=0;i<jp.getPath().size();i++){
                if (!spaths.contains(jp.getPath().get(i))){
                    jr.getPath().add(jp.getPath().get(i).clone());
                }
            }
            ret = new Vector<JLeaf>();
            ret.add(jr);
            return ret;
        }
        if (mode==SEGMENT_MODE){
            Vector<FlagedSegments> fsegs=createFlagedSegments();
            Vector<JSimplePath> rpath=new Vector<JSimplePath>();
            
            for (int i=0;i<fsegs.size();i++){
                if (!fsegs.get(i).isNotSelectedAll())
                    continue;
                if (!fsegs.get(i).hasSelected()){
                    rpath.add(fsegs.get(i).getSimplePath());
                    continue;
                }
                int index=fsegs.get(i).getFirstIndex();
                JSimplePath rp=null;
                for (int j=0;j<fsegs.get(i).size();j++){
                    if (!fsegs.get(i).isSelected(index)){
                        if (rp==null){
                            rp=new JSimplePath();
                        }
                        rp.add(fsegs.get(i).get(index));
                    }else{
                        if (rp!=null)
                            rpath.add(rp);
                        rp=null;
                    }
                    index++;
                    if (index>=fsegs.get(i).size())
                        index=0;
                }
                if (rp!=null){
                    rpath.add(rp);
                }
            }
            if (!rpath.isEmpty()){
                ret=new Vector<JLeaf>();
                if (jp.getPath().size()==1){
                    for (int i=0;i<rpath.size();i++){
                        JPathObject robj=jp.clone();
                        robj.getPath().clear();
                        robj.getPath().add(rpath.get(i));
                        ret.add(robj);
                    }
                }else{
                    JPathObject robj=jp.clone();
                    robj.getPath().clear();
                    for (int i=0;i<rpath.size();i++){
                        robj.getPath().add(rpath.get(i));
                    }
                    ret.add(robj);
                }
            }
        }
        return ret;
    }
    
    private Vector<FlagedSegments> createFlagedSegments(){
        if (!(leaf instanceof JPathObject)) return null;
        JPathObject jp=(JPathObject)leaf;
        Vector<FlagedSegments> ret=new Vector<FlagedSegments>();
        for (int i=0;i<jp.getPath().size();i++){
            FlagedSegments fseg=new FlagedSegments(jp.getPath().get(i));
            for (int j=0;j<fseg.size();j++){
                if (segments.contains(fseg.get(j))){
                    fseg.select(j);
                }
            }
            ret.add(fseg);
        }
        return ret;
    }
    public static Vector<JPathDeleteSet> createJPathEditSets(JRequest req){
        Vector<JPathDeleteSet> ret=new Vector<JPathDeleteSet>();
        Vector<JLeaf> leafs=new Vector<JLeaf>();
        Vector<JPathObject> jpo=new Vector<JPathObject>();
        Vector<JSimplePath> jsp=new Vector<JSimplePath>();
        Vector<JSegment> jsg=new Vector<JSegment>();
        for (int i=0;i<req.size();i++){
            Object o=req.get(i);
            if (o instanceof JSegment)
                jsg.add((JSegment)o);
            else if(o instanceof JSimplePath)
                jsp.add((JSimplePath)o);
            else if (o instanceof JPathObject)
                jpo.add((JPathObject)o);
            else if (o instanceof JLeaf){
                leafs.add((JLeaf)o);
            }
        }
        boolean omode=true;
        for(int i=0;i<jpo.size();i++){
            Vector<JSegment> pjseg=new Vector<JSegment>();
            Vector<JSimplePath> pjsp=new Vector<JSimplePath>();
            JPathObject jobj=jpo.get(i);
            for (int j=0;j<jsg.size();j++){
                if (jobj.getPath().contains(jsg.get(j))){
                    pjseg.add(jsg.get(j));
                }
            }
            for (int j=0;j<jsp.size();j++){
                if (jobj.getPath().contains(jsp.get(j))){
                    pjsp.add(jsp.get(j));
                }
            }
            if (!pjseg.isEmpty()){
                ret.add(new JPathDeleteSet(jobj,pjsp,pjseg, SEGMENT_MODE));
                omode=false;
            }else if (jsg.isEmpty()){
                if (jobj.getParent().size()==pjsp.size()){
                    ret.add(new JPathDeleteSet(jobj,pjsp,pjseg,OBJECT_MODE));
                }else{
                    ret.add(new JPathDeleteSet(jobj,pjsp,pjseg,PATH_MODE));
                }
            }
            
        }
        if (omode){
            for (int i=0;i<leafs.size();i++){
                ret.add(new JPathDeleteSet(leafs.get(i),null,null,OBJECT_MODE));
            }
        }
        return ret;
    }
    
}
