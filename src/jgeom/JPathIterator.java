/*
 * JPathIterator.java
 *
 * Created on 2007/08/25, 10:21
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jgeom;

import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.Vector;

/**
 *
 * @author TI
 */
public class JPathIterator implements PathIterator {
    public Vector<Segment> segments;
    public int windingRule;
    public int index;
    /** Creates a new instance of JPathIterator */
    public JPathIterator(PathIterator path) {
        segments=new Vector<Segment>();
        windingRule=path.getWindingRule();
        index=0;
        double[] coords=new double[6];
        int type;
        Point2D lastPoint=null;
        while (!path.isDone()){
            type=path.currentSegment(coords);
            Segment seg=new Segment(type,coords);
            if (seg.pointType==SEG_QUADTO){
                Point2D p1=seg.getPoint1();
                Point2D p2=seg.getPoint2();
                Point2D ctl1=new Point2D.Double((p1.getX()*2+lastPoint.getX())/3,(p1.getY()*2+lastPoint.getY())/3);
                Point2D ctl2=new Point2D.Double((p1.getX()*2+p2.getX())/3,(p1.getY()*2+p2.getY())/3);
                seg=new Segment(SEG_CUBICTO,ctl1,ctl2,p2);
            }
            segments.add(seg);
            lastPoint=seg.getAncur();
            path.next();
        }
    }
    public JPathIterator(JComplexPath jps){
        segments=new Vector<Segment>();
        windingRule=jps.getWindingRule();
        index=0;
        for (int i=0;i<jps.size();i++){
            setJPath(jps.get(i));
        }
        
    }
    public JPathIterator(JSimplePath js,int wrule){
        windingRule=wrule;
        index=0;
        segments=new Vector<Segment>();
        setJPath(js);
    }
    private void setJPath(JSimplePath jp){
        if (jp.size()==0) return;
        int type;
        JSegment pseg,cseg=jp.get(0);
        type=PathIterator.SEG_MOVETO;
        segments.add(new Segment(type,cseg.getAncur(),null,null));
        pseg=cseg;
        for (int i=1;i<jp.size();i++){
            cseg=jp.get(i);
            addSegment(pseg,cseg);
            pseg=cseg;
        }
        if (jp.size()>2 && jp.isLooped()){
            cseg=jp.get(0);
            addSegment(pseg,cseg);
            segments.add(new Segment(PathIterator.SEG_CLOSE,null,null,null));
        }
    }
    private void addSegment(JSegment pseg,JSegment cseg){
        double[] coords =new double[6];
        int type;
        Point2D p1,p2,p3;
        p1=p2=p3=null;
        if (pseg.getControl2() != null || cseg.getControl1() !=null){
            type=PathIterator.SEG_CUBICTO;
            p3=cseg.getAncur();
            if (pseg.getControl2()==null)
                p1=pseg.getAncur();
            else
                p1=pseg.getControl2();
            if (cseg.getControl1()==null)
                p2=cseg.getAncur();
            else
                p2=cseg.getControl1();
        }else{
            type=PathIterator.SEG_LINETO;
            p1=cseg.getAncur();
        }
        segments.add(new Segment(type,p1,p2,p3));
    }
    public JComplexPath getJPath(){
        JComplexPath ret=new JComplexPath(windingRule);
        Vector<Vector<Segment>> divs=devide();
        for (int i=0;i<divs.size();i++){
            Vector<Segment> segs=divs.get(i);
            boolean looped=isLoopPath(segs);
            JSimplePath jp= new JSimplePath();
            ret.add(jp);
            Segment cseg,next,last;
            Point2D ancur,c1,c2;
            int loopCount=segs.size();
            for(int j=0;j<loopCount;j++){
                cseg=segs.get(j);
                next=null;
                if (j+1<segs.size() && segs.get(j+1).pointType !=SEG_CLOSE)
                    next=segs.get(j+1);
                ancur=c1=c2=null;
                if (cseg.pointType==SEG_MOVETO){
                    ancur=cseg.getAncur();
                    if (next !=null && next.pointType==SEG_CUBICTO)
                        c2=next.getPoint1();
                    if (looped){
                        last=segs.get(segs.size()-2);
                        if (last.pointType==SEG_CUBICTO)
                            c1=last.getPoint2();
                        loopCount-=2;
                    }
                }else if(cseg.pointType !=SEG_CLOSE){
                    ancur=cseg.getAncur();
                    if (cseg.pointType==SEG_CUBICTO)
                        c1=cseg.getPoint2();
                    if (next != null && next.pointType==SEG_CUBICTO)
                        c2=next.getPoint1();
                }
                if (ancur !=null){
                    if (c1 != null && c1.equals(ancur))
                        c1=null;
                    if(c2 != null && c2.equals(ancur))
                        c2=null;
                    jp.add(new JSegment(ancur,c1,c2,true));
                }
            }
            jp.setLooped(looped);
        }
        return ret;
    }
    private boolean isLoopPath(Vector<Segment> segs){
        int size=segs.size();
        if (size<4) return false;
        if (segs.get(size-1).pointType != PathIterator.SEG_CLOSE) return false;
        //if (segs.get(size-2).getAncur().equals(segs.get(0).getAncur())) return true;
        return true;
    }
    public void normalize(){
        Vector<Vector<Segment>> segs=devide();
        boolean flag=false;
        for (int i=0;i<segs.size();i++){
            Vector<Segment> vecs=segs.get(i);
            if (vecs.get(vecs.size()-1).pointType==SEG_CLOSE && !vecs.get(vecs.size()-2).getAncur().equals(vecs.get(0).getAncur())){
                double[] coords=new double[2];
                for (int j=0;j<2;j++){
                    coords[j]=vecs.get(0).pointcoords[j];
                }
                vecs.add(vecs.size()-1,new Segment(SEG_LINETO,coords));
                flag=true;
            }
            Segment preSeg=vecs.get(0);
            for (int j=1;j<vecs.size();j++){
                Segment cSeg=vecs.get(j);
                if (cSeg.pointType==SEG_CLOSE) break;
                Point2D pp=preSeg.getAncur();
                Point2D cp=cSeg.getAncur();
                double dx=pp.getX()-cp.getX();
                double dy=pp.getY()-cp.getY();
                double d=dx*dx+dy*dy;
                if (d<0.000001){
                    vecs.remove(j--);
                    flag=true;
                }else{
                    preSeg=cSeg;
                }
            }
            if (vecs.size()<2 || (vecs.size()==2 && vecs.get(1).pointType==SEG_CLOSE)){
                segs.remove(i--);
            }
        }
        if (!flag) return;
        segments.clear();
        for (int i=0;i<segs.size();i++){
            Vector<Segment> vecs=segs.get(i);
            for (int j=0;j<vecs.size();j++){
                segments.add(vecs.get(j));
            }
        }
    }
    private Vector<Vector<Segment>> devide(){
        Vector<Vector<Segment>> ret=new Vector<Vector<Segment>>();
        Vector<Segment> cVec=null;
        for (int i=0;i<segments.size();i++){
            Segment seg=segments.get(i);
            if (seg.pointType==PathIterator.SEG_MOVETO){
                cVec=new Vector<Segment>();
                ret.add(cVec);
            }
            cVec.add(seg);
        }
        return ret;
    }
    private int pathSize(){
        int ret=0;
        for (int i=0;i<segments.size();i++){
            if(segments.get(i).pointType==PathIterator.SEG_MOVETO){
                ret++;
            }
        }
        return ret;
    }
    public int getWindingRule() {
        return windingRule;
    }
    public boolean isDone() {
        return(index>segments.size()-1);
    }
    public void next() {
        index++;
    }
    public int currentSegment(float[] coords) {
        Segment cseg=segments.get(index);
        int ret=cseg.pointType;
        int loop=pointCount(ret)*2;
        for (int i=0;i<loop;i++){
            coords[i]=(float)cseg.pointcoords[i];
        }
        return ret;
    }
    public int currentSegment(double[] coords) {
        Segment cseg=segments.get(index);
        int ret=cseg.pointType;
        int loop=pointCount(ret)*2;
        for (int i=0;i<loop;i++){
            coords[i]=(float)cseg.pointcoords[i];
        }
        return ret;
        
    }
    private  int pointCount(int type){
        if (type==PathIterator.SEG_MOVETO || type==PathIterator.SEG_LINETO)
            return 1;
        if (type==PathIterator.SEG_QUADTO)
            return 2;
        if (type==PathIterator.SEG_CUBICTO)
            return 3;
        return 0;
    }
    public class Segment{
        private int pointType;
        private float[] pointcoords;
        
        public Segment(int type,double[] coords){
            int size=pointCount(type)*2;
            pointType=type;
            pointcoords=null;
            if(size>0)
                pointcoords=new float[size];
            for (int i=0;i<size;i++){
                pointcoords[i]=(float)coords[i];
            }
        }
        public Segment(int type,Point2D p1,Point2D p2,Point2D p3){
            Point2D[] pcod =new Point2D[]{p1,p2,p3};
            pointType=type;
            int size=pointCount(type);
            pointcoords=null;
            if (size>0)
                pointcoords=new float[size*2];
            for (int i=0;i<size;i++){
                pointcoords[i*2]=(float)pcod[i].getX();
                pointcoords[i*2+1]=(float)pcod[i].getY();
            }
        }
        public Point2D getAncur(){
            if (pointType==SEG_CLOSE) return null;
            int size=(pointCount(pointType)-1)*2;
            return new Point2D.Float(pointcoords[size],pointcoords[size+1]);
        }
        public Point2D getPoint1(){
            if (pointType==SEG_CLOSE) return null;
            return new Point2D.Float(pointcoords[0],pointcoords[1]);
        }
        public Point2D getPoint2(){
            if (pointType!=SEG_QUADTO && pointType !=SEG_CUBICTO) return null;
            return new Point2D.Float(pointcoords[2],pointcoords[3]);
        }
        public Point2D getPoint3(){
            if (pointType != SEG_CUBICTO) return null;
            return new Point2D.Float(pointcoords[4],pointcoords[5]);
        }
    }
    
}
