/*
 * FontFamilyCombo.java
 *
 * Created on 2008/06/08, 17:54
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jdraw.textpalette;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import jdraw.typemenus.JTypeMenuItem;

/**
 *
 * @author takashi
 */
public class FontFamilyCombo extends JComboBox implements JTypeMenuItem {

    /** Creates a new instance of FontFamilyCombo */
    private boolean activateEvent = true;

    public FontFamilyCombo() {
        this.setRenderer(new MyCellRenderer());
        this.setToolTipText("ƒtƒHƒ“ƒg");
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (int i = 0; i < fonts.length; i++) {
            this.addItem(fonts[i]);
        }

    }

    @Override
    public void setAttributes(AttributeSet attr) {
        activateEvent = false;
        String fn = StyleConstants.getFontFamily(attr);
        this.setSelectedItem(fn);
    }

    @Override
    public AttributeSet getAttributes() {
        MutableAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setFontFamily(attr, getSelectedItem().toString());
        return attr;
    }

    @Override
    protected void fireActionEvent() {
        if (activateEvent) {
            super.fireActionEvent();
        }
        activateEvent = true;
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.paintComponent(g);
    }
    @Override
    public Font getFont(){
        Font ret;
        if (getSelectedItem() !=null){
            String s = getSelectedItem().toString();
            ret = new Font(s, Font.PLAIN, 14);
        }else{
            ret=super.getFont();
        }
        return ret;
    }
    public class MyCellRenderer extends JLabel implements ListCellRenderer {

        public MyCellRenderer() {
            this.setOpaque(true);
        }

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value != null) {
                this.setFont(new Font(value.toString(), Font.PLAIN, 14));
                this.setText(value.toString());
            }
            if (isSelected) {
                setBackground(java.awt.SystemColor.textHighlight);
            } else {
                setBackground(Color.WHITE);
            }
            return this;
        }
    }
}
