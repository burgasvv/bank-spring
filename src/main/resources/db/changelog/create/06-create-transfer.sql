--liquibase formatted sql

--changeset burgasvv:1
create table if not exists transfer
(
    id         uuid               default gen_random_uuid() unique not null,
    sender_id     uuid references card (id) on delete cascade on update cascade,
    receiver_id   uuid references card (id) on delete cascade on update cascade,
    amount     decimal   not null default 0 check ( amount >= 0 ),
    created_at timestamp not null
)