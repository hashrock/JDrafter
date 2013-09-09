/*
 * JScroller.java
 *
 * Created on 2007/08/19, 20:17
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jscreen;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

/**
 *JDrawViewer専用のScrollPaneサブクラスです。
 * @author TI
 */
public class JScroller extends JScrollPane{
    JDocumentViewer viewer=null;
    JRuler vertical=null,horizontal=null;
    /** デフォルトコンストラクター*/
    public JScroller() {
        setViewer(new JDocumentViewer());
        vertical=new JRuler(JRuler.VERTICAL,this);
        horizontal=new JRuler(JRuler.HORIZONTAL,this);
        this.setColumnHeaderView(horizontal);
        this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.setRowHeaderView(vertical);
        this.setPreferredSize(new Dimension(800,600));
        this.getViewport().addComponentListener(new cListener());
        vertical.setPreferredHeight(viewer.getPreferredSize().height);
        horizontal.setPreferredWidth(viewer.getPreferredSize().width);
        this.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
    }
    /**JScrollerにJDocumentViwerを設定します.*/
    public void setViewer(JDocumentViewer vw){
        viewer=vw;
        this.setViewportView(vw);
        vw.setScroller(this);
    }
    /**このJScrollerが表示するJDrawViewerを返します.*/
    public JDocumentViewer getViewer(){
        return viewer;
    }
    /**表示インターフェースを返します.*/
    public JEnvironment getEnvironment(){
        return getViewer().getEnvironment();
    }
    /**ルーラーサイズを調整します.*/
    public void adjustSize(){
        vertical.setPreferredHeight(viewer.getPreferredSize().height);
        horizontal.setPreferredWidth(viewer.getPreferredSize().width);       
    }
    /**コンポーネントアダプタを実装するサブクラスです.*/
    public class cListener extends ComponentAdapter{
       public void componentResized(ComponentEvent e){
            viewer.adjustSize();
       }
    }
}
