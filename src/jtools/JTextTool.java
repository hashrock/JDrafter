/*
 * JTextTool.java
 *
 * Created on 2007/11/05, 13:38
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jtools;

import jactions.JUndoRedoEvent;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;
import javax.swing.undo.UndoableEdit;
import jedit.JInsertObjectsEdit;
import jedit.textobjectedit.JChangeTextEdit;
import jobject.JLeaf;
import jobject.JTextObject;
import jscreen.JDragPane;
import jscreen.JEnvironment;
import jscreen.JRequest;
import jobject.JText;
import jobject.text.InlineTextPane;

/**
 *
 * @author i002060
 */
public class JTextTool extends JRectangleTool implements DocumentListener,ItemListener{
    /** Creates a new instance of JTextTool */
    protected JText targetText=null;
    protected boolean textChanged=false;
    protected UEListener uEListener=new UEListener();
    public JTextTool(JDragPane dragPane) {
        super(dragPane);
        presentationName="テキスト";
        getViewer().getDocument().addItemListener(this);
        
    }
    @Override
    public void documentChanged(){
        getViewer().getDocument().addItemListener(this);
    }
    @Override
    public void paint(Graphics2D g){
        if (getViewer().getTextPane().isVisible()){
            AffineTransform tx=getEnvironment().getToScreenTransform();
            tx.concatenate(targetText.getTotalTransform());
            getViewer().getTextPane().setTransform(tx);
            Shape s=targetText.getLayoutShape();
            JLeaf tg=(JLeaf)targetText;
            if (s !=null){
                s=getEnvironment().getToScreenTransform().createTransformedShape(s);
                g.setColor(tg.getLayer().getPreviewColor());
                g.draw(s);
            }
        }
        super.paint(g);
    }
    @Override
    public void mouseReleased(MouseEvent e){
        JEnvironment env=getEnvironment();
        JRequest req=getRequest();
        //Point2D p=new Point2D.Double();
        //env.getToAbsoluteTransform().transform(e.getPoint(),p);
        Point2D p=env.getAbsoluteMousePoint(e.getPoint(),getViewer().getCurrentPage());
        req.hitResult=req.HIT_NON;
        req.hitObjects.clear();
        getViewer().getCurrentPage().hitByPoint(env,req,p);
        if (req.hitResult !=req.HIT_NON){
            for (int i=0;i<req.hitObjects.size();i++){
                Object o=req.hitObjects.get(i);
                if (o instanceof JText){
                    req.clear();
                    req.add(o);
                    dragPane.repaint();
                    modifyObject((JText)o,e.getPoint());
                    req.hitObjects.clear();
                    req.hitResult=req.HIT_NON;
                    
                    break;
                }else{
                    req.hitResult=req.HIT_NON;
                    req.hitObjects.clear();
                    createObject(p);
                }
            }
        }else{
            createObject(p);
        }
        startPoint=endPoint=null;
        
    }
    private void createObject(Point2D startAt){
        //JFrame frame=getRootFrame(getViewer().getRootPane().getParent());
        DefaultStyledDocument doc=new DefaultStyledDocument();
        InlineTextPane tPane=getViewer().getTextPane();
        
        JTextObject creatingObject=new JTextObject(startAt,doc);
        AffineTransform tx=getEnvironment().getToScreenTransform();
        tx.concatenate(creatingObject.getTotalTransform());
        tPane.setTransform(tx);
        tPane.setJText(creatingObject);
        getViewer().getAvailableLayer().add(creatingObject);
        getViewer().getCurrentRequest().clear();
        Vector<JLeaf> objs=new Vector<JLeaf>(1);
        objs.add(creatingObject);
        UndoableEdit edt=new JInsertObjectsEdit(getViewer(),objs);
        getViewer().getDocument().fireUndoEvent(edt);
        getViewer().getCurrentRequest().clear();
        doc.addDocumentListener(this);
        targetText=creatingObject;
        targetText.getStyledDocument().addUndoableEditListener(uEListener);
        tPane.setVisible(true);
    }
    private void endEditing(){
        if (getViewer().getTextPane().isVisible()){
            getViewer().getTextPane().setVisible(false);
            if (targetText==null) return;
            StyledDocument doc=targetText.getStyledDocument();
            doc.removeDocumentListener(this);
            doc.removeUndoableEditListener(uEListener);
            targetText.updatePath();
            JLeaf jl=(JLeaf)targetText;
            if (!jl.isVisible())
                jl.setVisible(true);
            getViewer().getTextPane().setJText(null);
            getViewer().getCurrentRequest().add(targetText);            
            targetText.textUpdate(getViewer().getEnvironment());
            getViewer().getDocument().fireUndoRedoEvent(new JUndoRedoEvent(jl,JUndoRedoEvent.REDO));
            getViewer().repaint();
        }
        
        
    }
    private static JFrame getRootFrame(Component c){
        if (c instanceof JFrame) return (JFrame)c;
        return getRootFrame(c.getParent());
    }
    protected void modifyObject(JText t,Point p){
        InlineTextPane tPane=getViewer().getTextPane();
        tPane.setJText(t);
        targetText=t;
        getViewer().getCurrentRequest().remove(t);
        JLeaf obj=(JLeaf)t;
        obj.setVisible(false);
        getViewer().getEnvironment().addClip(obj.getBounds());
        getViewer().repaint();
        AffineTransform tx=getEnvironment().getToScreenTransform();
        tx.concatenate(t.getTotalTransform());
        tPane.setTransform(tx);
        targetText.getStyledDocument().addUndoableEditListener(uEListener);
        tPane.setVisible(true);
        tPane.putCursor(p);
        /*
        double ratio=JEnvironment.screenDPI/72;
        JFrame frame=getRootFrame(getViewer().getRootPane().getParent());
        textPane=new JTextDialog(frame,true);
        DefaultStyledDocument svdoc=t.getCloneStyledDocument();
        DefaultStyledDocument doc=t.getStyledDocument();
        textPane.setDocument(doc);
        targetText=t;
        textChanged=false;
        doc.addDocumentListener(this);
        textPane.setVisible(true);
        doc.removeDocumentListener(this);
        if (textChanged){
            UndoableEdit anEdit=new JTextModifyEdit(getViewer(),t,doc,svdoc);
            getViewer().getDocument().fireUndoEvent(anEdit);
            targetText.textUpdate(getViewer().getEnvironment());
            getViewer().isDraftMode=false;
            getViewer().repaint();
        }
        targetText=null;
         */
    }
    @Override
    public void sleep(){
        endEditing();
    }
    @Override
    public void changeCursor() {
        JCursor jc=dragPane.getJCursor();
        setCursor(jc.TEXT);
    }
    protected void updateText(){
        textChanged=true;
        
        /**
         * if (targetText !=null){
         *
         * targetText.textUpdate(getViewer().getEnvironment());
         * getViewer().repaint();
         * }
         */
    }
    @Override
    public void insertUpdate(DocumentEvent e) {
        updateText();
    }
    
    @Override
    public void removeUpdate(DocumentEvent e) {
        updateText();
    }
    
    @Override
    public void changedUpdate(DocumentEvent e) {
        updateText();
    }
    
    @Override
    public void itemStateChanged(ItemEvent e) {
        endEditing();
    }
    protected class UEListener implements UndoableEditListener{
        @Override
        public void undoableEditHappened(UndoableEditEvent e) {
            UndoableEdit anEdit=new JChangeTextEdit(getViewer(),(JLeaf)targetText,e.getEdit(),false);
            getViewer().getDocument().fireUndoEvent(anEdit);
        }
        
    }
    
}
