/*
 * JPageNavigator.java
 *
 * Created on 2007/12/08, 14:30
 */

package jui;

import jactions.JPageActions;
import jactions.JUndoRedoEvent;
import jactions.JUndoRedoListener;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;
import javax.swing.JDialog;
import javax.swing.JPopupMenu;
import jobject.JDocument;
import jscreen.JEnvironment;

/**
 *
 * @author  takashi
 */
public class JPageNavigator extends JToolWindow {
    private Vector<JPagePreviewer> previews;
    private JDocument doc;
    private JPageSelecter selecter;
    private JPageActions actions;
    /** Creates new form JPageNavigator */
    public JPageNavigator(java.awt.Frame parent, boolean modal) {
        this(parent,null);
    }
    public JPageNavigator(java.awt.Frame parent,JDocument doc){
        super(parent,false);
        actions=new JPageActions(doc);
        initComponents();
        selecter=new JPageSelecter();
        setDocument(doc);
        //selecter.setBackground(Color.WHITE);
        scrollPane.setViewportView(selecter);
        pack();       
        this.setIconImage(JEnvironment.ICONS.NULL_ICON.getImage());
        JPopupMenu pMenu=new JPopupMenu();
        menu.add(actions.insertPageAction);
        pMenu.add(actions.insertPageAction);
        menu.addSeparator();
        pMenu.addSeparator();
        menu.add(actions.cutPageAction);
        pMenu.add(actions.cutPageAction);
        menu.add(actions.copyPageAction);
        pMenu.add(actions.copyPageAction);
        menu.add(actions.pastePageAction);
        pMenu.add(actions.pastePageAction);
        menu.addSeparator();
        pMenu.addSeparator();
        menu.add(actions.deletePageAction);
        pMenu.add(actions.deletePageAction);
        selecter.setComponentPopupMenu(pMenu);
        
        
    }
    public void setDocument(JDocument doc){
        this.doc=doc;
        selecter.setDocument(doc);
        actions.setDocument(doc);
        
    }
    public JPageSelecter getSelecter(){
        return selecter;
    }

    // <editor-fold defaultstate="collapsed" desc=" Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        scrollPane = new javax.swing.JScrollPane();
        jMenuBar2 = new javax.swing.JMenuBar();
        menu = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("\u30da\u30fc\u30b8");
        setLocationByPlatform(true);
        scrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        getContentPane().add(scrollPane, java.awt.BorderLayout.CENTER);

        menu.setLabel("\u30da\u30fc\u30b8");
        jMenuBar2.add(menu);

        setJMenuBar(jMenuBar2);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JMenu menu;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables
    
}
