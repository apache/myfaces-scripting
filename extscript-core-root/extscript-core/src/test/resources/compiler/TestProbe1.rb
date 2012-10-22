# a small ruby testing class to test for a valid compilation
# from the compiler
# @author Werner Punz
class TestProbe1
  java_signature 'void helloWorld()'
  def helloWorld
      puts "Hello from Ruby"
  end

  java_signature 'public String stringReturn()'
  def stringReturn
    'hello world'
  end

end
