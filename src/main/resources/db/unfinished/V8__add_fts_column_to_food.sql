-- TODO: Add support for ukrainian in postgres

ALTER TABLE food
    ADD COLUMN search_vector tsvector GENERATED ALWAYS AS (
        setweight(to_tsvector('ukrainian', name), 'A') ||
        setweight(to_tsvector('ukrainian', coalesce(description, '')), 'B')
        ) STORED;

CREATE INDEX food_search_idx ON food USING GIN (search_vector);
