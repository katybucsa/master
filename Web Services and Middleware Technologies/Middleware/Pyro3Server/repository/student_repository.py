from config.config import Session
from domain.Student import Student


class StudentRepository:

    def __init__(self):
        self.__session = Session()

    def add(self, student):
        try:
            self.__session.add(student)
            self.__session.commit()
        except Exception:
            self.__session.rollback()
            raise Exception('Could not save student!\n')

    def delete(self, student_id):
        try:
            student = self.__session.query(Student).get(student_id)
            self.__session.delete(student)
            self.__session.commit()
        except Exception:
            self.__session.rollback()
            raise Exception('Could not delete student!\n')

    def update(self, student_id, first_name, last_name, email):
        try:
            field_map = {}
            if first_name:
                field_map['first_name'] = first_name
            if last_name:
                field_map['last_name'] = last_name
            if email:
                field_map['email'] = email
            self.__session.query(Student) \
                .where(Student.student_id == student_id) \
                .update(field_map)
            self.__session.commit()
        except Exception as e:
            self.__session.rollback()
            raise Exception(e)

    def get_all(self):
        return self.__session.query(Student).all()
