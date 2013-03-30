#!/bin/bash

export MATLAB_ROOT="/Applications/MATLAB_R2012b.app"
export MCR_ROOT="/Applications/MATLAB/MATLAB_Compiler_Runtime"

export DYLD_LIBRARY_PATH="/Applications/MATLAB/MATLAB_Compiler_Runtime/v80/runtime/maci64:/Applications/MATLAB/MATLAB_Compiler_Runtime/v80/sys/os/maci64:/Applications/MATLAB/MATLAB_Compiler_Runtime/v80/bin/maci64:/System/Library/Frameworks/JavaVM.framework/JavaVM:/System/Library/Frameworks/JavaVM.framework/Libraries"

export XAPPLRESDIR="$MCR_ROOT/v80/X11/app-defaults"

java -classpath build/install/vdpgm_matlab/lib/vdpgm_matlab-0.0.0-SNAPSHOT.jar:build/install/vdpgm_matlab/lib/vdpgm.jar:/Applications/MATLAB_R2012b.app/toolbox/javabuilder/jar/maci64/javabuilder.jar -Djava.rmi.server.codebase="file:///Applications/MATLAB_R2012b.app/toolbox/javabuilder/jar/javabuilder.jar file:////Users/luke/git/vdpgm_stack/vdpgm_matlab/build/install/vdpgm_matlab/lib/vdpgm.jar" vdpgm.client.TestClient
