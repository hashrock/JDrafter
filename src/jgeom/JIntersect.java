/*
 * JDInterSection.java
 *
 * Created on 2006/12/29, 23:46
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jgeom;
import java.awt.geom.*;
import java.util.*;
import jgeom.JFlagedPath;
import jgeom.JSegment;
import jgeom.JSimplePath;
import jgeom.JSolution;
/**
 * 1次及び３次のパラメトリック曲線と直線の交点を返すメソッドを提供します.
 * @author TK
 */
public  class JIntersect {
    
    /** Creates a new instance of JDInterSection */
    public JIntersect() {
    }
    /**二つの直線の交点を返します.
     *@param p1 一つ目の直線の始点<BR>
     *@param p2 一つ目の直線の終点<BR>
     *@param p3 二つ目の直線の始点<BR>
     *@param P4 二つ目の直線の終点<BR>
     *@return 交点の座標交点がない場合はnull;*/
    public static Point2D lineIntersection(Point2D p1,Point2D p2,Point2D p3,Point2D p4){
        if (!Line2D.linesIntersect(p1.getX(),p1.getY(),p2.getX(),p2.getY(),p3.getX(),p3.getY(),p4.getX(),p4.getY())){
            return null;
        }
        double dx1=p2.getX()-p1.getX();
        double dx2=p4.getX()-p3.getX();
        double dy1=p2.getY()-p1.getY();
        double dy2=p4.getY()-p3.getY();
        double ba1=p1.getY()*p2.getX()-p1.getX()*p2.getY();
        double ba2=p3.getY()*p4.getX()-p3.getX()*p4.getY();
        double x=0,y=0;
        if (dx1!=0 ){
            if (dx2==0){
                x=p3.getX();
                y=x*dy1/dx1+ba1/dx1;
            }else{
                x=(ba2/dx2-ba1/dx1)/(dy1/dx1-dy2/dx2);
                y=x*dy1/dx1+ba1/dx1;
            }
        }else if (dy1!=0){
            if (dy2==0){
                y=p3.getY();
                x=y*dx1/dy1-ba1/dy1;
            }else{
                y=-(ba2/dy2-ba1/dy1)/(dx1/dy1-dx2/dy2);
                x=y*dx1/dy1-ba1/dy1;
            }
        }else{
            return null;
        }
        return (new Point2D.Double(x,y));
    }
    /**直線のと３次パラメトリック曲線の交点の解を返します。
     *@param p0 直線の始点<BR>
     *p1 直線の終点<BR>
     *p パラメトリック曲線のコントロールポイント*/
    public static JSolution[] cubicIntersection(Point2D p0,Point2D p1,Point2D[] p){
        int n,i;
        Line2D ln=new Line2D.Double(p0,p1);
        Rectangle2D.Double rc=new Rectangle2D.Double();
        double minX=Math.min(p0.getX(),p1.getX());
        double minY=Math.min(p0.getY(),p1.getY());
        double maxX=Math.max(p0.getX(),p1.getX());
        double maxY=Math.max(p0.getY(),p1.getY());
        for(i=0;i<4;i++){
            rc.add(p[i]);
        }
        if (!ln.intersects(rc)) return null;
        Vector<JSolution> ret=null;
        JSolution[] retArray=null;
        double dx=p1.getX()-p0.getX();
        double dy=p1.getY()-p0.getY();
        double ba=p0.getY()*p1.getX()-p0.getX()*p1.getY();
        double[] fx=new double[4];
        double x,y,t;
        fx[0]=dx*p[0].getY()-dy*p[0].getX()-ba;
        fx[1]=3*((-p[0].getY()+p[1].getY())*dx-(-p[0].getX()+p[1].getX())*dy);
        fx[2]=3*((p[0].getY()-2*p[1].getY()+p[2].getY())*dx-(p[0].getX()-2*p[1].getX()+p[2].getX())*dy);
        fx[3]=(-p[0].getY()+3*p[1].getY()-3*p[2].getY()+p[3].getY())*dx-(-p[0].getX()+3*p[1].getX()-3*p[2].getX()+p[3].getX())*dy;
        n=CubicCurve2D.solveCubic(fx);
        for (i=0;i<n;i++){
            if (fx[i]<0 || fx[i]>1) continue;
            t=fx[i];
            if (dx==0){
                x=p0.getX();
                y=(1-t)*(1-t)*(1-t)*p[0].getY()+3*(1-t)*(1-t)*t*p[1].getY()+3*(1-t)*t*t*p[2].getY()+t*t*t*p[3].getY();
            }else{
                x=(1-t)*(1-t)*(1-t)*p[0].getX()+3*(1-t)*(1-t)*t*p[1].getX()+3*(1-t)*t*t*p[2].getX()+t*t*t*p[3].getX();
                y=x*dy/dx+ba/dx;
            }
            if ((x>=minX && x<=maxX && y>=minY && y<=maxY)||
                    (dx==0 && y>=minY && y<=maxY) || (dy==0 && x>=minX && x<=maxX)){
                if (ret==null) ret=new Vector<JSolution>();
                ret.add(new JSolution(x,y,t));
            }
        }
        JSolution cs=null;
        if (ret != null){
            retArray=new JSolution[ret.size()];
            for (int j=0;j<retArray.length;j++){
                retArray[j]=ret.get(j);
            }
            for (int j=0;j<retArray.length-1;j++){
                for (int k=j+1;k<retArray.length;k++){
                    if (retArray[j].t>retArray[k].t){
                        cs=retArray[j];
                        retArray[j]=retArray[k];
                        retArray[k]=cs;
                    }
                }
            }
        }
        return retArray;
    }
    /**直線とパスが交差する場合交点にコントロールポイントを追加します.
     */
    public static int addPath(Point2D p0,Point2D p1,JSimplePath sourcePath){
        JSimplePath ret;
        JSegment current,next;
        Point2D[] p=new Point2D[4];
        ret=sourcePath;
        
        for (int i=0;i<ret.size();i++){
            current=ret.get(i);
            if (i==ret.size()-1){
                if (sourcePath.isLooped()){
                    next=ret.get(0);
                }else{
                    break;
                }
            }else{
                next=ret.get(i+1);
            }
            if ((current.getControl2()== null || current.getAncur().equals(current.getControl2()))
            && (next.getControl1()==null || next.getAncur().equals(next.getControl1()))){
                Point2D pi=lineIntersection(p0,p1,current.getAncur(),next.getAncur());
                if (pi !=null){
                    JSegment newSeg=new JSegment();
                    newSeg.setAncur(pi);
                    newSeg.setControl1(null);
                    newSeg.setControl2(null);
                    newSeg.setJoined(true);
                    ret.add(i+1,newSeg);
                    return i+1;
                }
            }else{
                p[0]=current.getAncur();
                Point2D pa=current.getControl2();
                if (pa==null) pa=current.getAncur();
                p[1]=pa;
                pa=next.getControl1();
                if (pa==null) pa=next.getAncur();
                p[2]=pa;
                p[3]=next.getAncur();
                JSolution[] js=cubicIntersection(p0,p1,p);
                if (js !=null){
                    JSegment newSeg=new JSegment();
                    newSeg.setJoined(true);
                    double t=js[0].t;
                    double x=js[0].p.getX();
                    double y=js[0].p.getY();
                    newSeg.setAncur(x,y);
                    Point2D.Double cp=new Point2D.Double();
                    //newPoint Control1;
                    cp.x=(1-t)*(1-t)*p[0].getX()+2*(1-t)*t*p[1].getX()+t*t*p[2].getX();
                    cp.y=(1-t)*(1-t)*p[0].getY()+2*(1-t)*t*p[1].getY()+t*t*p[2].getY();
                    if (cp.equals(newSeg.getAncur())){
                        newSeg.setControl1(null);
                    }else{
                        newSeg.setControl1(cp);
                    }
                    //newPoint Control2;
                    cp.x=(1-t)*(1-t)*p[1].getX()+2*(1-t)*t*p[2].getX()+t*t*p[3].getX();
                    cp.y=(1-t)*(1-t)*p[1].getY()+2*(1-t)*t*p[2].getY()+t*t*p[3].getY();
                    if (cp.equals(newSeg.getAncur())){
                        newSeg.setControl2(null);
                    }else{
                        newSeg.setControl2(cp);
                    }
                    //currentPoint Control2;
                    cp.x=(1-t)*p[0].getX()+t*p[1].getX();
                    cp.y=(1-t)*p[0].getY()+t*p[1].getY();
                    if (cp.equals(p[0])){
                        current.setControl2(null);
                    }else{
                        current.setControl2(cp);
                    }
                    //nextPoint control1;
                    cp.x=(1-t)*p[2].getX()+t*p[3].getX();
                    cp.y=(1-t)*p[2].getY()+t*p[3].getY();
                    if (cp.equals(p[3])){
                        next.setControl1(null);
                    }else{
                        next.setControl1(cp);
                    }
                    ret.add(i+1,newSeg);
                    return i+1;
                }
            }
        }
        return -1;
    }
    /**直線とパスが交差する場合パスを分割し、分割結果を返します.
     *
     */
    public static Vector<JSimplePath> cutPath(Point2D p0,Point2D p1,JSimplePath sourcePath){
        Vector<JSimplePath> ret=new Vector<JSimplePath>();
        JSegment current,next;
        Point2D[] p=new Point2D[4];
        JFlagedPath r=new JFlagedPath(sourcePath);
        current=r.get(0);
        int adding=-1;
        Point2D.Double cp=new Point2D.Double();
        current=r.get(0);
        for (int i=0;i<r.size();i++){
            JSimplePath resultPath=null;
            JSegment resultSeg=null;
            if (i==r.size()-1){
                if (r.isLooped()){
                    next=r.get(0);
                }else{
                    break;
                }
            }else{
                next=r.get(i+1);
            }
            //解を探す.
            if ((current.getControl2()== null || current.getAncur().equals(current.getControl2()))
            && (next.getControl1()==null || next.getAncur().equals(next.getControl1()))){
                Point2D pi=lineIntersection(p0,p1,current.getAncur(),next.getAncur());
                if (pi !=null){
                    JSegment newSeg=new JSegment();
                    newSeg.setAncur(pi);
                    newSeg.setControl1(null);
                    newSeg.setControl2(null);
                    newSeg.setJoined(true);
                    r.add(++i,newSeg);
                    r.setSelected(newSeg,true);
                }
            }else{
                p[0]=current.getAncur();
                Point2D pa=current.getControl2();
                if (pa==null) pa=current.getAncur();
                p[1]=pa;
                pa=next.getControl1();
                if (pa==null) pa=next.getAncur();
                p[2]=pa;
                p[3]=next.getAncur();
                JSolution[] js=cubicIntersection(p0,p1,p);
                Point2D cp0,cp1,cp2,cp3;
                if (js !=null){
                    double tp=0;
                    for (int j=0;j<js.length;j++){
                        cp0=current.getAncur();
                        cp1=current.getControl2();
                        if (cp1==null) cp1=current.getAncur();
                        cp2=next.getControl1();
                        if (cp2==null) cp2=next.getAncur();
                        cp3=next.getAncur();
                        double t=(js[j].t-tp)/(1-tp);
                        double x=js[j].p.getX();
                        double y=js[j].p.getY();
                        JSegment newSeg=new JSegment();
                        newSeg.setAncur(x,y);
                        //newPoint Control1;
                        cp.x=(1-t)*(1-t)*cp0.getX()+2*(1-t)*t*cp1.getX()+t*t*cp2.getX();
                        cp.y=(1-t)*(1-t)*cp0.getY()+2*(1-t)*t*cp1.getY()+t*t*cp2.getY();
                        if (cp.equals(newSeg.getAncur())){
                            newSeg.setControl1(null);
                        }else{
                            newSeg.setControl1(cp);
                        }
                        //newPoint Control2;
                        cp.x=(1-t)*(1-t)*cp1.getX()+2*(1-t)*t*cp2.getX()+t*t*cp3.getX();
                        cp.y=(1-t)*(1-t)*cp1.getY()+2*(1-t)*t*cp2.getY()+t*t*cp3.getY();
                        if (cp.equals(newSeg.getAncur())){
                            newSeg.setControl2(null);
                        }else{
                            newSeg.setControl2(cp);
                        }
                        //currentPoint Control2;
                        cp.x=(1-t)*cp0.getX()+t*cp1.getX();
                        cp.y=(1-t)*cp0.getY()+t*cp1.getY();
                        if (cp.equals(cp0)){
                            current.setControl2(null);
                        }else{
                            current.setControl2(cp);
                        }
                        //nextPoint control1;
                        cp.x=(1-t)*cp2.getX()+t*cp3.getX();
                        cp.y=(1-t)*cp2.getY()+t*cp3.getY();
                        if (cp.equals(cp3)){
                            next.setControl1(null);
                        }else{
                            next.setControl1(cp);
                        }
                        r.add(++i,newSeg);
                        r.setSelected(newSeg,true);
                        tp=t;
                        current=newSeg;
                    }
                }
            }
            current=next;
        }
        int sSize=r.getSelectionSize();
        if (sSize>0 && r.isLooped()){
            JFlagedPath result= new JFlagedPath();
            int idx=r.firstSelectionIndex();
            for (int i=0;i<r.size();i++){
                if (idx==r.size()) idx=0;
                result.add(r.get(idx));
                result.setSelected(i,r.isSelected(idx++));
            }
            result.setSelected(r.size()-1,false);
            result.add(result.get(0).clone());
            result.setSelected(0,false);
            result.setLooped(false);
            r=result;
        }
        if (sSize==0){
            ret.add(r.getSimplePath());
        }else{
            int idx=r.firstSelectionIndex();
            JSimplePath result=new JSimplePath();
            result.setLooped(false);
            for (int i=0;i<r.size();i++){
                JSegment seg=r.get(i);
                result.add(seg);
                if (r.isSelected(seg)){
                    result.setLooped(false);
                    ret.add(result);
                    result=new JSimplePath();
                    result.setLooped(false);
                    result.add(seg.clone());
                }
            }
            if (result.size()>1){
                ret.add(result);
            }
        }
        return ret;
    }
}
