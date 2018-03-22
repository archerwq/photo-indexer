package io.github.archerwq.cmd;

import java.io.File;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class Config {
	public static String[] PHOTO_DIRS;
	public static String ES_URL;
	public static String UGI_TS_PATH;

	public static String DB_URL;
	public static String DB_USER;
	public static String DB_PWD;
	public static int DB_CONN_MIN_IDLE;
	public static int DB_CONN_MAX_IDLE;

	public static void load(String confPath) throws ConfigurationException {
		Configurations configs = new Configurations();
		Configuration config = configs.properties(new File(confPath));
		PHOTO_DIRS = config.getStringArray("photo.dirs");
		ES_URL = config.getString("es.url");
		UGI_TS_PATH = config.getString("ugi.ts.path");

		DB_URL = config.getString("db.url");
		DB_USER = config.getString("db.username");
		DB_PWD = config.getString("db.password");
		DB_CONN_MIN_IDLE = config.getInt("db.conn.min.idle");
		DB_CONN_MAX_IDLE = config.getInt("db.conn.max.idle");
	}
}
