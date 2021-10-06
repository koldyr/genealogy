insert into T_PERSON (PERSON_ID, PLACE, OCCUPATION, NOTE, GENDER, FIRST_NAME, MIDDLE_NAME, LAST_NAME, MAIDEN_NAME)
values (SEQ_PERSON.nextval, 'p1_place', 'p1_occup', 'p1_note', 'M', 'p1_first', 'p1_middle', 'p1_last', null);

insert into T_PERSON (PERSON_ID, PLACE, OCCUPATION, NOTE, GENDER, FIRST_NAME, MIDDLE_NAME, LAST_NAME, MAIDEN_NAME)
values (SEQ_PERSON.nextval, 'p2_place', 'p2_occup', 'p2_note', 'F', 'p2_first', 'p2_middle', 'p2_last', 'p2_maiden');

insert into T_PERSON (PERSON_ID, PLACE, OCCUPATION, NOTE, GENDER, FIRST_NAME, MIDDLE_NAME, LAST_NAME, MAIDEN_NAME)
values (SEQ_PERSON.nextval, 'p3_place', 'p3_occup', 'p3_note', 'M', 'p3_first', 'p3_middle', 'p3_last', null);

insert into T_PERSON (PERSON_ID, PLACE, OCCUPATION, NOTE, GENDER, FIRST_NAME, MIDDLE_NAME, LAST_NAME, MAIDEN_NAME)
values (SEQ_PERSON.nextval, 'p4_place', 'p4_occup', 'p4_note', 'F', 'p4_first', 'p4_middle', 'p4_last', null);

insert INTO T_FAMILY (FAMILY_ID, HUSBAND_ID, WIFE_ID, NOTE)
VALUES (SEQ_FAMILY.nextval, 1, 2, 'f1_note');

update T_PERSON
set FAMILY_ID = 1
where PERSON_ID in (1, 2);

insert into T_FAMILY_CHILDREN (FAMILY_FAMILY_ID, CHILDREN_PERSON_ID)
values (1, 3);

insert into T_FAMILY_CHILDREN (FAMILY_FAMILY_ID, CHILDREN_PERSON_ID)
values (1, 4);