package com.luckycode.gearman;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Java 读取Properties的工具类 无依赖框架
 *
 * @author Sean
 *
 */
public class PropertiesUtil {

	/**
	 * Properties地址值，不需要加根标记"/"
	 */
	private final static String ENCODE = "utf-8";
	private final String CLASSPATH_PERFIX = "classpath:";
	public static Properties properties = new Properties();

	/**
	 * 默认构造函数
	 */
	public PropertiesUtil() {
	}

	/**
	 * 构造函数 实现加载配置文件功能
	 *
	 * @param location
	 *            传入Properties地址值，此处允许两种配置格式，第一种为"classpath:filename"，
	 *            第二种直接以目录path为参数，该参数默认以'/'开始，故若是在classpaht也可以直接输入文件名
	 */
	public PropertiesUtil(String location) {
		// 控制判断
		if (location.trim().equals("")) {
			throw new RuntimeException("The path of Properties File is need");
		}
		// 进行第一种格式化判断处理
		if (location.toUpperCase().startsWith(CLASSPATH_PERFIX.toUpperCase())) {
			properties = load(location.substring(CLASSPATH_PERFIX.length()));
		} else {
			properties = load(location);
		}
	}

	/**
	 * 构造函数 实现加载配置文件功能
	 *
	 * @param location
	 *            传入Properties地址值列表，该列表包含多个配置文件地址
	 */
	public PropertiesUtil(List<String> location) {
		for (String src : location) {
			// 控制判断
			if (src.trim().equals("")) {
				throw new RuntimeException(
						"The path of Properties File is need");
			}
			// 进行第一种格式化判断处理
			if (src.toUpperCase().startsWith(CLASSPATH_PERFIX.toUpperCase())) {
				Properties p = load(src.substring(CLASSPATH_PERFIX.length()));
				properties.putAll(p);
			} else {
				Properties p = load(src);
				properties.putAll(p);
			}

		}
	}

	/**
	 * 加载properties文件
	 *
	 * @author Sean
	 * @date 2015-12-16
	 * @return 返回读取到的properties对象
	 */
	public Properties load(String location) {
		if (location.trim().equals("")) {
			throw new RuntimeException("The path of Properties File is need");
		}
		InputStreamReader inputStream = null;
		Properties p = new Properties();
		try {
			inputStream = new InputStreamReader(getClass().getClassLoader()
					.getResourceAsStream(location), ENCODE);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		load(p,inputStream);
		return p;
	}

	/* 读取操作 */
	/**
	 * 通过关键字获取值
	 *
	 * @author Sean
	 * @date 2015-12-16
	 * @param key
	 *            需要获取的关键字
	 * @return 返回对应的字符串，如果无，返回null
	 */
	public String getValueByKey(String key) {
		String val = properties.getProperty(key.trim());
		return val;

	}

	/**
	 * 通过关键字获取值
	 *
	 * @author Sean
	 * @date 2015-12-16
	 * @param key
	 *            需要获取的关键字
	 * @param defaultValue
	 *            若找不到对应的关键字时返回的值
	 * @return 返回找到的字符串
	 */
	public String getValueByKey(String key, String defaultValue) {
		String val = properties.getProperty(key.trim(), defaultValue.trim());
		return val;
	}

	/**
	 * 获取Properties所有的值
	 *
	 * @author Sean
	 * @date 2015-6-5
	 * @return 返回Properties的键值对
	 */
	public Map<String, String> getAllProperties() {
		Map<String, String> map = new HashMap<String, String>();
		// 获取所有的键值
		Enumeration enumeration = properties.propertyNames();
		while (enumeration.hasMoreElements()) {
			String key = (String) enumeration.nextElement();
			String value = getValueByKey(key);
			map.put(key, value);
		}
		return map;
	}

	/**
	 * 通过关键字获取值
	 *
	 * @author Sean
	 * @date 2015-12-16
	 * @param key
	 *            需要获取的关键字
	 * @return 返回对应的字符串，如果无，返回null
	 */
	public static String getValue(String location, String key) {

		if (location.trim().equals("")) {
			throw new RuntimeException("The path of Properties File is need");
		}
		InputStreamReader inputStream = null;
		Properties p = new Properties();
		try {
			inputStream = new InputStreamReader(PropertiesUtil.class
					.getClassLoader().getResourceAsStream(location), ENCODE);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		load(p,inputStream);

		return p.getProperty(key);

	}

	protected static  void load(Properties properties, InputStreamReader inputStream){
		try {
			properties.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			if (inputStream != null) {
				inputStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
