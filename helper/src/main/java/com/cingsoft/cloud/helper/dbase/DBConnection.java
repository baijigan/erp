package com.cingsoft.cloud.helper.dbase;

import com.cingsoft.cloud.helper.dbase.config.BaseConfig;

import java.io.*;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * 数据库连接池管理类
 * 客户程序可以调用getInstance()方法访问本类的唯一实例
 */
public class DBConnection {

	private static DBConnection instance; // 唯一实例
	private static int clients; // 连接的客户端
	private Vector drivers = new Vector();// 驱动集合
	private Hashtable pools = new Hashtable();// 连接池
	private Properties dbProps;// 属性文件
	private PrintWriter log; // 日志变量

	public static BaseConfig config;
	static{
		try {
			String name = "com.cingsoft.cloud.helper.dbase.config.DevConfig";
			config = (BaseConfig) Class.forName(name).newInstance();
		}catch (Exception E){ E.printStackTrace(); }
	}

	/**
	* 单例模式建构私有函数以防止其它对象创建本类实例
	*/
	private DBConnection() {
		System.out.println("---------------------001");
		this.init2();
	}

	/**
	* 采用单例模式，返回唯一实例.如果是第一次调用此方法,则创建实例
	*/
	public static synchronized DBConnection getInstance() {
		if (instance == null) {
			instance = new DBConnection();
		}

		//System.out.println("+");
		clients++;
		return instance;
	}

	/**
	* 获得一个可用的(空闲的)连接.如果没有可用连接,且已有连接数小于最大连接数 限制,则创建并返回新连接
	*/
	public Connection getConnection(String name) {
		DBConnectionPool dbPool = (DBConnectionPool) pools.get(name);
		if (dbPool != null) {
			return dbPool.getConnection();
		}

		return null;
	}

	/**
	* 获得一个可用连接.若没有可用连接,且已有连接数小于最大连接数限制, 则创建并返回新连接. 否则,在指定的时间内等待其它线程释放连接.
	*/
	public Connection getConnection(String name, long time) {
		DBConnectionPool dbPool = (DBConnectionPool) pools.get(name);
		if (dbPool != null) {
			return dbPool.getConnection(time);
		}

		return null;
	}

	/**
	* 将连接对象返回给由名字指定的连接池
	*/

	public void freeConnection(String name, Connection con) {
		DBConnectionPool dbPool = (DBConnectionPool) pools.get(name);
		if (dbPool != null) {
			dbPool.freeConnection(con);
		}
	}

	/**
	* 关闭所有连接,撤销驱动程序的注册
	*/
	public synchronized void release() {
		if (--clients != 0) {
			return;
		}

		Enumeration allPools = pools.elements();
		while (allPools.hasMoreElements()) {
			DBConnectionPool pool = (DBConnectionPool) allPools.nextElement();
			pool.release();
		}

		Enumeration allDrivers = drivers.elements();
		while (allDrivers.hasMoreElements()) {
			Driver driver = (Driver) allDrivers.nextElement();
			try {
				DriverManager.deregisterDriver(driver);
			} catch (SQLException e) {
				e.printStackTrace();;
			}
		}
	}

	/**
	 * 复位所有连接,撤销驱动程序的注册
	 */
	public synchronized void reset() {
		Enumeration allPools = pools.elements();
		while (allPools.hasMoreElements()) {
			DBConnectionPool pool = (DBConnectionPool) allPools.nextElement();
			pool.release();
		}

		instance= null;
		clients= 0;
	}

	/**
	* 读取属性完成初始化
	*/
	private void init() {
		InputStream fileinputstream = null;
		try {
			fileinputstream = new FileInputStream("db.properties");
			dbProps = new Properties();
			dbProps.load(fileinputstream);

		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		// 加载驱动
		loadDrivers(dbProps);

		// 创建连接池
		createPools(dbProps);
	}

	private void init2(){
		try {
			String connStr= config.dbConnection;
			dbProps = new Properties();
			dbProps.load(new StringReader(connStr));
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		// 加载驱动
		loadDrivers(dbProps);

		// 创建连接池
		createPools(dbProps);
	}

	/**
	* 装载和注册所有JDBC驱动程序
	*/
	private void loadDrivers(Properties props) {
		String driverClasses = props.getProperty("drivers");
		StringTokenizer st = new StringTokenizer(driverClasses);

		while (st.hasMoreElements()) {
			String driverClassName = st.nextToken().trim();
			try {
				Driver driver = (Driver) Class.forName(driverClassName).newInstance();
				DriverManager.registerDriver(driver);
				drivers.addElement(driver);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	* 根据指定属性创建连接池实例.
	*/
	private void createPools(Properties props) {
		Enumeration propNames = props.propertyNames();
		while (propNames.hasMoreElements()) {
			String name = (String) propNames.nextElement();
			if (name.endsWith(".url")) {
				String poolName = name.substring(0, name.lastIndexOf("."));
				// System.out.println(" poolName =" + poolName);

				String url = props.getProperty(poolName + ".url");
				if (url == null) {
					continue;
				}

				String user = props.getProperty(poolName + ".username");
				String password = props.getProperty(poolName + ".password");
				String maxconn = props.getProperty(poolName + ".maxconn", "0");
				int max;
				try {
					max = Integer.valueOf(maxconn).intValue();
				} catch (NumberFormatException e) { max = 0; }

				DBConnectionPool pool = new DBConnectionPool(poolName, url, user, password, max);
				pools.put(poolName, pool);
			}
		}
	}

/**
*
* @功能：数据库连接池内类 此内部类定义了一个连接池.它能够根据要求创建新连接,直到预定的最大连接数为止.
*              			在返回连接给客户程序之前,它能够验证连接的有效性
*/

class DBConnectionPool {
	private String poolName; 			// 连接池名字
	private String dbConnUrl; 			// 数据库的JDBC URL
	private String dbUserName; 			// 数据库账号或null
	private String dbPassWord; 			// 数据库账号密码或null
	private int maxConn; 				// 此连接池允许建立的最大连接数
	private int checkedOut; 			// 当前连接数
	private Vector<Connection> freeConnections; 	// 保存所有可用连接

	public DBConnectionPool(String poolName, String dbConnUrl,
							String dbUserName, String dbPassWord, int maxConn) {

		this.poolName = poolName;
		this.dbConnUrl = dbConnUrl;
		this.dbUserName = dbUserName;
		this.dbPassWord = dbPassWord;
		this.maxConn = maxConn;
		this.freeConnections = new Vector<Connection>();
	}

	/**
	* 从连接池获得一个可用连接.如果没有空闲的连接且当前连接数小于最大连接 数限制,则创建新连接.
	* 如原来登记为可用的连接不再有效,则从向量删除之,然后递归调用自己以尝试新的可用连接.
	*/
	public synchronized Connection getConnection() {
		Connection conn = null;
		if (freeConnections != null && freeConnections.size() > 0) {
			conn = (Connection) freeConnections.firstElement();
			freeConnections.removeElementAt(0);

			try {
				if (conn.isClosed())conn = getConnection();
				// else System.out.println("%");
			} catch (SQLException e) {
				e.printStackTrace();
				conn = getConnection();
			}

		} else if (maxConn == 0 || checkedOut < maxConn) {
			conn = newConnection();
		}

		if (conn != null)checkedOut++;
		return conn;
	}


	/**
	* 从连接池获取可用连接.可以指定客户程序能够等待的最长时间 参见前一个getConnection()方法.
	*/
	public synchronized Connection getConnection(long timeout) {
		long startTime = System.currentTimeMillis();
		Connection conn = null;

		while ((conn = getConnection()) == null) {
			try {
				wait(timeout);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if ((System.currentTimeMillis() - startTime) >= timeout) {
				return null;
			}
		}

		return conn;
	}

	/**
	* 创建新的连接
	*/
	private Connection newConnection() {
		Connection conn = null;
		try {
			if (dbUserName == null) conn = DriverManager.getConnection(dbConnUrl);
			else conn = DriverManager.getConnection(dbConnUrl, dbUserName, dbPassWord);
		} catch (SQLException e) { e.printStackTrace(); }

		return conn;
	}

	/**
	* 将不再使用的连接返回给连接池
	*/
	public synchronized void freeConnection(Connection conn) {
		freeConnections.addElement(conn);
		checkedOut--;
		notifyAll();
	}

	/**
	* 关闭所有连接
	*/
	public synchronized void release() {
		Enumeration<Connection> allConnections = freeConnections.elements();
		while (allConnections.hasMoreElements()) {
			Connection con = (Connection) allConnections.nextElement();
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		freeConnections.removeAllElements();
	}
}
}
