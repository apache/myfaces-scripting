import org.apache.myfaces.groovyloader.core.DelegatingGroovyClassloader
import org.apache.myfaces.groovyloader.core.Groovy2GroovyObjectReloadingProxy
import org.apache.myfaces.groovyloader.core.GroovyWeaver

/**
 * Created by IntelliJ IDEA.
 * User: werpu
 * Date: 11.04.2008
 * Time: 16:31:54
 * To change this template use File | Settings | File Templates.
 */
class TestClassloader {

    /**
     * GroovyClassLoader gcl = new GroovyClassLoader(getClass().getClassLoader());
     File currentClassFile = new File(fileName)
     def aclass = gcl.parseClass(currentClassFile)
     def myMetaClass = new org.apache.myfaces.groovyloader.core.Groovy2GroovyObjectReloadingProxy(aclass, currentClassFile)

     def invoker = InvokerHelper.instance
     invoker.metaRegistry.setMetaClass(aclass, this)
     */
    private replaceClass(String fileName) {
        Class aclass = Groovy2GroovyObjectReloadingProxy.getClass(fileName);
        return aclass
    }

    def doObjectWeaving() {

        Class myClass = GroovyWeaver.instance.loadDynamicGroovyClass(
                "/Users/werpu/Desktop/myfaces-groovy/core/src/main/groovy/HelloWorld.groovy"); //org.apache.myfaces.groovyloader.core.Groovy2GroovyObjectReloadingProxy.getClass("/Users/werpu/Desktop/development/sparinvest/testgroovy/src/main/groovy/org.apache.myfaces.groovyloader.HelloWorld.groovy")


        def args = new Object[0]
        def retVal = myClass.newInstance()
        return retVal;
    }

    def sayHello() {

    }

    public static void main(String[] argv) {
        DelegatingGroovyClassloader loader = new DelegatingGroovyClassloader();

        loader.testRoot = "/Users/werpu/Desktop/myfaces-groovy/core/src/main/groovy/";
        Thread.currentThread().setContextClassLoader(loader)
        TestClassloader hello = new TestClassloader()
        //def  myObj = hello.doObjectWeaving()
        def myObj = loader.loadClass("org.apache.myfaces.groovloader.HelloWorld").newInstance()
        myObj.printIt()
        Thread.sleep 15000
        myObj = myObj.class.newInstance()
        myObj.printIt()
        myObj = myObj.class.newInstance()
        myObj.printIt()

        //hello.doit()
    }
}