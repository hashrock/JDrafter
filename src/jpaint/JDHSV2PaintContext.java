/*
 * JDHuePaintContext.java
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
public class JDHSV2PaintContext implements PaintContext{
    ColorModel cm;
    WritableRaster rs;
    static int mode=0;
    static Color baseColor=null;
    static int[][] buffer=null;
    double dx,dy;//デバイス座標が１動く際のユーザー座標上の移動距離
    double x1,y1;//開始点のデバイス座標での位地；
    /** Creates a new instance of JDHuePaintContext */
    public JDHSV2PaintContext(Color bc,Rectangle2D rect,int mode ,AffineTransform xform) {
        AffineTransform invert=null;
        cm=ColorModel.getRGBdefault();
        this.mode=mode;
        rs=null;
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

        if (buffer==null || this.mode !=mode || (mode ==JDHSV2Paint.SB_MODE && !bc.equals(baseColor))){
            this.mode=mode;
            this.baseColor=bc;
            if (buffer==null)
                buffer=new int[256][256];
            for (int y=0;y<256;y++){
                for (int x=0;x<256;x++){
                    buffer[y][x]=getRGB(x/255d,y/255d);
                }
            }
            
        }
        this.mode=mode;
        this.baseColor=bc;
        //
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
        double valuex=(x-x1)*dx;
        double valuey=(y-y1)*dy;
        double vx,vy;
        for (int sy=0;sy<h;sy++){
            vx=valuex;
            vy=valuey;
            for (int sx=0;sx<w;sx++){
                double ix=(vx<=0)?0:(vx>=1.00)?1:vx;
                double iy=(vy<=0)?0:(vy>=1.00)?1:vy;
                pixels[off++]=buffer[(int)(iy*255)][(int)(ix*255)]; 
                vx+=dx;
            }
            valuey+=dy;
            off+=adjust;
        }
    }
    static float[] bc=null;
    private int getRGB(double vx,double vy){
        if (bc ==null)
            bc=Color.RGBtoHSB(baseColor.getRed(),baseColor.getGreen(),baseColor.getRed(),null);
        float x=(float)((vx<=0)?0f:((vx>=1.0)?1.0f:vx));
        float y=(float)((vy<=0)?0f:((vy>1.0)?1.0f:vy));
        switch (mode){
            case JDHSV2Paint.HB_MODE:
                return Color.HSBtoRGB(x,bc[1],1-y);
            case JDHSV2Paint.HS_MODE:
                return Color.HSBtoRGB(x,1-y,bc[2]);
            case JDHSV2Paint.SB_MODE:
                return Color.HSBtoRGB(bc[0],x,1-y);
        }
        return baseColor.getRGB();
    }
    
}
