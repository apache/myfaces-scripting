package demo

import javax.faces.bean.ManagedBean

/**
 *
 * @author Werner Punz (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
@ManagedBean
public class GroovyHello {
  def helloWorld = "hello world from a dynamic groovy bean"
}
