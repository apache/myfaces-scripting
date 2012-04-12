struberg: ThreadContextClassLoader
[4:46pm] werpu_: mhh thats what I have been replacing weird
[4:48pm] werpu_: ok enough for today
[4:48pm] werpu_: i really have to debug this out on the owb side of things
[4:48pm] werpu_: any hint which file does the bean classloading in the owb impl?
[4:49pm] werpu_: outside of that using an owb extension to bootstrap my system worked like a charm
[4:49pm] werpu_:
[4:50pm] struberg: ScannerService
[4:50pm] struberg: this is the SPI interface
[4:50pm] struberg: check which one is used in your installation
[4:50pm] werpu_: DefaultScannerService
[4:50pm] werpu_: i assume
[4:50pm] werpu_:
[4:50pm] struberg: yup
[4:51pm] werpu_: given that it is a standard web environment
[4:51pm] werpu_: i will have a look
[4:51pm] struberg: well, in WebApps there is another one
[4:51pm] werpu_: ah
[4:51pm] werpu_: ok
[4:51pm] struberg: yup, needs to take WEB-INF/classes into consideration as well