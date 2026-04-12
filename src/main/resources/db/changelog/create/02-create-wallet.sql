--liquibase formatted sql

--changeset burgasvv:1
create table if not exists wallet
(
    id          uuid               default gen_random_uuid() unique not null,
    identity_id uuid unique references identity (id) on delete cascade on update cascade,
    created_at  timestamp not null default now()
)