java_package 'compiler'
java_import 'javax.faces.bean.ManagedBean'
# a small ruby testing class to test for a valid compilation
# from the compiler
# @author Werner Punz
add_class_annotation '@ManagedBean'
class TestProbe1JRuby
  java_signature 'void helloWorld()'
  def helloWorld
      puts "Hello from Ruby"
  end

  java_signature 'public String stringReturn()'
  def stringReturn
    'hello world'
  end

end
