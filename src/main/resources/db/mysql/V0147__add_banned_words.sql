insert into banned_words (word, category, created_at)
values ('마이뷰팁', 1, now()),
       ('mybeautip', 1, now()),
       ('마뷰띠', 1, now()),
       ('러뷰띠', 1, now());

# DELETE FROM banned_words WHERE word in ('마이뷰팁', 'mybeautip', '마뷰띠', '러뷰띠');
