@echo off

REM set dbloc=net://trelenggtk.ottawa.ibm.com

java -Declipse.perf.dbloc=%dbloc% -jar .\..\..\..\plugins\org.eclipse.equinox.launcher.jar -application org.eclipse.test.performance.ui.resultGenerator %*

