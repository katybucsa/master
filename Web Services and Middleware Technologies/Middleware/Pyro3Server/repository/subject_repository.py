from config.config import Session
from domain.Subject import Subject


class SubjectRepository:

    def __init__(self):
        self.__session = Session()

    def add(self, subject):
        try:
            self.__session.add(subject)
            self.__session.commit()
        except Exception:
            self.__session.rollback()
            raise Exception('Could not save subject!\n')

    def delete(self, subject_id):
        try:
            subject = self.__session.query(Subject).get(subject_id)
            self.__session.delete(subject)
            self.__session.commit()
        except Exception:
            self.__session.rollback()
            raise Exception('Could not delete subject!\n')

    def update(self, subject_id, name):
        try:
            field_map = {'name': name}
            self.__session.query(Subject) \
                .where(Subject.subject_id == subject_id) \
                .update(field_map)
            self.__session.commit()
        except Exception:
            self.__session.rollback()
            raise Exception('Could not update subject!\n')

    def get_all(self):
        return self.__session.query(Subject).all()
