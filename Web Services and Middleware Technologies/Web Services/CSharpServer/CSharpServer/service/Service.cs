using System;
using CookComputing.XmlRpc;
using CSharpServer.domain;
using CSharpServer.repository;
using CSharpServer.util;

namespace CSharpServer.service
{
    public class Service : XmlRpcListenerService
    {
        private readonly StudentRepository _studentRepository;
        private readonly SubjectRepository _subjectRepository;
        private readonly GradeRepository _gradeRepository;

        public Service(StudentRepository studentRepository, SubjectRepository subjectRepository,
            GradeRepository gradeRepository)
        {
            this._studentRepository = studentRepository;
            this._subjectRepository = subjectRepository;
            this._gradeRepository = gradeRepository;
        }

        [XmlRpcMethod("service.AddStudent", Description = "Adds a student with given attributes to the database")]
        public void AddStudent(string firstName, string lastName, string email)
        {
            Console.WriteLine("New AddStudent Request " + DateTime.Now);
            var student = new Student {FirstName = firstName, LastName = lastName, Email = email};
            this._studentRepository.Add(student);
        }

        [XmlRpcMethod("service.DeleteStudent", Description = "Deletes student with given id from the database")]
        public void DeleteStudent(int studentId)
        {
            Console.WriteLine("New DeleteStudent Request " + DateTime.Now);
            this._studentRepository.Delete(studentId);
        }

        [XmlRpcMethod("service.UpdateStudent", Description = "Updates student with given id")]
        public void UpdateStudent(int studentId, string firstName, string lastName, string email)
        {
            Console.WriteLine("New UpdateStudent Request " + DateTime.Now);
            this._studentRepository.Update(studentId, firstName, lastName, email);
        }

        [XmlRpcMethod("service.GetAllStudents", Description = "Retrieves all students from the database")]
        public XmlRpcStruct[] GetAllStudents()
        {
            Console.WriteLine("New GetAllStudents Request " + DateTime.Now);
            return Converter.StudentsToXmlRpcStructs(this._studentRepository.GetAll());
        }

        [XmlRpcMethod("service.AddSubject", Description = "Adds a subject with given attributes to the database")]
        public void AddSubject(string code, string name)
        {
            Console.WriteLine("New AddSubject Request " + DateTime.Now);
            var subject = new Subject {SubjectId = code, Name = name};
            this._subjectRepository.Add(subject);
        }

        [XmlRpcMethod("service.DeleteSubject", Description = "Deletes subject with given id from the database")]
        public void DeleteSubject(string subjectId)
        {
            Console.WriteLine("New DeleteSubject Request " + DateTime.Now);
            this._subjectRepository.Delete(subjectId);
        }

        [XmlRpcMethod("service.UpdateSubject", Description = "Updates subject with given id")]
        public void UpdateSubject(string subjectId, string name)
        {
            Console.WriteLine("New UpdateSubject Request " + DateTime.Now);
            this._subjectRepository.Update(subjectId, name);
        }

        [XmlRpcMethod("service.GetAllSubjects", Description = "Retrieves all subjects from the database")]
        public XmlRpcStruct[] GetAllSubjects()
        {
            Console.WriteLine("New GetAllSubjects Request " + DateTime.Now);
            return Converter.SubjectsToXmlRpcStructs(this._subjectRepository.GetAll());
        }

        [XmlRpcMethod("service.AddGrade", Description = "Adds a grade with given attributes to the database")]
        public void AddGrade(int studentId, string subjectId, double value)
        {
            Console.WriteLine("New AddGrade Request " + DateTime.Now);
            var grade = new Grade {StudentId = studentId, SubjectId = subjectId, Value = (float) value};
            this._gradeRepository.Add(grade);
        }

        [XmlRpcMethod("service.DeleteGrade", Description = "Deletes grade with given id from the database")]
        public void DeleteGrade(int gradeId)
        {
            Console.WriteLine("New DeleteGrade Request " + DateTime.Now);
            this._gradeRepository.Delete(gradeId);
        }

        [XmlRpcMethod("service.UpdateGrade", Description = "Updates grade with given id")]
        public void UpdateGrade(int gradeId, double value)
        {
            Console.WriteLine("New UpdateGrade Request " + DateTime.Now);
            this._gradeRepository.Update(gradeId, (float) value);
        }

        [XmlRpcMethod("service.GetAllGrades", Description = "Retrieves all grades from the database")]
        public XmlRpcStruct[] GetAllGrades()
        {
            Console.WriteLine("New GetAllGrades Request " + DateTime.Now);
            return Converter.GradesToXmlRpcStructs(this._gradeRepository.GetAll());
        }
    }
}