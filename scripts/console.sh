#!/bin/sh

# Start math console server for imath cloud application
#
su $1 -c "(cd /iMathCloud/$1 && /usr/local/bin/ipython notebook --port=$2 --ip=* --pylab=inline --user=$1)"

