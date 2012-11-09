Changes for mojarra
a) We need a context listener for the startup plugin chainloader initialisation
(just like we did it for owb)
b) We need a servlet filter which sets throw away classloader as context classloader
c) We need to write the adapter classes accordingly so that bean refreshes work in mojarra in conjunction with
the ext-scripting api
d) We need to externalize the myfaces adapter classes accordingly and move the call to a real Java SPI
so that we can switch adapter implementations on the fly
e) We need to move the entire facelet part into the myfaces submodule

