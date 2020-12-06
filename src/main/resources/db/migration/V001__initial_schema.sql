CREATE TABLE IF NOT EXISTS GRADE (
  id serial,
  name VARCHAR(128),
  ingredients TEXT[] NOT NULL,
  method VARCHAR(2048) NOT NULL,
  dificulty_ranking int NOT NULL,
  preparing_time int NOT NULL,
  constraint pk_recipe primary key (id)
  constraint score_rank check (dificulty_ranking>=1 and dificulty_ranking<=5)
);
