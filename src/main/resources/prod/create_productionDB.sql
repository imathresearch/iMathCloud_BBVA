--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = public, pg_catalog;

ALTER TABLE ONLY public.fileshared DROP CONSTRAINT fk_tr8h1rhpb5uij5qywo163juqd;
ALTER TABLE ONLY public.source_file_jobs DROP CONSTRAINT fk_t9i3hu8nnb3yiochs7xuoebk2;
ALTER TABLE ONLY public.file_jobs DROP CONSTRAINT fk_sgivmhugyby28bjd12br7wtuk;
ALTER TABLE ONLY public.job DROP CONSTRAINT fk_r2a345blc9ooyhmaydxv0ky3d;
ALTER TABLE ONLY public.role_mathgroups DROP CONSTRAINT fk_qob1nne8vse353d6g9kw63ouu;
ALTER TABLE ONLY public.imr_user DROP CONSTRAINT fk_qngxxj3g8o953h6bgmd37fiqc;
ALTER TABLE ONLY public.file DROP CONSTRAINT fk_o8k3kd9w5k33xpnbntkxnxhwt;
ALTER TABLE ONLY public.output_file_jobs DROP CONSTRAINT fk_n4sal1ks0n0vqy3t1shd7cjn0;
ALTER TABLE ONLY public.file_jobs DROP CONSTRAINT fk_klm31wh54eglsnjcp1e84lux4;
ALTER TABLE ONLY public.role_mathgroups DROP CONSTRAINT fk_kh61lq74pdv5w9m44hfnyk680;
ALTER TABLE ONLY public.mathfunction DROP CONSTRAINT fk_i8elo2p2akvc84tp9rjf6rdij;
ALTER TABLE ONLY public.source_file_jobs DROP CONSTRAINT fk_exopixjvd7q5m11eockm35yp9;
ALTER TABLE ONLY public.imr_user DROP CONSTRAINT fk_94mkq6xj2bxr1swqsmubl8osc;
ALTER TABLE ONLY public.file DROP CONSTRAINT fk_8ois5o51cfb11xhafuy0nvo5q;
ALTER TABLE ONLY public.job DROP CONSTRAINT fk_84omu22c4a058uhso8nl0gl0d;
ALTER TABLE ONLY public.job DROP CONSTRAINT fk_7a5fbl1js1r9th7vugh8hvdax;
ALTER TABLE ONLY public.output_file_jobs DROP CONSTRAINT fk_4o101d55ppsn4u4gnbq0cv3nv;
ALTER TABLE ONLY public.job DROP CONSTRAINT fk_3te4l4veewimedpdptkpyf1ye;
ALTER TABLE ONLY public.session DROP CONSTRAINT fk_3l55aqxwypki7d5s71v3u7ptf;
ALTER TABLE ONLY public.session DROP CONSTRAINT fk_33cn6wrtbfxt7r8hi4xtt3bit;
ALTER TABLE ONLY public.fileshared DROP CONSTRAINT fk_2duvgy9rbt9b8pqyyrj7eka1b;
ALTER TABLE ONLY public.userjbossroles DROP CONSTRAINT userjbossroles_pkey;
ALTER TABLE ONLY public.userjboss DROP CONSTRAINT userjboss_pkey;
ALTER TABLE ONLY public.file DROP CONSTRAINT uk_elmyvflql0ku4koc7s6ricypw;
ALTER TABLE ONLY public.source_file_jobs DROP CONSTRAINT source_file_jobs_pkey;
ALTER TABLE ONLY public.session DROP CONSTRAINT session_pkey;
ALTER TABLE ONLY public.role DROP CONSTRAINT role_pkey;
ALTER TABLE ONLY public.role_mathgroups DROP CONSTRAINT role_mathgroups_pkey;
ALTER TABLE ONLY public.output_file_jobs DROP CONSTRAINT output_file_jobs_pkey;
ALTER TABLE ONLY public.mathlanguage DROP CONSTRAINT mathlanguage_pkey;
ALTER TABLE ONLY public.mathgroup DROP CONSTRAINT mathgroup_pkey;
ALTER TABLE ONLY public.mathfunction DROP CONSTRAINT mathfunction_pkey;
ALTER TABLE ONLY public.jobresult DROP CONSTRAINT jobresult_pkey;
ALTER TABLE ONLY public.job DROP CONSTRAINT job_pkey;
ALTER TABLE ONLY public.imr_user DROP CONSTRAINT imr_user_pkey;
ALTER TABLE ONLY public.host DROP CONSTRAINT host_pkey;
ALTER TABLE ONLY public.fileshared DROP CONSTRAINT fileshared_pkey;
ALTER TABLE ONLY public.file DROP CONSTRAINT file_pkey;
ALTER TABLE ONLY public.file_jobs DROP CONSTRAINT file_jobs_pkey;
DROP TABLE public.userjbossroles;
DROP TABLE public.userjboss;
DROP TABLE public.source_file_jobs;
DROP TABLE public.session;
DROP TABLE public.role_mathgroups;
DROP TABLE public.role;
DROP TABLE public.output_file_jobs;
DROP TABLE public.mathlanguage;
DROP TABLE public.mathgroup;
DROP TABLE public.mathfunction;
DROP TABLE public.jobresult;
DROP TABLE public.job;
DROP TABLE public.imr_user;
DROP TABLE public.host;
DROP SEQUENCE public.hibernate_sequence;
DROP TABLE public.fileshared;
DROP TABLE public.file_jobs;
DROP TABLE public.file;
DROP EXTENSION plpgsql;
DROP SCHEMA public;
--
-- Name: public; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA public;


ALTER SCHEMA public OWNER TO postgres;

--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS 'standard public schema';


--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: file; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE file (
    id bigint NOT NULL,
    imr_type character varying(255),
    name character varying(255),
    openbyuser character varying(255),
    sharingstate integer,
    url character varying(255),
    id_dir bigint,
    iduserowner character varying(100) NOT NULL
);


ALTER TABLE public.file OWNER TO postgres;

--
-- Name: file_jobs; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE file_jobs (
    jobs_id bigint NOT NULL,
    files_id bigint NOT NULL
);


ALTER TABLE public.file_jobs OWNER TO postgres;

--
-- Name: fileshared; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE fileshared (
    id bigint NOT NULL,
    permission integer,
    idfile bigint NOT NULL,
    iduser character varying(100) NOT NULL
);


ALTER TABLE public.fileshared OWNER TO postgres;

--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.hibernate_sequence OWNER TO postgres;

--
-- Name: host; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE host (
    id bigint NOT NULL,
    active boolean,
    alias character varying(255),
    console boolean,
    url character varying(255)
);


ALTER TABLE public.host OWNER TO postgres;

--
-- Name: imr_user; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE imr_user (
    username character varying(100) NOT NULL,
    email character varying(255) NOT NULL,
    firstname character varying(255),
    lastname character varying(255),
    organization character varying(255),
    phone1 character varying(15),
    phone2 character varying(15),
    rootname character varying(255) NOT NULL,
    storage bigint NOT NULL,
    idmathlanguage bigint NOT NULL,
    idrole bigint NOT NULL
);


ALTER TABLE public.imr_user OWNER TO postgres;

--
-- Name: job; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE job (
    id bigint NOT NULL,
    description character varying(255),
    enddate timestamp without time zone,
    startdate timestamp without time zone,
    state integer NOT NULL,
    idhost bigint,
    jobresult_id bigint,
    iduserowner character varying(100) NOT NULL,
    idsession bigint
);


ALTER TABLE public.job OWNER TO postgres;

--
-- Name: jobresult; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE jobresult (
    id bigint NOT NULL,
    json character varying(1024) NOT NULL
);


ALTER TABLE public.jobresult OWNER TO postgres;

--
-- Name: mathfunction; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE mathfunction (
    id bigint NOT NULL,
    description character varying(255),
    params character varying(255),
    servicename character varying(255),
    shortname character varying(255),
    idmathgroup bigint NOT NULL
);


ALTER TABLE public.mathfunction OWNER TO postgres;

--
-- Name: mathgroup; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE mathgroup (
    id bigint NOT NULL,
    description character varying(255) NOT NULL,
    plugin character varying(255) NOT NULL
);


ALTER TABLE public.mathgroup OWNER TO postgres;

--
-- Name: mathlanguage; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE mathlanguage (
    id bigint NOT NULL,
    basename character varying(255),
    consolecode character varying(255),
    version character varying(255)
);


ALTER TABLE public.mathlanguage OWNER TO postgres;

--
-- Name: output_file_jobs; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE output_file_jobs (
    outputjobs_id bigint NOT NULL,
    outputfiles_id bigint NOT NULL
);


ALTER TABLE public.output_file_jobs OWNER TO postgres;

--
-- Name: role; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE role (
    id bigint NOT NULL,
    longname character varying(255),
    shortname character varying(25) NOT NULL
);


ALTER TABLE public.role OWNER TO postgres;

--
-- Name: role_mathgroups; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE role_mathgroups (
    roles_id bigint NOT NULL,
    mathgroups_id bigint NOT NULL
);


ALTER TABLE public.role_mathgroups OWNER TO postgres;

--
-- Name: session; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE session (
    id bigint NOT NULL,
    enddate timestamp without time zone,
    portconsole integer,
    startdate timestamp without time zone NOT NULL,
    idhostconsole bigint NOT NULL,
    iduser character varying(100) NOT NULL
);


ALTER TABLE public.session OWNER TO postgres;

--
-- Name: source_file_jobs; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE source_file_jobs (
    sourcejobs_id bigint NOT NULL,
    sourcefiles_id bigint NOT NULL
);


ALTER TABLE public.source_file_jobs OWNER TO postgres;

--
-- Name: userjboss; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE userjboss (
    username character varying(25) NOT NULL,
    password character varying(200)
);


ALTER TABLE public.userjboss OWNER TO postgres;

--
-- Name: userjbossroles; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE userjbossroles (
    username character varying(25) NOT NULL,
    role character varying(255),
    rolegroup character varying(255)
);


ALTER TABLE public.userjbossroles OWNER TO postgres;

--
-- Data for Name: file; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY file (id, imr_type, name, openbyuser, sharingstate, url, id_dir, iduserowner) FROM stdin;
\.


--
-- Data for Name: file_jobs; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY file_jobs (jobs_id, files_id) FROM stdin;
\.


--
-- Data for Name: fileshared; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY fileshared (id, permission, idfile, iduser) FROM stdin;
\.


--
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('hibernate_sequence', 15, true);


--
-- Data for Name: host; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY host (id, active, alias, console, url) FROM stdin;
-1	t	localhost	t	127.0.0.1
\.


--
-- Data for Name: imr_user; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY imr_user (username, email, firstname, lastname, organization, phone1, phone2, rootname, storage, idmathlanguage, idrole) FROM stdin;
\.


--
-- Data for Name: job; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY job (id, description, enddate, startdate, state, idhost, jobresult_id, iduserowner, idsession) FROM stdin;
\.


--
-- Data for Name: jobresult; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY jobresult (id, json) FROM stdin;
\.


--
-- Data for Name: mathfunction; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY mathfunction (id, description, params, servicename, shortname, idmathgroup) FROM stdin;
-1	The arithmetic mean	fileName=#0#&directory=#1#	Mean	Mean	-1
-2	Frecuencies	fileName=#0#&directory=#1#	Frecuencies	Frecuencies	-1
-3	Standar desviation	fileName=#0#&directory=#1#	StandardDeviation	Std Dev	-1
-4	Variance	fileName=#0#&directory=#1#	Variance	Variance	-1
-5	Median	fileName=#0#&directory=#1#	Median	Median	-1
-6	Maximums	fileName=#0#&directory=#1#	Maximum	Maximum	-1
-7	Minimums	fileName=#0#&directory=#1#	Minimum	Minimum	-1
-8	Linear Regression	fileName=#0#&directory=#1#	LinearRegression	LRegression	-1
-9	Descriptive statistics	fileName=#0#&directory=#1#&parameter=#2#	DescriptiveStatistics	All Statistics	-1
-10	Plot Descriptive Statistics	fileName=#0#&directory=#1#	PlotDescStat	Plot Statistics	-1
-11	Plot Linear Regression	fileName=#0#&directory=#1#&parameter=#2#	PlotLinReg	Plot Lin. Regression	-1
\.


--
-- Data for Name: mathgroup; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY mathgroup (id, description, plugin) FROM stdin;
-1	Basic Statistics	stat
\.


--
-- Data for Name: mathlanguage; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY mathlanguage (id, basename, consolecode, version) FROM stdin;
-1	Python	code	2.7
-2	R	codeR	2.15.3
\.


--
-- Data for Name: output_file_jobs; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY output_file_jobs (outputjobs_id, outputfiles_id) FROM stdin;
\.


--
-- Data for Name: role; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY role (id, longname, shortname) FROM stdin;
-1	Administration User	admin
-2	Guess User	guess
\.


--
-- Data for Name: role_mathgroups; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY role_mathgroups (roles_id, mathgroups_id) FROM stdin;
-1	-1
-2	-1
\.


--
-- Data for Name: session; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY session (id, enddate, portconsole, startdate, idhostconsole, iduser) FROM stdin;
\.


--
-- Data for Name: source_file_jobs; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY source_file_jobs (sourcejobs_id, sourcefiles_id) FROM stdin;
\.


--
-- Data for Name: userjboss; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY userjboss (username, password) FROM stdin;
WSUser	3ea89391cabe5787f66b228b3464e4d3
\.


--
-- Data for Name: userjbossroles; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY userjbossroles (username, role, rolegroup) FROM stdin;
WSUser	WebAppUser	\N
\.


--
-- Name: file_jobs_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY file_jobs
    ADD CONSTRAINT file_jobs_pkey PRIMARY KEY (jobs_id, files_id);


--
-- Name: file_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY file
    ADD CONSTRAINT file_pkey PRIMARY KEY (id);


--
-- Name: fileshared_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY fileshared
    ADD CONSTRAINT fileshared_pkey PRIMARY KEY (id);


--
-- Name: host_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY host
    ADD CONSTRAINT host_pkey PRIMARY KEY (id);


--
-- Name: imr_user_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY imr_user
    ADD CONSTRAINT imr_user_pkey PRIMARY KEY (username);


--
-- Name: job_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY job
    ADD CONSTRAINT job_pkey PRIMARY KEY (id);


--
-- Name: jobresult_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY jobresult
    ADD CONSTRAINT jobresult_pkey PRIMARY KEY (id);


--
-- Name: mathfunction_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY mathfunction
    ADD CONSTRAINT mathfunction_pkey PRIMARY KEY (id);


--
-- Name: mathgroup_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY mathgroup
    ADD CONSTRAINT mathgroup_pkey PRIMARY KEY (id);


--
-- Name: mathlanguage_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY mathlanguage
    ADD CONSTRAINT mathlanguage_pkey PRIMARY KEY (id);


--
-- Name: output_file_jobs_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY output_file_jobs
    ADD CONSTRAINT output_file_jobs_pkey PRIMARY KEY (outputjobs_id, outputfiles_id);


--
-- Name: role_mathgroups_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY role_mathgroups
    ADD CONSTRAINT role_mathgroups_pkey PRIMARY KEY (roles_id, mathgroups_id);


--
-- Name: role_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY role
    ADD CONSTRAINT role_pkey PRIMARY KEY (id);


--
-- Name: session_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY session
    ADD CONSTRAINT session_pkey PRIMARY KEY (id);


--
-- Name: source_file_jobs_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY source_file_jobs
    ADD CONSTRAINT source_file_jobs_pkey PRIMARY KEY (sourcejobs_id, sourcefiles_id);


--
-- Name: uk_elmyvflql0ku4koc7s6ricypw; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY file
    ADD CONSTRAINT uk_elmyvflql0ku4koc7s6ricypw UNIQUE (url);


--
-- Name: userjboss_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY userjboss
    ADD CONSTRAINT userjboss_pkey PRIMARY KEY (username);


--
-- Name: userjbossroles_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY userjbossroles
    ADD CONSTRAINT userjbossroles_pkey PRIMARY KEY (username);


--
-- Name: fk_2duvgy9rbt9b8pqyyrj7eka1b; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY fileshared
    ADD CONSTRAINT fk_2duvgy9rbt9b8pqyyrj7eka1b FOREIGN KEY (iduser) REFERENCES imr_user(username);


--
-- Name: fk_33cn6wrtbfxt7r8hi4xtt3bit; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY session
    ADD CONSTRAINT fk_33cn6wrtbfxt7r8hi4xtt3bit FOREIGN KEY (iduser) REFERENCES imr_user(username);


--
-- Name: fk_3l55aqxwypki7d5s71v3u7ptf; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY session
    ADD CONSTRAINT fk_3l55aqxwypki7d5s71v3u7ptf FOREIGN KEY (idhostconsole) REFERENCES host(id);


--
-- Name: fk_3te4l4veewimedpdptkpyf1ye; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY job
    ADD CONSTRAINT fk_3te4l4veewimedpdptkpyf1ye FOREIGN KEY (idsession) REFERENCES session(id);


--
-- Name: fk_4o101d55ppsn4u4gnbq0cv3nv; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY output_file_jobs
    ADD CONSTRAINT fk_4o101d55ppsn4u4gnbq0cv3nv FOREIGN KEY (outputjobs_id) REFERENCES job(id);


--
-- Name: fk_7a5fbl1js1r9th7vugh8hvdax; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY job
    ADD CONSTRAINT fk_7a5fbl1js1r9th7vugh8hvdax FOREIGN KEY (jobresult_id) REFERENCES jobresult(id);


--
-- Name: fk_84omu22c4a058uhso8nl0gl0d; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY job
    ADD CONSTRAINT fk_84omu22c4a058uhso8nl0gl0d FOREIGN KEY (iduserowner) REFERENCES imr_user(username);


--
-- Name: fk_8ois5o51cfb11xhafuy0nvo5q; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY file
    ADD CONSTRAINT fk_8ois5o51cfb11xhafuy0nvo5q FOREIGN KEY (iduserowner) REFERENCES imr_user(username);


--
-- Name: fk_94mkq6xj2bxr1swqsmubl8osc; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY imr_user
    ADD CONSTRAINT fk_94mkq6xj2bxr1swqsmubl8osc FOREIGN KEY (idmathlanguage) REFERENCES mathlanguage(id);


--
-- Name: fk_exopixjvd7q5m11eockm35yp9; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY source_file_jobs
    ADD CONSTRAINT fk_exopixjvd7q5m11eockm35yp9 FOREIGN KEY (sourcejobs_id) REFERENCES job(id);


--
-- Name: fk_i8elo2p2akvc84tp9rjf6rdij; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY mathfunction
    ADD CONSTRAINT fk_i8elo2p2akvc84tp9rjf6rdij FOREIGN KEY (idmathgroup) REFERENCES mathgroup(id);


--
-- Name: fk_kh61lq74pdv5w9m44hfnyk680; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY role_mathgroups
    ADD CONSTRAINT fk_kh61lq74pdv5w9m44hfnyk680 FOREIGN KEY (mathgroups_id) REFERENCES mathgroup(id);


--
-- Name: fk_klm31wh54eglsnjcp1e84lux4; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY file_jobs
    ADD CONSTRAINT fk_klm31wh54eglsnjcp1e84lux4 FOREIGN KEY (jobs_id) REFERENCES job(id);


--
-- Name: fk_n4sal1ks0n0vqy3t1shd7cjn0; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY output_file_jobs
    ADD CONSTRAINT fk_n4sal1ks0n0vqy3t1shd7cjn0 FOREIGN KEY (outputfiles_id) REFERENCES file(id);


--
-- Name: fk_o8k3kd9w5k33xpnbntkxnxhwt; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY file
    ADD CONSTRAINT fk_o8k3kd9w5k33xpnbntkxnxhwt FOREIGN KEY (id_dir) REFERENCES file(id);


--
-- Name: fk_qngxxj3g8o953h6bgmd37fiqc; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY imr_user
    ADD CONSTRAINT fk_qngxxj3g8o953h6bgmd37fiqc FOREIGN KEY (idrole) REFERENCES role(id);


--
-- Name: fk_qob1nne8vse353d6g9kw63ouu; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY role_mathgroups
    ADD CONSTRAINT fk_qob1nne8vse353d6g9kw63ouu FOREIGN KEY (roles_id) REFERENCES role(id);


--
-- Name: fk_r2a345blc9ooyhmaydxv0ky3d; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY job
    ADD CONSTRAINT fk_r2a345blc9ooyhmaydxv0ky3d FOREIGN KEY (idhost) REFERENCES host(id);


--
-- Name: fk_sgivmhugyby28bjd12br7wtuk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY file_jobs
    ADD CONSTRAINT fk_sgivmhugyby28bjd12br7wtuk FOREIGN KEY (files_id) REFERENCES file(id);


--
-- Name: fk_t9i3hu8nnb3yiochs7xuoebk2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY source_file_jobs
    ADD CONSTRAINT fk_t9i3hu8nnb3yiochs7xuoebk2 FOREIGN KEY (sourcefiles_id) REFERENCES file(id);


--
-- Name: fk_tr8h1rhpb5uij5qywo163juqd; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY fileshared
    ADD CONSTRAINT fk_tr8h1rhpb5uij5qywo163juqd FOREIGN KEY (idfile) REFERENCES file(id);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

