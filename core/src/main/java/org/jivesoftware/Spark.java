package org.jivesoftware;

import com.sweet.util.DESUtil;
import com.sweet.util.DESedeCoder;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import org.jivesoftware.resource.Default;
import org.jivesoftware.resource.Res;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.themes.ColorSettingManager;
import org.jivesoftware.spark.ui.themes.ColorSettings;
import org.jivesoftware.spark.ui.themes.LookAndFeelManager;
import org.jivesoftware.spark.util.ModelUtil;
import org.jivesoftware.spark.util.UIComponentRegistry;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;


/**
 * In many cases, you will need to know the structure of the Spark installation, such as the directory structures, what
 * type of system Spark is running on, and also the arguments which were passed into Spark on startup. The <code>Spark</code>
 * class provides some simple static calls to retrieve this information.
 *
 * @version 1.0, 11/17/2005
 */
public final class Spark {



    private static String USER_SPARK_HOME;



    public static String ARGUMENTS;

    private static File RESOURCE_DIRECTORY;
    private static File BIN_DIRECTORY;
    private static File LOG_DIRECTORY;
    private static File PLUGIN_DIRECTORY;
    private static File SECURITY_DIRECTORY;

    //--------
    private static String SERVER_NAME;
    private static String SERVER_PORT;
//    private static String SERVER_NAME = "222.128.85.159";
//    private static String SERVER_PORT ="19916";
    //--------
    /**
     * Private constructor that invokes the LoginDialog and
     * the Spark Main Application.
     */
    public Spark() {

    }

    private static synchronized File initializeDirectory(File directoryHome, String directoryName){
    	File targetDir = new File(directoryHome, directoryName).getAbsoluteFile();
        if(!targetDir.exists()){
        	targetDir.mkdirs();
        }
        return targetDir;
    }
    
    
    private static synchronized File initializeDirectory(String directoryName){
    	return initializeDirectory(new File(USER_SPARK_HOME), directoryName);
    }
    
    public void startup() {
	if (System.getenv("APPDATA") != null && !System.getenv("APPDATA").equals("")) {
	    USER_SPARK_HOME = System.getenv("APPDATA") + "/" + getUserConf();
	} else {
	    USER_SPARK_HOME = System.getProperties().getProperty("user.home") + "/" + getUserConf();
	}

        String current = System.getProperty("java.library.path");
        String classPath = System.getProperty("java.class.path");

        // Set UIManager properties for JTree
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        /** Update Library Path **/
        StringBuilder buf = new StringBuilder();
        buf.append(current);
        buf.append(";");

    	SparkCompatibility sparkCompat = new SparkCompatibility();
    	try {
    		// Absolute paths to a collection of files or directories to skip
			Collection<String> skipFiles = new HashSet<>();
			skipFiles.add(new File(USER_SPARK_HOME, "plugins").getAbsolutePath());

    		sparkCompat.transferConfig(USER_SPARK_HOME, skipFiles);
    	} catch (IOException e) {
    		// Do nothing
    	}

    	
    	RESOURCE_DIRECTORY = initializeDirectory("resources");
    	BIN_DIRECTORY = initializeDirectory("bin");
    	LOG_DIRECTORY = initializeDirectory("logs");
        File USER_DIRECTORY = initializeDirectory( "user" );
    	PLUGIN_DIRECTORY = initializeDirectory("plugins");
        File XTRA_DIRECTORY = initializeDirectory( "xtra" );
        SECURITY_DIRECTORY = initializeDirectory("security");
    	// TODO implement copyEmoticonFiles();
        final String workingDirectory = System.getProperty("appdir");
        
        if (workingDirectory == null) {
            System.out.println( "Warning: no working directory set. This might cause updated data to be missed. Please set a system property 'appdir' to the location where Spark is installed to correct this." );
            if (!RESOURCE_DIRECTORY.exists() || !LOG_DIRECTORY.exists() || !USER_DIRECTORY.exists() || !PLUGIN_DIRECTORY.exists() || !XTRA_DIRECTORY.exists() || !SECURITY_DIRECTORY.exists()) {
            	UIManager.put("OptionPane.okButtonText", Res.getString("ok"));
            	JOptionPane.showMessageDialog(new JFrame(), "Unable to create directories necessary for runtime.", "Spark Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }
        // This is the Spark.exe or Spark.dmg installed executable.

        else {
            // This is the installed executable.
            File workingDir = new File(workingDirectory);
            RESOURCE_DIRECTORY = initializeDirectory(workingDir, "resources");
            BIN_DIRECTORY = initializeDirectory(workingDir, "bin");
            File emoticons = new File( XTRA_DIRECTORY, "emoticons").getAbsoluteFile();
            if(!emoticons.exists() || emoticons.listFiles() == null || emoticons.listFiles().length == 0 ){
                copyEmoticonFiles(workingDirectory);
            	//Copy emoticon files from install directory to the spark user home directory
            }
            SECURITY_DIRECTORY = initializeDirectory(workingDir, "security");
            LOG_DIRECTORY = initializeDirectory("logs");
            LOG_DIRECTORY = new File(USER_SPARK_HOME, "logs").getAbsoluteFile();
            LOG_DIRECTORY.mkdirs();
            try {
                buf.append(RESOURCE_DIRECTORY.getCanonicalPath()).append(";");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Set default language set by the user.
        loadLanguage();

        /**
         * Loads the LookandFeel
         */
        LookAndFeelManager.loadPreferredLookAndFeel();


        buf.append(classPath);
        buf.append(";").append(RESOURCE_DIRECTORY.getAbsolutePath());

        // Update System Properties
        System.setProperty("java.library.path", buf.toString());


        System.setProperty("sun.java2d.noddraw", "true");
        System.setProperty("file.encoding", "UTF-8");

        SwingUtilities.invokeLater( () -> {
            // Start Application
            new Spark();
        } );

        installBaseUIProperties();

        if (Default.getBoolean(Default.CHANGE_COLORS_DISABLED)) {
            ColorSettingManager.restoreDefault();
        }

        try {
	        EventQueue.invokeAndWait( () -> {
            final LoginDialog dialog = UIComponentRegistry.createLoginDialog();
                dialog.invoke(new JFrame());
            } );
        }
        catch(Exception ex) {
        	ex.printStackTrace();
        }
    }

    // Setup the look and feel of this application.
    static {
        com.install4j.api.launcher.StartupNotification.registerStartupListener(new SparkStartupListener());
    }

    /**
     * Return if we are running on windows.
     *
     * @return true if we are running on windows, false otherwise.
     */
    public static boolean isWindows() {
        final String osName = System.getProperty("os.name").toLowerCase();
        return osName.startsWith("windows");
    }

    /**
     * Returns true if Spark is running on vista.
     *
     * @return true if running on Vista.
     */
    public static boolean isVista() {
        final String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("vista");
    }

    /**
     * Return if we are running on a mac.
     *
     * @return true if we are running on a mac, false otherwise.
     */
    public static boolean isMac() {
        String lcOSName = System.getProperty("os.name").toLowerCase();
        return lcOSName.contains( "mac" );
    }


    /**
     * Returns the value associated with a passed in argument. Spark
     * accepts HTTP style attributes to allow for name-value pairing.
     * ex. username=foo&password=pwd.
     * To retrieve the value of username, you would do the following:
     * <pre>
     * String value = Spark.getArgumentValue("username");
     * </pre>
     *
     * @param argumentName the name of the argument to retrieve.
     * @return the value of the argument. If no argument was found, null
     *         will be returned.
     */
    public static String getArgumentValue(String argumentName) {
        if (ARGUMENTS == null) {
            return null;
        }

        String arg = argumentName + "=";

        int index = ARGUMENTS.indexOf(arg);
        if (index == -1) {
            return null;
        }

        String value = ARGUMENTS.substring(index + arg.length());
        int index2 = value.indexOf("&");
        if (index2 != -1) {
            // Must be the last argument
            value = value.substring(0, index2);
        }


        return value;
    }

    public void setArgument(String arguments) {
        ARGUMENTS = arguments;
    }

    /**
     * Returns the bin directory of the Spark install. The bin directory contains the startup scripts needed
     * to start Spark.
     *
     * @return the bin directory.
     */
    public static File getBinDirectory() {
    	if (BIN_DIRECTORY == null ) {
            BIN_DIRECTORY = initializeDirectory("bin");
        }
    	return BIN_DIRECTORY;
    }

    /**
     * Returns the resource directory of the Spark install. The resource directory contains all native
     * libraries needed to run os specific operations, such as tray support. You may place other native
     * libraries within this directory if you wish to have them placed into the system.library.path.
     *
     * @return the resource directory.
     */
    public static File getResourceDirectory() {
        if (RESOURCE_DIRECTORY == null) {
            RESOURCE_DIRECTORY = initializeDirectory("resources");
        }
        return RESOURCE_DIRECTORY;

    }
    
    /**
     * Returns the plugins directory of the Spark install. THe plugins-dir contains all the third-party plugins.
     * 
     * @return the plugins directory
     */
    public static File getPluginDirectory() {
        if (PLUGIN_DIRECTORY == null) {
            PLUGIN_DIRECTORY = initializeDirectory("plugins");
        }
        return PLUGIN_DIRECTORY; 
    }

    /**
     * Returns the log directory. The log directory contains all debugging and error files for Spark.
     *
     * @return the log directory.
     */
    public static File getLogDirectory() {
    	if (LOG_DIRECTORY == null ) {
            LOG_DIRECTORY = initializeDirectory("logs");
        }
        return LOG_DIRECTORY;
    }

    /**
     * Return if we are running on Linux.
     *
     * @return true if we are running on Linux, false otherwise.
     */

    public static boolean isLinux() {
        final String osName = System.getProperty("os.name").toLowerCase();
        return osName.startsWith("linux");
    }

    /**
     * Keep track of the users configuration directory.
     *
     * @return Directory name depending on Operating System.
     */
    public static String getUserConf() {
        if (isLinux()) {
            return Default.getString(Default.USER_DIRECTORY_LINUX);
        }
        else if(isMac())
        {
            return Default.getString(Default.USER_DIRECTORY_MAC);
        }
        else {
            return Default.getString(Default.USER_DIRECTORY_WINDOWS);
        }
    }


    /**
     * Returns the Spark directory for the current user (user.home). The user home is where all user specific
     * files are placed to run Spark within a multi-user system.
     *
     * @return the user home / Spark;
     */
    public static String getSparkUserHome() {
        return USER_SPARK_HOME;
    }

    /**
     * Return the base user home.
     *
     * @return the user home.
     */
    public static String getUserHome() {
        return System.getProperties().getProperty("user.home");
    }



    //-------
    public static String getSreverPort() {
        return SERVER_PORT;
    }

    public static void setServerPort(String port) {
        SERVER_PORT = port;
    }

    public static String getServerName() {
        return SERVER_NAME;
    }

    public static void setServerName(String server) {
        SERVER_NAME = server;
    }
    //-------
    public static boolean disableUpdatesOnCustom() {
	return Default.getBoolean(Default.DISABLE_UPDATES);
    }

	public static synchronized void setApplicationFont(Font f) {
        UIDefaults defaults = UIManager.getLookAndFeelDefaults();
        for (Object ui_property : defaults.keySet()) {
            if (ui_property.toString().endsWith(".font")) {
                UIManager.put(ui_property, f);
            }
        }
	}

    /**
     * Sets Spark specific colors
     */
    public static void installBaseUIProperties() {
    	setApplicationFont(new Font("Dialog", Font.PLAIN, 11));
        UIManager.put("ContactItem.border", BorderFactory.createLineBorder(Color.white));
        //UIManager.put("TextField.font", new Font("Dialog", Font.PLAIN, 11));
        //UIManager.put("Label.font", new Font("Dialog", Font.PLAIN, 11));

        ColorSettings colorsettings = ColorSettingManager.getColorSettings();

        for(String property : colorsettings.getKeys())
        {
            Color c = colorsettings.getColorFromProperty(property);
            UIManager.put(property, c);
        }


    }

    /**
     * Loads the language set by the user. If no language is set, then the default implementation will be used.
     */
    private void loadLanguage() {
        final LocalPreferences preferences = SettingsManager.getLocalPreferences();
        final String setLanguage = preferences.getLanguage();
        if (ModelUtil.hasLength(setLanguage)) {
            Locale[] locales = Locale.getAvailableLocales();
            for (Locale locale : locales) {
                if (locale.toString().equals(setLanguage)) {
                    Locale.setDefault(locale);
                    break;
                }
            }
        }
    }
   public void copyEmoticonFiles(String workdir) {
        // Current Plugin directory
        File newEmoticonDir = new File(Spark.getLogDirectory().getParentFile(), "xtra" + File.separator + "emoticons").getAbsoluteFile();
        newEmoticonDir.mkdirs();

        File EMOTICON_DIRECTORY = new File(workdir + File.separator + "xtra" +  File.separator+"emoticons");

        if (EMOTICON_DIRECTORY.listFiles() != null)
        {
            for ( File file : EMOTICON_DIRECTORY.listFiles() )
            {
                if ( file.isFile() )
                {
                    // Copy over
                    File newFile = new File(newEmoticonDir, file.getName());
                }
            }
        }
    }


    //---------

    public static String getOAServerURL() {
        String imServerName = Spark.getServerName();
        if (imServerName == null) {
            imServerName = "localhost";
//            imServerName = "222.128.85.159";
        }
        String port = Spark.getSreverPort();
        if (port == null) {
            port = "8080";
//            port = "19916";
        }

        StringBuffer sb = new StringBuffer();
        sb.append("http://" + imServerName + ":" + port + "/ProjManager/imforward.jsp");
        String user = SparkManager.getSessionManager().getUsername();
        String passwd = SparkManager.getSessionManager().getPassword();
        sb.append("?uid=" + DESedeCoder.encrypt(user + ":" + passwd));

//        try {
//            System.out.println(DESedeCoder.decrypt(DESedeCoder.encrypt(user + ":" + passwd)));
//        } catch (InvalidKeyException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (InvalidKeySpecException e) {
//            e.printStackTrace();
//        } catch (NoSuchPaddingException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (IllegalBlockSizeException e) {
//            e.printStackTrace();
//        } catch (BadPaddingException e) {
//            e.printStackTrace();
//        }


        return sb.toString();
    }
    // 小信使新增插件初始化
    private void initIAngelPlugin() {
        checkNewVersion(false);
    }

    /**
     * 检测是否有新版本
     */
    private void checkNewVersion(boolean type) {
        MainWindow mw = MainWindow.getInstance();
        mw.checkUpdate(type);
    }
    //---------

}
