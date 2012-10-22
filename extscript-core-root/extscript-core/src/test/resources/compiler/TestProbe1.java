import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyObject;
import org.jruby.javasupport.util.RuntimeHelpers;
import org.jruby.runtime.builtin.IRubyObject;


public class TestProbe1 extends RubyObject  {
    private static final Ruby __ruby__ = Ruby.getGlobalRuntime();
    private static final RubyClass __metaclass__;

    static {
        String source = new StringBuilder("# a small ruby testing class to test for a valid compilation\n" +
            "# from the compiler\n" +
            "# @author Werner Punz\n" +
            "class TestProbe1\n" +
            "  java_signature 'void helloWorld()'\n" +
            "  def helloWorld\n" +
            "      puts \"Hello from Ruby\"\n" +
            "  end\n" +
            "\n" +
            "  java_signature 'public String stringReturn()'\n" +
            "  def stringReturn\n" +
            "    'hello world'\n" +
            "  end\n" +
            "\n" +
            "end\n" +
            "").toString();
        __ruby__.executeScript(source, "TestProbe1JRuby.rb");
        RubyClass metaclass = __ruby__.getClass("TestProbe1");
        metaclass.setRubyStaticAllocator(TestProbe1.class);
        if (metaclass == null) throw new NoClassDefFoundError("Could not load Ruby class: TestProbe1");
        __metaclass__ = metaclass;
    }

    /**
     * Standard Ruby object constructor, for construction-from-Ruby purposes.
     * Generally not for user consumption.
     *
     * @param ruby The JRuby instance this object will belong to
     * @param metaclass The RubyClass representing the Ruby class of this object
     */
    private TestProbe1(Ruby ruby, RubyClass metaclass) {
        super(ruby, metaclass);
    }

    /**
     * A static method used by JRuby for allocating instances of this object
     * from Ruby. Generally not for user comsumption.
     *
     * @param ruby The JRuby instance this object will belong to
     * @param metaclass The RubyClass representing the Ruby class of this object
     */
    public static IRubyObject __allocate__(Ruby ruby, RubyClass metaClass) {
        return new TestProbe1(ruby, metaClass);
    }
        
    /**
     * Default constructor. Invokes this(Ruby, RubyClass) with the classloader-static
     * Ruby and RubyClass instances assocated with this class, and then invokes the
     * no-argument 'initialize' method in Ruby.
     */
    public TestProbe1() {
        this(__ruby__, __metaclass__);
        RuntimeHelpers.invoke(__ruby__.getCurrentContext(), this, "initialize");
    }

    
    public void helloWorld() {

        IRubyObject ruby_result = RuntimeHelpers.invoke(__ruby__.getCurrentContext(), this, "helloWorld");
        return;

    }

    
    public String stringReturn() {

        IRubyObject ruby_result = RuntimeHelpers.invoke(__ruby__.getCurrentContext(), this, "stringReturn");
        return (String)ruby_result.toJava(String.class);

    }

}
