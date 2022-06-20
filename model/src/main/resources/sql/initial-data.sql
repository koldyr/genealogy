insert into T_USER (USER_ID, EMAIL, PASSWORD, NAME, SURNAME)
values (next value for SEQ_USER, 'me@koldyr.com', '$2a$10$g8xdFAgZI.ZK2YsyhFSfMuyDHIlFBvRWEe25uR1G0NLR2gT0PMM9a', 'me', 'koldyr');

insert into T_PERSON (PERSON_ID, PLACE, OCCUPATION, NOTE, GENDER, FIRST_NAME, MIDDLE_NAME, LAST_NAME, MAIDEN_NAME, USER_ID)
values (NEXTVAL('SEQ_PERSON'), 'p1_place', 'p1_occup', 'p1_note', 'M', 'p1_first', 'p1_middle', 'p1_last', null, CURRVAL('SEQ_USER'));
insert into T_PERSON_EVENT (EVENT_ID, TYPE, PLACE, NOTE, EVENT_DATE, PERSON_ID)
values (NEXTVAL('SEQ_EVENT'), 'BIRT', 'Place 1', 'Note 1', '1990-01-01', CURRVAL('SEQ_PERSON'));
update T_PERSON
set PHOTO_URL = 'https://wow.zamimg.com/uploads/screenshots/small/661522.jpg'
where PERSON_ID = CURRVAL('SEQ_PERSON');

insert into T_PERSON (PERSON_ID, PLACE, OCCUPATION, NOTE, GENDER, FIRST_NAME, MIDDLE_NAME, LAST_NAME, MAIDEN_NAME, USER_ID)
values (NEXTVAL('SEQ_PERSON'), 'p2_place', 'p2_occup', 'p2_note', 'F', 'p2_first', 'p2_middle', 'p2_last', 'p2_maiden', CURRVAL('SEQ_USER'));
insert into T_PERSON_EVENT (EVENT_ID, TYPE, PLACE, NOTE, EVENT_DATE, PERSON_ID)
values (NEXTVAL('SEQ_EVENT'), 'BIRT', 'Place 2', 'Note 2', '1991-01-01', CURRVAL('SEQ_PERSON'));
update T_PERSON
set PHOTO_URL = 'https://wow.zamimg.com/uploads/screenshots/small/661505.jpg'
where PERSON_ID = CURRVAL('SEQ_PERSON');

insert into T_PERSON (PERSON_ID, PLACE, OCCUPATION, NOTE, GENDER, FIRST_NAME, MIDDLE_NAME, LAST_NAME, MAIDEN_NAME, USER_ID)
values (NEXTVAL('SEQ_PERSON'), 'p3_place', 'p3_occup', 'p3_note', 'M', 'p3_first', 'p3_middle', 'p3_last', null, CURRVAL('SEQ_USER'));
insert into T_PERSON_EVENT (EVENT_ID, TYPE, PLACE, NOTE, EVENT_DATE, PERSON_ID)
values (NEXTVAL('SEQ_EVENT'), 'BIRT', 'Place 3', 'Note 3', '1992-01-01', CURRVAL('SEQ_PERSON'));
update T_PERSON
set PHOTO_URL = 'https://wow.zamimg.com/uploads/screenshots/small/661519.jpg'
where PERSON_ID = CURRVAL('SEQ_PERSON');

insert into T_PERSON (PERSON_ID, PLACE, OCCUPATION, NOTE, GENDER, FIRST_NAME, MIDDLE_NAME, LAST_NAME, MAIDEN_NAME, USER_ID)
values (NEXTVAL('SEQ_PERSON'), 'p4_place', 'p4_occup', 'p4_note', 'F', 'p4_first', 'p4_middle', 'p4_last', null, CURRVAL('SEQ_USER'));
insert into T_PERSON_EVENT (EVENT_ID, TYPE, PLACE, NOTE, EVENT_DATE, PERSON_ID)
values (NEXTVAL('SEQ_EVENT'), 'BIRT', 'Place 4', 'Note 4', '1993-01-01', CURRVAL('SEQ_PERSON'));
update T_PERSON
set PHOTO_URL = 'https://wow.zamimg.com/uploads/screenshots/small/661521.jpg'
where PERSON_ID = CURRVAL('SEQ_PERSON');

insert into T_FAMILY (FAMILY_ID, HUSBAND_ID, WIFE_ID, NOTE, USER_ID)
values (NEXTVAL('SEQ_FAMILY'), 1, 2, 'f1_note', CURRVAL('SEQ_USER'));

insert into T_FAMILY_EVENT (EVENT_ID, TYPE, PLACE, NOTE, EVENT_DATE, FAMILY_ID)
values (NEXTVAL('SEQ_EVENT'), 'MARR', 'Place 4', 'Note 4', '2015-01-01', CURRVAL('SEQ_FAMILY'));

update T_PERSON
set FAMILY_ID = CURRVAL('SEQ_FAMILY')
where PERSON_ID in (1, 2);

update T_PERSON
set PARENT_FAMILY_ID = CURRVAL('SEQ_FAMILY')
where PERSON_ID in (3, 4);

insert into T_CHILDREN (FAMILY_ID, PERSON_ID)
values (CURRVAL('SEQ_FAMILY'), 3);

insert into T_CHILDREN (FAMILY_ID, PERSON_ID)
values (CURRVAL('SEQ_FAMILY'), 4);
