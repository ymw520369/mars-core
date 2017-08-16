package com.tsixi.reflectasm;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.google.common.base.Stopwatch;

import java.lang.reflect.Method;

/**
 * Created on 2017/7/28.
 *
 * @author Alan
 * @since 1.0
 */
public class ReflectasmTest {
    public static class SampleClass {
        public void fun(String name) {
            if (name != null) {
                //name.getBytes();
            }
        }
    }

    public static void call(SampleClass sampleClass, String name) {
        sampleClass.fun(name);
    }

    public static void testMethodCall() throws Exception {
        SampleClass someObject = new SampleClass();
//        for (int i = 0; i < 5; i++) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        for (int j = 0; j < 500000000; j++) {
            call(someObject, "Unmi");
        }
        System.out.println("Output spend "+stopwatch.toString());
//        }
    }

    public static void testJdkReflect() throws Exception {
        SampleClass someObject = new SampleClass();
        Method method = SampleClass.class.getMethod("fun", String.class);
//        for (int i = 0; i < 5; i++) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        for (int j = 0; j < 500000000; j++) {
            method.invoke(someObject, "Unmi");
        }
        System.out.println("Output spend "+stopwatch.toString());
//        }
    }

    public static void testReflectAsm() {
        SampleClass someObject = new SampleClass();
        MethodAccess access = MethodAccess.get(SampleClass.class);
        int index = access.getIndex("fun");
//        for (int i = 0; i < 5; i++) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        for (int j = 0; j < 500000000; j++) {
            access.invoke(someObject, index, "Unmi");
        }
        System.out.println("Output spend "+stopwatch.toString());
//        }
    }

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 1000; i++) {
            testMethodCall();
            testJdkReflect();
            testReflectAsm();
            System.out.println();
        }
    }

}
