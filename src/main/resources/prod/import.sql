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

insert into Host (id, active, alias, console, url) values (-1,true, 'localhost', true, '127.0.0.1');

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
