package com.yinglishzhi;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author LDZ
 * @date 2020/8/13 10:27 上午
 */
public class BootStrap {
    public static void main(String[] args) {
        ClassReader cr;
        try {
            cr = new ClassReader("com.yinglishzhi.MyProgram");
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            AdviceWeaver classAdapter = new AdviceWeaver(cw);
            cr.accept(classAdapter, ClassReader.EXPAND_FRAMES);

            byte[] data = cw.toByteArray();

            write2File("/Users/mtdp/dev/SELF/nonintrusion/nonintrusion-test/src/main/java/com/yinglishzhi/test.class", data);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void write2File(String path, byte[] data) {
        File file = new File(path);
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(file);
            fout.write(data);
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }
}
