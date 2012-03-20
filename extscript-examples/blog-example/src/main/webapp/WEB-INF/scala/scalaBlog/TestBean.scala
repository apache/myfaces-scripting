package scalaBlog

import beans.BeanProperty
import javax.faces.bean.{ApplicationScoped, ManagedBean}

@ManagedBean(name="scalaTestBean")
@ApplicationScoped
class TestBean {
   @BeanProperty
   var title = "A Simple Blogging Example"
}