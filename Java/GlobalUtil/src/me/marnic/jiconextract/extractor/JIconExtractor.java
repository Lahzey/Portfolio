package me.marnic.jiconextract.extractor;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.PointerByReference;
import me.marnic.jiconextract.converter.HICONConverter;
import me.marnic.jiconextract.jnaextras.*;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by Marcel Jedig on Aug,2018
 */

public class JIconExtractor {
    private static final HICONConverter CONVERTER = new HICONConverter();

    public static JIconExtractor getJIconExtractor() {
        return new JIconExtractor();
    }

    public BufferedImage extractIconFromFile(String file,IconSize size) {
        int ICON_SIZE;
        int sizeS;

        if(size==IconSize.SMALL) {
            ICON_SIZE = SHIL.SMALL;
            sizeS = 16;
        }else if(size==IconSize.LARGE) {
            ICON_SIZE = SHIL.LARGE;
            sizeS = 32;
        }else if(size==IconSize.EXTRALARGE) {
            ICON_SIZE = SHIL.EXTRALARGE;
            sizeS = 48;
        }else{
            ICON_SIZE = SHIL.JUMBO;
            sizeS = 256;
        }

        int index = getIconIndex(file);

        WinDef.HICON hIcon = getHIcon(index,ICON_SIZE);

        BufferedImage image = CONVERTER.convertHICONToImage(sizeS,hIcon);

        User32.INSTANCE.DestroyIcon(hIcon);

        return image;
    }

    public BufferedImage extractIconFromFile(File path,IconSize size) {
        return extractIconFromFile(path.getPath(),size);
    }

    private int getIconIndex(String file) {
        SHFILEINFO info = new SHFILEINFO();

        Shell32Extra.INSTANCE.SHGetFileInfo(file,0,info,info.size(),(SHGFI.SYSICONINDEX|SHGFI.LARGEICON|SHGFI.USEFILEATTRIBUTES));

        return info.iIcon;
    }

    private WinDef.HICON getHIcon(int index,int size) {
        PointerByReference pointerByReference = new PointerByReference(new Pointer(0));

        Guid.REFIID id = new Guid.REFIID(new Guid.IID(Shell32Extra.IID_IImageList));

        IImageList list;

        Shell32Extra.INSTANCE.SHGetImageList(size,id,pointerByReference);

        list = new IImageList(pointerByReference.getValue());

        WinDef.HICON hIcon;

        PointerByReference hIconPointer = new PointerByReference();

        list.GetIcon(index,(Shell32Extra.ILD_TRANSPARENT|Shell32Extra.ILD_IMAGE),hIconPointer);

        hIcon = new WinDef.HICON(hIconPointer.getValue());

        return hIcon;
    }
}
