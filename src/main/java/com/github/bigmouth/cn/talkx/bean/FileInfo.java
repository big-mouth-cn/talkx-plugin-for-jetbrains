package com.github.bigmouth.cn.talkx.bean;

import com.intellij.openapi.vfs.VirtualFile;

/**
 * @author allen
 * @date 2023/6/14
 * @since 1.0.0
 */
public class FileInfo {
    VirtualFile vf;
    boolean isCouldFile = false;

    public VirtualFile getVf() {
        return this.vf;
    }

    public void setVf(VirtualFile vf) {
        this.vf = vf;
    }

    public boolean isCouldFile() {
        return this.isCouldFile;
    }

    public void setCouldFile(boolean couldFile) {
        this.isCouldFile = couldFile;
    }

    public FileInfo(VirtualFile vf, boolean isCouldFile) {
        this.vf = vf;
        this.isCouldFile = isCouldFile;
    }
}
