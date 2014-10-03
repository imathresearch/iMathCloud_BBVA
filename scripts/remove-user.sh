#!/bin/sh

grep -v $1= ../standalone/configuration/application-roles.properties > ../standalone/configuration/application-roles.properties-aux; mv ../standalone/configuration/application-roles.properties-aux ../standalone/configuration/application-roles.properties

grep -v $1= ../domain/configuration/application-roles.properties > ../domain/configuration/application-roles.properties-aux; mv ../domain/configuration/application-roles.properties-aux ../domain/configuration/application-roles.properties

grep -v $1= ../standalone/configuration/application-users.properties > ../standalone/configuration/application-users.properties-aux; mv ../standalone/configuration/application-users.properties-aux ../standalone/configuration/application-users.properties

grep -v $1= ../domain/configuration/application-users.properties > ../domain/configuration/application-users.properties-aux; mv ../domain/configuration/application-users.properties-aux ../domain/configuration/application-users.properties

userdel $1

rm -R /iMathCloud/$1

rm -R /iMathCloud/exec_dir/$1

rm -R /home/$1