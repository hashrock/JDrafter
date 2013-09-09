/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plugins;

import jactions.JUndoRedoEvent;
import jactions.JUndoRedoListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.AbstractAction;
import jscreen.JDocumentViewer;

/**
 *JDrafter アプリケーションのプラグインの基底クラスです。<BR>
 * プラグインは、全てこの抽象クラスを継承しなければなりません。
 * コンパイルしたクラスはアプリケーションと同じパスのpluginsフォルダに
 * 配置します。また、pluginsフォルダにサブフォルダを配置するとアプリケーションは
 * サブフォルダ名と同名のサブメニューを構築します。サブメニューにニーモニックを指定
 * したい場合はサブフォルダ名末尾に"("+ニーモニック+")"をフォルダ名として指定します。
 * 
 * @author Ikita 
 */
public abstract class AbstractPlugin extends AbstractAction {

    private JDocumentViewer viewer = null;
    private InnerListener innerListener = null;

    /**
     * デフォルトコンストラクターです。
     */
    public AbstractPlugin() {
        innerListener = new InnerListener();
    }

    /**
     * アプリケーションによりViewerが変更されたときに呼ばれます.
     * @param v アクティブなJDocumentViewer アクティブなviewerがない場合はnull;
     */
    public final void setViewer(JDocumentViewer v) {
        if (viewer != v) {
            if (viewer != null) {
                viewer.getDocument().removeUndoRedoListener(innerListener);
                viewer.getDocument().removeItemListener(innerListener);
            }
            viewer = v;
            if (viewer != null) {
                viewer.getDocument().addItemListener(innerListener);
                viewer.getDocument().addUndoRedoListener(innerListener);
            }
            changeStates();
        }
    }

    /**
     * アクティブなJDocumentViewerを取得します.
     * @return アクティブなViewer.アクティブなビュアーがない場合はnull
     */
    public final JDocumentViewer getViewer() {
        return viewer;
    }

    /**
     * ドキュメントに何らかの変更が加えられた場合、カレントドキュメントが変更された場合
     * もしくは選択が変更された場合の処理を記述します.
     * @param viewer アクティブなDocumentViewer。アクティブなDocumentViewerがない場合はnull
     */
    public abstract void changeStates();

    private class InnerListener implements ItemListener, JUndoRedoListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            changeStates();
        }
        @Override
        public void undoRedoEventHappened(JUndoRedoEvent e) {
            changeStates();
        }
    }
}
