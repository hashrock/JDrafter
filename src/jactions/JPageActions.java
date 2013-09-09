/*
 * JPageActions.java
 *
 * Created on 2007/12/11, 20:55
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jactions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.print.PageFormat;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import jobject.JDocument;
import jobject.JPage;
import jprinter.JPageFormat;

/**
 *
 * @author takashi
 */
public class JPageActions implements ItemListener{
    public static JDocument document=null;
    public static JPage copiedPage=null;
    public static NewPageAction newPageAction=null;
    public static InsertPageAction insertPageAction=null;
    public static CopyPageAction copyPageAction=null;
    public static CutPageAction cutPageAction=null;
    public static DeletePageAction deletePageAction=null;
    public static PastePageAction pastePageAction=null;
    
    
    /**
     * Creates a new instance of JPageActions
     */
    public JPageActions(JDocument doc) {
        
        newPageAction=new NewPageAction();
        insertPageAction=new InsertPageAction();
        copyPageAction=new CopyPageAction();
        cutPageAction=new CutPageAction();
        deletePageAction=new DeletePageAction();
        pastePageAction=new PastePageAction();
        setDocument(doc);
        setupActions();
        
    }
    public void setDocument(JDocument doc){
        if (document !=null){
            document.removeItemListener(this);
        }
        document=doc;
        if (document !=null){
            document.addItemListener(this);
        }
        
    }
    public void itemStateChanged(ItemEvent e) {
        setupActions();
    }
    public void setupActions(){
        if (document==null){
            insertPageAction.setEnabled(false);
            newPageAction.setEnabled(false);
            copyPageAction.setEnabled(false);
            pastePageAction.setEnabled(false);
            cutPageAction.setEnabled(false);
            deletePageAction.setEnabled(false);
            return;
        }
        newPageAction.setEnabled(true);
        insertPageAction.setEnabled(true);
        copyPageAction.setEnabled(true);
        if (copiedPage !=null){
            pastePageAction.setEnabled(true);
        }else{
            pastePageAction.setEnabled(false);
        }
        if (document.size()<2){
            cutPageAction.setEnabled(false);
            deletePageAction.setEnabled(false);
        }else{
            cutPageAction.setEnabled(true);
            deletePageAction.setEnabled(true);
        }
    }
    public class NewPageAction extends AbstractAction{
        public NewPageAction(){
            putValue(NAME,"新規ページ(N)");
            putValue(MNEMONIC_KEY,KeyEvent.VK_N);
        }
        public void actionPerformed(ActionEvent e){
            JPage page=new JPage();
            PageFormat pformat=document.getCurrentPage().getPageFormat();
            PageFormat np=document.printerJob.pageDialog(pformat);
            if (np==pformat) return;
            page.setPageFormat(new JPageFormat(np));
            document.add(page);
            setupActions();
        }
        
    }
    public class InsertPageAction extends AbstractAction{
        public InsertPageAction(){
            putValue(NAME,"ページ挿入");
        }
        public void actionPerformed(ActionEvent e){
            JPage page=new JPage();
            PageFormat pformat=document.getCurrentPage().getPageFormat();
            PageFormat np=document.printerJob.pageDialog(pformat);
            if (np==pformat) return;
            page.setPageFormat(new JPageFormat(np));
            document.add(document.getPageIndex()+1,page);
            setupActions();
            document.getViewer().isDraftMode=false;
     
        }
    }
    public class CopyPageAction extends AbstractAction{
        public CopyPageAction(){
            putValue(NAME,"ページコピー");
        }
        public void actionPerformed(ActionEvent e) {
            try {
                copiedPage=(JPage)document.getCurrentPage().clone();
                setupActions();
            } catch (CloneNotSupportedException ex) {
                ex.printStackTrace();
            }
        }
    }
    public class PastePageAction extends AbstractAction{
        public PastePageAction(){
            putValue(NAME,"ページペースト");
        }
        public void actionPerformed(ActionEvent e) {
            try {
                document.add(document.getPageIndex()+1,(JPage)copiedPage.clone());
                setupActions();
            } catch (CloneNotSupportedException ex) {
                ex.printStackTrace();
            }
        }
    }
    public class CutPageAction extends AbstractAction{
        public CutPageAction(){
            putValue(NAME,"ページカット");
        }
        public void actionPerformed(ActionEvent e) {
            try {
                copiedPage=(JPage)document.getCurrentPage().clone();
                document.remove(document.getCurrentPage());
                setupActions();
            } catch (CloneNotSupportedException ex) {
                ex.printStackTrace();
            }
        }
    }
    public class DeletePageAction extends AbstractAction{
        public DeletePageAction(){
            putValue(NAME,"ページ削除");
            
        }
        public void actionPerformed(ActionEvent e) {
            if (JOptionPane.YES_OPTION==JOptionPane.showConfirmDialog((Component)e.getSource(),"選択したページは全て削除されます．削除しますか?",
                    "JDraw",JOptionPane.YES_NO_OPTION)){
                document.remove(document.getCurrentPage());
                setupActions();
            }
        }
        
    }
    
}
