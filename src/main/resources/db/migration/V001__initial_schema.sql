CREATE TABLE IF NOT EXISTS RECIPE (
  id serial,
  name VARCHAR(128),
  ingredients TEXT[] NOT NULL,
  method VARCHAR(2048) NOT NULL,
  dificulty int NOT NULL,
  preparing_time int NOT NULL,
  constraint pk_recipe primary key (id),
  constraint score_rank check (dificulty>=1 and dificulty<=5)
);
