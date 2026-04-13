--liquibase formatted sql

--changeset burgasvv:1
create table if not exists account
(
    id         uuid default gen_random_uuid() unique not null,
    wallet_id  uuid references wallet (id) on delete cascade on update cascade,
    number     bigint unique                         not null,
    inn        bigint unique                         not null,
    cpp        bigint unique                         not null,
    created_at timestamp                             not null
)