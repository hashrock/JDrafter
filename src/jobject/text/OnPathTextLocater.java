/*
 * OnPathTextLocater.java
 *
 * Created on 2008/06/01, 19:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jobject.text;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.text.AttributedCharacterIterator;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import jobject.JPathTextObject;
import jpaint.JPaint;

/**
 *
 * @author takashi
 */
public class OnPathTextLocater extends TextLocater {

    private JPathTextObject pathText;
    private Vector<AffineTransform> transforms;

    /** Creates a new instance of OnPathTextLocater */
    public OnPathTextLocater(StyledDocument doc, JPathTextObject o, FontRenderContext frc) {
        super(doc, o, frc);

    }

    @Override
    public Vector<TextRow> createRows(JParagraphIterator pit, Object o) {
        Vector<TextRow> retVec = new Vector<TextRow>();
        pathText = (JPathTextObject) o;
        transforms = new Vector<AffineTransform>();
        Vector<Point2D> poly = pathText.dividePathRelatively(pathText.getPath().get(0), pathText.getStartPosition(), 0.01f);
        FontRenderContext frc = new FontRenderContext(null, true, true);
        Iterator<Point2D> it = poly.iterator();
        //
        if (!it.hasNext()) {
            return retVec;
        }
        Point2D preP = it.next();
        Point2D cP = new Point2D.Float();
        cP.setLocation(preP);
        if (!it.hasNext()) {
            return retVec;
        }
        Point2D nP = it.next();
//        JParagraphIterator pit=new JParagraphIterator(document);
        AttributedCharacterIterator cit = pit.first();

        char[] ch = new char[1];
        TextRow row = null;
        int characterPos = 0;
        outer:
        while (cit != null) {
            char c = cit.first();
            while (c != cit.DONE) {
                ch[0] = c;
                TextLayout layout = new TextLayout(new String(ch), cit.getAttributes(), frc);
                float dst = layout.getAdvance();
                float theta = (float) (Math.atan2(nP.getY() - preP.getY(), nP.getX() - preP.getX()));
                AffineTransform af = new AffineTransform();
                af.setToTranslation(cP.getX(), cP.getY());
                af.rotate(theta);
                if (row == null) {
                    row = new TextRow(0, StyleConstants.ALIGN_LEFT);
                    retVec.add(row);
                }
                row.add(new LocatedTextLayout((float) cP.getX(), layout, characterPos++));
                transforms.add(af);
                float X = (float) (nP.getX() - cP.getX()), Y = (float) (nP.getY() - cP.getY());
                float pdst = (float) Math.sqrt(X * X + Y * Y);
                while (dst > pdst) {
                    if (!it.hasNext()) {
                        break outer;
                    }
                    preP = nP;
                    nP = it.next();
                    dst -= pdst;
                    cP.setLocation(preP);
                    X = (float) (nP.getX() - cP.getX());
                    Y = (float) (nP.getY() - cP.getY());
                    pdst = (float) Math.sqrt(X * X + Y * Y);

                }
                float t = dst / pdst;
                cP.setLocation((nP.getX() - cP.getX()) * t + cP.getX(), (nP.getY() - cP.getY()) * t + cP.getY());
                c = cit.next();
            }
            cit = pit.next();
        }
        return retVec;
    }
    //
    @Override
    public void draw(Graphics2D g) {
        Iterator<LocatedTextLayout> it = rows.firstElement().iterator();
        int i = 0;
        g = (Graphics2D) g.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        AffineTransform savedTx=(AffineTransform)g.getTransform().clone();
        AffineTransform rev=new AffineTransform();
        JPaint jp=null;
        if (g.getPaint() !=null && g.getPaint() instanceof JPaint){ 
            jp=(JPaint)g.getPaint();
            if (jp.getPaintMode()==JPaint.LINEAR_GRADIENT_MODE || jp.getPaintMode()==JPaint.RADIAL_GRADIENT_MODE){
                jp=(JPaint)g.getPaint();
            }else{
                jp=null;
            }
        }
        while (it.hasNext()) {
            LocatedTextLayout ltl=it.next();
            AttributedCharacterIterator ait=new JDocumentCharacterIterator(document,ltl.getTextPosition(),ltl.getTextPosition()+ltl.getTextLayout().getCharacterCount());
            AffineTransform tx=transforms.get(i++);
            g.transform(tx);
            if (jp !=null){
                try {
                    rev = tx.createInverse();
                } catch (NoninvertibleTransformException ex) {
                }
                jp.transform(rev);
            }
            g.drawString(ait,0, 0);
            if (jp !=null) {
                jp.transform(tx);
                rev.setToIdentity();
            }
           // Shape s = it.next().getTextLayout().getOutline(transforms.get(i++));
            //g.fill(s);
            g.setTransform(savedTx);
        }
        g.dispose();
    }

    @Override
    public GeneralPath getOutlineShape() {
        GeneralPath gen = new GeneralPath();
        Iterator<LocatedTextLayout> it = rows.firstElement().iterator();
        int i = 0;
        while (it.hasNext()) {
            Shape s = it.next().getTextLayout().getOutline(transforms.get(i++));
            gen.append(s, false);
        }
        return gen;
    }

    public GeneralPath getOutlineShape(AffineTransform tx) {
        GeneralPath gen = new GeneralPath();
        Iterator<LocatedTextLayout> it = rows.firstElement().iterator();
        int i = 0;
        AffineTransform af=new AffineTransform();
        while (it.hasNext()) {
            af.setTransform(transforms.get(i++));
            af.concatenate(tx);
            Shape s = it.next().getTextLayout().getOutline(af);
            gen.append(s, false);
        }
        return gen;
    }

    @Override
    public Shape getCursor(HitSet hitset) {
        Shape s = hitset.getLayout().getTextLayout().getCaretShape(hitset.getHitInfo());
        AffineTransform tx = transforms.get(hitset.getCharIndex());
        return tx.createTransformedShape(s);
    }

    @Override
    public Shape getLogicalHitShape(HitSet dot, HitSet mark) {
        int st = Math.min(dot.getInsertionIndex(), mark.getInsertionIndex());
        int en = Math.max(dot.getInsertionIndex(), mark.getInsertionIndex());
        GeneralPath ret = new GeneralPath();
        Vector<LocatedTextLayout> vec = new Vector<LocatedTextLayout>(rows.firstElement().sMap.values());
        for (int i = st; i < en; i++) {
            Shape s = transforms.get(i).createTransformedShape(vec.get(i).getTextLayout().getLogicalHighlightShape(0, 1));
            ret.append(s, false);
        }
        return ret;
    }

    @Override
    public HitSet hit(float x, float y) {
        float minimumDist = Float.MAX_VALUE;
        LocatedTextLayout nearistLayout = null;
        Point2D.Float nearistPoint = null;
        int i = 0;
        Iterator<LocatedTextLayout> it = rows.firstElement().iterator();
        while (it.hasNext()) {
            LocatedTextLayout ly = it.next();
            Point2D.Float p = new Point2D.Float((float) transforms.get(i).getTranslateX(), (float) transforms.get(i).getTranslateY());
            Shape s = transforms.get(i++).createTransformedShape(ly.getTextLayout().getBounds());
            if (s.contains(x, y)) {
                HitSet ret = new HitSet(ly.getTextLayout().hitTestChar(x - p.x, y - p.y), ly);
                if (ret.getInsertionIndex() < document.getLength()) {
                    return ret;
                }
                return hitPosition(document.getLength() - 1);
            }
            float dist = (p.x - x) * (p.x - x) + (p.y - y) * (p.y - y);
            if (dist < minimumDist) {
                minimumDist = dist;
                nearistLayout = ly;
                nearistPoint = p;
            }
        }
        Point2D.Float p = nearistPoint;
        HitSet ret = new HitSet(nearistLayout.getTextLayout().hitTestChar(x - p.x, y - p.y), nearistLayout);
        if (ret.getInsertionIndex() < document.getLength()) {
            return ret;
        }
        return hitPosition(document.getLength() - 1);
    }
}
