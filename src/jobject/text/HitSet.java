package jobject.text;

import java.awt.Shape;
import java.awt.font.TextHitInfo;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

//
public class HitSet{
    private TextHitInfo info;
    private LocatedTextLayout layout;
    public HitSet(TextHitInfo info, LocatedTextLayout ly){
        this.info=info;
        layout=ly;
    }
    public int getInsertionIndex(){
        return layout.getTextPosition()+info.getInsertionIndex();
    }
    public int getCharIndex(){
        return layout.getTextPosition()+info.getCharIndex();
    }
    public Point2D.Float getLogicalPosition(){
        Rectangle2D r=layout.getTextLayout().getCaretShape(info).getBounds2D();
        return new Point2D.Float(layout.getOffsetX()+(float)r.getX(),layout.getTextRow().getOffsetY()+(float)r.getY());     
    }
    public Shape getCursor(){
        return layout.getTextRow().getLocater().getCursor(this);
        /*
        Shape s=layout.getTextLayout().getCaretShape(info);
        AffineTransform tx=new AffineTransform();
        tx.setToTranslation(layout.getOffsetX(),layout.getTextRow().getOffsetY());
        return tx.createTransformedShape(s);
         */
    }
    public TextHitInfo getHitInfo(){
        return info;
    }
    public LocatedTextLayout getLayout(){
        return layout;
    }
    public void setOtherSide(){
        info=info.getOtherHit();
    }
    public boolean isLeadingEdge(){
        return info.isLeadingEdge();
    }
}