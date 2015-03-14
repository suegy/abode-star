# Starcraft AI using ABODE #

On this page we present an interesting show case for the commercial game [StarCraft](http://us.blizzard.com/en-us/games/sc/). The game itself is a real-time strategy game and is known for its good game balance. Due to the development of the [BWAPI](http://code.google.com/p/bwapi/) interface it is possible to create artificial agents for the game and use it as a sandbox for testing and development new AI methods.

_The herein presented example was done by Simon illustrating the usage of ABODE and [jyPOSH](http://sourceforge.net/projects/jyposh/) to create such an agent._

**This show case includes all instructions and created files to run, test, and extend the POSH Starcraft AI.** If you encounter any issues in setting the environment up and have suggestions for further improvements feel free to contact us using this page.


## JNIBWAPI / BOD StarCraft : Brood War AI ##

The AI presented in this showcase was optimized for the Azalea Map which is avaiable freely. The agent also works for most other maps, but to see its intended behaviour we recommend using the map.

_**To run this example you need following software**_:
  * [StarCraft : Brood War](http://eu.blizzard.com/store/details.xml?id=210000002) _(patched to 1.16.1)_
  * [Azalea Map](http://www.teamliquid.net/tlpd/korean/maps/25_Azalea) for Starcraft

  * [BWAPI](http://code.google.com/p/bwapi/) (tested with BWAPI 3.6.1)
  * ChaosLauncher _(generally comes with BWAPI)_

  * Java _(tested on Java 1.6 32bit)_
  * Python _(tested on Python 2.7)_

  * Jython _(tested on Jython 2.2.1)_
  * [JyPOSH](http://www.cs.bath.ac.uk/~jjb/web/pyposh.html)



### Installation Instructions ###

  * Download and Install a **[JAVA sdk](http://www.oracle.com/technetwork/java/javase/downloads/index.html)** _(java 6 recommended)_
    * set the **`JAVA_HOME`** environment variable to your java installation directory, eg. `C:\Program Files\Java`
    * test installation by opening a terminal or console and run `java -version`
    * ouput should be something like this:
```
java version "1.6.0_32"
Java(TM) SE Runtime Environment (build 1.6.0_32-b05)
Java HotSpot(TM) 32-Bit Server VM (build 20.7-b02, mixed mode)
```

  * Download and Install **[Python](http://www.python.org/download/)** _(2.X recommended)_
    * ensure that the environment variable **`PYTHONPATH`** is set correctly to your installation directory and to be included in the `PATH` SYSTEM variable, eg. _`PYTHONPATH` should be similar to: `C:\Program Files\Python27;C:\Program Files\Python27\Lib\site-packages;`_

  * Download and [Install](http://wiki.python.org/jython/InstallationInstructions) **[Jython](http://www.jython.org/downloads.html)**
    * ensure that the environment variable **`JYTHONPATH`** is set correctly to your installation directory and to be included in the `PATH` SYSTEM variable, eg. _`JYTHONPATH` should be similar to: `%PYTHONPATH%;C:\Program Files\jython252;C:\Program Files\jython252\Lib;`_
    * test installation by opening a terminal or console and run `jython`
    * ouput should be something like this:
```
C:\Users\you>jython
*sys-package-mgr*: processing new jar, 'C:\Program Files\Java\jre6\lib\resources.jar'
*sys-package-mgr*: processing new jar, 'C:\Program Files\Java\jre6\lib\rt.jar'
*sys-package-mgr*: processing new jar, 'C:\Program Files\Java\jre6\lib\jsse.jar'

*sys-package-mgr*: processing new jar, 'C:\Program Files\Java\jre6\lib\jce.jar'
*sys-package-mgr*: processing new jar, 'C:\Program Files\Java\jre6\lib\charsets.jar'
*sys-package-mgr*: processing new jar, 'C:\Program Files\Java\jre6\lib\ext\dnsns.jar'
*sys-package-mgr*: processing new jar, 'C:\Program Files\Java\jre6\lib\ext\localedata.jar'
*sys-package-mgr*: processing new jar, 'C:\Program Files\Java\jre6\lib\ext\sunjce_provider.jar'
Jython 2.5.2 (Release_2_5_2:7206, Mar 2 2011, 23:12:06)
[Java HotSpot(TM) 64-Bit Server VM (Sun Microsystems Inc.)] on java1.6.0_32
Type "help", "copyright", "credits" or "license" for more information.
>>> →
```

  * Download and Extract **[jyPOSH](http://sourceforge.net/projects/jyposh/files/latest/download)**
    * place jyPOSH into some directory which you would use for working projects, eg. `X:\Projects\jyposh-651`
    * add log4j to the java `CLASSPATH` SYSTEM variable, eg. `X:\Projects\TestLibs\log4j-1.2.jar;`
    * re-run `jython` to include the new library
    * test jyPOSH installation by navigation to the installation directory and running `jython launch.py --help`
    * ouput should start with:
```
X:\Projects\jyposh-651>jython launch.py
requires one and only one argument (the library); plus optional options
for help use --help

X:\Projects\jyposh-651>jython launch.py --help
Launches a POSH agent or a set of agents.

Synopsis:
    launch.py [OPTIONS] library

Description:
    Launches a POSH agent by fist initialising the world and then the
    agents. The specified library is the behaviour library that will be used.

    -v, --verbose
        writes more initialisation information to the standard output.

    -h, --help
        print this help message.

...
```

  * Buy and Install **Starcraft : Brood War**
  * patch game to version 1.16.1 if not already done

  * [Download](http://code.google.com/p/bwapi/downloads/detail?name=BWAPI_3.6.1.zip&can=1&q=) and Install **[BWAPI](http://code.google.com/p/bwapi/)** _(tested with 3.6.1)_
    * follow the installation instructions in the contained _**README**_ file
  * Set Up **POSH Starcraft AI**
    * extract from [bod file](http://code.google.com/p/abode-star/downloads/detail?name=davies-sd-code-2012-05.zip&can=2&q=) the `posh-library` into the jyposh library folder, eg. move `starcraftbod` to `X:\Projects\jyposh-651\library`
    * extract `behaviours-jniwapi` into a directory of your choice, eg. `X:\Projects\`
    * add the `AIClient.jar` from the previously extracted _jniwapi_ release folder to your _CLASSPATH_
    * extract `ExampleAIClient.dll` to your jyPOSH directory
    * open the `bwapi.ini` in your bwapi data folder _(inside your starcraft installation folder)_ ,eg. `<starcraft-dir>/bwapi-data/bwapi.ini`
      * change `AI` to `AI = NULL` and `AI_dbg` to `AI_dbg = NULL`

  * run CHAOSLauncher and enable BWAPI

  * start Starcraft

  * using the `command prompt` or `terminal` navigate to your jyPOSH directory
    * run `jython launch.py starcraftbod`

  * This will then start the AI, and if it has worked successfully then the following should appear:
```
	Bridge: BWAPI Client launched!!!
	Bridge: Connecting...
	Bridge: waiting to enter match
```

## Then start a game with the player set as Zerg using the Azalea map in StarCraft! ##

# Error Handling #

If you are using a 64bit machine and encounter following Error:
```
```
you hopefully find this helpful: [jython\_Win7](jython_Win7.md)
