import Pyro4

from config.config import create_tables
from repository.grade_repository import GradeRepository
from repository.student_repository import StudentRepository
from repository.subject_repository import SubjectRepository
from service.service import Service


def main():
    create_tables()
    student_repo = StudentRepository()
    subject_repo = SubjectRepository()
    grade_repo = GradeRepository()
    service = Service(student_repo, subject_repo, grade_repo)
    daemon = Pyro4.Daemon()
    name_server = Pyro4.locateNS()
    service_url = daemon.register(service)
    name_server.register('service', service_url)

    print('Pyro 4 server is running...')
    daemon.requestLoop()


main()
