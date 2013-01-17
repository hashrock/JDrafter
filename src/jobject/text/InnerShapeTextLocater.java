/*
 * InnerTextLocater.java
 *
 * Created on 2008/05/30, 8:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jobject.text;

import java.awt.Font;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.LineMetrics;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.TabSet;

/**
 *
 * @author i002060
 */
public class InnerShapeTextLocater extends TextLocater{
    private Shape shape;
    /** Creates a new instance of InnerTextLocater */
    public InnerShapeTextLocater(StyledDocument doc,Shape s,FontRenderContext frc) {
        super(doc,s,frc);
    }
    @Override
    protected Vector<TextRow> createRows(JParagraphIterator pi,Object s){
        Vector<TextRow> retVec=new Vector<TextRow>();
        shape=(Shape)s;
        FontRenderContext frc=new FontRenderContext(null,true,true);
//        JParagraphIterator pi=new JParagraphIterator(document);
        AttributedCharacterIterator text=pi.first();
        float verticalPos=0;
        int charPos=0;
        while (text !=null){
            //行スタイル
            AttributeSet attribute=pi.getParagraphAttributeSet();
            float leftIndent=StyleConstants.getLeftIndent(attribute);
            float firstIndent=StyleConstants.getFirstLineIndent(attribute);
            float rightIndent=StyleConstants.getRightIndent(attribute);
            float lineSpacing=StyleConstants.getLineSpacing(attribute);
            int alignment=StyleConstants.getAlignment(attribute);
            TabSet tabset=StyleConstants.getTabSet(attribute);
            if (tabset==null) tabset=DEFAULT_TABSET;
            //タブポジション
            Vector<Integer> tabPosition=new Vector<Integer>();
            for (char c=text.first();c!=text.DONE;c=text.next()){
                if (c=='\t')
                    tabPosition.add(text.getIndex());
            }
            tabPosition.add(text.getEndIndex());
            //フォントメトリクス
            Font font=document.getFont(attribute);
            LineMetrics metrics=font.getLineMetrics(" ",frc);
            float tabSpace=(new TextLayout("    ",font,frc)).getAdvance();
            //
            float horizontalPos = leftIndent+firstIndent;
            int tabIndex=0;
            //
            Rectangle2D bounds =shape.getBounds2D();
            float height=(float)bounds.getHeight();
            BlockIterator blockIterator=null;
            boolean loop=true;
            while (verticalPos<height){
                blockIterator=getRowBlocks(verticalPos+metrics.getAscent(),leftIndent+firstIndent,rightIndent);
                if (blockIterator!=null){
                    if (moveToBlock(blockIterator,horizontalPos))
                        break;
                }
                verticalPos+=metrics.getAscent()+metrics.getDescent();
            }
            if (verticalPos>=height) break;
            boolean paragraphEnd=false;
            LineBreakMeasurer measurer=new LineBreakMeasurer(text,frc);
            while (!paragraphEnd){
                boolean lineContainsText=false;
                boolean lineEnd=false;
                TextRow textRow=null;
                Vector<LocatedTextLayout> addedLayouts=new Vector<LocatedTextLayout>();
                while(!lineEnd){
                    horizontalPos=Math.max(horizontalPos,blockIterator.current().ofsX);
                    float wrapingWidth=Math.max(blockIterator.current().getEndPos()-horizontalPos,0);
                    TextLayout layout=measurer.nextLayout(wrapingWidth,tabPosition.get(tabIndex)+1,lineContainsText);
                    LocatedTextLayout addingLayout=null;
                    if (layout !=null){
                        if (textRow==null)
                            textRow=new TextRow(verticalPos,alignment);
                        addingLayout=new LocatedTextLayout(horizontalPos,layout,charPos);
                        addedLayouts.add(addingLayout);
                        textRow.add(addingLayout);
                        charPos+=layout.getCharacterCount();
                        horizontalPos+=layout.getAdvance();
                        
                    }else{
                        lineEnd=true;
                    }
                    lineContainsText=true;
                    if (measurer.getPosition()==tabPosition.get(tabIndex)+1){
                        tabIndex++;
                    }
                    if (measurer.getPosition()>=text.getEndIndex()){
                        lineEnd=true;
                        paragraphEnd=true;
                    }
                    if (alignment==StyleConstants.ALIGN_LEFT){
                        float tabAfter=tabset.getTabAfter(horizontalPos).getPosition();
                        if (tabAfter==horizontalPos){
                            int idx=tabset.getTabIndexAfter(horizontalPos);
                            horizontalPos=tabset.getTab(idx+1).getPosition();
                        }else{
                            horizontalPos=tabAfter;
                        }
                    }else{
                        horizontalPos+=tabSpace;
                    }
                    if (horizontalPos>=blockIterator.current().getEndPos() ){
                        addedLayouts.clear();
                        if (!moveToBlock(blockIterator,horizontalPos)){
                            lineEnd=true;
                            break;
                        }else{
                            lineContainsText=false;
                        }
                    }
                    
                }
                if (!addedLayouts.isEmpty()){
                    float w=addedLayouts.lastElement().getOffsetX()+addedLayouts.lastElement().getTextLayout().getAdvance()-
                            addedLayouts.firstElement().getOffsetX();
                    float ofsX=blockIterator.current().width-w;
                    if (alignment==StyleConstants.ALIGN_LEFT){
                        ofsX=0;
                    }else if (alignment==StyleConstants.ALIGN_CENTER){
                        ofsX/=2;
                    }
                    for (int i=0;i<addedLayouts.size();i++){
                        addedLayouts.get(i).setOffsetX(addedLayouts.get(i).getOffsetX()+ofsX);
                    }
                }
                if (textRow !=null){
                    verticalPos+=textRow.getAcent()+lineSpacing;
                    textRow.setOffsetY(verticalPos);
                    retVec.add(textRow);
                    verticalPos+=textRow.getDescent();
                }
                horizontalPos=leftIndent;
                while (verticalPos<height){
                    blockIterator=getRowBlocks(verticalPos+metrics.getAscent()+lineSpacing,leftIndent,rightIndent);
                    if (blockIterator!=null){
                        if (moveToBlock(blockIterator,horizontalPos))
                            break;
                    }
                    //verticalPos+=metrics.getAscent()+metrics.getDescent();
                    verticalPos+=textRow.getAcent()+textRow.getDescent()+lineSpacing;
                }
                if (verticalPos>=height) break;
            }
            text=pi.next();
        }
        return retVec;
    }
    //
    public boolean moveToBlock(BlockIterator block,float x){
        while (!block.done()){
            if (block.current().ofsX+block.current().width>x) return true;
            block.next();
        }
        return false;
    }
    //
    @Override
    public float getWidth(){
        if (shape==null) return 0f;
        return (float)shape.getBounds2D().getWidth();
    }
    Shape flattened=null;
    private final float FLATTNESS=0.01f;
    private BlockIterator getRowBlocks(float vPos,float leftIndent,float rightIndent){
        if (flattened==null){
            GeneralPath gp=new GeneralPath();
            gp.append(shape.getPathIterator(null,FLATTNESS),false);
            Rectangle2D r=gp.getBounds();
            AffineTransform tx=new AffineTransform();
            tx.setToTranslation(-r.getX(),-r.getY());
            flattened=gp.createTransformedShape(tx);
        }
        Vector<Float> intersections=new Vector<Float>();
        float prevY=0,prevX=0,bx=0,by=0;
        PathIterator pt=flattened.getPathIterator(null);
        float[] coords=new float[6];
        while (!pt.isDone()){
            int type=pt.currentSegment(coords);
            if (type==PathIterator.SEG_MOVETO){
                prevX=bx=coords[0];
                prevY=by=coords[1];
            }else if (type==PathIterator.SEG_LINETO){
                float isec=intersectionX(vPos,prevX,prevY,coords[0],coords[1]);
                if (isec !=Float.MAX_VALUE)
                    intersections.add(isec);
                prevX=coords[0];
                prevY=coords[1];;
            }else if(type==PathIterator.SEG_CLOSE){
                // float isec=intersectionX(vPos,bx,by,prevX,prevY);
            }
            pt.next();
        }
        if (intersections.size()<2) return null;
        float[] array=new float[intersections.size()];
        Iterator<Float> it=intersections.iterator();
        int i=0;
        while (it.hasNext())
            array[i++]=it.next();
        Arrays.sort(array);
        float rightTerm=array[array.length-1]-rightIndent;
        float leftTerm=array[0]+leftIndent;
        i=0;
        Vector<RowBlock> ret=new Vector<RowBlock>();
        while (i<array.length-1){
            float st=Math.max(leftTerm,array[i]);
            float en=Math.min(array[i+1],rightTerm);
            if (st<en){
                ret.add(new RowBlock(st,en-st));
            }
            i+=2;
        }
        return new BlockIterator(ret);
    }
    private float intersectionX(float y,float bx,float by,float ex,float ey){
        if ((ey-y)*(by-y)>0){
            return Float.MAX_VALUE;
        }
        float dx=ex-bx;
        float dy=ey-by;
        if (dy==0)
            return bx;
        else
            return bx+(y-by)*dx/dy;
    }
    private class RowBlock{
        public  float ofsX;
        public  float width;
        public RowBlock(float ofs,float w){
            this.ofsX=ofs;
            this.width=w;
        }
        public float getEndPos(){
            return ofsX+width;
        }
    }
    private class BlockIterator{
        private int  index=0;
        private Vector<RowBlock> vec;
        public BlockIterator( Vector<RowBlock> vec){
            index=0;
            this.vec=vec;
        }
        public RowBlock first(){
            index=0;
            return vec.get(0);
        }
        public RowBlock current(){
            if (index>=vec.size()) return null;
            return vec.get(index);
        }
        public RowBlock next(){
            index++;
            if (index>=vec.size()) return null;
            return vec.get(index);
        }
        public RowBlock previous(){
            return vec.get(--index);
        }
        public boolean hasNext(){
            return index<vec.size();
        }
        public boolean done(){
            return (index>=vec.size());
        }
        public RowBlock last(){
            return vec.get(index=vec.size()-1);
        }
    }
}
