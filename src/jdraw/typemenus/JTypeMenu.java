/*
 * JFontMenu.java
 *
 * Created on 2008/06/02, 20:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jdraw.typemenus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JMenu;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyledDocument;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import jedit.textobjectedit.JChangeTextEdit;
import jobject.JLeaf;
import jscreen.JDocumentViewer;
import jscreen.JRequest;
import jobject.JText;
import jobject.text.InlineTextPane;

/**
 *
 * @author takashi
 */
public class JTypeMenu extends JMenu implements CaretListener,ActionListener,ItemListener,ComponentListener,UndoableEditListener{
    JFontMenu fontMenu;
    ResentFonts resentFonts;
    JFontSize fontSize;
    JAlignmentMenu alignment;
    JStyleMenu styleMenu;
    ParagraphOptionMenu pOptionMenu;
    JDocumentViewer viewer=null;
    /** Creates a new instance of JFontMenu */
    public JTypeMenu() {
        setText(java.util.ResourceBundle.getBundle("main").getString("menu_text_format"));
        setMnemonic(KeyEvent.VK_T);
        fontMenu=new JFontMenu();
        fontMenu.addActionListener(this);
        resentFonts=new ResentFonts();
        resentFonts.addActionListener(this);
        fontSize=new JFontSize();
        fontSize.addActionListener(this);
        alignment=new JAlignmentMenu();
        alignment.addActionListener(this);
        styleMenu=new JStyleMenu();
        styleMenu.addActionListener(this);
        pOptionMenu=new ParagraphOptionMenu();
        pOptionMenu.addActionListener(this);
        add(fontMenu);
        add(resentFonts);
        add(fontSize);
        addSeparator();
        add(styleMenu);
        add(alignment);
        addSeparator();
        add(pOptionMenu);
        stateChanged();
    }
    public void setViewer(JDocumentViewer v){
        if (viewer !=null){
            viewer.getDocument().removeItemListener(this);
            viewer.getTextPane().removeComponentListener(this);
        }
        viewer=v;
        if (viewer !=null){
            viewer.getDocument().addItemListener(this);
            viewer.getTextPane().addComponentListener(this);
        }
        stateChanged();
    }
    public ResentFonts getResetnFonts(){
        return resentFonts;
    }
    private void stateChanged(){
        if (viewer==null){
            setChildEnabled(false);
            return;
        }
        if (viewer.getTextPane().isVisible()){
            setChildEnabled(true);
        }else{
            JRequest req=viewer.getCurrentRequest();
            boolean en=false;
            JText jt=null;
            for (int i=0;i<req.size();i++){
                if (req.get(i)instanceof JText){
                    en=true;
                    jt=(JText)req.get(i);
                    break;
                }
                    
            }
            setChildEnabled(en);
            if (en){
                AttributeSet attr=jt.getStyledDocument().getCharacterElement(0).getAttributes();
                setAttributesToMenu(attr);
            }
        }
    }
    private void setChildEnabled(boolean en){
        fontMenu.setEnabled(en);
        resentFonts.setEnabled(en);
        fontSize.setEnabled(en);
        alignment.setEnabled(en);
        styleMenu.setEnabled(en);
        pOptionMenu.setEnabled(en);
    }
    public void setAttributesToMenu(AttributeSet attr){
        fontMenu.setAttributes(attr);
        resentFonts.setAttributes(attr);
        fontSize.setAttributes(attr);
        alignment.setAttributes(attr);
        styleMenu.setAttributes(attr);
        pOptionMenu.setAttributes(attr);
    }
    @Override
    public void caretUpdate(CaretEvent e) {
        InlineTextPane pane=(InlineTextPane)e.getSource();
        setAttributesToMenu(pane.getInputAttributes());
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (viewer==null) return;
        if (!(e.getSource() instanceof JTypeMenuItem)) return;
        JTypeMenuItem itm=(JTypeMenuItem)e.getSource();
        InlineTextPane pane=viewer.getTextPane();
        if (pane.isVisible()){
            if (itm==alignment || itm==pOptionMenu){
                pane.setParagraphAttributes(itm.getAttributes(),false);
            }else{
                pane.setCharacterAttributes(itm.getAttributes(),false);
            }
            if (itm==fontMenu)
                resentFonts.addItem(fontMenu.getAttributes());
        }else{
            Vector<JText> selectedTexts=new Vector<JText>();
            JRequest req=viewer.getCurrentRequest();
            for (int i=0;i<req.size();i++){
                Object o=req.get(i);
                if (o instanceof JText)
                    selectedTexts.add((JText)o);
            }
            if (!selectedTexts.isEmpty()){
                startCompound(selectedTexts);
                for (int i=0;i<selectedTexts.size();i++){
                    StyledDocument doc=selectedTexts.get(i).getStyledDocument();
                    doc.addUndoableEditListener(this);
                    if (itm==alignment || itm==pOptionMenu){
                        doc.setParagraphAttributes(0,doc.getLength(),itm.getAttributes(),false);
                    }else{
                       doc.setCharacterAttributes(0,doc.getLength(),itm.getAttributes(),false);
                    }                  
                    doc.removeUndoableEditListener(this);
                }
                if (itm==fontMenu)
                    resentFonts.addItem(fontMenu.getAttributes());
                endCompound();
            }
        }
    }
    private CompoundEdit cedit=null;
    private HashMap<StyledDocument,JText> textMap;
    private void startCompound(Vector<JText> vec){
        cedit=new CompoundEdit();
        textMap=new HashMap<StyledDocument,JText>();
        for (int i=0;i<vec.size();i++){
            JText jt=vec.get(i);
            textMap.put(jt.getStyledDocument(),jt);
        }
    }
    @Override
    public void undoableEditHappened(UndoableEditEvent e){
        StyledDocument doc=(StyledDocument)e.getSource();
        JText targetText=textMap.get(doc);
        UndoableEdit anEdit=new JChangeTextEdit(viewer,(JLeaf)targetText,e.getEdit(),true);
        cedit.addEdit(anEdit);
    }
    private void endCompound(){
        cedit.end();
        viewer.getDocument().fireUndoEvent(cedit);
        cedit=null;
        textMap=null;
    }
    @Override
    public void itemStateChanged(ItemEvent e) {
        stateChanged();
    }
    //viewer上のコンポーネントが変更されたときの処理.
    @Override
    public void componentResized(ComponentEvent e) {
    }
    @Override
    public void componentMoved(ComponentEvent e) {
    }
    @Override
    public void componentShown(ComponentEvent e) {
        InlineTextPane pane=(InlineTextPane)e.getSource();
        pane.addCaretListener(this);
        stateChanged();
    }
    @Override
    public void componentHidden(ComponentEvent e) {
        InlineTextPane pane=(InlineTextPane)e.getSource();
        pane.removeCaretListener(this);
        stateChanged();
    }
    
}
