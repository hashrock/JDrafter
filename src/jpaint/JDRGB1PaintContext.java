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
public class JDRGB1PaintContext implements PaintContext{
    ColorModel cm;
    static WritableRaster rs=null;    
    int[] rgbs=null;
    int mode=-1;
    int direction=-1;
    AffineTransform xForm;
    Color baseColor=null;
    double dx,dy;//デバイス座標が１動く際のユーザー座標上の移動距離
    double x1,y1;//開始点のデバイス座標での位地；
    /**
     * Creates a new instance of JDHSV1PaintContext
     */
    public JDRGB1PaintContext(int direction,int mode,Rectangle2D rect,Color base,AffineTransform xform) {
        xForm=xform;
        this.direction=direction;
        cm=ColorModel.getRGBdefault();
        changeRect(rect);
        this.mode=mode;  
        changeColor(base);
        //

    }
    public void changeColor(Color base){
        int alpha=(base.getAlpha() <<24) & 0xff000000;
        int red=(base.getRed() <<16) & 0xff0000;
        int green=(base.getGreen() << 8 )& 0xff00;
        int blue=base.getBlue() & 0xff;
        if (rgbs==null || !base.equals(baseColor)){
            if (rgbs==null)
                rgbs=new int[256];         
            for (int i=0;i<256;i++){
                switch (mode){
                    case JDRGB1Paint.R: rgbs[i]=(i<<16) & 0xff0000 | alpha | green | blue;break;
                    case JDRGB1Paint.G: rgbs[i]=(i<< 8) & 0xff00 | alpha | red | blue;break;
                    case JDRGB1Paint.B: rgbs[i]=i & 0xff | alpha | red | green;break;
                }
             }
        }
        this.baseColor=base;
     
    }
    public void changeRect(Rectangle2D rect){
        AffineTransform invert=null;
        try{
            invert=xForm.createInverse();
        }catch (java.awt.geom.NoninvertibleTransformException ex){
            invert=new AffineTransform();
        }
        Point2D xVec=new Point2D.Double(1,0);
        Point2D yVec=new Point2D.Double(0,1);
        invert.deltaTransform(xVec,xVec);
        invert.deltaTransform(yVec,yVec);

        if (rect.getWidth()==0 || direction!=JDHSV1Paint.HOLIZONTAL)
            dx=0;
        else
            dx=Math.sqrt(xVec.getX()*xVec.getX()+xVec.getY()*xVec.getY())/rect.getWidth();
        if (rect.getHeight()==0 || direction!=JDHSV1Paint.VERTICAL)
            dy=0;
        else
            dy=Math.sqrt(yVec.getX()*yVec.getX()+yVec.getY()*yVec.getY())/rect.getHeight();
        Point2D dp1=xForm.transform(new Point2D.Double(rect.getX(),rect.getY()),null);
        x1=dp1.getX();
        y1=dp1.getY();
        
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
        double value=(x-x1)*dx+(y-y1)*dy;
        double v;
        int rgb;
        for (int sy=0;sy<h;sy++){
            v=value;
            for (int sx=0;sx<w;sx++){
                v+=dx;
                if (v<=0)
                    rgb=rgbs[0];
                else if (v>1.0)
                    rgb=rgbs[255];
                else
                    rgb=rgbs[(int)(255*v)];
                pixels[off++]=rgb;                
            }
            value+=dy;
            off+=adjust;
        }
    }
    
}
