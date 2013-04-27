/*
 * LocatedTextLayout.java
 *
 * Created on 2008/05/27, 14:23
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jobject.text;

import com.lowagie.text.DocumentException;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGraphics2D;
import com.lowagie.text.pdf.PdfGraphics2D.HyperLinkKey;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.TextAttribute;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.Serializable;
import java.text.AttributedCharacterIterator;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdraw.JDrawApplication;

/**
 * 表示開始位置をもつTextLayout単語を表します.
 * @author i002060
 */
public class LocatedTextLayout implements Serializable {

    private float offsetX = 0;
    private int textPosition = -1;
    private TextLayout textLayout = null;
    private TextRow textRow = null;

    /**  デフォルトコンストラクター*/
    public LocatedTextLayout() {
    }

    public LocatedTextLayout(float ofsx, TextLayout layout, int pos) {
        offsetX = ofsx;
        textLayout = layout;
        textPosition = pos;
    }

    public Point2D.Float getLocation() {
        if (textRow == null) {
            return null;
        }
        return new Point2D.Float(offsetX, textRow.getOffsetY());
    }

    public void setTextRow(TextRow row) {
        textRow = row;
    }

    public TextRow getTextRow() {
        return textRow;
    }

    public void setOffsetX(float newX) {
        offsetX = newX;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public int getTextPosition() {
        return textPosition;
    }

    public void setTextPosition(int p) {
        textPosition = p;
    }

    public TextLayout getTextLayout() {
        return textLayout;
    }

    public TextHitInfo hit(float x, float y) {
        x -= offsetX;
        y -= textRow.getOffsetY();
        return textLayout.hitTestChar(x, y);
    }

    public void draw(Graphics2D g) {
        // if (textRow==null) return;       
        //textLayout.draw(g,offsetX,textRow.getOffsetY());
        AttributedCharacterIterator it = new JDocumentCharacterIterator(textRow.getLocater().document, textPosition, textPosition + textLayout.getCharacterCount());
        Object o = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.drawString(it, offsetX, textRow.getOffsetY());
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, o);
    }

 

}
