insert into T_USER (USER_ID, EMAIL, PASSWORD, NAME, SURNAME)
values (SEQ_USER.nextval, 'me@koldyr.com', '$2a$10$g8xdFAgZI.ZK2YsyhFSfMuyDHIlFBvRWEe25uR1G0NLR2gT0PMM9a', 'me', 'koldyr');

insert into T_PERSON (PERSON_ID, PLACE, OCCUPATION, NOTE, GENDER, FIRST_NAME, MIDDLE_NAME, LAST_NAME, MAIDEN_NAME, USER_ID)
values (SEQ_PERSON.nextval, 'p1_place', 'p1_occup', 'p1_note', 'M', 'p1_first', 'p1_middle', 'p1_last', null, SEQ_USER.currval);

insert into T_PERSON (PERSON_ID, PLACE, OCCUPATION, NOTE, GENDER, FIRST_NAME, MIDDLE_NAME, LAST_NAME, MAIDEN_NAME, USER_ID)
values (SEQ_PERSON.nextval, 'p2_place', 'p2_occup', 'p2_note', 'F', 'p2_first', 'p2_middle', 'p2_last', 'p2_maiden', SEQ_USER.currval);

insert into T_PERSON (PERSON_ID, PLACE, OCCUPATION, NOTE, GENDER, FIRST_NAME, MIDDLE_NAME, LAST_NAME, MAIDEN_NAME, USER_ID)
values (SEQ_PERSON.nextval, 'p3_place', 'p3_occup', 'p3_note', 'M', 'p3_first', 'p3_middle', 'p3_last', null, SEQ_USER.currval);

insert into T_PERSON (PERSON_ID, PLACE, OCCUPATION, NOTE, GENDER, FIRST_NAME, MIDDLE_NAME, LAST_NAME, MAIDEN_NAME, USER_ID)
values (SEQ_PERSON.nextval, 'p4_place', 'p4_occup', 'p4_note', 'F', 'p4_first', 'p4_middle', 'p4_last', null, SEQ_USER.currval);

insert INTO T_FAMILY (FAMILY_ID, HUSBAND_ID, WIFE_ID, NOTE, USER_ID)
VALUES (SEQ_FAMILY.nextval, 1, 2, 'f1_note', SEQ_USER.currval);

update T_PERSON
set FAMILY_ID = 1
where PERSON_ID in (1, 2);

update T_PERSON
set PARENT_FAMILY_ID = 1
where PERSON_ID in (3, 4);

insert into T_CHILDREN (FAMILY_ID, PERSON_ID)
values (1, 3);

insert into T_CHILDREN (FAMILY_ID, PERSON_ID)
values (1, 4);
