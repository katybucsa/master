from sqlalchemy import create_engine
from sqlalchemy.orm import declarative_base, sessionmaker

base = declarative_base()
db_string = 'postgresql://postgres:postgres@localhost:5432/middleware-db'
engine = create_engine(db_string)
Session = sessionmaker(engine)


def create_tables():
    base.metadata.create_all(engine)
