package com.mycp.updatex;

import com.thl.filechooser.FileInfo;

import java.util.List;

/**
 * Description:
 * Data：2018/11/12-16:45
 * Author: Allen
 */
public interface FileSelectCall {
//    void onFileSelect(String filePath);
    void onFileSelectList(List<FileInfo> filePathList);
}
