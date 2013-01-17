/*
 * myEditor.java
 *
 * Created on 2008/05/27, 18:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jobject.text;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import jscreen.JDocumentViewer;
import jobject.JText;

/**
 *
 * @author takashi
 */
public class InlineTextPane extends JComponent{
    private TextLocater locater=null;
    private StyledDocument document=null;
    private JText jText=null;
    private HitSet dot=null,mark=null;
    private DocListener docListener=null;
    private MListener mListener=null;
    private KListener kListener=null;
    private Blinker blinker=null;
    private JDocumentViewer viewer=null;
    private CEvent cEvent=null;
    private Vector<CaretListener> caretListeners=null;
    //
    private AttributeSet inputCharacterAttributes=null;
    //
    private Vector<DocumentListener> documentListeners=null;
    //
    private AffineTransform transform=null;
    //
    private MutableAttributeSet inputAttributes=new SimpleAttributeSet();
    /** Creates a new instance of myEditor */
    public InlineTextPane() {
        transform=new AffineTransform();
        mListener=new MListener();
        kListener=new KListener();
        cEvent=new CEvent(this);
        caretListeners=new Vector<CaretListener>();
        this.addMouseListener(mListener);
        this.addMouseMotionListener(mListener);
        this.addKeyListener(kListener);
        blinker=new Blinker();
        this.setLayout(new java.awt.BorderLayout());
        this.add(blinker,java.awt.BorderLayout.CENTER);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        docListener=new DocListener();
        this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,Collections.EMPTY_SET);
        this.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,Collections.EMPTY_SET);
        this.requestFocus();
        CListener cListener=new CListener();
        this.addComponentListener(cListener);
        this.addAncestorListener(cListener);
        documentListeners=new Vector<DocumentListener>();
        
    }
    public InlineTextPane(JDocumentViewer view){
        this();
        this.viewer=view;
        
    }
    public void setJText(JText jt){
        this.jText=jt;
        if (jt !=null)
            setDocument(jt.getStyledDocument());
        else
            setDocument(null);
    }
    public JText getJText(){
        return jText;
    }
    public JDocumentViewer getViewer(){
        return viewer;
    }
    public void setTransform(AffineTransform tx){
        transform=tx;
    }
    private  void setDocument(StyledDocument doc){
        blinker.suspend();
        if (document !=null){
            document.removeDocumentListener(docListener);
        }
        document=doc;
        if (document !=null){
            try{
                if (doc.getLength()==0){
                    AttributeSet attr;
                    if (inputAttributes !=null){
                        attr=inputAttributes;
                        doc.setCharacterAttributes(0,1, attr, true);
                    }else{
                        attr=doc.getCharacterElement(doc.getLength()).getAttributes();
                    }
                    doc.insertString(0,"\n",attr);
                } else if (!doc.getText(doc.getLength()-1,1).equals("\n")){
                    AttributeSet attr=doc.getCharacterElement(doc.getLength()-1).getAttributes();
                    doc.insertString(doc.getLength(),"\n",attr);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            doc.addDocumentListener(docListener);
            locater=jText.createLocater(null);
            dot=mark=locater.hitPosition(0);
            fireCaretEvent();
            blinker.start();
        }
        
    }
    //
    public  void addDouumentListener(DocumentListener l){
        if (l !=null && !documentListeners.contains(l))
            documentListeners.add(l);
    }
    public void removeDocumentListener(DocumentListener l){
        documentListeners.remove(l);
    }
    public String getSelectionString(){
        if (document==null || dot==null || dot==mark)
            return null;
        try{
            int st=Math.min(dot.getInsertionIndex(),mark.getInsertionIndex());
            int en=Math.max(dot.getInsertionIndex(),mark.getInsertionIndex());
            return document.getText(st,en-st);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public void insertString(String s){
        //AttributeSet attr=document.getCharacterElement(dot.getInsertionIndex()).getAttributes();
        AttributeSet attr=getInputAttributes();
        try{
            if (getDot()==getMark()){
                document.insertString(dot.getInsertionIndex(),s,attr);
            }else{
                int st=Math.min(getDot(),getMark());
                int en=Math.max(getDot(),getMark());
                document.remove(st,en-st);
                document.insertString(st,s,attr);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void removeSelectionString(){
        try{
            if (getDot()==getMark()){
                return;
            }else{
                int st=Math.min(getDot(),getMark());
                int en=Math.max(getDot(),getMark());
                document.remove(st,en-st);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void setSelection(int dt,int mk){
        dot=locater.hitPosition(dt);
        mark=locater.hitPosition(mk);
    }
    public StyledDocument getDocument(){
        return document;
    }
    //
    
    public int getDot() {
        if(dot==null){
            return 0;
        }
        return dot.getInsertionIndex();
    }
    public int getMark() {
        if (mark==null){
            return 0;
        }
        return mark.getInsertionIndex();
    }
    //
    @Override
    public void paintComponent(Graphics g){
        Graphics2D g2=(Graphics2D)g;
        g2.transform(transform);
        //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        if (document==null) return;
        
        if (locater==null ){
            locater=jText.createLocater(null);
        }
        g2.setColor(Color.BLACK);
        locater.draw(g2);
        if (dot != mark && dot!=null){
            Shape s=locater.getLogicalHitShape(dot,mark);
            Graphics2D g3=(Graphics2D)g2.create();
            g3.setXORMode(Color.WHITE);
            g3.fill(s);
            g3.dispose();
        }
        requestFocus();
    }
    public void addCaretListener(CaretListener l){
        if (l!=null & !caretListeners.contains(l)){
            caretListeners.add(l);
        }
    }
    public void removeCaretListener(CaretListener l){
        caretListeners.remove(l);
    }
    private void fireCaretEvent(){
        Rectangle r=dot.getCursor().getBounds();
        if (r.width==0) r.width=1;
        if (r.height==0) r.height=1;
        r=transform.createTransformedShape(r).getBounds();
        viewer.getDragPane().scrollRectToVisible(r);
        updateInputAttributes(getDot(),getMark());
        for (int i=0;i<caretListeners.size();i++){
            caretListeners.get(i).caretUpdate(cEvent);
        }
        
    }
    //
    public void putCursor(Point p){
        AffineTransform tx=getInvert();
        tx.transform(p,p);
        if (locater !=null){
            dot=mark=locater.hit(p.x,p.y);
            fireCaretEvent();
            repaint();
        }else{
            dot=mark=null;
        }
    }
    //
    public void setParagraphAttributes(AttributeSet attr, boolean replace) {
        int p0 = Math.min(getDot(),getMark());
        int p1 = Math.max(getDot(),getMark());
        document.setParagraphAttributes(p0, p1 - p0, attr, replace);
    }
    //
    public AttributeSet getParagraphAttributes() {
        Element paragraph = document.getParagraphElement(getDot());
        if (paragraph != null) {
            return paragraph.getAttributes();
        }
        return null;
    }
    //
    public AttributeSet getCharacterAttributes() {
        Element run = document.getCharacterElement(getDot());
        if (run != null) {
            return run.getAttributes();
        }
        return null;
    }
    //
    public MutableAttributeSet getInputAttributes(){
        return inputAttributes;
    }
    //
    public void setCharacterAttributes(AttributeSet attr, boolean replace) {
        int p0 = Math.min(getDot(),getMark());
        int p1 = Math.max(getDot(),getMark());
        if (p0==p1 && p0==document.getLength()-1){
            document.setCharacterAttributes(p0,1,attr,replace);
            inputAttributes.addAttributes(attr);
            return;
        }
        if (p0 != p1) {
            if (p1==document.getLength()-1)
                p1=document.getLength();
            document.setCharacterAttributes(p0, p1 - p0, attr, replace);
        } else {
            //inputAttributes = getInputAttributes();
            if (replace) {
                inputAttributes.removeAttributes(inputAttributes);
            }
            inputAttributes.addAttributes(attr);
        }
    }
    //
    Element currentRun=null;
    void updateInputAttributes(int dot, int mark) {
        int start = Math.min(dot, mark);
        // record current character attributes.
        StyledDocument doc = document;
        // If nothing is selected, get the attributes from the character
        // before the start of the selection, otherwise get the attributes
        // from the character element at the start of the selection.
        Element run;
        Element currentParagraph = doc.getParagraphElement(start);
        if (currentParagraph.getStartOffset() == start || dot != mark) {
            // Get the attributes from the character at the selection
            // if in a different paragrah!
            run = doc.getCharacterElement(start);
        } else {
            run = doc.getCharacterElement(Math.max(start-1, 0));
        }
        if (run != currentRun) {
            currentRun = run;
            inputAttributes=new SimpleAttributeSet();
            inputAttributes.addAttributes(run.getAttributes());
            //createInputAttributes(currentRun, getInputAttributes());
        }
    }
    protected void createInputAttributes(Element element,
            MutableAttributeSet set) {
        if (element.getAttributes().getAttributeCount() > 0
                || element.getEndOffset() - element.getStartOffset() > 1
                || element.getEndOffset() < element.getDocument().getLength()) {
            set.removeAttributes(set);
            set.addAttributes(element.getAttributes());
        }
    }
    private AffineTransform getInvert(){
        AffineTransform ret=new AffineTransform();
        try{
            ret=transform.createInverse();
        }catch (Exception e){}
        return ret;
    }
    private InlineTextPane getEditor(){
        return this;
    }
    private Window getOwner(Component c){
        if (c instanceof Window) return (Window)c;
        if (c==null) return null;
        return getOwner(c.getParent());
    }
    public void setInputAttributes(AttributeSet attr){
        //MutableAttributeSet at=getInputAttributes();
        inputAttributes=new SimpleAttributeSet();
        inputAttributes.addAttributes(attr);
        
    }
    private class Blinker extends JComponent implements Runnable{
        private boolean running=false;
        private boolean blinking=false;
        private Rectangle oldRegion=null;
        private Thread thread=null;
        @Override
        public void run() {
            while(running && thread !=null){
                if (document==null || !isVisible()) running=false;
                repaint();
                blinking = !blinking;
                try{
                    Thread.sleep(400);
                }catch (InterruptedException e){
                    
                }
            }
            running=false;
            thread=null;
        }
        public void start(){
            if (running) return;
            thread=new Thread(this);
            running=true;
            thread.start();
        }
        public void suspend(){
            thread=null;
            running=false;
        }
        @Override
        public void paint(Graphics g){
            Graphics2D g2=(Graphics2D)g;
            if (dot !=null && blinking){
                g2.setColor(Color.BLACK);
                g2.draw(dot.getCursor());
            }
        }
    }
    private class DocListener implements DocumentListener{
        @Override
        public void insertUpdate(DocumentEvent e) {
//            try{
//               locater.insertCharAt(e.getOffset(),e.getDocument().getText(e.getOffset(),1).charAt(0));
//            } catch (Exception ex){}
            locater=jText.createLocater(null);
            dot=mark=locater.hitPosition(e.getOffset()+e.getLength());
            fireCaretEvent();
            Iterator<DocumentListener> it=documentListeners.iterator();
            while (it.hasNext())
                it.next().insertUpdate(e);
            repaint();
        }
        
        @Override
        public void removeUpdate(DocumentEvent e) {
            
            locater=jText.createLocater(null);
            dot=mark=locater.hitPosition(e.getOffset());
            
            Iterator<DocumentListener> it=documentListeners.iterator();
            while (it.hasNext())
                it.next().removeUpdate(e);
            fireCaretEvent();
            repaint();
        }
        
        @Override
        public void changedUpdate(DocumentEvent e) {
            
            locater=jText.createLocater(null);
            dot=locater.hitPosition(getDot());
            mark=locater.hitPosition(getMark());
            
            Iterator<DocumentListener> it=documentListeners.iterator();
            while (it.hasNext())
                it.next().changedUpdate(e);
            repaint();
        }
        
    }
    private class MListener extends MouseAdapter{
        @Override
        public void mousePressed(MouseEvent e){
            Point p=e.getPoint();
            AffineTransform tx=getInvert();
            tx.transform(p,p);
            if (locater !=null){
                dot=mark=locater.hit(p.x,p.y);
                fireCaretEvent();
                repaint();
            }else{
                dot=mark=null;
            }
        }
        @Override
        public void mouseDragged(MouseEvent e){
            Point p=e.getPoint();
            AffineTransform tx=getInvert();
            tx.transform(p,p);
            if (locater !=null){
                dot=locater.hit(p.x,p.y);
                fireCaretEvent();
                repaint();
            }else{
                dot=mark=null;
            }
        }
    }
    private class KListener extends KeyAdapter{
        private boolean ignorEvent=false;
        @Override
        public void keyPressed(KeyEvent e){
            if (dot==null) return;
            ignorEvent=true;
            TextRow row=dot.getLayout().getTextRow();
            int idx=locater.indexOf(row);
            try{
                switch (e.getKeyCode()){
                    case KeyEvent.VK_UP:
                        if (idx>0){
                            dot=mark=locater.hit(dot.getLogicalPosition().x,locater.get(idx-1).getOffsetY());
                            blinker.blinking=true;
                            blinker.repaint();
                            fireCaretEvent();
                        }
                        e.consume();
                        break;
                    case KeyEvent.VK_DOWN:
                        
                        if (idx<locater.size()-1){
                            dot=mark=locater.hit(dot.getLogicalPosition().x,locater.get(idx+1).getOffsetY());
                            blinker.blinking=true;
                            blinker.repaint();
                            fireCaretEvent();
                        }
                        e.consume();
                        break;
                    case KeyEvent.VK_LEFT:
                        if (dot.getInsertionIndex()>0){
                            dot=mark=locater.previous(dot);
                            //dot=mark=locater.hitPosition(dot.getInsertionIndex()-1);
                            blinker.blinking=true;
                            blinker.repaint();
                            fireCaretEvent();
                        }
                        e.consume();
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (dot.getInsertionIndex()<document.getLength()-1){
                            dot=mark=locater.next(dot);
                            //dot=mark=locater.hitPosition(dot.getInsertionIndex()+1);
                            blinker.blinking=true;
                            blinker.repaint();
                            fireCaretEvent();
                        }
                        e.consume();
                        break;
                    case KeyEvent.VK_BACK_SPACE:
                        if (dot==mark){
                            if (dot.getInsertionIndex()>0){
                                document.remove(dot.getInsertionIndex()-1,1);
                            }
                        }else{
                            removeSelectionString();
                        }
                        e.consume();
                        break;
                    case KeyEvent.VK_DELETE:
                        if (dot==mark){
                            if (dot.getInsertionIndex()<document.getLength()-1){
                                document.remove(dot.getInsertionIndex(),1);
                            }
                        } else{
                            removeSelectionString();
                        }
                        e.consume();
                        break;
                    default :
                        ignorEvent=false;
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        @Override
        public void keyTyped(KeyEvent e){
            if (ignorEvent){
                ignorEvent=false;
                return;
            }
            if (e.isAltDown()||e.isControlDown()) return;
            if (document==null) return;
            insertString(String.valueOf(e.getKeyChar()));
            e.consume();
        }
    }
    private class CListener implements ComponentListener,AncestorListener{
        @Override
        public void componentResized(ComponentEvent e) {
        }
        
        @Override
        public void componentMoved(ComponentEvent e) {
        }
        
        @Override
        public void componentShown(ComponentEvent e) {
            if (!blinker.running)
                blinker.start();
        }
        
        @Override
        public void componentHidden(ComponentEvent e) {
            blinker.suspend();
        }
        
        @Override
        public void ancestorAdded(AncestorEvent event) {
        }
        
        @Override
        public void ancestorRemoved(AncestorEvent event) {
            blinker.suspend();
        }
        
        @Override
        public void ancestorMoved(AncestorEvent event) {
        }
        
        
    }
    private class CEvent extends CaretEvent{
        public CEvent(Object o){
            super(o);
        }
        @Override
        public int getDot() {
            if(dot==null){
                return 0;
            }
            return dot.getInsertionIndex();
        }
        
        @Override
        public int getMark() {
            if (mark==null){
                return 0;
            }
            return mark.getInsertionIndex();
        }
        
    }
    
}
