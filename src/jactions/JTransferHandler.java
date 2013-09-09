/*
 * JTransferHandler.java
 *
 * Created on 2007/09/05, 10:26
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jactions;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import jedit.JInsertObjectsEdit;
import jedit.pathedit.JDeleteSegmentEdit;
import jobject.JDocument;
import jobject.JImageObject;
import jobject.JLayer;
import jobject.JLeaf;
import jobject.JObject;
import jobject.JPage;
import jobject.JTextObject;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;
import jscreen.JRequest;
import jobject.JText;
import jobject.text.InlineTextPane;

/**
 *
 * @author i002060
 */
public class JTransferHandler implements ItemListener,FlavorListener,ClipboardOwner,CaretListener{
    private DataFlavor JObjectFlavor;
    private Clipboard clipboard;
    public  CutAction cutAction;
    public  CopyAction copyAction;
    public  PasteAction pasteAction;
    public  ClearAction clearAction;
    public SelectAllAction selectAllAction;
    public JDocumentViewer viewer;
    /**
     * Creates a new instance of JTransferHandler
     */
    public JTransferHandler(JDocumentViewer view) {
        JObjectFlavor=new DataFlavor(JObject.class,"JObject");
        clipboard=Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.addFlavorListener(this);
        cutAction=new CutAction();
        copyAction=new CopyAction();
        pasteAction=new PasteAction();
        clearAction=new ClearAction();
        selectAllAction=new SelectAllAction();
        viewer=view;
    }
    public boolean canImport(DataFlavor[] flavors){
        if (viewer !=null && viewer.getTextPane().isVisible()){
            return isStringFlavor(flavors);
        }
        boolean ret=(isJObjectFlavor(flavors) || isImageFlavor(flavors) || isStringFlavor(flavors));
        return ret;
    }
    public void exportToClipboard(){
        Transferable tf=createTransferable();
        if (tf!=null){
            clipboard.setContents(tf,this);
        }
    }
    public void importFromClipboard(){
        DataFlavor[] avFlavor=clipboard.getAvailableDataFlavors();
        Rectangle2D rc=new Rectangle2D.Double();
        rc.setFrame(viewer.getVisibleRect());
        rc=viewer.getEnvironment().getToAbsoluteTransform().createTransformedShape(rc).getBounds2D();
        try{
            if (isJObjectFlavor(avFlavor) && !viewer.getTextPane().isVisible()){
                Vector<JLeaf> data=(Vector<JLeaf>)clipboard.getData(JObjectFlavor);
                viewer.getDocument().fireUndoEvent(new JInsertObjectsEdit(viewer,data));
                viewer.repaint();
            }else if(isStringFlavor(avFlavor)){
                String str=(String)clipboard.getData(DataFlavor.stringFlavor);
                if (viewer.getTextPane().isVisible()){
                    viewer.getTextPane().insertString(str);
                }else{
                    JTextObject jbo=new JTextObject(new Point2D.Double(0,0),str);
                    Rectangle2D or=jbo.getSelectionBounds();
                    double sx=rc.getX()+(rc.getWidth()-or.getWidth())/2;
                    double sy=rc.getY()+(rc.getHeight()-or.getHeight())/2;
                    jbo=new JTextObject(new Point2D.Double(sx,sy),str);
                    Vector<JLeaf> data=new Vector<JLeaf>();
                    data.add(jbo);
                    viewer.getDocument().fireUndoEvent(new JInsertObjectsEdit(viewer,data));
                    viewer.repaint();
                }
            }else if(isImageFlavor(avFlavor)){
                Image img=(Image)clipboard.getData(DataFlavor.imageFlavor);
                ImageIcon icon=new ImageIcon(img);
                double sx=rc.getX()+(rc.getWidth()-icon.getIconWidth())/2;
                double sy=rc.getY()+(rc.getHeight()-icon.getIconHeight())/2;
                JImageObject jio=new JImageObject(img,new Point2D.Double(sx,sy));
                Vector<JLeaf> data=new Vector<JLeaf>();
                data.add(jio);
                viewer.getDocument().fireUndoEvent(new JInsertObjectsEdit(viewer,data));
                viewer.repaint();
            }
        }catch (UnsupportedFlavorException e) {
            e.printStackTrace();
        } catch(IOException es){
            es.printStackTrace();
        }
    }
    public Transferable createTransferable(){
        if (viewer.getTextPane().isVisible()){
            String s=viewer.getTextPane().getSelectionString();
            if (s==null) return null;
            return new StringSelection(s);
        }
        JRequest req=viewer.getCurrentRequest();
        if (req.isEmpty()) return null;
        Vector<JPathCopySet> copyset=JPathCopySet.createJPathCopySet(req);
        if (copyset==null || copyset.isEmpty()) return null;
        Vector<JLeaf> ret=new Vector<JLeaf>();
        for (int i=0;i<copyset.size();i++){
            Vector<JLeaf> lfs=copyset.get(i).createCopyObjects();
            if (lfs !=null){
                for (int j=0;j<lfs.size();j++){
                    ret.add(lfs.get(j));
                }
            }
        }
        if (ret.isEmpty()) return null;
        return new JObjectTransferable(ret);
    }
    private boolean isJObjectFlavor(DataFlavor[] flavors){
        for(int i=0;i<flavors.length;i++){
            if (flavors[i].equals(JObjectFlavor))
                return true;
        }
        return false;
    }
    private boolean isImageFlavor(DataFlavor[] flavors){
        for (int i=0;i<flavors.length;i++){
            if (flavors[i].equals(DataFlavor.imageFlavor))
                return true;
        }
        return false;
    }
    private boolean isStringFlavor(DataFlavor[] flavors){
        for (int i=0;i<flavors.length;i++){
            if (flavors[i].equals(DataFlavor.stringFlavor))
                return true;
        }
        return false;
    }
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }
    public Clipboard getClipboard(){
        return clipboard;
    }
    
    
    
    private  void deletePath(){
        Vector<JPathDeleteSet> js=JPathDeleteSet.createJPathEditSets(viewer.getCurrentRequest());
        if (!js.isEmpty()){
            viewer.getDocument().fireUndoEvent(new JDeleteSegmentEdit(viewer,js));
            viewer.getDocument().fireUndoRedoEvent(new JUndoRedoEvent(this,JUndoRedoEvent.REDO));
            viewer.repaint();
        }
    }
    
    public void itemStateChanged(ItemEvent e) {
        if (viewer.getTextPane().isVisible()) return;
        JRequest source=((JDocument)e.getSource()).getCurrentPage().getRequest();
        if (source.isEmpty()){
            cutAction.setEnabled(false);
            copyAction.setEnabled(false);
            clearAction.setEnabled(false);
        }else{
            cutAction.setEnabled(true);
            copyAction.setEnabled(true);
            clearAction.setEnabled(true);
        }
    }
    
    public void flavorsChanged(FlavorEvent e) {
        if (canImport(clipboard.getAvailableDataFlavors())) {
            pasteAction.setEnabled(true);
        }else{
            pasteAction.setEnabled(false);
        }
    }
    
    public void caretUpdate(CaretEvent e) {
        InlineTextPane il=(InlineTextPane)e.getSource();
        if (!il.isVisible()) return;
        if (e.getDot()==e.getMark()){
            cutAction.setEnabled(false);
            copyAction.setEnabled(false);
            clearAction.setEnabled(false);
        }else{
            cutAction.setEnabled(true);
            copyAction.setEnabled(true);
            clearAction.setEnabled(true);
        }
    }
    
    
    public class JObjectTransferable implements Transferable{
        private Vector<JLeaf> data;
        public JObjectTransferable(Vector<JLeaf> obj){
            data=new Vector<JLeaf>();
            try{
                for (int i=0;i<obj.size();i++){
                    data.add((JLeaf)obj.get(i).clone());
                }
            } catch (CloneNotSupportedException e){
                e.printStackTrace();
            }
        }
        private boolean hasTextFlavor(){
            for (int i=0;i<data.size();i++){
                Object o=data.get(i);
                if (o instanceof JText){
                    return true;
                }
            }
            return false;
        }
        public DataFlavor[] getTransferDataFlavors() {
            Vector<DataFlavor> ret=new Vector<DataFlavor>();
            ret.add(JObjectFlavor);
            if (hasTextFlavor()){
                ret.add(DataFlavor.stringFlavor);
            }
            ret.add(DataFlavor.imageFlavor);
            DataFlavor[] ra=new DataFlavor[ret.size()];
            ret.toArray(ra);
            return ra;
        }
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            DataFlavor[] flavors=getTransferDataFlavors();
            for (int i=0;i<flavors.length;i++){
                if (flavors[i].equals(flavor))
                    return true;
            }
            return false;
        }
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (!isDataFlavorSupported(flavor))
                throw new UnsupportedFlavorException(flavor);
            
            if (flavor.equals(JObjectFlavor)){
                return data;
            }
            if (flavor.equals(DataFlavor.stringFlavor)){
                String s=null;
                for (int i=0;i<data.size();i++){
                    Object o=data.get(i);
                    if (o instanceof JText){
                        JText jt=(JText)o;
                        try{
                            if (s==null)
                                s=jt.getStyledDocument().getText(0,jt.getStyledDocument().getLength());
                            else
                                s+=" "+jt.getStyledDocument().getText(0,jt.getStyledDocument().getLength());
                        }catch (BadLocationException e) {
                            s="";
                        }
                    }
                }
                return s;
            }
            Rectangle2D br=null;
            for (int i=0;i<data.size();i++){
                Rectangle2D r=data.get(i).getBounds();
                if (r!=null){
                    if (br==null){
                        br=r;
                    }else{
                        br.add(r);
                    }
                }
            }
            BufferedImage img=new BufferedImage((int)(Math.round(br.getWidth())),(int)(Math.round(br.getHeight())),BufferedImage.TYPE_INT_ARGB);
            Graphics2D g=img.createGraphics();
            JEnvironment env=new JEnvironment();
            env.addClip(br);
            //g.setColor(Color.WHITE);
            g.setColor(new Color(255,255,255,0));
            g.translate(-br.getX(),-br.getY());
            g.fill(br);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            for (int i=data.size()-1;i>=0;i--){
                data.get(i).paintThis(env.getClip(),g);
            }
            return img;
        }
        
        
    }
    public class CutAction extends AbstractAction{
        public CutAction(){
            putValue(NAME,"カット(T)");
            putValue(MNEMONIC_KEY,KeyEvent.VK_T);
            putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_X,ActionEvent.CTRL_MASK));
            setEnabled(false);
        }
        public void actionPerformed(ActionEvent e) {
            exportToClipboard();
            deletePath();
        }
    }
    public class CopyAction extends AbstractAction{
        public CopyAction(){
            putValue(NAME,"コピー(C)");
            putValue(MNEMONIC_KEY,KeyEvent.VK_C);
            putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_C,ActionEvent.CTRL_MASK));
            setEnabled(false);
        }
        public void actionPerformed(ActionEvent e) {
            exportToClipboard();
        }
        
    }
    public class PasteAction extends AbstractAction{
        public PasteAction(){
            putValue(NAME,"ペースト(P)");
            putValue(MNEMONIC_KEY,KeyEvent.VK_P);
            putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_V,ActionEvent.CTRL_MASK));
            setEnabled(canImport(clipboard.getAvailableDataFlavors()));
        }
        public void actionPerformed(ActionEvent e) {
            importFromClipboard();
            
        }
        
    }
    public class ClearAction extends AbstractAction{
        public ClearAction(){
            putValue(NAME,"削除(D)");
            putValue(MNEMONIC_KEY,KeyEvent.VK_D);
            putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0));
            setEnabled(false);
        }
        public void actionPerformed(ActionEvent e) {
            deletePath();
        }
        
    }
    public class SelectAllAction extends AbstractAction{
        public SelectAllAction(){
            putValue(NAME,"全てを選択（A)");
            putValue(MNEMONIC_KEY,KeyEvent.VK_A);
            putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_A,ActionEvent.CTRL_MASK));
            setEnabled(true);
        }
        public void actionPerformed(ActionEvent e) {
            JRequest req=viewer.getCurrentRequest();
            req.clear();
            JPage page=viewer.getCurrentPage();
            for (int i=0;i<page.size();i++){
                JLayer l=page.get(i);
                if (!l.isVisible() || !l.isEnabled()) continue;
                for (int j=0;j<l.size();j++){
                    req.add(l.get(j));
                }
            }
            viewer.repaint();
        }
        
    }
}
