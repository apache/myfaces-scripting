=begin
 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements.  See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership.  The ASF licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.
=end

java_package 'compiler'
java_import 'javax.faces.bean.ManagedBean'
# a small ruby testing class to test for a valid compilation
# from the compiler
# @author Werner Punz

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
