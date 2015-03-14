# jyPOSH & JAVA on Windows7 #

The current java 64 bit version which I installed is not working very well with 32bit JNI for example. There are also issues with shared memory access from java under Windows7.

For the later you have to give the `javaw.exe` and the application/jar running ADMIN rights, which I think is undesirable but at the time of writing this unavoidable as well.

So the first thing when running JNI under Win7 and you want to use shared memory, eg. for [JNIBWAPI](http://code.google.com/p/jnibwapi/), is to give **ADMIN** rights to `javaw.exe` and to the application/jar you want to run in my case the "Command Prompt" `cmd.exe`.


If you still encounter problem like the following error:
```
Exception in thread "Thread-2" java.lang.UnsatisfiedLinkError:
          eisbot.proxy.JNIB WAPI.startClient(Leisbot/proxy/JNIBWAPI;)V         
at eisbot.proxy.JNIBWAPI.startClient(Native Method)         
at eisbot.proxy.JNIBWAPI.start(JNIBWAPI.java:552)         
at scbod.AIClient.run(AIClient.java:60)         
at java.lang.Thread.run(Unknown Source)
```

check what java VM you are running: `java -version`
if there is a 64 bit in there you are running the wrong java environment for JNIBWAPI.

To switch the environment you need to do three things:
  * install java x86 (32 bit version)
  * change `JAVA_HOME` to your new x86 home(see [StarCraftAIShowcase](StarCraftAIShowcase.md) on how to change /set it)
  * adapt jython to recognize the new JAVA home


## Adapt jython JAVA\_HOME ##

  * go to your jython installation directory,eg. `C:\Program Files\jython252`
  * open the `jython.bat` file and change `JAVA_HOME` to the new directory