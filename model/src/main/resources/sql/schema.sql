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
    PARENT_FAMILY_ID INTEGER,
    FAMILY_ID        INTEGER,
    USER_ID          INTEGER      NOT NULL
);
create sequence SEQ_PERSON start with 1;

CREATE TABLE IF NOT EXISTS T_FAMILY
(
    FAMILY_ID  INTEGER not null,
    HUSBAND_ID INTEGER,
    WIFE_ID    INTEGER,
    NOTE       VARCHAR(256),
    USER_ID    INTEGER NOT NULL
);
create sequence SEQ_FAMILY start with 1;

CREATE TABLE IF NOT EXISTS T_FAMILY_CHILDREN
(
    FAMILY_FAMILY_ID   INTEGER not null,
    CHILDREN_PERSON_ID INTEGER not null
);

CREATE TABLE IF NOT EXISTS T_PERSON_EVENT
(
    EVENT_ID   INTEGER     not null,
    TYPE       VARCHAR(32) not null,
    PREFIX     VARCHAR(3),
    PLACE      VARCHAR(256),
    NOTE       VARCHAR(256),
    EVENT_DATE DATE        not null,
    PERSON_ID  INTEGER     not null
);

CREATE TABLE IF NOT EXISTS T_FAMILY_EVENT
(
    EVENT_ID   INTEGER     not null,
    TYPE       VARCHAR(32) not null,
    PREFIX     VARCHAR(3),
    PLACE      VARCHAR(256),
    NOTE       VARCHAR(256),
    EVENT_DATE DATE        not null,
    FAMILY_ID  INTEGER     not null
);

create sequence SEQ_EVENT start with 1;


CREATE TABLE IF NOT EXISTS T_USER
(
    USER_ID  INTEGER      not null primary key,
    EMAIL    VARCHAR(100) not null unique,
    PASSWORD VARCHAR(256) not null,
    NAME     VARCHAR(32)  not null,
    SURNAME  VARCHAR(32)  not null
);

create sequence SEQ_USER start with 1;

alter table T_PERSON
    add constraint PK_PERSON PRIMARY KEY (PERSON_ID);
alter table T_PERSON
    add constraint FK_PERSON_PARENT_FAMILY FOREIGN KEY (PARENT_FAMILY_ID) REFERENCES T_FAMILY (FAMILY_ID);
alter table T_PERSON
    add constraint FK_PERSON_FAMILY FOREIGN KEY (FAMILY_ID) REFERENCES T_FAMILY (FAMILY_ID);
alter table T_PERSON
    add constraint FK_PERSON_USER FOREIGN KEY (USER_ID) REFERENCES T_USER (USER_ID);

alter table T_FAMILY
    add constraint PK_FAMILY PRIMARY KEY (FAMILY_ID);
alter table T_FAMILY
    add constraint FK_FAMILY_HUSBAND FOREIGN KEY (HUSBAND_ID) REFERENCES T_PERSON (PERSON_ID);
alter table T_FAMILY
    add constraint FK_FAMILY_WIFE FOREIGN KEY (WIFE_ID) REFERENCES T_PERSON (PERSON_ID);
alter table T_FAMILY
    add constraint FK_FAMILY_USER FOREIGN KEY (USER_ID) REFERENCES T_USER (USER_ID);

alter table T_PERSON_EVENT
    add constraint PK_PERSON_EVENT PRIMARY KEY (EVENT_ID, PERSON_ID);
alter table T_PERSON_EVENT
    add constraint FK_EVENT_PERSON FOREIGN KEY (PERSON_ID) REFERENCES T_PERSON (PERSON_ID);

alter table T_FAMILY_EVENT
    add constraint PK_FAMILY_EVENT PRIMARY KEY (EVENT_ID, FAMILY_ID);
alter table T_FAMILY_EVENT
    add constraint FK_EVENT_FAMILY FOREIGN KEY (FAMILY_ID) REFERENCES T_FAMILY (FAMILY_ID);

