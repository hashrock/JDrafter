/*
 * JEditActions.java
 *
 * Created on 2008/05/18, 15:58
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jactions;

import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.StyledDocument;
import jedit.pathedit.JDeleteSegmentEdit;
import jobject.JLayer;
import jobject.JPage;
import jscreen.JDocumentViewer;
import jscreen.JRequest;
import jobject.text.InlineTextPane;

/**
 *
 * @author takashi
 */
public class JEditActions implements FlavorListener,ItemListener,CaretListener{
    public  CutAction cutAction;
    public  CopyAction copyAction;
    public  PasteAction pasteAction;
    public  ClearAction clearAction;
    public SelectAllAction selectAllAction;
    private JDocumentViewer viewer=null;
    /** Creates a new instance of JEditActions */
    public JEditActions() {
        cutAction=new CutAction();
        copyAction=new CopyAction();
        pasteAction=new PasteAction();
        clearAction=new ClearAction();
        selectAllAction=new SelectAllAction();
    }
    private void actionStateChanged(){
        if (viewer==null){
            cutAction.setEnabled(false);
            copyAction.setEnabled(false);
            clearAction.setEnabled(false);
            pasteAction.setEnabled(false);
            selectAllAction.setEnabled(false);
            return;
        }
        if (viewer.getTextPane().isVisible()){
            InlineTextPane e=viewer.getTextPane();
            if (e.getDot()==e.getMark()){
                cutAction.setEnabled(false);
                copyAction.setEnabled(false);
                clearAction.setEnabled(false);
            }else{
                cutAction.setEnabled(true);
                copyAction.setEnabled(true);
                clearAction.setEnabled(true);
            }
        }else{
            JRequest source=viewer.getCurrentRequest();
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
        selectAllAction.setEnabled(true);
        if (viewer.getJTransferHandler().canImport(viewer.getJTransferHandler().getClipboard().getAvailableDataFlavors())) {
            pasteAction.setEnabled(true);
        }else{
            pasteAction.setEnabled(false);
        }
    }
    public void setViewer(JDocumentViewer view){
        if (view==viewer) return;
        if (viewer !=null){
            viewer.getDocument().removeItemListener(this);
            viewer.getJTransferHandler().getClipboard().removeFlavorListener(this);
            viewer.getTextPane().removeCaretListener(this);
        }
        if (view !=null){
            view.getDocument().addItemListener(this);
            view.getJTransferHandler().getClipboard().addFlavorListener(this);
            view.getTextPane().addCaretListener(this);
        }
        actionStateChanged();
        viewer=view;
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
        actionStateChanged();
    }
    
    public void flavorsChanged(FlavorEvent e) {
        actionStateChanged();
    }
    
    public void caretUpdate(CaretEvent e) {
        actionStateChanged();
    }
    public class CutAction extends AbstractAction{
        public CutAction(){
            putValue(NAME,java.util.ResourceBundle.getBundle("main").getString("menu_edit_cut"));
            putValue(MNEMONIC_KEY,KeyEvent.VK_T);
            putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_X,ActionEvent.CTRL_MASK));
            setEnabled(false);
        }
        public void actionPerformed(ActionEvent e) {
            if (viewer !=null){
                viewer.getJTransferHandler().exportToClipboard();
                if (viewer.getTextPane().isVisible())
                    viewer.getTextPane().removeSelectionString();
                else
                    deletePath();
                actionStateChanged();
            }
        }
    }
    public class CopyAction extends AbstractAction{
        public CopyAction(){
            putValue(NAME,java.util.ResourceBundle.getBundle("main").getString("menu_edit_copy"));
            putValue(MNEMONIC_KEY,KeyEvent.VK_C);
            putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_C,ActionEvent.CTRL_MASK));
            setEnabled(false);
        }
        public void actionPerformed(ActionEvent e) {
            if (viewer !=null){
                viewer.getJTransferHandler().exportToClipboard();
            }
            actionStateChanged();
        }
        
    }
    public class PasteAction extends AbstractAction{
        public PasteAction(){
            putValue(NAME,java.util.ResourceBundle.getBundle("main").getString("menu_edit_paste"));
            putValue(MNEMONIC_KEY,KeyEvent.VK_P);
            putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_V,ActionEvent.CTRL_MASK));
            if (viewer==null){
                setEnabled(false);
            }else{
                setEnabled(viewer.getJTransferHandler().canImport(viewer.getJTransferHandler().getClipboard().getAvailableDataFlavors()));
            }
        }
        public void actionPerformed(ActionEvent e) {
            if (viewer !=null){
                viewer.getJTransferHandler().importFromClipboard();
            }
            
        }
        
    }
    public class ClearAction extends AbstractAction{
        public ClearAction(){
            putValue(NAME,java.util.ResourceBundle.getBundle("main").getString("menu_edit_delete"));
            putValue(MNEMONIC_KEY,KeyEvent.VK_D);
            putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0));
            setEnabled(false);
        }
        public void actionPerformed(ActionEvent e) {
            if (viewer.getTextPane().isVisible()){
                viewer.getTextPane().removeSelectionString();
            }else{
                deletePath();
            }
        }
        
    }
    public class SelectAllAction extends AbstractAction{
        public SelectAllAction(){
            putValue(NAME,java.util.ResourceBundle.getBundle("main").getString("menu_edit_select_all"));
            putValue(MNEMONIC_KEY,KeyEvent.VK_A);
            putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_A,ActionEvent.CTRL_MASK));
            setEnabled(true);
        }
        public void actionPerformed(ActionEvent e) {
            if (viewer==null) return;
            if (viewer.getTextPane().isVisible()){
               InlineTextPane tp=viewer.getTextPane();
               StyledDocument doc=tp.getDocument();
               tp.setSelection(0,doc.getLength()-1);
            }else{
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
}
