import json

from sqlalchemy import String, Column

from config.config import base


class Subject(base):
    __tablename__ = 'subjects'

    subject_id = Column(String(32), primary_key=True)
    name = Column(String(128), nullable=False)

    def get_name(self):
        return self.__name

    def set_name(self, name):
        self.__name = name

    def to_json(self):
        return json.dumps({'id': self.subject_id,
                           'name': self.name})
