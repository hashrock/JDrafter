/*
 * JDocument.java
 *
 * Created on 2007/08/27, 14:55
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jobject;

import jactions.JUndoRedoListener;
import jactions.JUndoRedoEvent;
import java.awt.Graphics2D;
import java.awt.ItemSelectable;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEdit;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *全ての描画オブジェクトのルートとなる描画オブジェクトです。
 * 複数ページを所有するドキュメントを示します.
 * @author i002060
 */
public class JDocument extends JObject<JObject,JPage>implements ItemSelectable,Pageable,ChangeListener{
    /**
     * カレントのプリンタージョブを保持します.
     */
    public static  final PrinterJob printerJob=PrinterJob.getPrinterJob();
    private transient Vector<UndoableEditListener> undoListener;
    private transient Vector<JUndoRedoListener> undoRedoListeners;
    private transient Vector<ItemListener> itemListeners;
    private transient Vector<ChangeListener> environmentChangeListener;
    private transient JPage currentPage=null;
    //
    private static final long serialVersionUID=110l;
    //
    private transient JDocumentViewer viewer=null;
    /** デフォルトのコンストラクターです。
     * 一つの空白のページを保持するJDocumentを構築します.
     */
    public JDocument() {
        this(new JPage());
    }
    /**
     * 指定するページを保持するJDocumentを構築します.
     * @param page 指定するページ.
     */
    public JDocument(JPage page){
        //printerJob=PrinterJob.getPrinterJob();
        add(page);
        undoListener=new Vector<UndoableEditListener>();
        undoRedoListeners=new Vector<JUndoRedoListener>();
        itemListeners=new Vector<ItemListener>();
        environmentChangeListener=new Vector<ChangeListener>();
        viewer=null;
    }
    /**
     * このJDocumentを表示するJDocumentViewerを指定します.
     * @param v このJDocumentを表示するJDocumentViewer
     */
    public void setViewer(JDocumentViewer v){
        viewer=v;
    }
    /**
     * このJDocumentを表示するJDocumentViewerを返します.
     * @return
     */
    public JDocumentViewer getViewer(){
        return viewer;
    }
    /**
     * このJDocumentに取り消し可能な変更が加えられた際にUndoableEditEventを受け取るために、指定された
     * UndobaleEditListenerを追加します.
     * @param u 指定するUndoableEditListener
     */
    public void addUndoableEditListener(UndoableEditListener u){
        if (undoListener.contains(u)) return;
        undoListener.add(u);
    }
    /**
     * このJDocumentからUndoableEditEventを受け取らないように、指定された
     * UnodoableEditListenerを削除します.
     * @param u 削除するUndoableEditListener
     */
    public void removeUndoableEditListener(UndoableEditListener u){
        undoListener.remove(u);
    }
    /**
     * このJDocumentに取り消し可能な変更又は変更の取消しが加えられた際にJUndoRedoEventを受け取るために、
     * JUndoRedoListenerを追加します.
     * @param l 追加するJUndoRedoListener
     */
    public void addUndoRedoListener(JUndoRedoListener l){
        if (undoRedoListeners.contains(l)) return;
        undoRedoListeners.add(l);
        
    }
    /**
     * このJDocumentに取り消し可能な変更又は変更の取消しが加えられた際にJUndoRedoEventを受け取らないように
     * JUndoRedoListenerを削除します.
     * @param l
     */
    public void removeUndoRedoListener(JUndoRedoListener l){
        undoRedoListeners.remove(l);
    }
    /**
     * UndoableEditEventを通知します.
     * @param e 加えられた変更を保持するUndoableEdit
     */
    public void fireUndoEvent(UndoableEdit e){
        JPage p=getCurrentPage();
        if (e!=null)
            p.getUndoManager().addEdit(e);
        UndoableEditEvent ue=new UndoableEditEvent(p,e);
        if (undoListener==null) return;
        for(int i=0;i<undoListener.size();i++){
            undoListener.get(i).undoableEditHappened(ue);
        }
        fireUndoRedoEvent(new JUndoRedoEvent(this,JUndoRedoEvent.REDO));
        
    }
    /**
     * UnodoRedoEventを通知します.
     * @param e 加えられた変更又は取り消された変更を保持するJUndoRedoEvent
     */
    public void fireUndoRedoEvent(JUndoRedoEvent e){
        if (undoRedoListeners==null) return;
        for (int i=0;i<undoRedoListeners.size();i++){
            undoRedoListeners.get(i).undoRedoEventHappened(e);
        }
    }
    /**
     * 現在の描画環境が変更された際にChangeEventを受け取るためにChangeListenerを
     * 追加します.
     * @param ls 追加するChangeListener
     */
    public void addenvironmentChangeListener(ChangeListener ls){
        if (ls !=null && !environmentChangeListener.contains(ls))
            environmentChangeListener.add(ls);
    }
    /**
     * 現在の描画環境が変更された際にChangeEventを受け取らないようにChangeListenerを
     * 削除します。
     * @param ls 削除するChangeLIstener
     */
    public void removeenvironmentChangeListener(ChangeListener ls){
        environmentChangeListener.remove(ls);
    }
    /**
     * 描画環境が変更されたことを通知します.
     * @param e 描画環境の変更を保持するChangeEvent
     */
    public void fireEnvironmentChange(ChangeEvent e){
        Iterator<ChangeListener> it=environmentChangeListener.iterator();
        while(it.hasNext())
            it.next().stateChanged(e);
    }
    /**
     * 現在の有効なprinterJobjを返します.
     * @return
     */
    public PrinterJob getPrinterJob(){
        return printerJob;
    }
    /**
     * カレントのページインデックスを設定します。
     * @param index　カレントに設定するページインデックス.
     */
    public void setPageIndex(int index){
        if (index>=0 && index<size() && indexOf(getCurrentPage()) !=index){
            setCurrentPage(get(index));
        }
        return;
    }
    /**
     * カレントのページインデックスを返します.
     * @return カレントのページインデックス.
     */
    public int getPageIndex(){
        return indexOf(getCurrentPage());
    }
    /**
     * 指定するページをカレントページに設定します.
     * @param page カレントに指定するページ
     */
    public void setCurrentPage(JPage page){
        if (contains(page) && currentPage !=page){
            currentPage=page;
            fireItemEvent(getCurrentPage().getRequest().getSelectedVector(),ItemEvent.SELECTED);
            fireUndoEvent(null);
        }
    }
    /**
     * カレントページを取得します。
     * @return　カレントページ.
     */
    public JPage getCurrentPage(){
        if (currentPage==null)
            currentPage=get(0);
        return currentPage;
    }
    /**
     * 現在の描画環境を取得します.
     * @return 現在の描画環境
     */
    public JEnvironment getEnvironment(){
        if (getCurrentPage()==null) return null;
        return getCurrentPage().getEnvironment();
    }
    /**
     * ドキュメントの末尾に指定するページを追加します。
     * @param p 追加するJPage
     */
    @Override
    public void add(JPage p){
        if (children.contains(p)) return;
        if(children.add(p))
            p.setParent(this);
        setCurrentPage(p);
        p.getEnvironment().addChangeListener(this);
    }
    /**
     * 指定する位置にページを追加します.
     * @param index 追加指定位置.
     * @param p 追加するページ
     */
    @Override
    public void add(int index,JPage p){
        if (children.contains(p)) return;
        children.add(index,p);
        p.setParent(this);
        setCurrentPage(p);
        p.getEnvironment().addChangeListener(this);
    }
    /**
     * 指定するページをこのドキュメントから削除します.
     * @param p 削除するページ
     */
    @Override
    public void remove(JPage p){
        if (p==getCurrentPage()){
            int index=indexOf(p);
            children.remove(p);
            if(index>=size()){
                index--;
            }
            setCurrentPage(get(index));
        }else{
            children.remove(p);
        }
        p.getEnvironment().removeChangeListener(this);
    }
    /**
     * 指定インデックスにあるページを削除します.
     * @param idx 指定するインデックス.
     * @return 削除されたページ
     */
    @Override
    public JPage remove(int idx){
        JPage cPage=getCurrentPage();
        JPage ret=remove(idx);
        if (cPage==ret){
            if (idx>=size())
                setCurrentPage(get(idx-1)); 
        }
        ret.getEnvironment().removeChangeListener(this);
        return ret;
    }
    /**
     * この描画オブジェクトを保持するJDocumentを
     * 返します.
     * @return この描画オブジェクトを保持するJDocument
     */
    @Override
    public JDocument getDocument(){
        return this;
    }
    /**
     * この描画オブジェクトを保持するJpage
     * @return　常にnullを返します.
     */
    @Override
    public JPage getPage(){
        return null;
    }
    /**
     * 継承のための実装です。何も変更しません。
     * 
     * @param tr 
     * @param req
     * @param p
     */
    @Override
    public void transform(AffineTransform tr,JRequest req,Point p) {
        //Do Nothing
    }
    @Override
    public void transform(AffineTransform tr){
        //DO Nothing
    }
    /**
     * 指定する点にPoint2Dにヒットするオブジェクトを探索し、ヒットしたオブジェクトをJRequestに格納します。
     * @param env 描画環境を保持するJevironment
     * @param req 現在の選択内容及びヒットしたオブジェクトを格納するJRequest
     * @param point ヒットを判定するPoint2D
     * @return 探索結果 JRequest.HIT_NON,JRequest.HIT_OBJECT,JRequew.HIT_PATH,JRequest.HIT_ANCUR,JRequest.HIT_LCONTROL,JRequest.HIT_RCONTROL
     */
    @Override
    public int hitByPoint(JEnvironment env, JRequest req, Point2D point) {
        if (getCurrentPage() != null)
            return getCurrentPage().hitByPoint(env,req,point);
        else
            return req.HIT_NON;
    }
    /**
     * 指定する点にRectangle2Dにヒットするオブジェクトを探索し、ヒットしたオブジェクトをJRequestに格納します。
     * @param env 描画環境を保持するJevironment
     * @param req 現在の選択内容及びヒットしたオブジェクトを格納するJRequest
     * @param rect ヒットを判定するRectangle2D
     */    
    @Override
    public void hitByRect(JEnvironment env, JRequest req, Rectangle2D rect) {
        if (getCurrentPage()!=null)
            getCurrentPage().hitByRect(env,req,rect);
    }
    /**
     * 継承のための実装です.何もしません。
     * @param env
     * @return
     */
    @Override
    public UndoableEdit updateTransform(JEnvironment env) {
        //Do Nothing;
        return null;
    }
    /**
    * 継承のための実装です何もしません。
    * @param env
    * @param rotation
    * @return
    */
    @Override
    public UndoableEdit updateRotate(JEnvironment env,double rotation){
        return null;
    }
    /**
     * このオブジェクトを描画します.
     * @param clip　描画のクリップのためのバウンディングボックス
     * @param g　グラフィックスコンテキスト
     */
    @Override
    public void paintThis(Rectangle2D clip, Graphics2D g) {
    }
    /**
     * このオブジェクトのプレビューを描画します.
     * @param env 描画環境を保持するJEnvironment
     * @param req 選択内容を保持するJRequest
     * @param g グラフィックコンテキスト
     */
    @Override
    public void paintPreview(JEnvironment env, JRequest req, Graphics2D g) {
        //Do Nothing;
    }
    /**
     * このオブジェクトの複製を返します.
     * @return このオブジェクトの複製.
     * @throws java.lang.CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException{
        throw new CloneNotSupportedException();
    }
    /**
     * このオブジェクトの有効な選択範囲を包含する最小のRectangle2Dを返します。
     * @return　このオブジェクトの有効な選択範囲を包含する最小のRectangle2D
     */
    @Override
    public Rectangle2D getSelectionBounds() {
        return null;
    }
    /**
     * このオブジェクトの累積された回転変換がなかった場合の選択範囲を包含する最小の
     * Rectangle2Dを返します。
     * @param x 累積された回転をリセットする際の中心のX座標
     * @param y 累積された回転をリセットする際の中心のY座標
     * @return 累積された回転変換がなかった場合の選択範囲を包含する最小のRectangle2D
     */
    @Override
    public Rectangle2D getOriginalSelectionBounds(double x,double y) {
        return null;
    }
    /**
     * このオブジェクトを自動命名する際の冠詞を返します.
     * @return このオブジェクトを自動命名する際の冠詞.
     */
    @Override
    public String getPrefixer(){
        return "Document";
    }
    /**
     * この描画オブジェクトの描画範囲を包含する最小のRectangle2Dを返します。
     * @return この描画オブジェクトの描画範囲を包含する最小のRectangle2D
     */
    @Override
    public Rectangle2D getBounds() {
        return null;
    }
    /**
     *  この描画オブジェクトに含まれる選択されたオブジェクトを要素とする配列を返します。
     * @return　この描画オブジェクトに含まれる選択されたオブジェクトを要素とする配列.
     */
    @Override
    public Object[] getSelectedObjects() {
        JRequest req=getCurrentPage().getRequest();
        return req.getSelectedVector().toArray();
    }
    /**
     * この描画オブジェクトに含まれる描画オブジェクトの選択が変更された場合に、ItemEventを受け取るために
     * 指定されたItemListenerを追加します.
     * @param l 追加するItemListener
     */
    @Override
    public void addItemListener(ItemListener l) {
        if (!itemListeners.contains(l))
            itemListeners.add(l);
    }
    /**
     * この描画オブジェクトに含まれる描画オブジェクトの選択が変更された場合に、ItemEventを受け取らないようにするために
     * 指定されたItemListenerを削除します。
     * @param l 削除するItemListener
     */    
    @Override
    public void removeItemListener(ItemListener l) {
        itemListeners.remove(l);
    }
    /**
     * ItemListenerにItemEventを通知します.
     * @param o ItemEventが発生したオブジェクト
     * @param stateChange イベントの種類.
     */
    public void fireItemEvent(Object o,int stateChange){
        if (itemListeners==null) return;
        ItemEvent e=new ItemEvent(this,ItemEvent.ITEM_STATE_CHANGED,o,stateChange);
        for (int i=0;i<itemListeners.size();i++){
            itemListeners.get(i).itemStateChanged(e);
        }
    }
    /**
     * この描画オブジェクトが描画するShapeを返します.
     * @return
     */
    @Override
    public Shape getShape() {
        return null;
    }
    private void readObject(java.io.ObjectInputStream in)
    throws IOException, ClassNotFoundException{
        in.defaultReadObject();
        for (int i=0;i<size();i++){
            get(i).setParent(this);
        }
        //printerJob=PrinterJob.getPrinterJob();
        environmentChangeListener =new Vector<ChangeListener>();
        undoListener=new Vector<UndoableEditListener>();
        undoRedoListeners=new Vector<JUndoRedoListener>();
        itemListeners=new Vector<ItemListener>();
        viewer=null;
        for (int i=0;i<size();i++){
            get(i).getEnvironment().addChangeListener(this);
        }
    }
    /**
     * このJDocumentが保持するページの数を返します。
     * @return　このJDocumentが保持するページの数
     */
    @Override
    public int getNumberOfPages() {
        return size();
    }
    /**
     * このJDocumentの指定位置のページにPageFormatを返します.
     * @param pageIndex 指定するページ.
     * @return 指定ページのPageFormat
     * @throws java.lang.IndexOutOfBoundsException
     */
    @Override
    public PageFormat getPageFormat(int pageIndex) throws IndexOutOfBoundsException {
        return get(pageIndex).getPageFormat();
    }
    /**
     * 指定するインデックスのページのPrintableオブジェクトを返します.
     * @param pageIndex 指定するインデックス.
     * @return 指定するインデックスのページのPrintableオブジェクト
     * @throws java.lang.IndexOutOfBoundsException
     */
    @Override
    public Printable getPrintable(int pageIndex) throws IndexOutOfBoundsException {
        return get(pageIndex);
    }
    /**
     * 表示倍率が変更された際にJEnvironmentオブジェクトから呼び出されます.
     * @param e
     */
    @Override
    public final void stateChanged(ChangeEvent e) {
        fireEnvironmentChange(e);
    }
    
}
