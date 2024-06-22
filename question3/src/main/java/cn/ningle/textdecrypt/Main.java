package cn.ningle.textdecrypt;

/**
 * @author ningle
 * @version : Main.java, v 0.1 2024/06/22 23:30 ningle
 **/
public class Main {
    public static void main(String[] args) {
        new TextDecryptor(
                "attachments/Question 3/sdxl_prop.txt",
                "attachments/Question 3/sdxl_template.txt",
                "."
        )
                .doDecrypt();
    }
}
