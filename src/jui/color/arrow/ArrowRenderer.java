/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jui.color.arrow;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

/**
 *
 * @author takashi
 */
public class ArrowRenderer extends JLabel implements ListCellRenderer {

    private Object selectedItem = null;
    private boolean cellHasFocus = false;
    private boolean isSelected = false;
    private static float RATIO = 4;
    private static float SWIDTH = 2f;
    private static float OFFSET = 4;
    private int direction = END;

    public ArrowRenderer() {
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int dr) {
        if (dr < 1 || dr > 3) {
            return;
        }
        direction = dr;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        selectedItem = value;
        this.cellHasFocus = cellHasFocus;
        this.isSelected = isSelected;
        this.setPreferredSize(new Dimension(80, 24));
        this.setOpaque(false);
        return this;
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        AffineTransform tx = new AffineTransform();
        tx.setToTranslation(20, 10);
        tx.scale(6, 6);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (isSelected) {
            g2.setColor(javax.swing.UIManager.getDefaults().getColor("ComboBox.selectionBackground"));
        } else {
            g2.setColor(Color.WHITE);
        }
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(Color.BLACK);
        Shape s=null;
        Rectangle2D rct= new Rectangle2D.Float();
        if (selectedItem !=null && selectedItem instanceof Shape){
            s = (Shape) selectedItem;
            rct=s.getBounds2D();
        }

        float ofs0 = OFFSET;
        float ofs1 = OFFSET;
        if (s != null) {
            if ((direction & 1) != 0) {
                ofs0 += -(float)rct.getX() * SWIDTH * RATIO;
            }
            if ((direction & 2) != 0) {
                ofs1 += -(float) rct.getX() * SWIDTH * RATIO;
            }
        }

        Rectangle r = ((JComponent) getParent().getParent()).getVisibleRect();
        float maxX=Math.min(r.width,getWidth());
        renderArrow(g2, s, ofs0, this.getHeight() / 2,maxX - ofs1, getHeight() / 2, SWIDTH, Color.BLACK, RATIO, direction);

    }
    public static final int START = 1;
    public static final int END = 2;
    public static final int BOTH = 3;

    public static void renderArrow(Graphics2D g, Shape arrow, float x1, float y1, float x2, float y2, float width, Color c, float size, int direction) {
        AffineTransform tx = new AffineTransform();
        tx.setToTranslation(x1, y1);
        tx.scale(width * size, width * size);
        tx.rotate(x2 - x1, y2 - y1);
        Shape bgShape = tx.createTransformedShape(arrow);
        tx.setToTranslation(x2, y2);
        tx.scale(width * size, width * size);
        tx.rotate(x1 - x2, y1 - y2);
        Shape enShape = tx.createTransformedShape(arrow);
        g.setColor(c);
        g.setStroke(new BasicStroke(width,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND));
        Line2D line = new Line2D.Float(x1, y1, x2, y2);
        g.draw(line);
        if ((direction & 1) != 0 && bgShape != null) {
            g.fill(bgShape);
        }
        if ((direction & 2) != 0 && enShape != null) {
            g.fill(enShape);
        }
    }
}
