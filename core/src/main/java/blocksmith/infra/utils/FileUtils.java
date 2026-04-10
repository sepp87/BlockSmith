package blocksmith.infra.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mozilla.universalchardet.UniversalDetector;

/**
 *
 * @author Joost
 */
public class FileUtils {

    public static String readResourceAsString(String path) {
        String result = "";
        try {

            InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(path);
            result = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            inputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public static File createFile(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return file;
    }

    public static File createDirectory(File file) {
        if (!file.exists()) {
            file.mkdir();
        }
        return file;
    }

    public static Properties loadProperties(File file) {
        Properties properties = new Properties();
        try (InputStream inputStream = new FileInputStream(file)) {
            properties.load(inputStream);
        } catch (IOException ex) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return properties;
    }

    public static String detectEncoding(File file) {
        byte[] buf = new byte[4096];
        String encoding = null;
        try (FileInputStream fis = new FileInputStream(file)) {
            UniversalDetector detector = new UniversalDetector(null);
            int nread;
            while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }
            detector.dataEnd();
            encoding = detector.getDetectedCharset();
            detector.reset();
        } catch (IOException ex) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (encoding == null) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.INFO, "Unknown file encoding, defaulting to UTF-8");
            encoding = "UTF-8";
        }

        return encoding;
    }
}
