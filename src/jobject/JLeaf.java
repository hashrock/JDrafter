/*
 * JLeafObject.java
 *
 * Created on 2007/08/27, 19:35
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jobject;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Enumeration;
import javax.swing.tree.TreeNode;
import javax.swing.undo.UndoableEdit;
import jobject.effector.JDefaultEffector;
import jobject.effector.JEffector;
import jpaint.JPaint;
import jpaint.JStroke;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 * JDocument上の全てのオブジェクトの基本クラス.
 *
 * @author TI
 */
public abstract class  JLeaf<P extends JObject> implements Serializable,TreeNode{
    private transient P  parent=null;
    private boolean enabled=true;
    private boolean visible=true;
    private boolean locked=false;
    private String name="";
    protected double totalRotation=0;
    protected JPaint fillPaint=JEnvironment.currentFill;
    protected JPaint strokePaint=JEnvironment.currentBorder;
    protected JStroke stroke=JEnvironment.currentStroke;
    protected JEffector effector=new JDefaultEffector();
    //
    private static final long serialVersionUID = 110l;
    /** Creates a new instance of JLeafObject */
    public JLeaf() {
    }
    @Override
    public P getParent(){
        return parent;
    }
    public void setParent(P p){
        parent=p;
    }
    public boolean isVisible(){
        return visible;
    }
    public void setVisible(boolean b){
        visible=b;
    }
    public void setLocked(boolean b){
        this.locked=b;
    }
    public boolean isLocked(){
        return locked;
    }
    public boolean isEnabled(){
        if (!enabled) return false;
        if (parent!=null) return parent.isEnabled();
        return enabled;
    }
    public void setEnabled(boolean b){
        enabled=b;
    }
    public String getName(){
        return name;
    }
    public  void setName(String nm){
        name=nm;
    }
    public void addTotalRotate(double theta){
        totalRotation += theta;
        double abs=Math.abs(totalRotation);
        totalRotation=Math.signum(totalRotation)*(abs-2*Math.PI*(Math.floor(abs/(2*Math.PI))));
        
    }
    public double getTotalRotation(){
        return totalRotation;
    }
    public void setTotalRotation(double r){
        totalRotation=r;
    }
    public void setFillPaint(JPaint p){
        fillPaint=p;
    }
    public JPaint getFillPaint(){
        return fillPaint;
    }
    public void setStrokePaint(JPaint p){
        strokePaint=p;
    }
    public JPaint getStrokePaint(){
        return strokePaint;
    }
    public void setStroke(JStroke stroke){
        this.stroke=stroke;
    }
    public JStroke getStroke(){
        return stroke;
    }
    public void setEffector(JEffector ef){
        this.effector=ef;
    }
    public JEffector getEffector(){
        return effector;
    }
    public JDocument getDocument(){
        if (parent==null) return null;
        return parent.getDocument();
    }
    public JPage getPage(){
        if (parent==null) return null;
        return parent.getPage();
    }
    private JLayer getLayer(JLeaf jl){
        if (jl instanceof JLayer) return (JLayer)jl;
        JLeaf lparent=jl.getParent();
        if (lparent==null) return null;
        return getLayer(lparent);
    }
    public Color getPreviewColor(){
        //JLayer jl=getLayer(this);
        JLayer jl=getLayer();
        if (jl==null) return JEnvironment.PREVIEW_COLOR;
        return jl.getPreviewColor();
    }
    public boolean canBeGuide(){
        return false;
    }
    public void paint(Rectangle2D clip,Graphics2D g){
        if (!isVisible()) return;
        paintThis(clip,g);
    }
    public abstract void transform(AffineTransform tr,JRequest req,Point p);
    public abstract void transform(AffineTransform tr);
    public abstract int hitByPoint(JEnvironment env,JRequest req,Point2D point);
    public abstract void hitByRect(JEnvironment env,JRequest req,Rectangle2D rect);
    public abstract UndoableEdit updateTransform(JEnvironment env);
    public abstract UndoableEdit updateRotate(JEnvironment env,double rotation);
    public abstract void paintThis(Rectangle2D clip,Graphics2D g);
    public abstract void paintPreview(JEnvironment env,JRequest req,Graphics2D g);
    public abstract String getPrefixer();
    //For TreeNode 
    @Override
    public Enumeration children(){
        return null;
    }
    @Override
    public boolean getAllowsChildren(){
        return false;
    }
    @Override
    public TreeNode getChildAt(int childIndex){
        return null;
    }
    @Override
    public int getChildCount(){
        return 0;
    }
    @Override
    public int getIndex(TreeNode node){
        return -1;
    }
    @Override
    public boolean isLeaf(){
        return true;
    }
    public abstract Shape getShape();
    public void updatePath(){}
    @Override
    public String toString(){
        return getName();
    }
    public boolean isAnscester(JLeaf a){
        JObject pr=getParent();
        JLeaf current=this;
        while (pr !=null){
            if (!pr.contains(current))
                break;
            if (pr==a){
                return true;
            }         
            current=(JLeaf)pr;
            pr=pr.getParent();
        }
        return false;
    }
    public boolean isDescender(JLeaf d){
        return d.isAnscester(this);
    }
    public JLayer getLayer(){
        if (parent !=null) return parent.getLayer();
        return null;
    }
    //セレクション領域
    public abstract Rectangle2D getSelectionBounds();
    //オブジェクトが収まるBounds
    public abstract Rectangle2D getBounds();
    /**回転移動前のセレクション領域*/
    public abstract Rectangle2D getOriginalSelectionBounds(double x,double y);
    @Override
    public abstract Object clone() throws CloneNotSupportedException;
    
    
}
