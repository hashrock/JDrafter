/*
 * JPathTextObject.java
 *
 * Created on 2007/10/31, 14:31
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jobject;

import jobject.text.JParagraphIterator;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.font.*;
import java.awt.geom.*;
import java.text.AttributedCharacterIterator;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.text.*;
import javax.swing.undo.UndoableEdit;
import jedit.textobjectedit.JPathTextPositionChangeEdit;
import jgeom.*;
import jpaint.*;
import jscreen.JEnvironment;
import jscreen.JRequest;
import jpaint.JStroke;
import jobject.text.OnPathTextLocater;
import jobject.text.TextLocater;
/**
 *パスに沿ったテキストのレイアウト、表示を実行するオブジェクトです.
 *このオブジェクトは単一パスオブジェクトのみに対応しています.
 * @author T-IKITA
 */
public class JPathTextObject extends JPathObject implements JText,JColorable {
    /**テキストの属性及び文字を保持するDefaultStyledDocument*/
    private DefaultStyledDocument document;
    /**テキストの描画開始点の位置を表します.*/
    private float startPosition=0;
    private float previewPosition=0;
    private static final long serialVersionUID=110l;
    /**文字の開始点を制御するコントロールポイントです。
     *       □control1<br>
     *       │<br>
     *-------│TEXT<br>
     *       □control2<br>
     */
    private transient JSegment control1=null,control2=null;
    private transient GeneralPath lineShape=null;
    private transient GeneralPath previewShape=null;
    
    /** デフォルトの属性でJPathTextObjectを構築します. */
    public JPathTextObject() {
        super(JEnvironment.currentTextFill,JEnvironment.currentTextBorder,JEnvironment.currentTextStroke);
        document=new DefaultStyledDocument();
    }
    /**指定した塗り、線種及び線色によりJPathTextObjectを構築します.
     *@param fillpaint 塗りのpaint
     *@param strokepaint 線の色
     *@param stroke 線種
     */
    public JPathTextObject(JPaint fillpaint,JPaint strokepaint,JStroke stroke){
        super(fillpaint,strokepaint,stroke);
        document=new DefaultStyledDocument();
    }
    /***デバッグ用のコンストラクターです*/
    public JPathTextObject(String t){
        this();
        JPathIterator it=new JPathIterator(new Ellipse2D.Double(100,100,100,100).getPathIterator(null));
        this.setPath(it.getJPath());
        SimpleAttributeSet attr=new SimpleAttributeSet();
        try{
            document.insertString(0,t,attr);
        }catch(Exception ex){}
        setStartPosition(0.2f);
    }
    /**パスの長さの近似値を返します.
     *@param path 長さを測定するJSimplePath
     *@param flatness パスの平滑化係数
     *@return pathの長さ
     */
    public static float getPathLength(JSimplePath path,float flatness){
        float ret=0f;
        PathIterator pt=path.getShape(PathIterator.WIND_NON_ZERO).getPathIterator(null,flatness);
        float[] coords=new float[6];
        float ox=0,oy=0,bx=0,by=0;
        while (!pt.isDone()){
            int type=pt.currentSegment(coords);
            if (type==PathIterator.SEG_MOVETO){
                bx=ox=coords[0];
                by=oy=coords[1];
            }
            if (type==PathIterator.SEG_LINETO){
                float dx=coords[0]-ox;
                float dy=coords[1]-oy;
                ret +=(float)Math.sqrt(dx*dx+dy*dy);
                ox=coords[0];
                oy=coords[1];
            }
            if (type==PathIterator.SEG_CLOSE){
                float dx=bx-ox;
                float dy=by-oy;
                ret +=(float)Math.sqrt(dx*dx+dy*dy);
                ox=coords[0];
                oy=coords[1];
            }
            pt.next();
        }
        return ret;
    }
    /**パスを指定した相対位置で分割し、結果を頂点を要素とするVectorを返します.
     *@param path 分割するPath
     *@param rpos  分割相対位置
     *@param flatness パスの平滑化係数
     *@return 分割結果のパスの頂点を要素とするVector
     */
    public static Vector<Point2D> dividePathRelatively(JSimplePath path,float rpos,float flatness){
        float pos=getPathLength(path,flatness)*rpos;
        return dividePath(path,pos,flatness);
        
    }
    
    /**パスを指定位置で分割し、結果を頂点を要素とするVectorで返します.
     *@param path　分割するパス
     *@param pos 分割するパスの始点からの絶対位置
     *@param flatness パスの平滑化係数.
     *@return 分割結果のパスの頂点を要素とするVector
     */
    public static Vector<Point2D> dividePath(JSimplePath path,float pos,float flatness){
        Vector<Point2D> result=new Vector<Point2D>(1);
        Vector<Point2D> saved=new Vector<Point2D>(1);
        PathIterator pt=path.getShape(PathIterator.WIND_NON_ZERO).getPathIterator(null,flatness);
        float[] coords=new float[6];
        float sx=0,sy=0,px=0,py=0;
        boolean isStarted=false;
        boolean isLooped=false;
        while (!pt.isDone()){
            int type=pt.currentSegment(coords);
            if (type==PathIterator.SEG_MOVETO){
                sx=px=coords[0];
                sy=py=coords[1];
                saved.add(new Point2D.Float(px,py));
            }
            if (type==PathIterator.SEG_LINETO){
                if (! isStarted){
                    float dx=coords[0]-px;
                    float dy=coords[1]-py;
                    if (dx==0 && dy==0){
                        pt.next();
                        continue;
                    }
                    float dst=(float)Math.sqrt(dx*dx+dy*dy);
                    if (dst>pos){
                        float x=px+dx*pos/dst;
                        float y=py+dy*pos/dst;
                        result.add(new Point2D.Float(x,y));
                        saved.add(new Point2D.Float(x,y));
                        result.add(new Point2D.Float(coords[0],coords[1]));
                        px=coords[0];
                        py=coords[1];
                        isStarted=true;
                    }else{
                        pos -=dst;
                        saved.add(new Point2D.Float(coords[0],coords[1]));
                        px=coords[0];
                        py=coords[1];
                    }
                } else{
                    if (px!=coords[0] || py!=coords[1]){
                        result.add(new Point2D.Float(coords[0],coords[1]));
                    }
                    px=coords[0];
                    py=coords[1];
                }
            }
            if (type==PathIterator.SEG_CLOSE){
                isLooped=true;
                if (px!=sx || py!=sy){
                    result.add(new Point2D.Float(sx,sy));
                }
            }
            pt.next();
        }
        if (isLooped){
            for (int i=0;i<saved.size();i++){
                result.add(saved.get(i));
            }
        }
        Point2D prep=null;
        for (int i=0;i< result.size();i++){
            if (prep==null){
                prep=result.get(i);
            }else{
                if (prep.equals(result.get(i))){
                    result.remove(i--);
                }else{
                    prep=result.get(i);
                }
                
            }           
        }
        return result;
    }
    /**多角形の頂点を要素とするVectorからテキスト始点移動用のコントロールの位置を決定します.
     *@param poly 多角形の頂点を要素とするVector
     *@retrurn 位置決定成功の場合true,それ以外はfalse
     */
    private boolean setConrolPos(Vector<Point2D> poly,FontRenderContext frc){
        if (poly.size()<2) return false;
        Point2D sp=poly.get(0);
        Point2D ep=null;
        for (int i=1;i<poly.size();i++){
            if (!poly.get(i).equals(sp)){
                ep=poly.get(i);
                break;
            }
        }
        if (ep==null) return false;
        LineMetrics fm=document.getFont(document.getCharacterElement(0).getAttributes()).getLineMetrics(" ",frc);
        float dx=(float)(ep.getX()-sp.getX()),dy=(float)(ep.getY()-sp.getY());
        float dst=(float)(Math.sqrt(dx*dx+dy*dy));
        if (control1==null){
            control1=new JSegment();
            control2=new JSegment();
        }
        control1.setAncur(sp.getX()+dy*fm.getAscent()/dst,sp.getY()-dx*fm.getAscent()/dst);
        control2.setAncur(sp.getX()-dy*fm.getDescent()/dst,sp.getY()+dx*fm.getDescent()/dst);
        return true;
    }
    /**指定する点に最も近いPath上の点を探索し、当該パス上の点のパス始点からの相対距離を返します.
     *
     *@param path 分割対象となるPath
     *@param x 指定点のX座標
     *@param y 指定点のY座標
     *@param flatness パスの平滑化係数
     *@return パス分割点のパス始点からの距離
     */
    public static float dividePathPt(JSimplePath path,float x,float y,float flatness){
        float minLen=Float.MAX_VALUE;
        int idx=-1;
        PathIterator pt=path.getShape(PathIterator.WIND_NON_ZERO).getPathIterator(null,flatness);
        float[] coords=new float[6];
        float preX=0,preY=0,sX=0,sY=0;
        float totalDist=0;
        float cDist=0;
        while (!pt.isDone()){
            int type=pt.currentSegment(coords);
            if (type==pt.SEG_MOVETO){
                sX=preX=coords[0];
                sY=preY=coords[1];
            }
            if (type==pt.SEG_LINETO){
                float d=(float)Line2D.ptSegDist(preX,preY,coords[0],coords[1],x,y);
                if (d<minLen){
                    cDist=totalDist+perpendDistance(preX,preY,coords[0],coords[1],x,y);
                    minLen=d;
                }
                totalDist+=getDistance(preX,preY,coords[0],coords[1]);
                preX=coords[0];
                preY=coords[1];
            }
            if (type==pt.SEG_CLOSE){
                if (preX !=sX || preY != sY){
                    float d=(float)Line2D.ptSegDist(preX,preY,sX,sY,x,y);
                    if (d<minLen){
                        cDist=totalDist+perpendDistance(preX,preY,sX,sY,x,y);
                        minLen=d;
                    }
                    totalDist+=getDistance(preX,preY,sX,sY);
                }
            }
            pt.next();
        }
        if (totalDist==0) return 0;
        return (cDist/totalDist);
    }
    /**線分と指定した点を通る垂線の交点の座標を返します。交点が線分上にない場合は、直近の線分の始点
     *または、終点の座標を返します.
     *@param x0 線分の始点のX座標
     *@param y0 線分の始点のy座標
     *@param x1 線分の終点のx座標
     *@param y1 線分の終点のy座標
     *@param x 垂線が通る点のx座標
     *@param y 垂線が通る点のy座標
     *@return 交点の座標
     */
    public static Point2D perpendIntersection(float x0,float y0,float x1,float y1,float x,float y){
        float dx=x1-x0,dy=y1-y0,dx1=x-x0,dy1=y-y0;
        float dst=dx*dx+dy*dy;//距離^2
        float inp=dx*dx1+dy*dy1;//内積
        float px=dx*inp/dst;
        float py=dy*inp/dst;
        float di=(px*dx+py*dy)/dst;
        if (di>=1 ) return new Point.Float(x1,y1);
        if (di<=0) return new Point.Float(x0,y0);
        return new Point.Float(px+x0,py+y0);
    }
    public static Point2D perpendInterSection(Point2D p0,Point2D p1,Point2D p){
        return perpendIntersection(
                (float)p0.getX(),(float)p0.getY(),
                (float)p1.getX(),(float)p1.getY(),
                (float)p.getX(),(float)p.getY());
    }
    /**2点間の距離を返します.
     *@param x0 点0のx座標
     *@param y0 点0のy座標
     *@param x1 点1のx座標
     *@param y1 点1のｙ座標
     *@return 2点間の距離
     */
    public static float getDistance(float x0,float y0,float x1,float y1){
        float dx=x1-x0,dy=y1-y0;
        return (float)Math.sqrt(dx*dx+dy*dy);
    }
    /**
     * 指定する2点間の距離を返します.
     * @param p0 指定する1番目のPoint2D
     * @param p1 指定する
     * @return 2点間の距離
     */
    public static float getDistance(Point2D p0,Point2D p1){
        return getDistance(
                (float)p0.getX(),(float)p0.getY(),
                (float)p1.getX(),(float)p1.getY());
        
    }
    /**線分と指定点を通る垂線の交点の線分の始点からの距離を返します.
     */
    private static float perpendDistance(float x0,float y0,float x1,float y1,float x,float y){
        Point2D p=perpendIntersection(x0,y0,x1,y1,x,y);
        return getDistance(x0,y0,(float)p.getX(),(float)p.getY());
    }
    /**属性付きテキストをパス上に配置し、結果のテキストアウトラインをGeneralPathとして返します.
     *@param poly 配置するパスの各頂点を要素とするVector
     *@param doc 配置する属性付テキストが格納されたDefaultStyledDocument
     *@param frc 描画のためのFontRenderContext
     *@return テキストの配置結果が格納されたStyledDocument;
     */
    public GeneralPath createLineShape(Vector<Point2D> poly,DefaultStyledDocument doc,FontRenderContext frc){
        GeneralPath ret=new GeneralPath();        
        Iterator<Point2D> it= poly.iterator();
        //
        if (frc==null){
            frc=new FontRenderContext(null,true,true);
        }
        //
        if (!it.hasNext()) return ret;
        Point2D preP=it.next();
        Point2D cP=new Point2D.Float();
        cP.setLocation(preP);
        if (!it.hasNext()) return ret;
        Point2D nP=it.next();
        JParagraphIterator pit=new JParagraphIterator(document);
        AttributedCharacterIterator cit=pit.first();
        AffineTransform af=new AffineTransform();
        char[] ch=new char[1];
        outer:while (cit != null){
            char c=cit.first();
            while (c !=cit.DONE){
                ch[0]=c;
                
                TextLayout layout=new TextLayout(new String(ch),cit.getAttributes(),frc);
                float dst=layout.getAdvance();
                float theta=(float)(Math.atan2(nP.getY()-preP.getY(),nP.getX()-preP.getX()));
                af.setToTranslation(cP.getX(),cP.getY());
                af.rotate(theta);
                ret.append(layout.getOutline(af),false);
                float X=(float)(nP.getX()-cP.getX()),Y=(float)(nP.getY()-cP.getY());
                float pdst=(float)Math.sqrt(X*X+Y*Y);
                while (dst>pdst){
                    if (!it.hasNext()) break outer;
                    preP=nP;
                    nP=it.next();
                    dst-=pdst;
                    cP.setLocation(preP);
                    X=(float)(nP.getX()-cP.getX());Y=(float)(nP.getY()-cP.getY());
                    pdst=(float)Math.sqrt(X*X+Y*Y);
                    
                }
                float t=dst/pdst;
                cP.setLocation((nP.getX()-cP.getX())*t+cP.getX(),(nP.getY()-cP.getY())*t+cP.getY());
                c=cit.next();
            }
            cit=pit.next();
        }
        return ret;
    }
    /**テキストのスタートポジションの相対位置を指定します。
     * @param sp 指定するテキスト開始位置の相対位置(0fから1f)
     */
    public void setStartPosition(float sp){
        startPosition=previewPosition=sp;
        previewShape=null;
        lineShape=null;
    }
    /**
     * 現在のテキストのスタートポジションの位置を返します。
     * @return 現在のテキストの開始位置(0fから1f)
     */
    public float getStartPosition(){
        return startPosition;
    }
    /**
     * Point2Dで指定する位置にヒットする、このオブジェクトの部分を返します.
     * @param env 有効な環境を格納するJEnvironmentオブジェクト
     * @param req 現在の選択状態を格納するJRequestオブジェクト
     * @param p ヒットを検査する位置.
     * @return 検証結果を示すint(JRequest.HIT_NON,JRequest.HIT_OBJECT又はJRequest.HIT_ANCUR)
     */
    @Override
    public int hitByPoint(JEnvironment env,JRequest req,Point2D p){
        if (isLocked() || !isVisible()) return JRequest.HIT_NON;
        if (req.getSelectionMode()==JRequest.DIRECT_MODE && control1 !=null ){
            double radius=JEnvironment.PATH_SELECTOR_SIZE;
            Rectangle2D.Double rect=new Rectangle2D.Double(0,0,radius,radius);
            rect.x=control1.getAncur().getX()-radius/2;
            rect.y=control1.getAncur().getY()-radius/2;
            if (rect.contains(p)){
                req.hitResult=JRequest.HIT_ANCUR;
                req.hitObjects.add(control1);
                req.hitObjects.add(this);
                return JRequest.HIT_ANCUR;
            }
        }
        int ret=super.hitByPoint(env,req,p);
        if (ret==JRequest.HIT_OBJECT){
            ret=req.hitResult=JRequest.HIT_NON;
            req.hitObjects.clear();          
        }
        if (ret==JRequest.HIT_NON){
            double rad=env.getToScreenRatio();
            Rectangle2D r=new Rectangle2D.Double(p.getX()-rad,p.getY()-rad,rad*2,rad*2);
            if (lineShape !=null){
                if (lineShape.intersects(r)){
                    ret=req.hitResult=JRequest.HIT_OBJECT;
                    req.hitObjects.add(this);
                }
            }
        }
        return ret;
    }
    /**
     * このオブジェクトが指定するRectangle2Dに交差する場合は、交差するこのオブジェクトの部分を
     * 指定するJRequestに格納します.
     * @param env 有効な環境を格納するJEnvironmentオブジェクト
     * @param req 現在の選択状態及び交差判定結果を格納するJRequestオブジェクト.
     * @param rect 交差判定を行うRectangle2D
     */
    @Override
    public void hitByRect(JEnvironment env, JRequest req, Rectangle2D rect) {
        if (isLocked() || !isVisible()) return;
        for (int i=0;i<getPath().segmentSize();i++){
            JSegment seg=getPath().getSegment(i);
            if (rect.contains(seg.getAncur())){
                if (req.getSelectionMode()==JRequest.GROUP_MODE){
                    req.hitObjects.add(this);
                    return;
                }else{
                    req.hitObjects.add(seg);
                    JSimplePath spath=getPath().getOwnerPath(seg);
                    if(!req.hitObjects.contains(spath))
                        req.hitObjects.add(getPath().getOwnerPath(seg));
                    if (!req.hitObjects.contains(this))
                        req.hitObjects.add(this);
                }
                
            }
        }
        
        BasicStroke sStroke=new BasicStroke((float)(JEnvironment.SELECTION_STROKE_SIZE/env.getToScreenRatio()));
        for (int i=0;i<getPath().size();i++){
            Shape ss=sStroke.createStrokedShape(getPath().get(i).getShape(getPath().getWindingRule()));
            if (ss.intersects(rect)){
                req.hitObjects.add(getPath().get(i));
                if (!req.hitObjects.contains(this))
                    req.hitObjects.add(this);
            }
        }
        if (req.hitObjects.contains(this)) return;
        if (lineShape != null && lineShape.intersects(rect))
            req.hitObjects.add(this);
        
    }
    /**
     * このオブジェクトを描画します.
     * @param clip 描画のクリッピングバウンディングボックス
     * @param g 描画のグラフィックスコンテキスト
     */
    @Override
    public void paintThis(Rectangle2D clip,Graphics2D g){
        if (!clip.intersects(getBounds())) return;
        Vector<Point2D> poly=null;
        if (lineShape==null){
            poly=dividePathRelatively(getPath().get(0),startPosition,0.01f);
            lineShape=createLineShape(poly,document,null);
        }
        TextLocater locater=createLocater(null);
        effector.paintText(g, locater,null, fillPaint, strokePaint, stroke);
        //effector.paint(g,lineShape,fillPaint,strokePaint,stroke);
    }
    /**
     * このオブジェクトのプレビューを描画します。
     * @param env 現在の描画環境を示すJEnvionment
     * @param req 選択状態を格納するJRequest
     * @param g 描画のグラフィックスコンテキスト
     */
    @Override
    public void paintPreview(JEnvironment env,JRequest req,Graphics2D g){
        super.paintPreview(env,req,g);
        if (previewShape==null){
            JSimplePath apath=getTransformedPath(req).get(0);
            Vector<Point2D> poly=dividePathRelatively(apath,previewPosition,0.01f);
            setConrolPos(poly,g.getFontRenderContext());
            previewShape=createLineShape(poly,document,null);
        }
        AffineTransform af=env.getToScreenTransform();
        
        
        if (getTransform() !=null || previewPosition !=startPosition)
            g.draw(af.createTransformedShape(previewShape));
        if (req.getSelectionMode()==JRequest.GROUP_MODE) return;
        double radius=JEnvironment.PATH_SELECTOR_SIZE;
        Rectangle2D.Double fr=new Rectangle2D.Double(0,0,radius,radius);
        if (control1==null) return;
        Point2D.Double p=new Point2D.Double(),p1=new Point2D.Double();
        af.transform(control1.getAncur(),p);
        af.transform(control2.getAncur(),p1);
        Line2D.Double line= new Line2D.Double(p,p1);
        g.draw(line);
        fr.x=p.x-radius/2;
        fr.y=p.y-radius/2;
        g.fill(fr);
    }
    /**
     * 加えられたアフィン変換をオブジェクトに適用し、変換動作を示すUndoaleEditを返します.
     * @param env 現在の描画環境を示すJEnvironment
     * @return 加えられた変換動作を示すUndoableEdit
     */
    @Override
    public UndoableEdit updateTransform(JEnvironment env){
        env.addClip(getBounds());
        lineShape=null;
        previewShape=null;
        UndoableEdit ret;
        if (startPosition !=previewPosition){
            ret=new JPathTextPositionChangeEdit(getDocument().getViewer(),this,previewPosition);
        } else{
            ret= super.updateTransform(env);
        }
        env.addClip(getBounds());
        return ret;
        
    }
    /**
     * 加えられた回転変換を含むアフィン変換をこのオブジェクトに適用し、変換動作を示すUndoableEditを返します.
     * @param env 現在の描画環境を示すJEnvironment
     * @param rotation 加えられたアフィン変換の回転移動要素の回転角
     * @return 加えられた変換動作を示すUndoableEdit
     */
    @Override
    public UndoableEdit updateRotate(JEnvironment env,double rotation){
        env.addClip(getBounds());
        lineShape=null;
        previewShape=null;
        UndoableEdit ret= super.updateRotate(env,rotation);
        env.addClip(getBounds());
        return ret;
        
    }
    /**
     * このオブジェクトに指定するAffine変換を適用し、プレビュー状態を更新します.
     * @param tr このオブジェクトに加えるアフィン変換
     * @param req 現在の選択状態を格納するJRequestオブジェクト
     * @param mp 変換の基準点
     */
    @Override
    public void transform(AffineTransform tr,JRequest req,Point  mp) {
        previewShape=null;
        lineShape=null;
        if (req.getSelectionMode()==JRequest.DIRECT_MODE && req.hitResult==JRequest.HIT_ANCUR && req.hitObjects.contains(control1)){
            Point2D p=new Point2D.Double();
            getDocument().getViewer().getEnvironment().getToAbsoluteTransform().transform(mp,p);
            previewPosition=dividePathPt(getPath().get(0),(float) p.getX(),(float) p.getY(),0.01f);
        }else{
            super.transform(tr,req,mp);
        }
    }
    /**
     * このオブジェクトの描画範囲を包含する最小のRecntangle2Dを返します.
     * @return このオブジェクトの描画範囲を包含する最小のRectangle2D
     */
    @Override
    public Rectangle2D getBounds(){
        Rectangle2D ret=super.getBounds();
        if (lineShape==null ){
            Vector<Point2D> poly=dividePathRelatively(getPath().get(0),startPosition,0.01f);
            lineShape=createLineShape(poly,document,null);
        }
        Rectangle2D r=lineShape.getBounds2D();
        if (strokePaint!=null){
            double rad=stroke.getWidth();
            if (stroke.getLineJoin()==BasicStroke.JOIN_MITER){
                rad+=stroke.getMiterLimit();
            }
            r.setFrame(r.getX()-rad/2,r.getY()-rad/2,r.getWidth()+rad,r.getHeight()+rad);
        }
        if (!r.isEmpty()) ret.add(r);
        return effector.culcBounds(ret,this);
    }
    /**
     * このオブジェクトを複製します.
     * @return 複製したJPathTextObject
     */
    @Override
    public JPathTextObject clone(){
        JPaint fp=null,sp=null;
        if (fillPaint!=null)
            fp=fillPaint.clone();
        if (strokePaint!=null)
            sp=strokePaint.clone();
        JPathTextObject ret=new JPathTextObject(fp,sp,stroke);
        ret.setPath(this.getPath().clone());
        ret.document=getCloneStyledDocument();
        ret.setStartPosition(getStartPosition());
        ret.totalRotation=totalRotation;
        ret.setEffector(getEffector());
        return ret;
    }
    /**
     * このオブジェクトの書式付テキストを保持するStyledDocumentを返します.
     * @return このオブジェクトの書式付テキストを保持するStyledDocument
     */
    @Override
    public DefaultStyledDocument getStyledDocument() {
        return document;
    }
    /**
     * このオブジェクトの書式付テキストを保持するStyledDocumentの複製を返します.
     * @return このオブジェクトの書式付テキストの複製.
     */
    @Override
    public DefaultStyledDocument getCloneStyledDocument() {
        return JTextObject.cloneDocument(document);
    }
    /**
     * このオブジェクトの書式付テキストを保持するStyledDocumentを設定します。
     * @param doc　指定するStyledDocument
     */
    @Override
    public void setStyledDocument(DefaultStyledDocument doc) {
        this.document=doc;
    }
    /**
     * 変更を更新するため、このオブジェクトのテキストのアウトラインを保持するShapeを更新します。
     */
    @Override
    public void updatePath(){
        lineShape=null;
        previewShape=null;
        getDocument().getEnvironment().addClip(getBounds());
    }

    /**
   * テキストの変更を更新するため、このオブジェクトを描画クリップに加えます.
   * @param env 現在の描画環境を格納する JEnvironment
   */
    @Override
    public void textUpdate(JEnvironment env) {
        env.addClip(getBounds());
        updatePath();
        env.addClip(getBounds());
    }
    /**
     * このテキストのアウトラインを保持するShapeオブジェクトを返します.
     * @return テキストのアウトラインを保持するShapeオブジェクト
     */
    @Override
    public Shape getShape(){
        return lineShape;
    }
    /**
     * このオブジェクトに累積されたアフィン変換を返します.
     * @return このオブジェクトに累積されたアフィン変換
     */
    @Override
    public AffineTransform getTotalTransform() {
        return new AffineTransform();
    }
    /**
     * このオブジェクトのレイアウトポリシーを保持するTextLocaterオブジェクトを返します。
     * @return このオブジェクトのレイアウトポリシーを保持するTextLocater
     */
    @Override
    public TextLocater createLocater(FontRenderContext frc) {
        if (frc==null){
            frc=new FontRenderContext(null,true,true);
        }
        return new OnPathTextLocater(getStyledDocument(),this,frc);
    }
    /**
     * このオブジェクトのレイアウトの基準となるShapeオブジェクトを返します.
     * @return このオブジェクトのレイアウトの基準となるShapeオブジェクト
     */
    @Override
    public Shape getLayoutShape() {
        return this.getPath().getShape();
    }
    /**
     * このオブジェクトの自動命名字の冠詞を返します。
     * @return　このオブジェクトを自動命名する際の冠詞
     */
    @Override
    public String getPrefixer(){
        return "Text";
    }
   
}
