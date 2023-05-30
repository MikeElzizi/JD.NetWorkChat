import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;


public class ChatTest {

    @Test
    public void testWriteMsg() throws IOException {
        String msg = "Test message";
        File file = new File("file.log");
        long beforeLength = file.length();
        Server.writeFileServer(msg);
        long afterLength = file.length();
        boolean afterLengthOverBefore = afterLength > beforeLength;
        String fileContent = readLogFile(file.getAbsolutePath());
        boolean haveTestMsg = fileContent.substring(fileContent.length() - msg.length()).contains(msg);
        Assertions.assertTrue(afterLengthOverBefore && haveTestMsg);
    }

    private static String readLogFile(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }
}