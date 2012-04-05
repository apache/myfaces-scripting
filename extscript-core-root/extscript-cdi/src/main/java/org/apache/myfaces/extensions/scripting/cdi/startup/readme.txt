What is missing is a cdi plugin which triggers the initial lifecycle once
at cdi startup time to make a first compile then pushes the
ThrowAwayClassloader in once for the bean loading
and once the beans are loaded deregisters everything so that the jsf lifecycle
can start.
