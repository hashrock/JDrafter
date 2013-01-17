/*
 * JTextObject.java
 *
 * Created on 2007/10/23, 15:41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jobject;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import javax.swing.text.*;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import jedit.*;
import jedit.textobjectedit.JRotateTextBoxEdit;
import jedit.textobjectedit.JTransformTextBoxEdit;
import jscreen.*;
import jobject.text.TextLocater;

/**
 *
 * @author i002060
 */
public class JTextObject extends JLeaf<JObject> implements JText,JColorable{
    private static final long serialVersionUID=110l;
    private DefaultStyledDocument document=null;
    private AffineTransform totalTransform=null;
    private transient GeneralPath lineShape=null;
    //private transient GeneralPath previewShape=null;
    private transient TextLocater locater=null;
    //values for preview;
    private transient AffineTransform addingTransform=null;
    private static TabSet defaultTabset=null;
    //
    private transient double width=0,height=0;
    /**
     * Creates a new instance of JTextObject
     */
    public JTextObject(Point2D startAt,DefaultStyledDocument doc) {
        document=doc;
        fillPaint=JEnvironment.currentTextFill;
        strokePaint=JEnvironment.currentTextBorder;
        stroke=JEnvironment.currentTextStroke;
        totalTransform=new AffineTransform();
        addingTransform=null;
        totalTransform.setToTranslation(startAt.getX(),startAt.getY());
    }
    public JTextObject(){
        this(new Point2D.Double(),new DefaultStyledDocument());
    }
    /**constructor for debug*/
    public JTextObject(Point2D startAt,String text){
        this(startAt,new DefaultStyledDocument());
        if (text.charAt(text.length()-1)!='\n')
            text+="\n";
        try{
            AttributeSet sAttr=document.getCharacterElement(0).getAttributes();
            document.insertString(0,text,sAttr);
        }catch (BadLocationException e){
            e.printStackTrace();
        }
    }
    public GeneralPath createLineShape(DefaultStyledDocument doc,FontRenderContext frc,boolean createBaseLine){
        if (frc==null){
            frc=getFontRenderContext();
        }
         locater=createLocater(frc);
        GeneralPath ret=locater.getOutlineShape();
        Rectangle2D r=ret.getBounds2D();
        width=locater.getWidth();
        height=locater.getHeight();
        return ret;
    }
    public static FontRenderContext getFontRenderContext(){
        AffineTransform tx=new AffineTransform();
        double scale=1;//JEnvironment.screenDPI/72;
        tx.setToScale(scale, scale);
        return new FontRenderContext(tx,true,true);
    }
    public static TabSet getDefaultTabset(){
        if (defaultTabset !=null) return defaultTabset;
        TabStop[] tabs=new TabStop[50];
        for (int i=0;i<50;i++){
            tabs[i]=new TabStop(72f*(i+1));
        }
        defaultTabset=new TabSet(tabs);
        return defaultTabset;
    }
    //private Point prevPoint=null;
    @Override
    public void transform(AffineTransform tr, JRequest req,Point p) {
        addingTransform =(AffineTransform)tr.clone();
    }
    @Override
    public void transform(AffineTransform tr){
        addTransform(tr);
        if (fillPaint != null && (fillPaint.getPaintMode()==jpaint.JPaint.LINEAR_GRADIENT_MODE  || fillPaint.getPaintMode()==jpaint.JPaint.RADIAL_GRADIENT_MODE))
        {
            fillPaint.transform(tr);
        }
    }
    @Override
    public int hitByPoint(JEnvironment env, JRequest req, Point2D point) {
        if (isLocked() || !isVisible()) return JRequest.HIT_NON;
        Shape s=getSelectionShape();
        double radius=(JEnvironment.PATH_SELECTOR_SIZE/2)/env.getToScreenRatio();
        Rectangle2D.Double sr=new Rectangle2D.Double(0,0,radius*2,radius*2);
        PathIterator pt=s.getPathIterator(null);
        double[] coords=new double[6];
        while (!pt.isDone()){
            int type=pt.currentSegment(coords);
            if (type==pt.SEG_LINETO || type==pt.SEG_MOVETO){
                sr.x=coords[0]-radius;
                sr.y=coords[1]-radius;
                if (sr.contains(point)){
                    req.hitObjects.add(this);
                    return (req.hitResult=JRequest.HIT_OBJECT);
                }
            }
            pt.next();
        }
        if (lineShape !=null){
            double rad=env.getToScreenRatio();
            Rectangle2D rc=new Rectangle2D.Double(point.getX()-rad,point.getY()-rad,rad*2,rad*2);
            if (totalTransform.createTransformedShape(lineShape).intersects(rc)){
                req.hitResult=JRequest.HIT_OBJECT;
                req.hitObjects.add(this);
                return JRequest.HIT_OBJECT;
            }
        }
        return JRequest.HIT_NON;
    }
    
    @Override
    public void hitByRect(JEnvironment env, JRequest req, Rectangle2D rect) {
        if (isLocked() || !isVisible()) return;
        if (lineShape==null) return;
        if (getSelectionShape().intersects(rect)){
            req.hitResult=JRequest.HIT_OBJECT;
            req.hitObjects.add(this);
            return;
        }
        return;
    }
    public void addTransform(AffineTransform af){
        if (totalTransform==null){
            totalTransform=new AffineTransform();
            totalTransform.setToIdentity();
        }
        totalTransform.preConcatenate(af);
        lineShape=null;
    }
    @Override
    public UndoableEdit updateTransform(JEnvironment env) {
        JDocumentViewer viewer=getDocument().getViewer();
        JRequest req=viewer.getCurrentRequest();
        CompoundEdit cEdit=new CompoundEdit();
        if (req.isAltDown && req.getSelectionMode()==JRequest.GROUP_MODE && addingTransform.getType()==AffineTransform.TYPE_TRANSLATION){
            cEdit.addEdit(new JDuplicateObjectEdit(viewer,this));
        }
        UndoableEdit ret;
        ret=new JTransformTextBoxEdit(viewer,this,addingTransform);
        cEdit.addEdit(ret);
        cEdit.end();
        addingTransform=null;
        return cEdit;
    }
    
    @Override
    public UndoableEdit updateRotate(JEnvironment env, double rotation) {
        JDocumentViewer viewer=getDocument().getViewer();
        UndoableEdit ret=new JRotateTextBoxEdit(viewer,this,addingTransform,rotation);
        addingTransform=null;
        return ret;
    }
    
    @Override
    public void paintThis(Rectangle2D clip, Graphics2D g) {
        if (!getBounds().intersects(clip)) return;
        if (lineShape==null || locater ==null) {
            lineShape=createLineShape(document,new FontRenderContext(null,true,true),false);
        }
        //if (! this.getBounds().intersects(env.getClip())) return;
        AffineTransform af=new AffineTransform(totalTransform);
        effector.paintText(g, locater, af, fillPaint, strokePaint, stroke);
    }
    
    @Override
    public void paintPreview(JEnvironment env, JRequest req, Graphics2D g) {
        g.setColor(getPreviewColor());
        AffineTransform af=new AffineTransform();
        if (lineShape==null) lineShape=createLineShape(document,null,false);
        if (addingTransform !=null){
            af.setTransform(addingTransform);
        }
        af.preConcatenate(env.getToScreenTransform());
        double radius=JEnvironment.PATH_SELECTOR_SIZE/2;
        Rectangle2D.Double sr=new Rectangle2D.Double(0,0,radius*2,radius*2);
        Shape s=af.createTransformedShape(getSelectionShape());
        g.draw(s);
        PathIterator pt=s.getPathIterator(null);
        double[] coords=new double[6];
        while (!pt.isDone()){
            int type=pt.currentSegment(coords);
            if (type==pt.SEG_MOVETO || type==pt.SEG_LINETO){
                sr.x=coords[0]-radius;
                sr.y=coords[1]-radius;
                g.fill(sr);
            }
            pt.next();
        }
        if (locater == null){
            locater=createLocater(null);
        }
        af.setTransform(totalTransform);
        if (addingTransform !=null)
            af.preConcatenate(addingTransform);
        af.preConcatenate(env.getToScreenTransform());
        Graphics2D gt=(Graphics2D)g.create();
        gt.transform(af);
        locater.drawBaseLine(gt);
        if (addingTransform !=null)
            locater.draw(gt);
        gt.dispose();
    }
    @Override
    public Rectangle2D getSelectionBounds() {
        if (lineShape==null) lineShape=createLineShape(document,null,false);
        double w=width;
        double h=height;
        if (w<0.0001) w=0.001;
        if (h<0.0001) h=0.001;
        Rectangle2D.Double r=new Rectangle2D.Double(0,0,w,h);
        AffineTransform af =new AffineTransform(totalTransform);
        return af.createTransformedShape(r).getBounds2D();
    }
    private Shape getSelectionShape(){
        if (lineShape==null) lineShape=createLineShape(document,null,false);
        double w=width;
        double h=height;
        if (w<0.0001) w=0.001;
        if (h<0.0001) h=0.001;
        Rectangle2D.Double r=new Rectangle2D.Double(0,0,w,h);
        AffineTransform af =new AffineTransform(totalTransform);
        return af.createTransformedShape(r);
    }
    @Override
    public Rectangle2D getBounds() {
        //lineShape=createLineShape(document,null,false);
        if (lineShape==null || width==0 || height==0) {
            lineShape=createLineShape(document,null,false);
        }
        double w=width;
        double h=height;
        if (w<0.0001) w=0.001;
        if (h<0.0001) h=0.001;
        Rectangle2D ret=new Rectangle2D.Double(0,0,w,h);
        ret=totalTransform.createTransformedShape(ret).getBounds();
        if (strokePaint !=null){
            double rad=stroke.getWidth();
            if (stroke.getLineJoin()==BasicStroke.JOIN_MITER){
                rad+=stroke.getMiterLimit();
            }
            ret.setFrame(ret.getX()-rad/2,ret.getY()-rad/2,ret.getWidth()+rad,ret.getHeight()+rad);
        }
        return effector.culcBounds(ret,this);
    }
    
    @Override
    public Rectangle2D getOriginalSelectionBounds(double x, double y) {
        if (lineShape==null) lineShape=createLineShape(document,null,false);
        double w=width;
        double h=height;
        if (w<0.0001) w=0.001;
        if (h<0.0001) h=0.001;
        Rectangle2D.Double r=new Rectangle2D.Double(0,0,w,h);
        AffineTransform af =new AffineTransform();
        af.rotate(-totalRotation,x,y);
        af.concatenate(totalTransform);
        return af.createTransformedShape(r).getBounds2D();
    }
    public static DefaultStyledDocument cloneDocument(DefaultStyledDocument doc){
        Element celm,pelm;
        int pos=0;
        celm=doc.getCharacterElement(pos);
        pelm=doc.getParagraphElement(0);
        DefaultStyledDocument ret=new DefaultStyledDocument();
        SimpleAttributeSet satr=new SimpleAttributeSet();
        try{
            ret.insertString(0,doc.getText(0,doc.getLength()),satr);
        } catch(BadLocationException e){
        }
        ret.setParagraphAttributes(pelm.getStartOffset(),pelm.getEndOffset()-pelm.getStartOffset()+1,pelm.getAttributes().copyAttributes(),true);
        ret.setCharacterAttributes(celm.getStartOffset(),celm.getEndOffset()-celm.getStartOffset()+1,celm.getAttributes().copyAttributes(),true);
        for (int i=1;i<doc.getLength();i++){
            Element cpelm=doc.getParagraphElement(i);
            Element ccelm=doc.getCharacterElement(i);
            if (cpelm != pelm){
                ret.setParagraphAttributes(cpelm.getStartOffset(),cpelm.getEndOffset()-cpelm.getStartOffset()+1,cpelm.getAttributes().copyAttributes(),true);
            }
            if (ccelm !=celm){
                ret.setCharacterAttributes(ccelm.getStartOffset(),ccelm.getEndOffset()-ccelm.getStartOffset()+1,ccelm.getAttributes().copyAttributes(),true);
            }
            celm=ccelm;
            cpelm=pelm;
        }
        return ret;
    }
    
    @Override
    public Object clone() {
        JTextObject ret=new JTextObject(new Point2D.Double(0,0),cloneDocument(document));
        ret.totalTransform=(AffineTransform)totalTransform.clone();
        ret.totalRotation=totalRotation;
        if (fillPaint !=null)
            ret.fillPaint=fillPaint.clone();
        else
            ret.fillPaint=null;
        if (strokePaint!=null)
            ret.strokePaint=strokePaint.clone();
        else
            ret.strokePaint=null;
        ret.stroke=stroke;
        ret.setEffector(getEffector());
        return ret;
        
    }
    @Override
    public AffineTransform getTotalTransform(){
        return totalTransform;
    }
    @Override
    public DefaultStyledDocument getCloneStyledDocument() {
        return cloneDocument(document);
    }
    @Override
    public DefaultStyledDocument getStyledDocument() {
        return document;
    }
    @Override
    public void setStyledDocument(DefaultStyledDocument doc) {
        this.document=doc;
        lineShape=null;
    }
    @Override
    public void updatePath(){
        lineShape=null;
        locater=null;
    }
    @Override
    public void textUpdate(JEnvironment env) {
        env.addClip(getBounds());
        updatePath();
        env.addClip(getBounds());
    }
    @Override
    public Shape getShape() {
        if (lineShape==null) {
            lineShape=createLineShape(document,null,false);
        }
        AffineTransform af=new AffineTransform(totalTransform);
        return af.createTransformedShape(lineShape);
    }
    @Override
    public String getPrefixer(){
        return "Text";
    }
    @Override
    public TextLocater createLocater(FontRenderContext frc) {
        return new TextLocater(getStyledDocument(),null,frc);
    }
    
    @Override
    public Shape getLayoutShape() {
        return null;
    }
}
