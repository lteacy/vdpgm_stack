vdpgm_stack
===========

Provides ROS Interface to Kenichi Kurihara's matlab code for Variational Dirichlet Process Gaussian Mixture Models

Installation Instructions
=========================
Install matlab revision R2012b or the equivalent MCR

Assuming you are using groovy on ubuntu, and have not yet installed rosjava, do the following:

    sudo apt-get install python-pip
    sudo pip install --upgrade rosinstall
    mkdir ~/my_workspace
    cd ~/my_workspace
    rosws init
    rosws merge /opt/ros/electric/.rosinstall
    rosws merge http://rosjava.googlecode.com/hg/.rosinstall
    rosws merge https://raw.github.com/lteacy/vdpgm_stack/master/rosinstall
    rosws update
    source setup.bash

Now build things in the following order

    rosmake vdpgm_msgs
    roscd rosjava_core
    ./gradlew install

To run tests on rosjava, do the following in the same directory

    ./gradlew test

Now build the vdpgm package:

    roscd vdpgm_srv
    make

Test installation
=================
To run and test the installation, do the following

    roscd vdpgm_srv
    roslaunch launch/imm_server.launch
    
In a separate terminal, do:

    rostopic pub /vdpgm/data vdpgm_msgs/DataStamped -f testData.yaml
    rosservice call /vdpgm/get_imm

You should see parameters for the fitted gaussian mixture printed out in yaml format.

Modifying MATLAB code
=====================
Note that the matlab code is prebuild using javabuilder for Matlab R2012b.
To use a different version of matlab, or change the behaviour of the matlab code, you must manually use matlab jbuilder
to replace the jar files in the jars directory.


