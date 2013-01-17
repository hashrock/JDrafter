/*
 * JCursor.java
 *
 * Created on 2007/09/09, 9:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jtools;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;

/**
 *
 * @author TI
 */
public class JCursor {

    public static Cursor DIRECT = null;
    public static Cursor DIRECT_MOVE = null;
    public static Cursor DIRECT_MOVE_COPY = null;
    public static Cursor DIRECT_ON_PATH = null;
    public static Cursor DIRECT_ON_OBJECT = null;
    public static Cursor DIRECT_ON_SEGMENT = null;
    public static Cursor DIRECT_PLUS = null;
    public static Cursor RESHAPE = null;
    public static Cursor RESHAPE_ON_OBJECT = null;
    public static Cursor RESHAPE_ON_SEGMENT = null;
    public static Cursor MOVE = null;
    public static Cursor COPY_AND_MOVE = null;
    public static Cursor N_RESIZE = null;
    public static Cursor NE_RESIZE = null;
    public static Cursor W_RESIZE = null;
    public static Cursor NW_RESIZE = null;
    public static Cursor ROTATE1 = null;
    public static Cursor ROTATE2 = null;
    public static Cursor ROTATE3 = null;
    public static Cursor ROTATE4 = null;
    public static Cursor ROTATE5 = null;
    public static Cursor ROTATE6 = null;
    public static Cursor ROTATE7 = null;
    public static Cursor ROTATE8 = null;
    public static Cursor PEN = null;
    public static Cursor PEN_PLUS = null;
    public static Cursor PEN_MINUS = null;
    public static Cursor PEN_ANCUR = null;
    public static Cursor PEN_DUM = null;
    public static Cursor PEN_LINK = null;
    public static Cursor PEN_JOIN = null;
    public static Cursor ANCUR = null;
    public static Cursor CUTTER = null;
    public static Cursor CUTTER_ON_OBJECT = null;
    public static Cursor CROSSHAIR = null;
    public static Cursor TEXT = null;
    public static Cursor LAYOUT_TEXT = null;
    public static Cursor PATH_TEXT = null;
    public static Cursor PATH_TEXT_ON_PATH = null;
    public static Cursor SPOIT = null;
    public static Cursor SPOIT_FILLED = null;
    public static Cursor MAGNIFY_PLUS = null;
    public static Cursor MAGNIFY_MINUS = null;
    public static Cursor MAGNIFY = null;
    public static Cursor HAND = null;
    public static Cursor GRIP = null;
    public static Cursor LOCKED = null;
    public static Cursor PENCIL = null;

    /** Creates a new instance of JCursor */
    public JCursor() {
        setupCursor();
    }

    private void setupCursor() {
        if (DIRECT != null) {
            return;
        }
        Toolkit tk = Toolkit.getDefaultToolkit();
        DIRECT = tk.createCustomCursor(tk.createImage(getClass().getResource("/iconImage/direct.png")), new Point(1, 1), "Direct");
        DIRECT_MOVE = tk.createCustomCursor(tk.createImage(getClass().getResource("/iconImage/directmove.png")), new Point(1, 1), "DirectMove");
        DIRECT_MOVE_COPY = tk.createCustomCursor(tk.createImage(getClass().getResource("/iconImage/directmoveandcopy.png")), new Point(1, 1), "DirectMoveCopy");
        DIRECT_ON_PATH = tk.createCustomCursor(tk.createImage(getClass().getResource("/iconImage/directonpath.png")), new Point(1, 1), "DirectOnPath");
        DIRECT_ON_OBJECT = tk.createCustomCursor(tk.createImage(getClass().getResource("/iconImage/directonobject.png")), new Point(1, 1), "DirectOnObject");
        DIRECT_ON_SEGMENT = tk.createCustomCursor(tk.createImage(getClass().getResource("/iconImage/directonsegment.png")), new Point(1, 1), "DirectOnSegment");
        DIRECT_PLUS = tk.createCustomCursor(tk.createImage(getClass().getResource("/iconImage/directplus.png")), new Point(1, 1), "DirectPlus");
        //
        RESHAPE = tk.createCustomCursor(tk.createImage(getClass().getResource("/iconImage/reshape.png")), new Point(1, 1), "Reshape");
        RESHAPE_ON_OBJECT = tk.createCustomCursor(tk.createImage(getClass().getResource("/iconImage/reshapeonobject.png")), new Point(1, 1), "Reshapeonobject");
        RESHAPE_ON_SEGMENT = tk.createCustomCursor(tk.createImage(getClass().getResource("/iconImage/reshapeonsegment.png")), new Point(1, 1), "ReshapeOnSegment");
        MOVE = tk.createCustomCursor(tk.createImage(getClass().getResource("/iconImage/move.png")), new Point(1, 1), "Move");
        COPY_AND_MOVE = tk.createCustomCursor(tk.createImage(getClass().getResource("/iconImage/copyandmove.png")), new Point(1, 1), "CopyANdMove");
//
        N_RESIZE = tk.createCustomCursor(tk.createImage(getClass().getResource("/iconImage/nresize.png")), new Point(8, 9), "NResize");
        NE_RESIZE = tk.createCustomCursor(tk.createImage(getClass().getResource("/iconImage/neresize.png")), new Point(8, 9), "NEResize");
        W_RESIZE = tk.createCustomCursor(tk.createImage(getClass().getResource("/iconImage/wresize.png")), new Point(8, 9), "WResize");
        NW_RESIZE = tk.createCustomCursor(tk.createImage(getClass().getResource("/iconImage/nwresize.png")), new Point(8, 9), "NWResize");
//
        ROTATE1 = tk.createCustomCursor(tk.createImage(getClass().getResource("/iconImage/rotate1.png")), new Point(7, 7), "Rotate1");
        ROTATE2 = tk.createCustomCursor(tk.createImage(getClass().getResource("/iconImage/rotate2.png")), new Point(7, 7), "Rotate2");
        ROTATE3 = tk.createCustomCursor(tk.createImage(getClass().getResource("/iconImage/rotate3.png")), new Point(7, 7), "Rotate3");
        ROTATE4 = tk.createCustomCursor(tk.createImage(getClass().getResource("/iconImage/rotate4.png")), new Point(7, 7), "Rotate4");
        ROTATE5 = tk.createCustomCursor(tk.createImage(getClass().getResource("/iconImage/rotate5.png")), new Point(7, 7), "Rotate5");
        ROTATE6 = tk.createCustomCursor(tk.createImage(getClass().getResource("/iconImage/rotate6.png")), new Point(7, 7), "Rotate6");
        ROTATE7 = tk.createCustomCursor(tk.createImage(getClass().getResource("/iconImage/rotate7.png")), new Point(7, 7), "Rotate7");
        ROTATE8 = tk.createCustomCursor(tk.createImage(getClass().getResource("/iconImage/rotate8.png")), new Point(7, 7), "Rotate8");
        //
        PEN = tk.createCustomCursor(tk.createImage(getClass().getResource("/iconImage/pen.png")), new Point(4, 0), "Pen");
        PEN_PLUS = tk.createCustomCursor(tk.createImage(getClass().getResource("/iconImage/penplus.png")), new Point(4, 0), "PenPlus");
        PEN_MINUS = tk.createCustomCursor(tk.createImage(getClass().getResource("/iconImage/penminus.png")), new Point(4, 0), "PenMinus");
        PEN_ANCUR = tk.createCustomCursor(tk.createImage(getClass().getResource("/iconImage/penancur.png")), new Point(4, 0), "PenAncur");
        PEN_DUM = tk.createCustomCursor(tk.createImage(getClass().getResource("/iconImage/pendum.png")), new Point(4, 0), "PenDum");
        PEN_LINK = tk.createCustomCursor(tk.createImage(getClass().getResource("/iconImage/penlink.png")), new Point(4, 0), "Penlink");
        PEN_JOIN = tk.createCustomCursor(tk.createImage(getClass().getResource("/iconImage/penjoin.png")), new Point(4, 0), "PenJoin");
        ANCUR = tk.createCustomCursor(tk.createImage(getClass().getResource("/iconImage/ancur.png")), new Point(1, 1), "Ancur");

//
        CUTTER = tk.createCustomCursor(tk.createImage(getClass().getResource("/iconImage/cutter.png")), new Point(2, 16), "Cutter");
        CUTTER_ON_OBJECT = tk.createCustomCursor(tk.createImage(getClass().getResource("/iconImage/cutteronobject.png")), new Point(2, 16), "CutterOnObject");
//
        CROSSHAIR = tk.createCustomCursor(tk.createImage(getClass().getResource("/iconImage/crosshair.png")), new Point(6, 6), "CrossHair");
//
        TEXT = tk.createCustomCursor(
                tk.createImage(getClass().getResource("/iconImage/text.png")), new Point(1, 1), "Text");
        LAYOUT_TEXT = tk.createCustomCursor(
                tk.createImage(getClass().getResource("/iconImage/layouttext.png")), new Point(1, 1), "LayoutText");
        PATH_TEXT = tk.createCustomCursor(
                tk.createImage(getClass().getResource("/iconImage/pathtext.png")), new Point(1, 1), "PathText");
        PATH_TEXT_ON_PATH = tk.createCustomCursor(
                tk.createImage(getClass().getResource("/iconImage/pathtextonpath.png")), new Point(1, 1), "PathTextOnPath");
        //
        SPOIT = tk.createCustomCursor(
                tk.createImage(getClass().getResource("/iconImage/spoit.png")), new Point(2, 14), "Spoit");
        SPOIT_FILLED = tk.createCustomCursor(
                tk.createImage(getClass().getResource("/iconImage/spoitfilled.png")), new Point(2, 14), "SpoitFilled");
        //
        MAGNIFY = tk.createCustomCursor(
                tk.createImage(getClass().getResource("/iconImage/magnify.png")), new Point(6, 6), "MagnifyPlus");
        MAGNIFY_PLUS = tk.createCustomCursor(
                tk.createImage(getClass().getResource("/iconImage/magnifyplus.png")), new Point(6, 6), "MagnifyPlus");
        MAGNIFY_MINUS = tk.createCustomCursor(
                tk.createImage(getClass().getResource("/iconImage/magnifyminus.png")), new Point(6, 6), "MagnifyMinus");
        //
        HAND = tk.createCustomCursor(
                tk.createImage(getClass().getResource("/iconImage/hand.png")), new Point(8, 8), "Hand");
        GRIP = tk.createCustomCursor(
                tk.createImage(getClass().getResource("/iconImage/grip.png")), new Point(8, 8), "Grip");
        //
        LOCKED = tk.createCustomCursor(
                tk.createImage(getClass().getResource("/iconImage/locked.png")), new Point(0, 0), "Locked");

        PENCIL = tk.createCustomCursor(
                tk.createImage(getClass().getResource("/iconImage/pencil.png")), new Point(0, 14), "Pencil");
    }
}
