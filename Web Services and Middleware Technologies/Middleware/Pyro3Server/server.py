import Pyro.core
import Pyro.naming

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
    Pyro.core.initServer()
    name_server = Pyro.naming.NameServerLocator().getNS()
    daemon = Pyro.core.Daemon()
    daemon.useNameServer(name_server)
    daemon.connect(service, "service")

    print('Pyro 3 server is running...')
    daemon.requestLoop()


if __name__ == "__main__":
    main()
