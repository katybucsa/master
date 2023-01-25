import json

from sqlalchemy import Column, String, Integer

from config.config import base


class Student(base):
    __tablename__ = 'students'

    student_id = Column(Integer, primary_key=True, autoincrement=True)
    first_name = Column(String(32), nullable=False)
    last_name = Column(String(32), nullable=False)
    email = Column(String(320), unique=True)

    def get_first_name(self):
        return self.__name

    def get_last_name(self):
        return self.__last_name

    def get_email(self):
        return self.__email

    def to_json(self):
        return json.dumps({'id': self.student_id,
                           'first_name': self.first_name,
                           'last_name': self.last_name,
                           'email': self.email})
