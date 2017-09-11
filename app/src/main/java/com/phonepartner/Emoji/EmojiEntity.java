package com.phonepartner.Emoji;


/**
 * 
 ******************************************
 * @author 廖乃波
 * @文件名称	:  EmojiEntity.java
 * @创建时间	: 2017-1-27 下午02:33:43
 * @文件描述	: 表情对象实体
 ******************************************
 */
public class EmojiEntity {

    /** 表情资源图片对应的ID */
    private int id;

    /** 表情资源对应的文字描述 */
    private String character;

    /** 表情资源的文件名 */
    private String faceName;

    /** 获取表情资源图片对应的ID */
    public int getId() {
        return id;
    }

    /** 设置表情资源图片对应的ID */
    public void setId(int id) {
        this.id=id;
    }

    /** 获取表情资源对应的文字描述 */
    public String getCharacter() {
        return character;
    }

    /** 设置表情资源对应的文字描述 */
    public void setCharacter(String character) {
        this.character=character;
    }

    /** 获取表情资源的文件名 */
    public String getFaceName() {
        return faceName;
    }

    /** 设置表情资源的文件名 */
    public void setFaceName(String faceName) {
        this.faceName=faceName;
    }
}
