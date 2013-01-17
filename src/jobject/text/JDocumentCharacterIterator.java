/*
 * JDocumentCharacterIterator.java
 *
 * Created on 2007/10/19, 16:17
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jobject.text;

import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.*;

/**
 *
 * @author i002060
 */
public class JDocumentCharacterIterator implements AttributedCharacterIterator {

    private StyledDocument doc;
    private JParagraphIterator it;
    private String text;
    private int startIndex;
    private int endIndex;//—LŒø•¶Žš+1;
    private int currentIndex;
    private Set<Attribute> allSet;

    /**
     * Creates a new instance of JDocumentCharacterIterator
     */
    public JDocumentCharacterIterator(JParagraphIterator it, int spos, int epos) {
        this.it = it;
        this.doc = it.getDocument();
        currentIndex = spos;
        startIndex = spos;
        endIndex = epos;
        this.text = it.getText();
        allSet = new HashSet<Attribute>();
        allSet.add(TextAttribute.FAMILY);
        allSet.add(TextAttribute.POSTURE);
        allSet.add(TextAttribute.WEIGHT);
        allSet.add(TextAttribute.SIZE);
        allSet.add(TextAttribute.STRIKETHROUGH);
        allSet.add(TextAttribute.UNDERLINE);

    }

    public JDocumentCharacterIterator(StyledDocument dc, int spos, int epos) {
        this.it = null;
        this.doc = dc;
        currentIndex = spos;
        startIndex = spos;
        endIndex = epos;
        try {
            this.text = doc.getText(0, doc.getLength());
        } catch (BadLocationException ex) {
            Logger.getLogger(JDocumentCharacterIterator.class.getName()).log(Level.SEVERE, null, ex);
        }
        allSet = new HashSet<Attribute>();
        allSet.add(TextAttribute.FAMILY);
        allSet.add(TextAttribute.POSTURE);
        allSet.add(TextAttribute.WEIGHT);
        allSet.add(TextAttribute.SIZE);
        allSet.add(TextAttribute.STRIKETHROUGH);
        allSet.add(TextAttribute.UNDERLINE);

    }

    @Override
    public int getRunStart() {
        Element run = doc.getCharacterElement(currentIndex);
        int sIdx = Math.max(startIndex, run.getStartOffset());
        for (int i = 0; i < run.getElementCount(); i++) {
            int spos = run.getElement(i).getEndOffset();
            if (spos < currentIndex && spos > sIdx) {
                sIdx = spos;
            }
        }
        return sIdx;
    }

    @Override
    public int getRunStart(AttributedCharacterIterator.Attribute attribute) {
        return getRunStart();
    }

    @Override
    public int getRunStart(Set<? extends AttributedCharacterIterator.Attribute> attributes) {
        return getRunStart();
    }

    @Override
    public int getRunLimit() {
        Element run = doc.getCharacterElement(currentIndex);
        int eIdx = Math.min(run.getEndOffset(), endIndex);
        for (int i = 0; i < run.getElementCount(); i++) {
            int spos = run.getElement(i).getStartOffset();
            if (spos > currentIndex && spos < eIdx) {
                eIdx = spos;
            }
        }
        return eIdx;
    }

    @Override
    public int getRunLimit(AttributedCharacterIterator.Attribute attribute) {
        return getRunLimit();
    }

    @Override
    public int getRunLimit(Set<? extends AttributedCharacterIterator.Attribute> attributes) {
        return getRunLimit();
    }

    @Override
    public Map<AttributedCharacterIterator.Attribute, Object> getAttributes() {
        AttributeSet attr = doc.getCharacterElement(currentIndex).getAttributes();
        Map<Attribute, Object> ret = new HashMap<Attribute, Object>();
        ret.put(TextAttribute.FAMILY, StyleConstants.getFontFamily(attr));
        ret.put(TextAttribute.SIZE, StyleConstants.getFontSize(attr));

        if (StyleConstants.isItalic(attr)) {
            ret.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
        }
        if (StyleConstants.isBold(attr)) {
            ret.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
        }
        if (StyleConstants.isUnderline(attr)) {
            ret.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL);
        }
        if (StyleConstants.isStrikeThrough(attr)) {
            ret.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
        }
        return ret;
    }

    @Override
    public Object getAttribute(AttributedCharacterIterator.Attribute attribute) {
        return getAttributes().get(attribute);
    }

    @Override
    public Set<AttributedCharacterIterator.Attribute> getAllAttributeKeys() {
        return allSet;
    }

    @Override
    public char first() {
        return setIndex(startIndex);
    }

    @Override
    public char last() {
        return setIndex(endIndex);
    }

    @Override
    public char current() {
        if (currentIndex >= endIndex) {
            return DONE;
        }
        try {
            char ret = text.charAt(currentIndex);
            return ret;
        } catch (StringIndexOutOfBoundsException e) {
            return DONE;
        }
    }

    @Override
    public char next() {
        currentIndex++;
        if (currentIndex >= endIndex) {
            currentIndex = endIndex;
            return DONE;
        }
        return current();
    }

    @Override
    public char previous() {
        currentIndex--;
        if (currentIndex < startIndex) {
            currentIndex = startIndex;
            return DONE;
        }
        return current();
    }

    @Override
    public char setIndex(int position) {
        if (position < startIndex || position > endIndex) {
            throw new IllegalArgumentException();
        }
        currentIndex = position;
        return current();
    }

    @Override
    public int getBeginIndex() {
        return startIndex;
    }

    @Override
    public int getEndIndex() {
        return endIndex;
    }

    @Override
    public int getIndex() {
        return currentIndex;
    }

    @Override
    public Object clone() {
        return new JDocumentCharacterIterator(it, startIndex, endIndex);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof JDocumentCharacterIterator)) {
            return false;
        }
        JDocumentCharacterIterator tit = (JDocumentCharacterIterator) o;
        return (doc.equals(tit.doc) && tit.startIndex == startIndex && tit.endIndex == endIndex);
    }

    public StyledDocument getDocument() {
        return doc;
    }
}