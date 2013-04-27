/*
 * JDHSV1PaintContext.java
 *
 * Created on 2007/02/13, 13:54
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jpaint;

import java.awt.Color;
import java.awt.geom.*;
import java.awt.PaintContext;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;

/**
 *
 * @author i002060
 */
public class JDHSV3PaintContext implements PaintContext{
    ColorModel cm;
    static WritableRaster rs=null;
    static int[][] svPixels=null;
    double dx,dy;//デバイス座標が１動く際のユーザー座標上の移動距離
    double x1,y1;//開始点のデバイス座標での位地；
    /**
     * Creates a new instance of JDHSV1PaintContext
     */
    public JDHSV3PaintContext(Rectangle2D rect,AffineTransform xform) {
        AffineTransform invert=null;
        cm=ColorModel.getRGBdefault();
        try{
            invert=xform.createInverse();
        }catch (java.awt.geom.NoninvertibleTransformException ex){
            invert=new AffineTransform();
        }
        Point2D xVec=new Point2D.Double(1,0);
        Point2D yVec=new Point2D.Double(0,1);
        invert.deltaTransform(xVec,xVec);
        invert.deltaTransform(yVec,yVec);

        if (rect.getWidth()==0)
            dx=0;
        else
            dx=Math.sqrt(xVec.getX()*xVec.getX()+xVec.getY()*xVec.getY())/rect.getWidth();
        if (rect.getHeight()==0)
            dy=0;
        else
            dy=Math.sqrt(yVec.getX()*yVec.getX()+yVec.getY()*yVec.getY())/rect.getHeight();
        Point2D dp1=xform.transform(new Point2D.Double(rect.getX(),rect.getY()),null);
        x1=dp1.getX();
        y1=dp1.getY();
        //
        if (svPixels==null){
            svPixels=new int[512][256];
            float h,s,b;
            for (int y=0;y<512;y++){
                for (int x=0;x<256;x++){
                    h=x/255f;
                    s=(y>255)?1.0f:y/255f;
                    b=(y<256)?1.0f:(511-y)/255f;
                    svPixels[y][x]=Color.HSBtoRGB(h,s,b);
                }
            }
        }
    }
    public void dispose() {
        rs=null;
    }

    public ColorModel getColorModel() {
        return cm;
    }

    public Raster getRaster(int x, int y, int w, int h) {
        if (rs!=null){
            if (rs.getWidth()<w || rs.getHeight()<h){
                rs=cm.createCompatibleWritableRaster(w,h);
            }
        }else{
            rs=cm.createCompatibleWritableRaster(w,h);
        }
        DataBufferInt buffer=(DataBufferInt)rs.getDataBuffer();
        SinglePixelPackedSampleModel model=(SinglePixelPackedSampleModel)rs.getSampleModel();
        int off=model.getOffset(0,0);
        int adjust=model.getScanlineStride()-w;
        int[] pixels=buffer.getData();
        fillPixels(pixels,x,y,w,h,adjust,off);
        return rs;
    }
    private void fillPixels(int[] pixels,int x,int y,int w,int h,int adjust,int off){
        double sx=(x-x1)*dx;
        double sy=(y-y1)*dy;
        double vx,vy;
        int rgb;
        for (int iy=0;iy<h;iy++){
            vx=sx;
            vy=sy;
            for (int ix=0;ix<w;ix++){
                int idx=(int)(vx*255);
                int idy=(int)(vy*511);
                idx=(idx<=0)?0:(idx>255 ? 255:idx);
                idy=(idy<=0)?0:(idy>511 ? 511:idy);
                pixels[off++]=svPixels[idy][idx];
                vx+=dx;
            }
            sy+=dy;
            off+=adjust;
        }
    }
    
}
