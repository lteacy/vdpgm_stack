#!/bin/bash

export MATLAB_ROOT="/usr/local/MATLAB/R2012b"
export LD_LIBRARY_PATH="$LD_LIBRARY_PATH:$MATLAB_ROOT/runtime/glnxa64"

./build/install/vdpgm_srv/bin/vdpgm_srv org.soton.vdpgm_srv.IMMServer
