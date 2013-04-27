/*
 * TextLocater.java
 *
 * Created on 2008/05/27, 15:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jobject.text;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.LineMetrics;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;

/**
 *
 * @author i002060
 */
public class TextLocater {

    Vector<TextRow> rows = null;
    StyledDocument document = null;
    String text = null;
    Object param = null;
    FontRenderContext fontRenderContext = null;
    private static BasicStroke zeroStroke = new BasicStroke(0);
    public static final TabSet DEFAULT_TABSET = getDefaultTabset();

    private static TabSet getDefaultTabset() {
        TabStop[] tabs = new TabStop[100];
        for (int i = 0; i < 100; i++) {
            tabs[i] = new TabStop(72f * (i + 1));
        }
        return new TabSet(tabs);
    }

    /** Creates a new instance of TextLocater */
    public TextLocater(StyledDocument doc, Object o, FontRenderContext frc) {
        JParagraphIterator iterator = new JParagraphIterator(doc);
        document = doc;
        param = o;
        text = iterator.getText();
        if (frc == null) {
            frc = new FontRenderContext(null, true, true);
        }
        fontRenderContext = frc;
        rows = createRows(iterator, o);
        for (TextRow tr : rows) {
            tr.setLocater(this);
        }
    }

    protected Vector<TextRow> createRows(JParagraphIterator pi, Object o) {
        Vector<TextRow> retVec = new Vector<TextRow>();
        FontRenderContext frc = fontRenderContext;
        AttributedCharacterIterator atIterator = pi.first();
        float verticalPos = 0;
        int charPos = 0;
        while (atIterator != null) {
            //行スタイル
            AttributeSet attribute = pi.getParagraphAttributeSet();
            float leftIndent = StyleConstants.getLeftIndent(attribute);
            float firstIndent = StyleConstants.getFirstLineIndent(attribute);
            float lineSpacing = StyleConstants.getLineSpacing(attribute);
            float rightIndent = StyleConstants.getRightIndent(attribute);
            int alignment = StyleConstants.getAlignment(attribute);
            TabSet tabset = StyleConstants.getTabSet(attribute);
            if (tabset == null) {
                tabset = DEFAULT_TABSET;
            //タブポジション
            }
            Vector<Integer> tabPosition = new Vector<Integer>();
            for (char c = atIterator.first(); c != atIterator.DONE; c = atIterator.next()) {
                if (c == '\t') {
                    tabPosition.add(atIterator.getIndex());
                }
            }
            tabPosition.add(atIterator.getEndIndex());
            //フォントメトリクス
            Font font = document.getFont(attribute);
            //LineMetrics metrics=font.getLineMetrics(" ",frc);
            float tabSpace = (new TextLayout("    ", font, frc)).getAdvance();
            //
            float horizontalPos = leftIndent + firstIndent;
            int tabIndex = 0;
            boolean paragraphEnd = false;
            //
            LineBreakMeasurer measurer = new LineBreakMeasurer(atIterator, frc);

            //1行抽出
            while (!paragraphEnd) {
                boolean lineContainsText = false;
                boolean lineEnd = false;
                TextRow textRow = null;
                while (!lineEnd) {
                    TextLayout layout = measurer.nextLayout(99999999f, tabPosition.get(tabIndex) + 1, lineContainsText);
                    if (layout != null) {
                        if (textRow == null) {
                            textRow = new TextRow(verticalPos, alignment);
                        }
                        textRow.add(new LocatedTextLayout(horizontalPos, layout, charPos));
                        charPos += layout.getCharacterCount();
                        horizontalPos += layout.getAdvance();
                    } else {
                        lineEnd = true;
                    }
                    lineContainsText = true;
                    if (measurer.getPosition() == tabPosition.get(tabIndex) + 1) {
                        tabIndex++;
                    }
                    if (measurer.getPosition() >= atIterator.getEndIndex()) {
                        lineEnd = true;
                        paragraphEnd = true;
                    }
                    if (alignment == StyleConstants.ALIGN_LEFT) {
                        float tabAfter = tabset.getTabAfter(horizontalPos).getPosition();
                        if (tabAfter == horizontalPos) {
                            int idx = tabset.getTabIndexAfter(horizontalPos);
                            horizontalPos = tabset.getTab(idx + 1).getPosition();
                        } else {
                            horizontalPos = tabAfter;
                        }
                    } else {
                        horizontalPos += tabSpace;
                    }
                }
                if (textRow != null) {
                    verticalPos += textRow.getAcent() + lineSpacing;
                    textRow.setOffsetY(verticalPos);
                    textRow.setRightIndent(rightIndent);
                    retVec.add(textRow);
                    verticalPos += textRow.getDescent();
                    horizontalPos = leftIndent;
                }
            }
            atIterator = pi.next();
        }
        float maxWidth = 0;
        TextRow maximumRow = null;
        for (TextRow rw : retVec) {
            if (rw.getWidth() > maxWidth) {
                maxWidth = rw.getWidth();
                maximumRow = rw;
            }
        }
        for (TextRow rw : retVec) {
            float offset = 0;
            switch (rw.getAlignment()) {
                case StyleConstants.ALIGN_CENTER:
                    offset = (maxWidth - rw.getWidth()) / 2;
                    break;
                case StyleConstants.ALIGN_RIGHT:
                    offset = maxWidth - rw.getWidth();
                    break;
            }
            if (offset != 0) {
                Iterator<LocatedTextLayout> it = rw.iterator();
                while (it.hasNext()) {
                    LocatedTextLayout lt = it.next();
                    lt.setOffsetX(lt.getOffsetX() + offset);
                }
            }
        }
        return retVec;
    }

    public FontRenderContext getFontRenderContext() {
        return fontRenderContext;
    }

    public float getWidth() {
        float ret = -1;
        for (int i = 0; i < rows.size(); i++) {
            if (ret < rows.get(i).getWidth()) {
                ret = rows.get(i).getWidth();
            }
        }
        return ret;
    }

    public float getHeight() {
        if (rows.size() == 0) {
            return 0;
        }
        TextRow last = rows.lastElement();
        return last.getOffsetY() + last.getDescent();
    }

    public void add(TextRow row) {
        row.setLocater(this);
        rows.add(row);
    }

    public void draw(Graphics2D g) {
        for (TextRow row : rows) {
            row.draw(g);
        }
    }

    public void drawBaseLine(Graphics2D g) {
        g.setStroke(zeroStroke);
        for (TextRow row : rows) {
            row.drawBaseLine(g);
        }
    }
    //
    public int indexOf(TextRow row) {
        return rows.indexOf(row);
    }

    public TextRow get(int index) {
        return rows.get(index);
    }

    public int size() {
        return rows.size();
    }

    public Shape getLogicalHitShape(HitSet dot, HitSet mark) {
        int st = Math.min(dot.getInsertionIndex(), mark.getInsertionIndex());
        int en = Math.max(dot.getInsertionIndex(), mark.getInsertionIndex());
        GeneralPath ret = new GeneralPath();
        TextRow currentRow = getHitRow(st);
        AffineTransform tx = new AffineTransform();
        while (currentRow != null && st < en) {
            Rectangle2D r = null;
            while (st <= currentRow.getEndPosition() && st < en) {
                LocatedTextLayout jtl = currentRow.getForPosition(st);
                Shape s = jtl.getTextLayout().getLogicalHighlightShape(st - jtl.getTextPosition(),
                        Math.min(jtl.getTextLayout().getCharacterCount(), en - jtl.getTextPosition()));
                tx.setToTranslation(jtl.getOffsetX(), currentRow.getOffsetY());
                Rectangle2D rs = tx.createTransformedShape(s).getBounds2D();
                if (rs.getWidth() == 0) {
                    float w = 2;
                    rs.setFrame(rs.getX(), rs.getY(), w, rs.getHeight());

                }
                if (r == null) {
                    r = rs;
                } else {
                    r.add(rs);
                }
                st++;
            }
            if (r != null) {
                ret.append(r, false);
            }
            currentRow = nextRow(currentRow);
        }
        return ret;
    }

    public TextRow nextRow(TextRow current) {
        int idx = rows.indexOf(current);
        if (idx < 0 || idx == rows.size() - 1) {
            return null;
        }
        return rows.get(++idx);
    }

    public TextRow getHitRow(int pos) {
        TextRow rw = null;
        for (int i = 0; i < rows.size(); i++) {
            rw = rows.get(i);
            if (pos >= rw.getStartPosition() && pos <= rw.getEndPosition()) {
                break;
            }
        }
        return rw;
    }

    public TextRow getHitRow(float x, float y) {
        TextRow row = null;
        for (int i = 0; i < rows.size(); i++) {
            row = rows.get(i);
            float sy = row.getOffsetY() + row.getDescent();
            if (sy >= y) {
                return row;
            }
        }

        return row;
    }

    public HitSet hitPosition(int pos) {
        if (pos > document.getLength()) {
            throw new IndexOutOfBoundsException();
        }
        if (pos > rows.lastElement().lastIndex()) {
            pos = rows.lastElement().lastIndex();
        }
        LocatedTextLayout ly = getHitRow(pos).getForPosition(pos);
        TextHitInfo info = TextHitInfo.leading(pos - ly.getTextPosition());
        return new HitSet(info, ly);
    }

    public HitSet hit(float x, float y) {
        TextRow hitRow = getHitRow(x, y);
        Iterator<LocatedTextLayout> it = hitRow.iterator();
        LocatedTextLayout lt = null, prev = null;
        while (it.hasNext()) {
            lt = it.next();
            TextHitInfo ti = lt.hit(x, y);
            if (ti.getInsertionIndex() < lt.getTextLayout().getCharacterCount()) {
                return new HitSet(ti, lt);
            }
            prev = lt;
        }
        int cnt = hitRow.lastIndex();
        String t = "";
        try {
            t = document.getText(cnt, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!t.equals("\n")) {
            TextHitInfo ti = TextHitInfo.trailing(cnt - lt.getTextPosition());
            return new HitSet(ti, lt);
        } else {
            TextHitInfo ti = TextHitInfo.leading(cnt - lt.getTextPosition());
            return new HitSet(ti, lt);
        // return hitPosition(hitRow.lastIndex());
        }
    }

    public HitSet next(HitSet current) {
        LocatedTextLayout lyt = current.getLayout();
        TextRow rw = lyt.getTextRow();
        String t = "";
        try {
            t = document.getText(current.getCharIndex(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (current.getInsertionIndex() == rw.lastIndex() && !t.equals("\n")) {
            return new HitSet(TextHitInfo.trailing(current.getHitInfo().getInsertionIndex()), current.getLayout());
        }
        if (current.getInsertionIndex() > rw.lastIndex()) {
            return hitPosition(current.getInsertionIndex());
        }
        return hitPosition(current.getInsertionIndex() + 1);
    }

    public HitSet previous(HitSet current) {
        if (current.getCharIndex() == 0) {
            return current;
        }
        LocatedTextLayout lyt = current.getLayout();
        TextRow rw = lyt.getTextRow();
        String t = "", p = "";
        try {
            t = document.getText(current.getInsertionIndex(), 1);
            p = document.getText(current.getInsertionIndex() - 1, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HitSet ret = hitPosition(current.getInsertionIndex() - 1);
        if (lyt == rw.first() && current.getHitInfo().getInsertionIndex() == 0) {
            TextRow pre = rows.get(rows.indexOf(rw) - 1);
            if (!p.equals("\t")) {
                return new HitSet(TextHitInfo.trailing(pre.last().getTextLayout().getCharacterCount() - 1), pre.last());
            }
        }
        return hitPosition(current.getInsertionIndex() - 1);
    }

    public GeneralPath getOutlineShape() {
        return getOutlineShape(new AffineTransform());
    }

    public GeneralPath getOutlineShape(AffineTransform tx) {
        GeneralPath gen = new GeneralPath();
        for (int i = 0; i < rows.size(); i++) {
            TextRow row = rows.get(i);
            Iterator<LocatedTextLayout> it = row.iterator();
            AffineTransform af=new AffineTransform();
            while (it.hasNext()) {
                LocatedTextLayout lt = it.next();
                af.setToTranslation(lt.getOffsetX(), row.getOffsetY());
                af.preConcatenate(tx);
                Shape s = lt.getTextLayout().getOutline(af);
                gen.append(s, false);
            }
        }
        return gen;
    }

    public Shape getCursor(HitSet hitset) {
        Shape s = hitset.getLayout().getTextLayout().getCaretShape(hitset.getHitInfo());
        AffineTransform tx = new AffineTransform();
        tx.setToTranslation(hitset.getLayout().getOffsetX(), hitset.getLayout().getTextRow().getOffsetY());
        return tx.createTransformedShape(s);
    }
}
