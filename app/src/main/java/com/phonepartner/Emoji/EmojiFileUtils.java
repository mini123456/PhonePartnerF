package com.phonepartner.Emoji;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 ******************************************
 * @author 廖乃波
 * @文件名称	:  EmojiFileUtils.java
 * @创建时间	: 2017-1-27 下午02:35:09
 * @文件描述	: 文件工具类
 ******************************************
 */
public class EmojiFileUtils {
	/**
	 * 读取表情配置文件
	 * 只在  EmojiConversionUtil  中被调用
	 * @param context
	 * @return
	 */
	public static List<String> getEmojiFile(Context context) {
		try {
			List<String> list = new ArrayList<String>();
			InputStream in = context.getResources().getAssets().open("emoji");// �ļ�����Ϊrose.txt
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String str = null;
			while ((str = br.readLine()) != null) {
				list.add(str);
			}

			return list;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
