This is the core of the groovy loader
we have two things in here
a groovy proxy which allows the reloading
of groovy objects loaded via our classloader under certain circumstances
The proxy on the groovy side loads itself into the class metadata
so that the objects under the best circumstances
can run new code or run a new instance


secondly our threaded filewatcher
and object reloader
which checks if files have changed and then reloads
objects wherever possible


