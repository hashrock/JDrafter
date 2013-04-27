/*
 * JRequest.java
 *
 * Created on 2007/08/27, 11:28
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jscreen;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;
import jobject.JLeaf;
import jobject.JObject;
import jobject.JPage;

/**
 *オブジェクトの選択状態を保持するClassです。
 * 
 * @author i002060
 */
public class JRequest {
    /**
     * 描画オブジェクトのどこにもHitしない状態です。
     */
    public static final int HIT_NON=0;
    /**
     * 描画オブジェクト全体にHitした状態です。
     */
    public static final int HIT_OBJECT=1;
    /**
     * オブジェクトのパスにHitした状態です。
     */
    public static final int HIT_PATH=2;
    /**
     * アンカーポイントにHitした状態です.
     */
    public static final int HIT_ANCUR=3;
    /**
     * 左側のコントロールハンドルにHitした状態です。
     */
    public static final int HIT_L_CONTROL=4;
    /**
     * 右側のコントロールハンドルにhitした状態です。
     */
    public static final int HIT_R_CONTROL=5;
    /**
     * ダイレクト選択モードをあらわします.
     */
    public static final int DIRECT_MODE=1;
    /**
     * グループ選択モードをあらわします。
     */
    public static final int GROUP_MODE=2;
    /**
     * ヒット検証結果を格納します.
     */
    public int hitResult;
    private int selectionMode;    
    public  Vector hitObjects;
    /**
     * ヒット検証時にAltキーの押下状態を示します.
     */
    public boolean isAltDown;
    /**
     * ヒット検証時にCtrlキーの押下状態を示します.
     */
    public boolean isCtlDown;
    /**
     * ヒット検証時のShiftキーの押下状態を示します.
     */
    public boolean isShiftDown;
    private JPage jpage;
    private  Vector selectedObjects;
    private Vector<ItemListener> listener;
    /**
     * 指定するJPgeオブジェクトの選択状態を表すJRequestのインスタンスを構築します.
     * @param page 指定するJPageオブジェクト
     */
    public JRequest(JPage page) {
        hitResult=HIT_NON;
        selectionMode=GROUP_MODE;
        selectedObjects=new Vector();
        hitObjects=new Vector();
        listener=new Vector<ItemListener>();
        isAltDown=false;
        isCtlDown=false;
        isShiftDown=false;
        this.jpage=page;
    }
    /**
     * ヒット検証時の選択モードを設定します。
     * @param mode ヒット検証時の選択モード(DIRECT_MODE又はGROUP_MODE)
     */
    public void setSelectionMode(int mode){
        if (mode != DIRECT_MODE && mode != GROUP_MODE) return;
        if (mode==selectionMode) return;
        if (mode ==DIRECT_MODE){
            Vector svect=new Vector();
            for (int i=0;i<size();i++){
                Object o=get(i);
                if (o instanceof JObject){
                   JObject jo=(JObject)o;
                   Vector v=jo.getLeafs();
                   for (int j=0;j<v.size();j++){
                       svect.add(v.get(j));
                   }
                }else if (o instanceof JLeaf){
                    svect.add(o);
                }
            }
            clear();
            for (int i=0;i<svect.size();i++){
                add(svect.get(i));
            }
        }else{
            for (int i=0;i<size();i++){
                Object o=get(i);
                if (!(o instanceof JLeaf)) {
                    remove(o);
                }
            }
        }
        selectionMode=mode;
    }
    /**
     * 現在設定されているヒット検証時の選択モードを取得します.
     * @return ヒット検証時の選択モード
     */
    public int getSelectionMode(){
        return selectionMode;
    }
    /**
     * 指定するObjectが選択状態にある場合にtrueを返します.
     * @param o 指定するObject
     * @return 指定するObjectが選択状態にある場合にtrueそれ以外はfalse
     */
    public boolean contains(Object o){
        return selectedObjects.contains(o);
    }
    /**
     * 指定するObjectを選択状態にします.
     * @param o 指定するObject
     */
    public void add(Object o){
        if (o instanceof JLeaf){
            if (!isSelectable((JLeaf)o)) return;
        }
        if(!selectedObjects.contains(o)){
            selectedObjects.add(o);
            fireChangeEvent(o,ItemEvent.SELECTED);
        }
        
    }
    /**
     * 指定するJLeafのインスタンスが選択可能である場合にtrueを返します.
     * @param jl 指定するJLeafのインスタンス。
     * @return 指定するJLeafのインスタンスが選択可能な場合trueそれ以外はfalse
     */
    private boolean isSelectable(JLeaf jl){
        if (!jl.isVisible() || jl.isLocked()) return false;
        JLeaf parent=jl.getParent();
        if (parent==null) return true;
        return isSelectable(parent);
    }
    /**
     * 指定するObjectの選択状態を解除します.
     * @param o 選択を解除するObject
     */
    public void remove(Object o){
        selectedObjects.remove(o);
        fireChangeEvent(o,ItemEvent.DESELECTED);
    }
    /**
     * 指定するインデックスのObjectの選択状態を解除います.
     * @param i 選択状態を解除するObjectのインデックス.
     * @return 選択状態を解除するObject
     */
    public Object remove(int i){
        return selectedObjects.remove(i);
    }
    /**
     * 全ての選択Objectの選択状態を解除します.
     */
    public void clear(){
        selectedObjects.clear();
        fireChangeEvent(null,ItemEvent.DESELECTED);
    }
    /**
     * 選択されたオブジェクトの数を返します.
     * @return 選択されたオブジェクト数
     */
    public int size(){
        return selectedObjects.size();
    }
    /**
     * 何も選択されていない場合にtrueを返します。
     * @return　何も選択されていない場合true、それ以外はfalse
     */
    public boolean isEmpty(){
        return selectedObjects.isEmpty();
    }
    /**
     * 指定するインデックスの選択オブジェクトを返します.
     * @param i 指定するインデックス.
     * @return 指定したインデクスの選択オブジェクト
     */
    public Object get(int i){
        return selectedObjects.get(i);
    }
    /**
     * 選択オブジェクトを要素とするVectorを返します.
     * @return 選択オブジェクトを要素とするVector
     */
    public Vector getSelectedVector(){
        return selectedObjects;
    }
    /**
     * 指定するVectorの要素のオブジェクトを選択状態にします.
     * @param v 選択状態とするオブジェクトを要素とするVector
     */
    public void setSelectedVector(Vector v){
        selectedObjects=v;
        fireChangeEvent(v,ItemEvent.SELECTED);
    }
    /**
     * 選択状態が変更されたことを、JRequestを所有するJPaeオブジェクトに送信します.
     * @param o 選択状態が変更された対象オブジェクト
     * @param stateChange 選択状態.
     */
    public void fireChangeEvent(Object o,int stateChange){
        jpage.fireChangeEvent(o,stateChange);
    }
}
