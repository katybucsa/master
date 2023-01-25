from config.config import Session
from domain.Grade import Grade


class GradeRepository:

    def __init__(self):
        self.__session = Session()

    def add(self, grade):
        try:
            self.__session.add(grade)
            self.__session.commit()
        except Exception:
            self.__session.rollback()
            raise Exception('Could not save grade!\n')

    def update(self, grade_id, value):
        try:
            field_map = {'value': value}
            self.__session.query(Grade) \
                .where(Grade.grade_id == grade_id) \
                .update(field_map)
            self.__session.commit()
        except Exception:
            self.__session.rollback()
            raise Exception('Could not update grade!\n')

    def get_all(self):
        return self.__session.query(Grade).all()
