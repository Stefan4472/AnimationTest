package plainsimple.spaceships.util.fileio;

import android.content.Context;

import java.io.*;

/**
 * File I/O methods
 */
public class FileUtil {

    // reads the specified file and returns the stored data as a String
    // String will be empty if the file does not exist
    // a blank file will be created if the given filename doesn't exist
    public static String readFile(Context context, String fileName) {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(context.openFileInput(fileName)));
            String result;
            StringBuffer stringBuffer = new StringBuffer();
            while ((result = reader.readLine()) != null) {
                stringBuffer.append(result + "\n");
            }
            return stringBuffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    // writes toWrite to the specified file
    public static boolean writeFile(Context context, String fileName, String toWrite) {
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(toWrite.getBytes());
            outputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // writes serialized object to the specified file
    public static boolean writeObject(Context context, String fileName, Object toWrite) {
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(toWrite);
            os.close();
            fos.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // reads serialized object from specified file
    public static Object readObject(Context context, String fileName) {
        try {
            FileInputStream fis = context.openFileInput(fileName);
            ObjectInputStream is = new ObjectInputStream(fis);
            Object o = is.readObject();
            is.close();
            fis.close();
            return o;
        } catch (IOException|ClassNotFoundException e) {
            return null;
        }
    }
}
