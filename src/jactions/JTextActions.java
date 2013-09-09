package jactions;
import jui.JTextIcons;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.Caret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
/*
 * JTextActions.java
 *
 * Created on 2007/10/08, 10:50
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author TI
 */
public class JTextActions implements CaretListener,UndoableEditListener{
    protected JTextPane textPane;
    public static final JTextIcons TEXTICON=new JTextIcons();
    private HashMap<String,Action> actionMap;
    private HashMap<String,Action> rActionMap;
    private StyledDocument document =null;
    private UndoManager undoManager;
    private CompoundEdit cEdit=null;
    
    
    /**
     * Creates a new instance of JTextActions
     */
    public JTextActions(JTextPane textPane) {
        this.textPane=textPane;
        textPane.addCaretListener(this);
        Action[] actions=textPane.getActions();
        actionMap=new HashMap<String,Action>();
        for (int i=0;i<actions.length;i++){
            actionMap.put((String)actions[i].getValue(Action.NAME),actions[i]);
        }
        setDocument(textPane.getStyledDocument());
        undoManager=new UndoManager();
        setupActions();
        actionStateChange();
    }
    public Action getAction(String nm){
        return rActionMap.get(nm);
    }
    public UndoManager getUndoManager(){
        return undoManager;
    }
    public void beginCompound(){
        cEdit=new CompoundEdit();
    }
    public void endCompound(){
        if (cEdit ==null) return;
        cEdit.end();
        if (cEdit.canUndo())
            undoManager.addEdit(cEdit);
        cEdit=null;
    }
    public void cancelCompound(){
        if (cEdit==null) return;
        cEdit.end();
        cEdit=null;
    }
    private void setupActions(){
        rActionMap=new HashMap<String,Action>();
        rActionMap.put("Undo",new UndoAction()); //NOI18N
        rActionMap.put("Redo",new RedoAction()); //NOI18N
        rActionMap.put("Cut",new CutAction()); //NOI18N
        rActionMap.put("Copy",new CopyAction()); //NOI18N
        rActionMap.put("Paste",new PasteAction()); //NOI18N
        rActionMap.put("Delete",new DeleteAction()); //NOI18N
        rActionMap.put("SelectAll",new SelectAllAction()); //NOI18N
        rActionMap.put("Bold",new BoldAction()); //NOI18N
        rActionMap.put("Italic",new ItalicAction()); //NOI18N
        rActionMap.put("Underline",new UnderlineAction()); //NOI18N
        rActionMap.put("Strikethrough",new StrikeThroughAction()); //NOI18N
        rActionMap.put("AlignLeft",new AlignLeftAction()); //NOI18N
        rActionMap.put("AlignCenter",new AlignCenterAction()); //NOI18N
        rActionMap.put("AlignRight",new AlignRightAction()); //NOI18N
        rActionMap.put("AlignJustify",new AlignJustifyAction()); //NOI18N
        undoStateChanged();
        actionStateChange();
    }
    public void setDocument(StyledDocument doc){
        if (document !=null){
            document.removeUndoableEditListener(this);
        }
        document=doc;
        if (document !=null) {
            document.addUndoableEditListener(this);
        }
    }
    @Override
    public void caretUpdate(CaretEvent e) {
        actionStateChange();
    }
    @Override
    public void undoableEditHappened(UndoableEditEvent e) {
        if (cEdit==null){
            undoManager.addEdit(e.getEdit());
        }else{
            cEdit.addEdit(e.getEdit());
        }
        undoStateChanged();
    }
    private void undoStateChanged(){
        rActionMap.get("Undo").setEnabled(undoManager.canUndo()); //NOI18N
        rActionMap.get("Redo").setEnabled(undoManager.canRedo()); //NOI18N
    }
    private void actionStateChange(){
        Caret c=textPane.getCaret();
        if (c.getDot()==c.getMark()){
            rActionMap.get("Cut").setEnabled(false); //NOI18N
            rActionMap.get("Copy").setEnabled(false); //NOI18N
            rActionMap.get("Delete").setEnabled(false); //NOI18N
        }else{
            rActionMap.get("Cut").setEnabled(true); //NOI18N
            rActionMap.get("Copy").setEnabled(true); //NOI18N
            rActionMap.get("Delete").setEnabled(true); //NOI18N
        }
        Clipboard clp=Toolkit.getDefaultToolkit().getSystemClipboard();
        if (textPane.getTransferHandler().canImport(textPane,clp.getAvailableDataFlavors())){
            rActionMap.get("Paste").setEnabled(true); //NOI18N
        }else{
            rActionMap.get("Paste").setEnabled(false); //NOI18N
        }
        int ln=document.getLength();
        boolean selBold,selItalic,selUnderline,selStrikeThrough;
        int selAlignment;
        AttributeSet attr;
        if (c.getDot()==c.getMark()){
            int pos=c.getDot()-1;
            if (pos<0) pos=0;
            attr=textPane.getStyledDocument().getCharacterElement(pos).getAttributes();
            selBold=StyleConstants.isBold(attr);
            selItalic=StyleConstants.isItalic(attr);
            selUnderline=StyleConstants.isUnderline(attr);
            selStrikeThrough=StyleConstants.isStrikeThrough(attr);
            selAlignment=StyleConstants.getAlignment(attr);
        }else{
            int start=Math.min(c.getDot(),c.getMark());
            int end=Math.max(c.getDot(),c.getMark());
            if (end==ln) end--;
            attr=textPane.getStyledDocument().getCharacterElement(start).getAttributes();
            selBold=StyleConstants.isBold(attr);
            selItalic=StyleConstants.isItalic(attr);
            selUnderline=StyleConstants.isUnderline(attr);
            selStrikeThrough=StyleConstants.isStrikeThrough(attr);
            selAlignment=StyleConstants.getAlignment(attr);
            for (int i=start+1;i<=end;i++){
                AttributeSet cAttr=textPane.getStyledDocument().getCharacterElement(i).getAttributes();
                if (attr.isEqual(cAttr)) continue;
                if (selBold && !StyleConstants.isBold(cAttr)) selBold=false;
                if (selItalic && !StyleConstants.isItalic(cAttr)) selItalic=false;
                if (selUnderline && !StyleConstants.isUnderline(cAttr)) selUnderline=false;
                if (selStrikeThrough && !StyleConstants.isStrikeThrough(cAttr)) selStrikeThrough=false;
                if (selAlignment !=-1 && selAlignment !=StyleConstants.getAlignment(cAttr)) selAlignment=-1;
                attr=cAttr;
            }
        }
        rActionMap.get("Bold").putValue(Action.SELECTED_KEY,selBold); //NOI18N
        rActionMap.get("Italic").putValue(Action.SELECTED_KEY,selItalic); //NOI18N
        rActionMap.get("Underline").putValue(Action.SELECTED_KEY,selUnderline); //NOI18N
        rActionMap.get("Strikethrough").putValue(Action.SELECTED_KEY,selStrikeThrough); //NOI18N
        rActionMap.get("AlignLeft").putValue(Action.SELECTED_KEY,(selAlignment==StyleConstants.ALIGN_LEFT)); //NOI18N
        rActionMap.get("AlignCenter").putValue(Action.SELECTED_KEY,selAlignment==StyleConstants.ALIGN_CENTER); //NOI18N
        rActionMap.get("AlignRight").putValue(Action.SELECTED_KEY,selAlignment==StyleConstants.ALIGN_RIGHT); //NOI18N
        rActionMap.get("AlignJustify").putValue(Action.SELECTED_KEY,selAlignment==StyleConstants.ALIGN_JUSTIFIED); //NOI18N
    }
    protected class UndoAction extends AbstractAction{
        public UndoAction(){
            putValue(Action.NAME,java.util.ResourceBundle.getBundle("main").getString("ta_undo_mne"));
            putValue(MNEMONIC_KEY,KeyEvent.VK_U);
            putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_Z,ActionEvent.CTRL_MASK));
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            if (undoManager.canUndo()){
                undoManager.undo();
                undoStateChanged();
            }
        }
    }
    protected class RedoAction extends AbstractAction{
        public RedoAction(){
            putValue(Action.NAME,java.util.ResourceBundle.getBundle("main").getString("ta_redo_mne"));
            putValue(MNEMONIC_KEY,KeyEvent.VK_R);
            putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_Y,ActionEvent.CTRL_MASK));
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            if (undoManager.canRedo()){
                undoManager.redo();
                undoStateChanged();
            }
        }
    }
    protected class CutAction extends AbstractAction{
        protected CutAction(){
            putValue(NAME,java.util.ResourceBundle.getBundle("main").getString("ta_cut_mne"));
            putValue(MNEMONIC_KEY,KeyEvent.VK_T);
            putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_X,ActionEvent.CTRL_MASK));
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            actionMap.get("cut-to-clipboard").actionPerformed(e); //NOI18N
            actionStateChange();
        }
    }
    protected class CopyAction extends AbstractAction{
        protected CopyAction(){
            putValue(NAME,java.util.ResourceBundle.getBundle("main").getString("ta_copy_mne"));
            putValue(MNEMONIC_KEY,KeyEvent.VK_C);
            putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_C,ActionEvent.CTRL_MASK));
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            actionMap.get("copy-to-clipboard").actionPerformed(e); //NOI18N
            actionStateChange();
        }
        
    }
    protected class PasteAction extends AbstractAction{
        public PasteAction(){
            putValue(NAME,java.util.ResourceBundle.getBundle("main").getString("ta_paste_mne"));
            putValue(MNEMONIC_KEY,KeyEvent.VK_P);
            putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_V,ActionEvent.CTRL_MASK));
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            actionMap.get("paste-from-clipboard").actionPerformed(e); //NOI18N
            actionStateChange();
        }
    }
    protected class DeleteAction extends AbstractAction{
        public DeleteAction(){
            putValue(NAME,java.util.ResourceBundle.getBundle("main").getString("ta_delete_mne"));
            putValue(MNEMONIC_KEY,KeyEvent.VK_D);
            putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0));
        }
        @Override
        public void actionPerformed(ActionEvent e){
            textPane.replaceSelection(""); //NOI18N
            actionStateChange();
        }
    }
    protected class SelectAllAction extends AbstractAction{
        public SelectAllAction(){
            putValue(NAME,java.util.ResourceBundle.getBundle("main").getString("ta_select_all_mne"));
            putValue(MNEMONIC_KEY,KeyEvent.VK_A);
            putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_A,ActionEvent.CTRL_MASK));
        }
        @Override
        public void actionPerformed(ActionEvent e){
            actionMap.get("select-all").actionPerformed(e); //NOI18N
            actionStateChange();
        }
    }
    protected class BoldAction extends AbstractAction{
        public BoldAction(){
            putValue(Action.SHORT_DESCRIPTION,java.util.ResourceBundle.getBundle("main").getString("ta_bold"));
            putValue(Action.LARGE_ICON_KEY,JTextIcons.BOLD);
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            actionMap.get("font-bold").actionPerformed(e); //NOI18N
            Caret c=textPane.getCaret();
            if (c.getDot() != c.getMark())
                actionStateChange();
        }
    }
    protected class ItalicAction extends AbstractAction{
        public ItalicAction(){
            putValue(Action.SHORT_DESCRIPTION,java.util.ResourceBundle.getBundle("main").getString("ta_italic"));
            putValue(Action.LARGE_ICON_KEY,JTextIcons.ITALIC);
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            actionMap.get("font-italic").actionPerformed(e); //NOI18N
            Caret c=textPane.getCaret();
            if (c.getDot() != c.getMark())
                actionStateChange();
        }
    }
    protected class UnderlineAction extends AbstractAction{
        public UnderlineAction(){
            putValue(Action.SHORT_DESCRIPTION,java.util.ResourceBundle.getBundle("main").getString("ta_underline"));
            putValue(Action.LARGE_ICON_KEY,JTextIcons.UNDERLINE);
        }
        @Override
        public void actionPerformed(ActionEvent e){
            actionMap.get("font-underline").actionPerformed(e); //NOI18N
            Caret c=textPane.getCaret();
            if (c.getDot() != c.getMark())
                actionStateChange();
        }
    }
    protected class StrikeThroughAction extends AbstractAction{
        public StrikeThroughAction(){
            putValue(Action.SHORT_DESCRIPTION,java.util.ResourceBundle.getBundle("main").getString("ta_line_through"));
            putValue(Action.LARGE_ICON_KEY,JTextIcons.STRIKETHROUGH);
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            SimpleAttributeSet attr=new SimpleAttributeSet();
            StyleConstants.setStrikeThrough(attr,!StyleConstants.isStrikeThrough(textPane.getInputAttributes()));
            textPane.setCharacterAttributes(attr,false);
            Caret c=textPane.getCaret();
            if (c.getDot() != c.getMark())
                actionStateChange();
        }
    }
    protected class AlignLeftAction extends AbstractAction{
        public AlignLeftAction(){
            putValue(Action.SHORT_DESCRIPTION,java.util.ResourceBundle.getBundle("main").getString("ta_align_left"));
            putValue(Action.LARGE_ICON_KEY,JTextIcons.ALIGNLEFT);
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            actionMap.get("left-justify").actionPerformed(e); //NOI18N
            actionStateChange();
        }
        
    }
    protected class AlignCenterAction extends AbstractAction{
        public AlignCenterAction(){
            putValue(Action.SHORT_DESCRIPTION,java.util.ResourceBundle.getBundle("main").getString("ta_align_center"));
            putValue(Action.LARGE_ICON_KEY,JTextIcons.ALIGNCENTER);
        }
        @Override
        public void actionPerformed(ActionEvent e){
            actionMap.get("center-justify").actionPerformed(e); //NOI18N
            actionStateChange();
        }
    }
    protected class AlignRightAction extends AbstractAction{
        public AlignRightAction(){
            putValue(Action.SHORT_DESCRIPTION,java.util.ResourceBundle.getBundle("main").getString("ta_align_right"));
            putValue(Action.LARGE_ICON_KEY,JTextIcons.ALIGNRIGHT);
        }
        @Override
        public void actionPerformed(ActionEvent e){
            actionMap.get("right-justify").actionPerformed(e); //NOI18N
            actionStateChange();
        }
    }
    protected class AlignJustifyAction extends AbstractAction{
        public AlignJustifyAction(){
            putValue(Action.SHORT_DESCRIPTION,java.util.ResourceBundle.getBundle("main").getString("ta_justify"));
            putValue(Action.LARGE_ICON_KEY,JTextIcons.KINTO);
        }
        @Override
        public void actionPerformed(ActionEvent e){
            SimpleAttributeSet attr=new SimpleAttributeSet();
            StyleConstants.setAlignment(attr,StyleConstants.ALIGN_JUSTIFIED);
            textPane.setCharacterAttributes(attr,false);
            actionStateChange();
        }
    }
}
