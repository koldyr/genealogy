alter table T_PERSON drop constraint FK_PERSON_FAMILY;
alter table T_PERSON drop constraint FK_PERSON_PARENT_FAMILY;

alter table T_FAMILY drop constraint FK_FAMILY_HUSBAND;
alter table T_FAMILY drop constraint FK_FAMILY_WIFE;

DROP TABLE IF EXISTS T_FAMILY_EVENT;
DROP TABLE IF EXISTS T_PERSON_EVENT;
DROP TABLE IF EXISTS T_FAMILY_CHILDREN;
DROP TABLE IF EXISTS T_PERSON;
DROP TABLE IF EXISTS T_FAMILY;
DROP TABLE IF EXISTS T_USER;

DROP SEQUENCE SEQ_PERSON;
DROP SEQUENCE SEQ_FAMILY;
DROP SEQUENCE SEQ_EVENT;
DROP SEQUENCE SEQ_USER;
