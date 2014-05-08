
    create table File (
        id int8 not null ,
        imr_type varchar(255),
        name varchar(255),
        url varchar(255),
        id_dir int8,
        idUserOwner varchar(255) not null,
        primary key (id)
    );

    create table Host (
        id int8 not null,
        active boolean,
        alias varchar(255),
        console boolean,
        url varchar(255),
        primary key (id)
    );

    create table IMR_User (
        userName varchar(255) not null,
        eMail varchar(255),
        firstName varchar(255),
        lastName varchar(255),
        organization varchar(255),
        phone1 varchar(255),
        phone2 varchar(255),
        idRole int8 not null,
        primary key (userName),
        unique (eMail)
    );

    create table Job (
        id int8 not null,
        description varchar(255),
        endDate timestamp,
        startDate timestamp,
        state int4 not null,
        idHost int8 not null,
        idJobResult int8,
        idUserOwner varchar(255) not null,
        idSession int8 not null,
        primary key (id)
    );

    create table JobResult (
        id int8 not null,
        json varchar(255),
        primary key (id)
    );

    create table MathFunction (
        id int8 not null,
        description varchar(255),
        params varchar(255),
        serviceName varchar(255),
        shortName varchar(255),
        idMathGroup int8 not null,
        primary key (id)
    );

    create table MathGroup (
        id int8 not null,
        description varchar(255),
        primary key (id)
    );

    create table Role (
        id int8 not null,
        longName varchar(255),
        shortName varchar(255),
        primary key (id)
    );

    create table Session (
        id int8 not null,
        endDate timestamp,
        startDate timestamp not null,
        idHostConsole int8 not null,
        idUser varchar(255) not null,
        primary key (id)
    );

    create table file_jobs (
        jobs_id int8 not null,
        files_id int8 not null,
        primary key (jobs_id, files_id)
    );

    create table output_file_jobs (
        outputJobs_id int8 not null,
        outputFiles_id int8 not null,
        primary key (outputJobs_id, outputFiles_id)
    );

    create table role_mathgroups (
        roles_id int8 not null,
        mathGroups_id int8 not null,
        primary key (roles_id, mathGroups_id)
    );

    create table source_file_jobs (
        sourceJobs_id int8 not null,
        sourceFiles_id int8 not null,
        primary key (sourceJobs_id, sourceFiles_id)
    );

    alter table File 
        add constraint FK21699CFE7A0393 
        foreign key (idUserOwner) 
        references IMR_User;

    alter table File 
        add constraint FK21699C211EC4AF 
        foreign key (id_dir) 
        references File;

    alter table IMR_User 
        add constraint FK19C619DC212D1C51 
        foreign key (idRole) 
        references Role;

    alter table Job 
        add constraint FK1239DFE7A0393 
        foreign key (idUserOwner) 
        references IMR_User;

    alter table Job 
        add constraint FK1239D7FE6EC0F 
        foreign key (idJobResult) 
        references JobResult;

    alter table Job 
        add constraint FK1239D212406B5 
        foreign key (idHost) 
        references Host;

    alter table Job 
        add constraint FK1239D912F3A7 
        foreign key (idSession) 
        references Session;

    alter table MathFunction 
        add constraint FKFE54AB20202F7489 
        foreign key (idMathGroup) 
        references MathGroup;

    alter table Session 
        add constraint FKD9891A768BA51026 
        foreign key (idHostConsole) 
        references Host;

    alter table Session 
        add constraint FKD9891A7623C1C7EC 
        foreign key (idUser) 
        references IMR_User;

    alter table file_jobs 
        add constraint FKB186AD393C7A9929 
        foreign key (files_id) 
        references File;

    alter table file_jobs 
        add constraint FKB186AD3994F01457 
        foreign key (jobs_id) 
        references Job;

    alter table output_file_jobs 
        add constraint FKE5F2337BA2A7212A 
        foreign key (outputFiles_id) 
        references File;

    alter table output_file_jobs 
        add constraint FKE5F2337BB1020836 
        foreign key (outputJobs_id) 
        references Job;

    alter table role_mathgroups 
        add constraint FK531C8A4549CA982B 
        foreign key (mathGroups_id) 
        references MathGroup;

    alter table role_mathgroups 
        add constraint FK531C8A455873A21D 
        foreign key (roles_id) 
        references Role;

    alter table source_file_jobs 
        add constraint FKCC14EA15375A67DC 
        foreign key (sourceJobs_id) 
        references Job;

    alter table source_file_jobs 
        add constraint FKCC14EA15E75AB644 
        foreign key (sourceFiles_id) 
        references File;

    create sequence hibernate_sequence;
