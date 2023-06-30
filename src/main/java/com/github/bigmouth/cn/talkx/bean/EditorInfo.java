package com.github.bigmouth.cn.talkx.bean;

/**
 * @author allen
 * @date 2023/6/14
 * @since 1.0.0
 */
public class EditorInfo {

    private String productName;
    private String uId;
    private String userLoginStatus;
    private String talkxVersion;
    private int fontSize;
    private String fontFamily;
    private String theme;

    public String getProductName() {
        return productName;
    }

    public EditorInfo setProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public String getuId() {
        return uId;
    }

    public EditorInfo setuId(String uId) {
        this.uId = uId;
        return this;
    }

    public String getUserLoginStatus() {
        return userLoginStatus;
    }

    public EditorInfo setUserLoginStatus(String userLoginStatus) {
        this.userLoginStatus = userLoginStatus;
        return this;
    }

    public String getTalkxVersion() {
        return talkxVersion;
    }

    public EditorInfo setTalkxVersion(String talkxVersion) {
        this.talkxVersion = talkxVersion;
        return this;
    }

    public int getFontSize() {
        return fontSize;
    }

    public EditorInfo setFontSize(int fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public EditorInfo setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
        return this;
    }

    public String getTheme() {
        return theme;
    }

    public EditorInfo setTheme(String theme) {
        this.theme = theme;
        return this;
    }
}
