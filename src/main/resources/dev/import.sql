--
-- JBoss, Home of Professional Open Source
-- Copyright 2012, Red Hat, Inc., and individual contributors
-- by the @authors tag. See the copyright.txt in the distribution for a
-- full listing of individual contributors.
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
-- http://www.apache.org/licenses/LICENSE-2.0
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

-- You can use this file to load seed data into the database using SQL statements
-- insert into Member_html5mobi (id, name, email, phone_number) values (0, 'John Smith', 'john.smith@mailinator.com', '2125551212')
insert into Role (id, shortName, longName) values (-1,'admin', 'Administration User');
insert into Role (id, shortName, longName) values (-2,'guess', 'Guess User');

insert into MathLanguage (id, baseName, version, consoleCode) values (-1, 'Python', '2.7','code');
insert into MathLanguage (id, baseName, version, consoleCode) values (-2, 'R', '2.15.3','codeR');

--ammartinez
insert into imr_user (username, email, firstname, lastname, organization, phone1, idrole, idMathLanguage, storage, rootName) values ('ammartinez', 'ammartinez@ammartinez.com', 'Andrea', 'Martinez','iMath Research S.L.','610003964',-1,-1,10, 'ROOT');
insert into imr_user (username, email, firstname, lastname, organization, phone1, idrole, idMathLanguage, storage, rootName) values ('ddXYZamtTest', '', '', '','','',-1,-1,10, 'dd');
--ammartinez

--imath
insert into imr_user (username, email, firstname, lastname, organization, phone1, idrole, idMathLanguage, storage, rootName) values ('imath', 'ammartinez@imathresearch.com', 'Andrea', 'Martinez','iMath Research S.L.','610203964',-1,-1,10, 'ROOT');
insert into imr_user (username, email, firstname, lastname, organization, phone1, idrole, idMathLanguage, storage, rootName) values ('imathTest', 'andrea.mmt84@gmail.com', 'Andrea', 'Martinez','iMath Research S.L.','610244964',-1,-1,10, 'ROOT');


-- user52
insert into imr_user (username, email, firstname, lastname, organization, phone1, idrole, idMathLanguage, storage, rootName) values ('user52', 'user@user.com', 'User', 'User','S.L.','111111112',-1,-1,10, 'ROOT');
--insert into imr_user (username, email, firstname, lastname, organization, phone1, idrole, idMathLanguage) values ('conanc', 'conan@user.com', 'Conan', 'conan','S.L.','111111112',-1,-1);

insert into imr_user (username, email, firstname, lastname, organization, phone1, idrole, idMathLanguage, storage, rootName) values ('ipinyol', 'ipinyol@imathresearch.com', 'Isaac', 'Pinyol','iMath Research S.L.','636683729',-1,-2,10, 'ROOT');
insert into imr_user (username, email, firstname, lastname, organization, phone1, idrole, idMathLanguage, storage, rootName) values ('ipinyolTest', 'ipinyolTest@imathresearch.com', 'Isaac', 'Pinyol','iMath Research S.L.','555555667',-1,-2,10, 'ROOT');

--conanc
insert into imr_user (username, email, firstname, lastname, organization, phone1, idrole, idMathLanguage, storage, rootName) values ('conanc', 'conan@imathresearch.com', 'conan', 'conan','iMath Research S.L.','636683729',-1,-1,2, 'ROOT');


--inavarro
insert into imr_user (username, email, firstname, lastname, organization, phone1, idrole, idMathLanguage, storage, rootName) values ('inavarro', 'inavarro@imathresearch.com', 'Ivan', 'Navarro','iMath Research S.L.','555443322',-1,-1,10, 'ROOT');
insert into imr_user (username, email, firstname, lastname, organization, phone1, idrole, idMathLanguage, storage, rootName) values ('inavarroUser', 'inavarroUser@imathresearch.com', 'Ivan', 'Navarro','iMath Research S.L.','555443322',-1,-2,10, 'ROOT');
--/inavarro

insert into Host (id, active, alias, console, url) values (-1,true, 'localhost', true, '127.0.0.1');
--inavarro
insert into Host (id, active, alias, console, url) values (-2,true, 'localhost', true, '127.0.0.1');
--/inavarro
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-1,'dir','ROOT','file://localhost/iMathCloud/ipinyolTest',null,'ipinyolTest',0, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-2,'dir','src','file://localhost/iMathCloud/ipinyolTest/src',-1,'ipinyolTest',0, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-3,'dir','examples','file://localhost/iMathCloud/ipinyolTest/src/examples',-2,'ipinyolTest',0, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-4,'dir','data','file://localhost/iMathCloud/ipinyolTest/data',-1,'ipinyolTest',1, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-5,'py','example1.py','file://localhost/iMathCloud/ipinyolTest/src/examples/example1.py',-3,'ipinyolTest',0, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-6,'py','example2.py','file://localhost/iMathCloud/ipinyolTest/src/examples/example2.py',-3,'ipinyolTest',0, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-7,'py','example3.py','file://localhost/iMathCloud/ipinyolTest/src/examples/example3.py',-3,'ipinyolTest',0, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-8,'csv','test.csv','file://localhost/iMathCloud/ipinyolTest/data/test.csv',-4,'ipinyolTest',1, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-100,'r','exampleInR.r','file://localhost/iMathCloud/ipinyolTest/src/examples/exampleInR.r',-3,'ipinyolTest',0, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-101,'csv','test.csv','file://localhost/iMathCloud/ipinyolTest/test.csv',-1,'ipinyolTest',0, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-102,'csv','test_small.csv','file://localhost/iMathCloud/ipinyolTest/test_small.csv',-1,'ipinyolTest',0, null);

--user52 
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-300,'dir','ROOT','file://localhost/iMathCloud/user52',null,'user52',0, null);

-- conanc
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-400,'dir','ROOT','file://localhost/iMathCloud/conanc',null,'conanc',0, null);

-- imath 
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-500,'dir','ROOT','file://localhost/iMathCloud/imath',null,'imath',0, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-600,'dir','ROOT','file://localhost/iMathCloud/imathTest',null,'imathTest',0, null);

--ammartinez
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-1000,'dir','ROOT','file://127.0.0.1/iMathCloud/ddXYZamtTest',null,'ddXYZamtTest',0, null);

insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-200,'dir','ROOT','file://localhost/iMathCloud/ammartinez',null,'ammartinez',0, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-201,'dir','src','file://localhost/iMathCloud/ammartinez/src',-200,'ammartinez',0, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-202,'dir','examples','file://localhost/iMathCloud/ammartinez/src/examples',-201,'ammartinez',0, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-203,'py','example1.py','file://localhost/iMathCloud/ammartinez/src/examples/example1.py',-202,'ammartinez',0, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-204,'csv','data.csv','file://localhost/iMathCloud/ammartinez/data.csv',-200,'ammartinez',0, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-205,'dir','Colossus','file://localhost/iMathCloud/ammartinez/src/Colossus',-201,'ammartinez',0, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-206,'csv','data_colossus.csv','file://localhost/iMathCloud/ammartinez/src/Colossus/data_colossus.csv',-205,'ammartinez',0, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-207,'py','Col_example.py','file://localhost/iMathCloud/ammartinez/src/Colossus/Col_example.py',-205,'ammartinez',0, null);

insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-208,'dir','TEST','file://localhost/iMathCloud/ammartinez/TEST',-200,'ammartinez',0, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-209,'txt','file1.txt','file://localhost/iMathCloud/ammartinez/TEST/file1.txt',-208,'ammartinez',0, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-210,'txt','file2.txt','file://localhost/iMathCloud/ammartinez/TEST/file2.txt',-208,'ammartinez',0, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-211,'dir','dir','file://localhost/iMathCloud/ammartinez/TEST/dir',-208,'ammartinez',0, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-212,'txt','file1_dir.txt','file://localhost/iMathCloud/ammartinez/TEST/dir/file1_dir.txt',-211,'ammartinez',0, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-213,'txt','file2_dir.txt','file://localhost/iMathCloud/ammartinez/TEST/dir/file2_dir.txt',-211,'ammartinez',0, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-214,'dir','dir2','file://localhost/iMathCloud/ammartinez/TEST/dir/dir2',-211,'ammartinez',0, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-215,'txt','file1_dir2.txt','file://localhost/iMathCloud/ammartinez/TEST/dir/dir2/file1_dir2.txt',-214,'ammartinez',0, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-216,'txt','file2_dir2.txt','file://localhost/iMathCloud/ammartinez/TEST/dir/dir2/file2_dir2.txt',-214,'ammartinez',0, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-217,'dir','dir3','file://localhost/iMathCloud/ammartinez/TEST/dir/dir2/dir3',-214,'ammartinez',0, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-218,'txt','file1_dir3.txt','file://localhost/iMathCloud/ammartinez/TEST/dir/dir2/dir3/file1_dir3.txt',-217,'ammartinez',0, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-219,'txt','file2_dir3.txt','file://localhost/iMathCloud/ammartinez/TEST/dir/dir2/dir3/file2_dir3.txt',-217,'ammartinez',0, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-220,'txt','file2.txt','file://localhost/iMathCloud/ammartinez/TEST/dir/dir2/dir3/file2.txt',-217,'ammartinez',0, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-221,'dir','dir3','file://localhost/iMathCloud/ammartinez/TEST/dir3',-208,'ammartinez',0, null);
--ammartinez

--inavarro
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-9,'dir','ROOT','file://localhost/iMathCloud/inavarro',null,'inavarro',0, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-10,'dir','EXAMPLES','file://localhost/iMathCloud/inavarro/EXAMPLES',-9,'inavarro',0, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-11,'dir','basic','file://localhost/iMathCloud/inavarro/EXAMPLES/basic',-10,'inavarro',0, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-12,'dir','fileAccess','file://localhost/iMathCloud/inavarro/EXAMPLES/fileAccess',-10,'inavarro',0, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-13,'py','colossusBasic.py','file://localhost/iMathCloud/inavarro/EXAMPLES/basic/colossusBasic.py',-11,'inavarro',0, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-14,'py','multiPlot.py','file://localhost/iMathCloud/inavarro/EXAMPLES/basic/multiPlot.py',-11,'inavarro',0, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-15,'py','pythonBars.py','file://localhost/iMathCloud/inavarro/EXAMPLES/basic/pythonBars.py',-11,'inavarro',0, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-16,'txt','readme.txt','file://localhost/iMathCloud/inavarro/EXAMPLES/basic/readme.txt',-11,'inavarro',0, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-17,'csv','iris_data.csv','file://localhost/iMathCloud/inavarro/EXAMPLES/fileAccess/iris_data.csv',-12,'inavarro',0, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-18,'py','irisPlot.py','file://localhost/iMathCloud/inavarro/EXAMPLES/fileAccess/irisPlot.py',-12,'inavarro',0, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-19,'csv','iris_target.csv','file://localhost/iMathCloud/inavarro/EXAMPLES/fileAccess/iris_target.csv',-12,'inavarro',0, null);
insert into File (id, imr_type, name, url, id_dir, iduserowner, sharingstate, openbyuser) values (-20,'txt','readme.txt','file://localhost/iMathCloud/inavarro/EXAMPLES/fileAccess/readme.txt',-12,'inavarro',0, null);
--/inavarro

insert into FileShared(id, iduser, idfile, permission) values(-1, 'ipinyolTest', -10, 1);
insert into FileShared(id, iduser, idfile, permission) values(-2, 'ipinyolTest', -12, 1);
insert into FileShared(id, iduser, idfile, permission) values(-3, 'inavarro', -4, 1);


insert into Session (id, enddate, startdate, idhostconsole, iduser, portConsole) values (-1,'2013-01-09 22:30', '2013-01-09 19:12', -1, 'ipinyolTest', 8888);
--inavarro
insert into Session (id, enddate, startdate, idhostconsole, iduser, portConsole) values (-2,'2013-01-09 22:30', '2013-01-09 19:12', -2, 'inavarro', 8888);
--/inavarro

insert into Job (id, description, startDate, state, idhost, iduserowner, idsession) values (-1, 'Recalculating PCA','2013-01-09 19:30',1,-1,'ipinyolTest',-1);
insert into Job (id, description, startDate, state, idhost, iduserowner, idsession,endDate) values (-2, 'Simulating the game of life','2013-01-09 20:35',4,-1,'ipinyolTest',-1, '2013-01-15 05:31');
insert into Job (id, description, startDate, state, idhost, iduserowner, idsession) values (-3, 'Statistics','2013-01-09 20:43',1,-1,'ipinyolTest',-1);
insert into Job (id, description, startDate, state, idhost, iduserowner, idsession, endDate) values (-4, 'Computing Neural Network ','2013-01-09 20:56',4,-1,'ipinyolTest',-1, '2013-01-09 20:59');
--inavarro
insert into Job (id, description, startDate, state, idhost, iduserowner, idsession) values (-5, 'Recalculating PCA','2013-01-09 19:30',1,-2,'inavarro',-2);
insert into Job (id, description, startDate, state, idhost, iduserowner, idsession,endDate) values (-6, 'Simulating the game of life','2013-01-09 20:35',4,-2,'inavarro',-2, '2013-01-15 05:31');
insert into Job (id, description, startDate, state, idhost, iduserowner, idsession) values (-7, 'Statistics','2013-01-09 20:43',1,-2,'inavarro',-2);
insert into Job (id, description, startDate, state, idhost, iduserowner, idsession, endDate) values (-8, 'Computing Neural Network ','2013-01-09 20:56',4,-2,'inavarro',-2, '2013-01-09 20:59');
--/inavarro

insert into MathGroup (id, description, plugin) values (-1, 'Basic Statistics', 'stat');

insert into role_mathgroups (mathGroups_id, roles_id) values (-1,-1);
insert into role_mathgroups (mathGroups_id, roles_id) values (-1,-2);

insert into MathFunction (id, description, shortName, serviceName, params, idMathGroup) values (-1,'The arithmetic mean','Mean','Mean','fileName=#0#&directory=#1#',-1);
insert into MathFunction (id, description, shortName, serviceName, params, idMathGroup) values (-2,'Frecuencies','Frecuencies','Frecuencies','fileName=#0#&directory=#1#',-1);
insert into MathFunction (id, description, shortName, serviceName, params, idMathGroup) values (-3,'Standar desviation','Std Dev','StandardDeviation','fileName=#0#&directory=#1#',-1);
insert into MathFunction (id, description, shortName, serviceName, params, idMathGroup) values (-4,'Variance','Variance','Variance','fileName=#0#&directory=#1#',-1);
insert into MathFunction (id, description, shortName, serviceName, params, idMathGroup) values (-5,'Median','Median','Median','fileName=#0#&directory=#1#',-1);
insert into MathFunction (id, description, shortName, serviceName, params, idMathGroup) values (-6,'Maximums','Maximum','Maximum','fileName=#0#&directory=#1#',-1);
insert into MathFunction (id, description, shortName, serviceName, params, idMathGroup) values (-7,'Minimums','Minimum','Minimum','fileName=#0#&directory=#1#',-1);
insert into MathFunction (id, description, shortName, serviceName, params, idMathGroup) values (-8,'Linear Regression','LRegression','LinearRegression','fileName=#0#&directory=#1#',-1);
insert into MathFunction (id, description, shortName, serviceName, params, idMathGroup) values (-9,'Descriptive statistics','All Statistics','DescriptiveStatistics','fileName=#0#&directory=#1#&parameter=#2#',-1);
insert into MathFunction (id, description, shortName, serviceName, params, idMathGroup) values (-10,'Plot Descriptive Statistics','Plot Statistics','PlotDescStat','fileName=#0#&directory=#1#',-1);
insert into MathFunction (id, description, shortName, serviceName, params, idMathGroup) values (-11,'Plot Linear Regression','Plot Lin. Regression','PlotLinReg','fileName=#0#&directory=#1#&parameter=#2#',-1);


-- Default user for authentication

insert into userjboss (username, password) values ('WSUser', MD5('943793072'));
insert into userjbossroles (username, role) values ('WSUser', 'WebAppUser');
