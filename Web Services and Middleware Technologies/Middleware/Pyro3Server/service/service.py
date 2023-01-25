import json

import Pyro.core

from domain.Grade import Grade
from domain.Student import Student
from domain.Subject import Subject


class Service(Pyro.core.ObjBase):

    def __init__(self, student_repo, subject_repo, grade_repo):
        Pyro.core.ObjBase.__init__(self)
        self.__student_repo = student_repo
        self.__subject_repo = subject_repo
        self.__grade_repo = grade_repo

    # @expose
    def add_student(self, first_name, last_name, email):
        student = Student(first_name=first_name, last_name=last_name, email=email)
        self.__student_repo.add(student)

    # @expose
    def delete_student(self, student_id):
        self.__student_repo.delete(student_id)

    # @expose
    def update_student(self, student_id, first_name, last_name, email):
        self.__student_repo.update(student_id, first_name, last_name, email)

    # @expose
    def get_all_students(self):
        jsons = {'data': list(map(lambda s: s.to_json(), self.__student_repo.get_all()))}
        return json.dumps(jsons)

    # @expose
    def add_subject(self, code, name):
        subject = Subject(subject_id=code, name=name)
        self.__subject_repo.add(subject)

    # @expose
    def delete_subject(self, subject_id):
        self.__subject_repo.delete(subject_id)

    # @expose
    def update_subject(self, subject_id, name):
        self.__subject_repo.update(subject_id, name)

    # @expose
    def get_all_subjects(self):
        jsons = {'data': list(map(lambda s: s.to_json(), self.__subject_repo.get_all()))}
        return json.dumps(jsons)

    # @expose
    def add_grade(self, student_id, subject_id, value):
        grade = Grade(student_id=student_id, subject_id=subject_id, value=value)
        self.__grade_repo.add(grade)

    # @expose
    def update_grade(self, grade_id, value):
        self.__grade_repo.update(grade_id, value)

    # @expose
    def get_all_grades(self):
        jsons = {'data': list(map(lambda g: g.to_json(), self.__grade_repo.get_all()))}
        return json.dumps(jsons)
