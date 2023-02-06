CREATE TABLE IF NOT EXISTS T_LINEAGE
(
    LINEAGE_ID   INTEGER      not null,
    LINEAGE_NAME VARCHAR(256) not null,
    NOTE         VARCHAR(256),
    USER_ID      INTEGER      not null,
    constraint PK_LINEAGE PRIMARY KEY (LINEAGE_ID),
    constraint UC_LINEAGE UNIQUE (LINEAGE_NAME, USER_ID)
);
create sequence SEQ_LINEAGE start with 1;


CREATE TABLE IF NOT EXISTS T_PERSON
(
    PERSON_ID        INTEGER      not null,
    PLACE            VARCHAR(256),
    OCCUPATION       VARCHAR(256),
    NOTE             VARCHAR(256),
    GENDER           CHAR(1)      not null,
    FIRST_NAME       VARCHAR(256) not null,
    MIDDLE_NAME      VARCHAR(256),
    LAST_NAME        VARCHAR(256),
    MAIDEN_NAME      VARCHAR(256),
    PHOTO_URL        VARCHAR(1024),
    PHOTO            BYTEA,
    PARENT_FAMILY_ID INTEGER,
    FAMILY_ID        INTEGER,
    LINEAGE_ID       INTEGER,
    USER_ID          INTEGER      not null,
    constraint PK_PERSON PRIMARY KEY (PERSON_ID)
);
create sequence SEQ_PERSON start with 1;


CREATE TABLE IF NOT EXISTS T_FAMILY
(
    FAMILY_ID  INTEGER not null,
    HUSBAND_ID INTEGER,
    WIFE_ID    INTEGER,
    NOTE       VARCHAR(256),
    LINEAGE_ID INTEGER,
    USER_ID    INTEGER not null,
    constraint PK_FAMILY PRIMARY KEY (FAMILY_ID)
);
create sequence SEQ_FAMILY start with 1;


CREATE TABLE IF NOT EXISTS T_CHILDREN
(
    FAMILY_ID INTEGER not null,
    PERSON_ID INTEGER not null,
    constraint PK_CHILDREN PRIMARY KEY (PERSON_ID, FAMILY_ID),
    constraint FK_CHILD_FAMILY FOREIGN KEY (FAMILY_ID) REFERENCES T_FAMILY (FAMILY_ID),
    constraint FK_CHILD_PERSON FOREIGN KEY (PERSON_ID) REFERENCES T_PERSON (PERSON_ID)
);


CREATE TABLE IF NOT EXISTS T_PERSON_EVENT
(
    EVENT_ID   INTEGER     not null,
    TYPE       VARCHAR(32) not null,
    PREFIX     VARCHAR(3),
    PLACE      VARCHAR(256),
    NOTE       VARCHAR(256),
    EVENT_DATE DATE,
    PERSON_ID  INTEGER     not null,
    constraint PK_PERSON_EVENT PRIMARY KEY (EVENT_ID, PERSON_ID),
    constraint FK_EVENT_PERSON FOREIGN KEY (PERSON_ID) REFERENCES T_PERSON (PERSON_ID)
);


CREATE TABLE IF NOT EXISTS T_FAMILY_EVENT
(
    EVENT_ID   INTEGER     not null,
    TYPE       VARCHAR(32) not null,
    PREFIX     VARCHAR(3),
    PLACE      VARCHAR(256),
    NOTE       VARCHAR(256),
    EVENT_DATE DATE,
    FAMILY_ID  INTEGER     not null,
    constraint PK_FAMILY_EVENT PRIMARY KEY (EVENT_ID, FAMILY_ID),
    constraint FK_EVENT_FAMILY FOREIGN KEY (FAMILY_ID) REFERENCES T_FAMILY (FAMILY_ID)
);

create sequence SEQ_EVENT start with 1;


CREATE TABLE T_ROLE
(
    ROLE_ID   INTEGER     not null,
    ROLE_NAME VARCHAR(32) not null unique,
    CONSTRAINT PK_ROLE PRIMARY KEY (ROLE_ID)
);

CREATE TABLE IF NOT EXISTS T_USER
(
    USER_ID  INTEGER      not null primary key,
    EMAIL    VARCHAR(100) not null unique,
    PASSWORD VARCHAR(256) not null,
    NAME     VARCHAR(32)  not null,
    SURNAME  VARCHAR(32)  not null,
    ROLE_ID  INTEGER      not null default 1,
    constraint FK_USER_ROLE FOREIGN KEY (ROLE_ID) REFERENCES T_ROLE (ROLE_ID)
);

create sequence SEQ_USER start with 1;

alter table T_LINEAGE
    add constraint FK_LINEAGE_USER FOREIGN KEY (USER_ID) REFERENCES T_USER (USER_ID);

alter table T_PERSON
    add constraint FK_PERSON_PARENT_FAMILY FOREIGN KEY (PARENT_FAMILY_ID) REFERENCES T_FAMILY (FAMILY_ID);
alter table T_PERSON
    add constraint FK_PERSON_FAMILY FOREIGN KEY (FAMILY_ID) REFERENCES T_FAMILY (FAMILY_ID);
alter table T_PERSON
    add constraint FK_PERSON_USER FOREIGN KEY (USER_ID) REFERENCES T_USER (USER_ID);

alter table T_FAMILY
    add constraint FK_FAMILY_HUSBAND FOREIGN KEY (HUSBAND_ID) REFERENCES T_PERSON (PERSON_ID);
alter table T_FAMILY
    add constraint FK_FAMILY_WIFE FOREIGN KEY (WIFE_ID) REFERENCES T_PERSON (PERSON_ID);
alter table T_FAMILY
    add constraint FK_FAMILY_USER FOREIGN KEY (USER_ID) REFERENCES T_USER (USER_ID);
