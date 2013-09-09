/*
 * JUndoRedoAction.java
 *
 * Created on 2007/09/04, 9:41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jactions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;
import jobject.JDocument;
import jscreen.JDragPane;

/**
 *
 * @author i002060
 */
public class JUndoRedoAction implements UndoableEditListener{
    private JDocument document=null;
    public  Undo undoAction;
    public Redo redoAction;
    /** Creates a new instance of JUndoRedoAction */
    public JUndoRedoAction(){
        document=null;
        undoAction=new Undo();
        redoAction=new Redo();
    }
    public JUndoRedoAction(JDocument doc) {
        undoAction=new Undo();
        redoAction=new Redo();
        setDocument(doc);
    }
    public void setDocument( JDocument doc){
        if (document !=null){
            document.removeUndoableEditListener(this);
        }
        document=doc;
        if (document !=null)
            document.addUndoableEditListener(this);
    }
    private void stateChanged(){
       if (document==null) return; 
       UndoManager m=document.getCurrentPage().getUndoManager();
       if (m==null) return;
       if (m.canUndo()){
           undoAction.putValue(undoAction.NAME,m.getUndoPresentationName()+"(U)"); //NOI18N
           undoAction.setEnabled(true);
       }else{
           undoAction.putValue(undoAction.NAME,java.util.ResourceBundle.getBundle("main").getString("ura_undo"));
           undoAction.setEnabled(false);
       }
       if (m.canRedo()){
           redoAction.putValue(redoAction.NAME,m.getRedoPresentationName()+"(R)"); //NOI18N
           redoAction.setEnabled(true);
       }else{
           redoAction.putValue(redoAction.NAME,java.util.ResourceBundle.getBundle("main").getString("ura_redo"));
           redoAction.setEnabled(false);
           
       }
    }
    public void undoableEditHappened(UndoableEditEvent e) {
        stateChanged();
    }
    public class Undo extends AbstractAction{
        public Undo(){
            putValue(NAME,java.util.ResourceBundle.getBundle("main").getString("ura_undo_mne"));
            putValue(MNEMONIC_KEY,KeyEvent.VK_U);
            putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_Z,ActionEvent.CTRL_MASK));
            setEnabled(false);
            
        }
        public void actionPerformed(ActionEvent e) {
            if (document==null) return;
            JDragPane dragPane=document.getViewer().getDragPane();
            if(dragPane.isDragging) return;
            UndoManager m=document.getCurrentPage().getUndoManager();
            if (m==null || !m.canUndo()) return;
            m.undo();
            stateChanged();
            document.fireUndoRedoEvent(new JUndoRedoEvent(this,JUndoRedoEvent.UNDO));
            if (document.getViewer() != null) document.getViewer().repaint();
        }        
    }
    public class Redo extends AbstractAction{
        public Redo(){
            putValue(NAME,java.util.ResourceBundle.getBundle("main").getString("ura_redo_mne"));
            putValue(MNEMONIC_KEY,KeyEvent.VK_R);
            putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_Y,ActionEvent.CTRL_MASK));
            setEnabled(false);
            
        }
        public void actionPerformed(ActionEvent e) {
            if (document==null) return;
            JDragPane dragPane=document.getViewer().getDragPane();
            if(dragPane.isDragging) return;
            UndoManager m=document.getCurrentPage().getUndoManager();
            if (m==null || !m.canRedo()) return;
            m.redo();
            stateChanged();
            document.fireUndoRedoEvent(new JUndoRedoEvent(this,JUndoRedoEvent.REDO));
            if (document.getViewer() != null) document.getViewer().repaint();
        } 
    }
}
