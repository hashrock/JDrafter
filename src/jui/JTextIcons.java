package jui;
import javax.swing.ImageIcon;
/*
 * JTextIcons.java
 *
 * Created on 2007/10/10, 16:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author i002060
 */
public class JTextIcons {
    public static ImageIcon BOLD=null;
    public static ImageIcon ITALIC=null;
    public static ImageIcon UNDERLINE=null;
    public static ImageIcon STRIKETHROUGH=null;
    public static ImageIcon ALIGNLEFT=null;
    public static ImageIcon ALIGNCENTER=null;
    public static ImageIcon ALIGNRIGHT=null;
    public static ImageIcon KINTO=null;
    public static ImageIcon LINESPACING=null;
    public static ImageIcon PARAGRAPH=null;
    /** Creates a new instance of JTextIcons */
    public JTextIcons() {
        if (BOLD==null) setupIcons();
    }
    private void setupIcons(){
        BOLD=new ImageIcon(getClass().getResource("/jui/uipicture/bold.png"));
        ITALIC=new ImageIcon(getClass().getResource("/jui/uipicture/italic.png"));
        UNDERLINE=new ImageIcon(getClass().getResource("/jui/uipicture/underline.png"));
        STRIKETHROUGH=new ImageIcon(getClass().getResource("/jui/uipicture/strikethrough.png"));
        ALIGNLEFT=new ImageIcon(getClass().getResource("/jui/uipicture/alignleft.png"));
        ALIGNCENTER=new ImageIcon(getClass().getResource("/jui/uipicture/aligncenter.png"));
        ALIGNRIGHT=new ImageIcon(getClass().getResource("/jui/uipicture/alignright.png"));
        KINTO=new ImageIcon(getClass().getResource("/jui/uipicture/kintou.png"));
        LINESPACING=new ImageIcon(getClass().getResource("/jui/uipicture/linespacing.png"));
        PARAGRAPH=new ImageIcon(getClass().getResource("/jui/uipicture/paragraph.png"));
    }
    
}
