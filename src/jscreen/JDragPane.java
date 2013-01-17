/*
 * JDragPane.java
 *
 * Created on 2007/08/29, 16:01
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jscreen;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JViewport;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import javax.swing.undo.CompoundEdit;
import jtools.JAbstractTool;
import jtools.JAncurTool;
import jtools.JBasicTool;
import jtools.JBevelTool;
import jtools.JCutCornerTool;
import jtools.JCursor;
import jtools.JCutterTool;
import jtools.JEllipseTool;
import jtools.JFreeHandTool;
import jtools.JGradientTool;
import jtools.JHandTool;
import jtools.JLayoutTextTool;
import jtools.JLineTool;
import jtools.JMagnifyTool;
import jtools.JPathTextTool;
import jtools.JPenTool;
import jtools.JPenMinusTool;
import jtools.JPenPlusTool;
import jtools.JPolygonTool;
import jtools.JRectangleTool;
import jtools.JReflectTool;
import jtools.JReshapeTool;
import jtools.JRotateTool;
import jtools.JRoundCornerTool;
import jtools.JRoundRectTool;
import jtools.JScaleTool;
import jtools.JShearTool;
import jtools.JSpoitTool;
import jtools.JStarTool;
import jtools.JTextTool;
import jobject.JLayer;
import jobject.JLeaf;
import jui.JIcons;

/**
 * ドラッグ層の表示及びドラッガーを選択します.
 * @author i002060
 */
public class JDragPane extends JComponent implements MouseListener,MouseMotionListener,KeyListener{
    public static final JIcons JICON=new JIcons();
    public HashMap<ImageIcon,DraggerAction> actionMap;
    private  JDocumentViewer viewer;
    private JAbstractTool currentDragger;
    private boolean paintRect;
    public Point2D startPoint;//Start Point by Abusolute Coordinates;
    private Rectangle2D.Double dragRect;//Dragging Rectangle by Absolute Coordinate;
    private JCursor jcursor;
    private boolean isFirstDragEvent=true;
    public boolean isDragging;
    public boolean isAutoScrolling=true;
    //
    private boolean controlDown=false;
    //
    private boolean spaceDown=false;
    //
    private JAbstractTool defaultDragger;
    private JAbstractTool savedDragger;
    
    /** Creates a new instance of JDragPane */
    public JDragPane(JDocumentViewer v) {
        viewer=v;
        v.setLayout(new BorderLayout());
        v.setLayer(this,JLayeredPane.DRAG_LAYER);
        v.add(this,BorderLayout.CENTER);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addKeyListener(this);
        this.addMouseWheelListener(new MWheelListener());
        jcursor=new JCursor();
        startPoint=null;
        dragRect=null;
        paintRect=true;
        isDragging=false;
        setupMap();
        DraggerAction ac=getAction(JICON.RESHAPE);
        defaultDragger=ac.getDragger();
        savedDragger=null;
        ac.putValue(ac.SELECTED_KEY,true);
        setDragger(ac.getDragger());
    }
    private void setupMap(){
        actionMap=new HashMap<ImageIcon,DraggerAction>();
        setAction(JICON.RESHAPE,new JReshapeTool(this));
        setAction(JICON.DIRECT,new JBasicTool(this));
        setAction(JICON.PEN,new JPenTool(this));
        setAction(JICON.PEN_PLUS,new JPenPlusTool(this));
        setAction(JICON.PEN_MINUS,new JPenMinusTool(this));
        setAction(JICON.ANCUR,new JAncurTool(this));
        setAction(JICON.LINE,new JLineTool(this));
        setAction(JICON.RECTANGLE,new JRectangleTool(this));
        setAction(JICON.ROUNDRECT,new JRoundRectTool(this));
        setAction(JICON.BEVEL,new JBevelTool(this));
        setAction(JICON.ELLIPSE,new JEllipseTool(this));
        setAction(JICON.POLYGON,new JPolygonTool(this));
        setAction(JICON.STAR,new JStarTool(this));
        setAction(JICON.TEXT,new JTextTool(this));
        setAction(JICON.LAYOUT_TEXT,new JLayoutTextTool(this));
        setAction(JICON.PATH_TEXT,new JPathTextTool(this));
        setAction(JICON.CUTTER,new JCutterTool(this));
        setAction(JICON.CUTOFF_CORNER,new JCutCornerTool(this));
        setAction(JICON.ROUND_CORNER,new JRoundCornerTool(this));
        setAction(JICON.RESIZE,new JScaleTool(this));
        setAction(JICON.ROTATE,new JRotateTool(this));
        setAction(JICON.SHEAR,new JShearTool(this));
        setAction(JICON.SYMMETRIC,new JReflectTool(this));
        setAction(JICON.GRADIENT,new JGradientTool(this));
        setAction(JICON.SPOIT,new JSpoitTool(this));
        setAction(JICON.MAGNIFY,new JMagnifyTool(this));
        setAction(JICON.HAND,new JHandTool(this));
        setAction(JICON.PENCIL_ICON,new JFreeHandTool(this));
    }
    private void setAction(ImageIcon img,JAbstractTool drg){
        actionMap.put(img,new DraggerAction(img,drg));
    }
    public DraggerAction getAction(ImageIcon img){
        return actionMap.get(img);
    }
    @Override
    public void paintComponent(Graphics g){
        Graphics2D g2=(Graphics2D)g;
        if (JEnvironment.PREVIEW_ANTI_AREASING)
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getEnvironment().PREVIEW_COLOR);
        g2.setStroke(new BasicStroke(0f));
        JRequest request= getViewer().getDocument().getCurrentPage().getRequest();
        for (int i=0;i<request.size();i++){
            if (request.get(i) instanceof JLeaf){
                JLeaf l=(JLeaf)request.get(i);
                l.paintPreview(getEnvironment(),request,g2);
            }
        }
        if (currentDragger!=null)
            currentDragger.paint(g2);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
        if (paintRect && dragRect !=null){
            Shape s=getEnvironment().getToScreenTransform().createTransformedShape(dragRect);
            g2.setColor(JEnvironment.DRAG_AREA_COLOR);
            g2.setStroke(JEnvironment.DRAG_AREA_STROKE);
            g2.draw(s);
        }
        if (!viewer.getTextPane().isVisible())
            requestFocusInWindow();
    }
    public boolean isDragging(){
        return isDragging;
    }
    public JRequest getCurrentRequest(){
        return getViewer().getDocument().getCurrentPage().getRequest();
    }
    public JDocumentViewer getViewer(){
        return viewer;
    }
    public void setDragger(JAbstractTool dr){
        if (currentDragger != dr){
            if (currentDragger!=null){
                currentDragger.sleep();
            }
            currentDragger=dr;
            if (dr instanceof JBasicTool || dr instanceof JReshapeTool){
                defaultDragger=dr;
            }
            dr.wakeup();
            repaint();
        }
    }
    public JAbstractTool getDragger(){
        return currentDragger;
    }
    public void setPaintRect(boolean b){
        paintRect=b;
    }
    public boolean isPaintRect(){
        return paintRect;
    }
    public JEnvironment getEnvironment(){
        return viewer.getEnvironment();
    }
    public Rectangle2D getDragRect(){
        return dragRect;
    }
    public Point2D getStartPoint(){
        return startPoint;
    }
    public JCursor getJCursor(){
        return jcursor;
    }
    public boolean isFirstDragEvent(){
        return isFirstDragEvent;
    }
    @Override
    public void mouseClicked(MouseEvent e) {
        JLayer layer=getViewer().getCurrentPage().getCurrentLayer();
        if (layer.isLocked() || !layer.isVisible())return;
        if (currentDragger!=null) currentDragger.mouseClicked(e);
    }
    @Override
    public void mousePressed(MouseEvent e) {
        JLayer layer=getViewer().getCurrentPage().getCurrentLayer();
        if (layer.isLocked() || !layer.isVisible())return;
        AffineTransform af=getEnvironment().getToAbsoluteTransform();
        if (SwingUtilities.isLeftMouseButton(e)){
            isDragging=true;
            //startPoint=new Point2D.Double();
            //af.transform(e.getPoint(),startPoint);
            startPoint=getEnvironment().getAbsoluteMousePoint(e.getPoint(),viewer.getCurrentPage());
            if (currentDragger!=null) {
                currentDragger.mousePressed(e);
                currentDragger.changeCursor();
            }
        }
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        JLayer layer=getViewer().getCurrentPage().getCurrentLayer();
        if (layer.isLocked() || !layer.isVisible())return;
        if (SwingUtilities.isLeftMouseButton(e)){
            
            if (currentDragger!=null){
                currentDragger.mouseReleased(e);
                currentDragger.changeCursor();
            }
            startPoint=null;
            dragRect=null;
            if(paintRect) repaint();
            isFirstDragEvent=true;
            isDragging=false;
            if (controlDown && !e.isControlDown()){
                controlDown=false;
                setDragger(savedDragger);
                currentDragger.changeCursor();
            }
        }
    }
    @Override
    public void mouseEntered(MouseEvent e){
        JLayer layer=getViewer().getCurrentPage().getCurrentLayer();
        if (layer.isLocked() || !layer.isVisible())return;
        if (currentDragger!=null) currentDragger.mouseEntered(e);
    }
    @Override
    public void mouseExited(MouseEvent e) {
        JLayer layer=getViewer().getCurrentPage().getCurrentLayer();
        if (layer.isLocked() || !layer.isVisible())return;
        if (currentDragger!=null) currentDragger.mouseExited(e);
    }
    @Override
    public void mouseDragged(MouseEvent e) {
        JLayer layer=getViewer().getCurrentPage().getCurrentLayer();
        if (layer.isLocked() || !layer.isVisible())return;
        if (startPoint==null){
            startPoint=new Point2D.Double();
            getEnvironment().getToAbsoluteTransform().transform(e.getPoint(),startPoint);
            return;
        }
        if (SwingUtilities.isLeftMouseButton(e)){
            Point2D.Double pd=new Point2D.Double();
            getEnvironment().getToAbsoluteTransform().transform(e.getPoint(),pd);
            dragRect=new Rectangle2D.Double();
            dragRect.setFrameFromDiagonal(startPoint,pd);
            if (currentDragger!=null){
                currentDragger.mouseDragged(e);
                currentDragger.changeCursor();
            }
            if (isAutoScrolling){
                JViewport vw=viewer.getScroller().getViewport();
                if (!vw.getViewRect().contains(e.getPoint())){
                    Point p=SwingUtilities.convertPoint(this,e.getPoint(),vw);
                    Rectangle rc=new Rectangle(p.x,p.y,1,1);
                    vw.scrollRectToVisible(rc);
                }
            }else{
                isAutoScrolling=true;
            }
            repaint();
            isFirstDragEvent=false;
        }
    }
    @Override
    public void mouseMoved(MouseEvent e) {
        JLayer layer=getViewer().getCurrentPage().getCurrentLayer();
        if (layer.isLocked() || !layer.isVisible()){
            if (this.getCursor() !=JEnvironment.MOUSE_CURSOR.LOCKED)
                this.setCursor(JEnvironment.MOUSE_CURSOR.LOCKED);
            return;
        }
        JRequest req=getCurrentRequest();
        req.isAltDown=e.isAltDown();
        req.isCtlDown=e.isControlDown();
        req.isShiftDown=e.isShiftDown();
        if (currentDragger!=null) {
            currentDragger.mouseMoved(e);
            currentDragger.changeCursor();
        }
    }
    @Override
    public void keyTyped(KeyEvent e) {
        if (currentDragger!=null) currentDragger.keyTyped(e);
    }
    @Override
    public void keyPressed(KeyEvent e) {
        getCurrentRequest().isAltDown=e.isAltDown();
        getCurrentRequest().isCtlDown=e.isControlDown();
        getCurrentRequest().isShiftDown=e.isShiftDown();
        
        
        if (e.getKeyCode()==e.VK_CONTROL && !controlDown && !isDragging 
                && defaultDragger !=currentDragger && !spaceDown && currentDragger.controlChangeTool()){
            controlDown=true;
            savedDragger=currentDragger;
            setDragger(defaultDragger);
            currentDragger.changeCursor();
            e.consume();
            return;
        }
        //HandTool
        JAbstractTool hand=getAction(JICON.HAND).getDragger();
        if (e.getKeyCode()==e.VK_SPACE &&  !isDragging && currentDragger!=hand && !controlDown){
            spaceDown=true;
            savedDragger=currentDragger;
            setDragger(hand);
            currentDragger.changeCursor();
            e.consume();
            return;
        }
        
        
        JEnvironment env=getEnvironment();
        JRequest req=getCurrentRequest();
        double delta=env.getGridSize()/getEnvironment().getGridDivision();
        double dx=0,dy=0;
        int key=e.getKeyCode();
        switch (key){
            case KeyEvent.VK_UP:dy-=delta;break;
            case KeyEvent.VK_DOWN:dy+=delta;break;
            case KeyEvent.VK_LEFT:dx-=delta;break;
            case KeyEvent.VK_RIGHT:dx+=delta;break;
            default:
                currentDragger.keyPressed(e);
                currentDragger.changeCursor();
                return;
                
        }
        if ((dx!=0 || dy!=0) && !req.isEmpty()){
            AffineTransform af=new AffineTransform();
            af.setToTranslation(dx,dy);
            CompoundEdit cEdit=new CompoundEdit();
            if (req.getSelectionMode()==req.GROUP_MODE){
                req.hitResult=req.HIT_OBJECT;
            }else{
                req.hitResult=req.HIT_PATH;
            }
            for (int i=0;i<req.size();i++){
                Object o=req.get(i);
                if (o instanceof JLeaf){
                    JLeaf l=(JLeaf)o;
                    l.transform(af,req,null);
                    cEdit.addEdit(l.updateTransform(env));
                }
            }
            cEdit.end();
            getViewer().getDocument().fireUndoEvent(cEdit);
            req.hitResult=req.HIT_NON;
            req.hitObjects.clear();
            getViewer().repaint();
            e.consume();
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {
        int k=e.getKeyCode();
        if (k==e.VK_ALT)
            getCurrentRequest().isAltDown=false;
        if (k==e.VK_SHIFT)
            getCurrentRequest().isShiftDown=false;
        if (e.getKeyCode()==e.VK_CONTROL && !isDragging ){
            getCurrentRequest().isCtlDown=false;
            if (controlDown){
                controlDown=false;
                setDragger(savedDragger);
                currentDragger.changeCursor();
                e.consume();
                return;
            }
        }
        //
        if (e.getKeyCode()==e.VK_SPACE && spaceDown){
            spaceDown=false;
            setDragger(savedDragger);
            currentDragger.changeCursor();
            e.consume();
            return;
        }
        //
        if (currentDragger!=null) {
            currentDragger.keyReleased(e);
            currentDragger.changeCursor();
        }
    }

    void documentChagned() {
        Iterator<DraggerAction> it=actionMap.values().iterator();
        while(it.hasNext()){
            it.next().dragger.documentChanged();
        }
    }
    //
    public class DraggerAction extends AbstractAction{
        JAbstractTool dragger;
        ImageIcon icon;
        public DraggerAction(ImageIcon img,JAbstractTool drg){
            dragger=drg;
            icon=img;
            putValue(SMALL_ICON,icon);
            putValue(LARGE_ICON_KEY,icon);
            putValue(SHORT_DESCRIPTION,drg.presentationName());
        }
        public void actionPerformed(ActionEvent e) {
            if (currentDragger!=dragger)
                setDragger(dragger);
        }
        public JAbstractTool getDragger(){
            return dragger;
        }
        
    }
    //
    public class MWheelListener implements MouseWheelListener{
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (!e.isControlDown()){
                getParent().dispatchEvent(e);
                return;
            }
            JEnvironment env=getEnvironment();
            float  ratio=(float)env.getMagnification();
            float newRatio=ratio;
            
            if (e.getWheelRotation()>0){
                newRatio=JMagnifyTool.getPrevRatio(ratio);
            }else{
                newRatio=JMagnifyTool.getNextRatio(ratio);
            }
            if (ratio==newRatio) return;
            JDocumentViewer viewer=getViewer();
            JScroller scroller =viewer.getScroller();
            Point2D p=new Point2D.Double();
            env.getToAbsoluteTransform().transform(e.getPoint(),p);
            env.setMagnification(newRatio);
            viewer.adjustSize();
            env.getToScreenTransform().transform(p,p);
            Rectangle bounds=viewer.getBounds();
            Rectangle view=scroller.getViewport().getViewRect();
            int ofsX=(int)Math.min(bounds.width-view.width,p.getX()-view.width/2);
            int ofsY=(int)Math.min(bounds.height-view.height,p.getY()-view.height/2);
            ofsX=Math.max(ofsX,0);
            ofsY=Math.max(ofsY,0);
            scroller.getViewport().setViewPosition(new Point(ofsX,ofsY));
            viewer.isDraftMode=false;
            
            RepaintManager.currentManager(scroller).markCompletelyDirty(scroller);
            RepaintManager.currentManager(viewer).markCompletelyDirty(viewer);
            RepaintManager.currentManager(viewer).paintDirtyRegions();
           
        }
        
    }
    
}
