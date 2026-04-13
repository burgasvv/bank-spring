--liquibase formatted sql

--changeset burgasvv:1
create table if not exists operation
(
    id         uuid               default gen_random_uuid() unique not null,
    card_id    uuid references card (id) on delete cascade on update cascade,
    type       varchar   not null,
    amount     decimal   not null check ( amount > 0 ),
    created_at timestamp not null default now()
)