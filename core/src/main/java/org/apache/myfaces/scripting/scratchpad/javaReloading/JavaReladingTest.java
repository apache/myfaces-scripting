package org.apache.myfaces.scripting.scratchpad.javaReloading;

import org.apache.myfaces.scripting.scratchpad.jsr199.CompilerFacade;
import org.apache.myfaces.scripting.api.DynamicCompiler;

/**
 * 
 */
public class JavaReladingTest {

   public static void main(String ... argv) {
       while(true) {
          // TempFileClassLoader classLoader = new TempFileClassLoader(Thread.currentThread().getContextClassLoader(), true, "/home/werpu/development/workspace/myfaces-groovy/core/src/main/java");
           DynamicCompiler compiler = new CompilerFacade();
           try {
               Class clazz = compiler.compileFile("/home/werpu/development/workspace/myfaces-groovy/core/src/main/java","","org/apache/myfaces/scripting/scratchpad/probes/TestClass.java");
               ITestClass proxy = (ITestClass) clazz.newInstance();
               proxy.helloWorld();
               
               //ITestClass proxy = (ITestClass) classLoader.loadClass("org.apache.myfaces.scripting.scratchpad.probes.TestClass").newInstance();
               //proxy.helloWorld();
           } catch (ClassNotFoundException e) {
               e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
           } catch (IllegalAccessException e) {
               e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
           } catch (InstantiationException e) {
               e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
           }


           try {
               Thread.sleep(1000);
           } catch (InterruptedException e) {
               e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
           }
       }


    }
}
