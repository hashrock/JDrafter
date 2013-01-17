/*
 * JPathCopySet.java
 *
 * Created on 2007/09/07, 11:12
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jactions;

import java.util.Vector;
import jgeom.JComplexPath;
import jgeom.JSegment;
import jgeom.JSimplePath;
import jobject.JLeaf;
import jobject.JPathObject;
import jscreen.JRequest;

/**
 *  コピー動作によるオブジェクトの作成をカプセル化します。
 * @author i002060
 */
public class JPathCopySet {
    public static final int OBJECT_COPY_MODE=1;
    public static final int PATH_COPY_MODE=2;
    public static final int SEGMENT_COPY_MODE=3;
    private JLeaf leaf;
    private Vector<JSimplePath> spaths;
    private Vector<JSegment> segments;
    private int mode;
    
    /** Creates a new instance of JPathCopySet */
    public JPathCopySet(JLeaf leaf,Vector<JSimplePath> spaths,Vector<JSegment> segments,int mode) {
        this.leaf=leaf;
        this.spaths=spaths;
        this.segments=segments;
        this.mode=mode;
    }
   
    
    public Vector<JLeaf> createCopyObjects(){
        Vector<JLeaf> ret=new Vector<JLeaf>();
        if (mode==OBJECT_COPY_MODE){
            try{
                ret.add((JLeaf)leaf.clone());
            }catch(CloneNotSupportedException e){
                
            }
        }else if (mode==PATH_COPY_MODE){
            JPathObject source=(JPathObject)leaf;
            JPathObject dist=new JPathObject(source.getFillPaint(),source.getStrokePaint(),source.getStroke());
            JComplexPath cmp=dist.getPath();
            for (int i=0;i<spaths.size();i++){
                cmp.add(spaths.get(i).clone());
            }
            ret.add(dist);
        }else if(mode==SEGMENT_COPY_MODE){
            ret=createFromSegment();
        }
        return ret;
    }
    private Vector<JLeaf> createFromSegment(){
        Vector<FlagedSegments> fsegs=new Vector<FlagedSegments>();
        Vector<JSegment> sg=(Vector<JSegment>)segments.clone();
        for (int i=0;i<spaths.size();i++){
            FlagedSegments fs=new FlagedSegments(spaths.get(i));
            for (int j=0;j<fs.size();j++){
                if (sg.contains(fs.get(j))){
                    fs.select(j);
                    sg.remove(fs.get(j));
                }
            }
            fsegs.add(fs);
        }
        Vector<JSimplePath> result=new Vector<JSimplePath>();
        for (int i=0;i<fsegs.size();i++){
            FlagedSegments fseg=fsegs.get(i);
            int idx=fseg.getFirstIndexForCopy();
            JSimplePath sp=null;
            for (int j=0;j<fseg.size();j++){
                if (fseg.isSelected(idx)){
                    if (sp==null){
                        sp=new JSimplePath();
                    }
                    sp.add(fseg.get(idx).clone());
                }else{
                    if (sp !=null){
                        sp.setLooped(false);
                        result.add(sp);
                    }
                    sp=null;
                }
                idx++;
                if (idx>=fseg.size())
                    idx=0;
            }
            if (sp!=null){
                if (!fseg.isNotSelectedAll() && fseg.isLooped()){
                    sp.setLooped(true);
                }
                result.add(sp);
            }   
        }
        Vector<JLeaf> ret=null;
        if (!result.isEmpty()){
            ret=new Vector<JLeaf>();
            JPathObject source=(JPathObject)leaf;
            if (source.getPath().size()>1){                
                JPathObject dist=new JPathObject(source.getFillPaint(),source.getStrokePaint(),source.getStroke());
                JComplexPath cmp=dist.getPath();
                for (int i=0;i<result.size();i++){
                    cmp.add(result.get(i).clone());
                }
                ret.add(dist);
            }else{
                for (int i=0;i<result.size();i++){
                    JPathObject dist=new JPathObject(source.getFillPaint(),source.getStrokePaint(),source.getStroke());
                    JComplexPath cmp=dist.getPath();
                    cmp.add(result.get(i));
                    ret.add(dist);
                }
            }
        }
        return ret;
    }
    public static Vector<JPathCopySet> createJPathCopySet(JRequest req){
        Vector<JLeaf> leafs=new Vector<JLeaf>();
        Vector<JPathObject> jobj=new Vector<JPathObject>();
        Vector<JSimplePath> jsp=new Vector<JSimplePath>();
        Vector<JSegment> segs=new Vector<JSegment>();
        Vector<JPathCopySet> ret=new Vector<JPathCopySet>();
        int mode;
        for (int i=0;i<req.size();i++){
            Object o=req.get(i);
            if (o instanceof JSegment){
                segs.add((JSegment)o);
            }else if (o instanceof JSimplePath){
                jsp.add((JSimplePath)o);
            }else if (o instanceof JPathObject){
                jobj.add((JPathObject)o);
            }else if (o instanceof JLeaf){
                leafs.add((JLeaf)o);
            }
        }
        Vector<JSimplePath> ppath=null;
        Vector<JSegment> pseg=null;
        boolean flag=segs.isEmpty();
        boolean leafCopy=true;
        for (int i=0;i<jobj.size();i++){
            JPathObject jo=jobj.get(i);
            pseg=new Vector<JSegment>();
            ppath=new Vector<JSimplePath>();
            for (int j=0;j<jsp.size();j++){
                if (jo.getPath().contains(jsp.get(j))){
                    ppath.add(jsp.remove(j--));
                }
            }
            for (int j=0;j<segs.size();j++){
                if (jo.getPath().contains(segs.get(j))){
                    pseg.add(segs.remove(j--));
                }
            }
            if (!pseg.isEmpty()){                
                ret.add(new JPathCopySet(jo,ppath,pseg,SEGMENT_COPY_MODE));
                leafCopy=false;
            }else{
                if (flag && !ppath.isEmpty()){
                    if (ppath.size()==jo.getPath().size()){
                        ret.add(new JPathCopySet(jo,ppath,pseg,OBJECT_COPY_MODE));
                        
                    }else{
                        ret.add(new JPathCopySet(jo,ppath,pseg,PATH_COPY_MODE));
                        leafCopy=false;
                    }
                }else if (flag){
                    ret.add(new JPathCopySet(jo,ppath,pseg,PATH_COPY_MODE));
                }
            }
        }
        if (leafCopy){
            for (int i=0;i<leafs.size();i++){
                ret.add(new JPathCopySet(leafs.get(i),null,null,OBJECT_COPY_MODE));
            }
        }
        return ret;
    }
    private static boolean isSelectedNothing(Vector<FlagedSegments> fsegs){
        for (int i=0;i<fsegs.size();i++){
            FlagedSegments fseg=fsegs.get(i);
            if (fseg.hasSelected()) return false;
        }
        return true;
    }
    private static boolean isSelectAll(Vector<FlagedSegments> fsegs){
        for (int i=0;i<fsegs.size();i++){
            FlagedSegments fseg=fsegs.get(i);
            if (fseg.isNotSelectedAll()) return false;
        }
        return true;
    }
    
}
