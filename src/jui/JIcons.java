/*
 * JIcons.java
 *
 * Created on 2007/09/28, 9:24
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jui;

import javax.swing.ImageIcon;

/**
 *
 * @author i002060
 */
public class JIcons {
    public static ImageIcon CLOSE=null;
    public static ImageIcon CLOSE_HILIGHT=null;
    public static ImageIcon CLOSE_PUSHED=null;
    //
    public static ImageIcon SELECT_BUTTON=null;
    public static ImageIcon HOVER_BUTTON=null;
    //
    public static ImageIcon RESHAPE=null;
    public static ImageIcon DIRECT=null;
    public static ImageIcon DIRECT_PLUS=null;
    public static ImageIcon ANCUR=null;
    public static ImageIcon PEN=null;
    public static ImageIcon PEN_PLUS=null;
    public static ImageIcon PEN_MINUS=null;
    public static ImageIcon CUTTER=null;
    public static ImageIcon LINE=null;
    public static ImageIcon RECTANGLE=null;
    public static ImageIcon ELLIPSE=null;
    public static ImageIcon ROUNDRECT=null;
    public static ImageIcon BEVEL=null;
    public static ImageIcon POLYGON=null;
    public static ImageIcon ARC=null;
    public static ImageIcon RESIZE=null;
    public static ImageIcon ROTATE=null;
    public static ImageIcon SHEAR=null;
    public static ImageIcon SYMMETRIC=null;
    public static ImageIcon ROUND_CORNER=null;
    public static ImageIcon CUTOFF_CORNER=null;
    public static ImageIcon GRADIENT=null;
    public static ImageIcon SPOIT=null;
    public static ImageIcon HAND=null;
    public static ImageIcon TEXT=null;
    public static ImageIcon LAYOUT_TEXT=null;
    public static ImageIcon PATH_TEXT=null;
    public static ImageIcon STAR=null;
    public static ImageIcon MAGNIFY=null;
    //
    public static ImageIcon CENTER=null;
    //ControlIcons
    public static ImageIcon ANGLESLIDER=null;
    public static ImageIcon SLIDER_HORIZONTAL=null;
    public static ImageIcon SLIDER_GRADIENT=null;
    //For Paint
    public static ImageIcon DEFAULT_COLOR=null;
    //nullimage
    public static ImageIcon NULL_ICON=null;
    //For Layer
    public static ImageIcon EYE_ICON=null;
    public static ImageIcon FOLDER_ICON=null;
    public static ImageIcon LOCK_ICON=null;
    public static ImageIcon PAGE_ICON=null;
    public static ImageIcon CHECK_ICON=null;
    
    public static ImageIcon OBJECT_ICON=null;
    public static ImageIcon IMAGE_OBJECT_ICON=null;
    public static ImageIcon TEXT_OBJECT_ICON=null;
    
    public static ImageIcon PENCIL_ICON=null;
    
    
    /**
     * Creates a new instance of JIcons
     */
    public JIcons() {
        setupIcons();
    }
    public void setupIcons(){
        if (CLOSE !=null) return;
        CLOSE=new ImageIcon(getClass().getResource("/jui/uipicture/closenorm.png"));
        CLOSE_HILIGHT=new ImageIcon(getClass().getResource("/jui/uipicture/closehilight.png"));
        CLOSE_PUSHED=new ImageIcon(getClass().getResource("/jui/uipicture/closepushed.png"));
        //
        HOVER_BUTTON=new ImageIcon(getClass().getResource("/jui/uipicture/hoverbutton.png"));
        SELECT_BUTTON=new ImageIcon(getClass().getResource("/jui/uipicture/selectbutton.png"));
        //
        RESHAPE=new ImageIcon(getClass().getResource("/jui/uipicture/reshape.png"));
        DIRECT=new ImageIcon(getClass().getResource("/jui/uipicture/direct.png"));
        DIRECT_PLUS=new ImageIcon(getClass().getResource("/jui/uipicture/directplus.png"));
        ANCUR=new ImageIcon(getClass().getResource("/jui/uipicture/ancur.png"));
        PEN=new ImageIcon(getClass().getResource("/jui/uipicture/pen.png"));
        PEN_PLUS=new ImageIcon(getClass().getResource("/jui/uipicture/penplus.png"));
        PEN_MINUS=new ImageIcon(getClass().getResource("/jui/uipicture/penminus.png"));
        CUTTER=new ImageIcon(getClass().getResource("/jui/uipicture/cutter.png"));
        LINE=new ImageIcon(getClass().getResource("/jui/uipicture/line.png"));
        RECTANGLE=new ImageIcon(getClass().getResource("/jui/uipicture/rectangle.png"));
        ELLIPSE=new ImageIcon(getClass().getResource("/jui/uipicture/ellipse.png"));
        ROUNDRECT=new ImageIcon(getClass().getResource("/jui/uipicture/roundrect.png"));
        BEVEL=new ImageIcon(getClass().getResource("/jui/uipicture/bebel.png"));
        POLYGON=new ImageIcon(getClass().getResource("/jui/uipicture/polygon.png"));
        ARC=new ImageIcon(getClass().getResource("/jui/uipicture/arc.png"));
        RESIZE=new ImageIcon(getClass().getResource("/jui/uipicture/resize.png"));
        ROTATE=new ImageIcon(getClass().getResource("/jui/uipicture/rotation.png"));
        SHEAR=new ImageIcon(getClass().getResource("/jui/uipicture/shear.png"));
        SYMMETRIC=new ImageIcon(getClass().getResource("/jui/uipicture/symmetric.png"));
        ROUND_CORNER=new ImageIcon(getClass().getResource("/jui/uipicture/roundcorner.png"));
        CUTOFF_CORNER=new ImageIcon(getClass().getResource("/jui/uipicture/cutoffcorner.png"));
        GRADIENT=new ImageIcon(getClass().getResource("/jui/uipicture/gradient.png"));
        SPOIT=new ImageIcon(getClass().getResource("/jui/uipicture/spoit.png"));
        HAND=new ImageIcon(getClass().getResource("/jui/uipicture/hand.png"));
        TEXT=new ImageIcon(getClass().getResource("/jui/uipicture/text.png"));
        LAYOUT_TEXT=new ImageIcon(getClass().getResource("/jui/uipicture/layouttext.png"));
        PATH_TEXT=new ImageIcon(getClass().getResource("/jui/uipicture/pathtext.png"));
        STAR=new ImageIcon(getClass().getResource("/jui/uipicture/star.png"));
        MAGNIFY=new ImageIcon(getClass().getResource("/jui/uipicture/magnify.png"));
        
        CENTER=new ImageIcon(getClass().getResource("/jui/uipicture/center.png"));
        ANGLESLIDER=new ImageIcon(getClass().getResource("/jui/uipicture/angleslider.png"));
        SLIDER_HORIZONTAL=new ImageIcon(getClass().getResource("/jui/uipicture/sliderholizon.png"));
        SLIDER_GRADIENT=new ImageIcon(getClass().getResource("/jui/uipicture/gradientslider.png"));
        
        DEFAULT_COLOR=new ImageIcon(getClass().getResource("/jui/uipicture/defaultcolor.png"));
        
        NULL_ICON=new ImageIcon(getClass().getResource("/jui/uipicture/nullimage.png"));
        //
        EYE_ICON=new ImageIcon(getClass().getResource("/jui/uipicture/eye.png"));        
        FOLDER_ICON=new ImageIcon(getClass().getResource("/jui/uipicture/folder.png"));
        LOCK_ICON=new ImageIcon(getClass().getResource("/jui/uipicture/lock.png"));
        PAGE_ICON=new ImageIcon(getClass().getResource("/jui/uipicture/page.png"));
        CHECK_ICON=new ImageIcon(getClass().getResource("/jui/uipicture/check.png"));
        
        OBJECT_ICON=new ImageIcon(getClass().getResource("/jui/uipicture/object.png"));
        IMAGE_OBJECT_ICON=new ImageIcon(getClass().getResource("/jui/uipicture/imageobject.png"));
        TEXT_OBJECT_ICON=new ImageIcon(getClass().getResource("/jui/uipicture/textobject.png"));
        
        PENCIL_ICON=new ImageIcon(getClass().getResource("/jui/uipicture/pencil.png"));
    }
}
