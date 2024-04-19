INSERT INTO users(user_name, user_email) values('user1', 'user1@mail.com');
insert into users(user_name, user_email) values('user2', 'user2@mail.com');
insert into users(user_name, user_email) values('user3', 'user3@mail.com');
insert into categories (category_name) values('pets');

insert into events(annotation, category_id, description, event_date, lat, lon, paid,
					participant_limit, request_moderation, title, created, user_id, state)
values('Pets exhibition', 1, 'World Wide Pets Exhibition', now(), 40.682732, -73.975876, true,
					20, true, 'WWPE', date'2024-01-01', 1, 'PENDING');

insert into events(annotation, category_id, description, event_date, lat, lon, paid,
					participant_limit, request_moderation, title, created, user_id, state, published)
values('Pets exhibition - 2', 1, 'World Wide Pets Exhibition - 2', now()::timestamp - interval '1 hours', 40.682732, -73.975876, true,
					20, true, 'WWPE-2', date'2024-01-01', 1, 'PUBLISHED', now()::timestamp - interval '5 minutes');

insert into participant_requests(status, created, user_id, event_id) values('PENDING', now(), 2, 1);
insert into participant_requests(status, created, user_id, event_id) values('CONFIRMED', now(), 3, 1);
insert into participant_requests(status, created, user_id, event_id) values('CONFIRMED', now(), 3, 2);