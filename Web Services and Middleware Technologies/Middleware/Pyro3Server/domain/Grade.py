import json

from sqlalchemy import Column, Integer, ForeignKey, String, Float

from config.config import base
from domain.Student import Student
from domain.Subject import Subject


class Grade(base):
    __tablename__ = 'grades'

    grade_id = Column(Integer, primary_key=True, autoincrement=True)
    student_id = Column(Integer, ForeignKey(Student.student_id))
    subject_id = Column(String(32), ForeignKey(Subject.subject_id))
    value = Column(Float, nullable=False)

    def get_student_id(self):
        return self.__student_id

    def set_student_id(self, student_id):
        self.__student_id = student_id

    def get_subject_id(self):
        return self.__subject_id

    def set_subject_id(self, subject_id):
        self.__subject_id = subject_id

    def get_value(self):
        return self.__value

    def set_value(self, value):
        self.__value = value

    def to_json(self):
        return json.dumps({'id': self.grade_id,
                           'student_id': self.student_id,
                           'subject_id': self.subject_id,
                           'value': self.value})
