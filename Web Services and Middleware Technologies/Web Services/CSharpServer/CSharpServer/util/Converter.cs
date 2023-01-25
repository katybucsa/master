using System.Collections.Generic;
using System.Linq;
using CookComputing.XmlRpc;
using CSharpServer.domain;

namespace CSharpServer.util
{
    public static class Converter
    {
        private static XmlRpcStruct StudentToXmlRpcStruct(Student student)
        {
            var xmlRpcStruct = new XmlRpcStruct
            {
                {"studentId", student.StudentId},
                {"firstName", student.FirstName},
                {"lastName", student.LastName},
                {"email", student.Email}
            };
            return xmlRpcStruct;
        }

        private static XmlRpcStruct SubjectToXmlRpcStruct(Subject subject)
        {
            return new XmlRpcStruct {{"subjectId", subject.SubjectId}, {"name", subject.Name}};
        }

        private static XmlRpcStruct GradeToXmlRpcStruct(Grade grade)
        {
            var xmlRpcStruct = new XmlRpcStruct
            {
                {"gradeId", grade.GradeId},
                {"studentId", grade.StudentId},
                {"subjectId", grade.SubjectId},
                {"value", (double) grade.Value}
            };
            return xmlRpcStruct;
        }

        public static XmlRpcStruct[] StudentsToXmlRpcStructs(IList<Student> students)
        {
            var studentStructs = new XmlRpcStruct[students.Count];
            foreach (var student in students)
            {
                studentStructs[students.IndexOf(student)] = StudentToXmlRpcStruct(student);
            }

            return studentStructs;
        }

        public static XmlRpcStruct[] SubjectsToXmlRpcStructs(IList<Subject> subjects)
        {
            var subjectStructs = new XmlRpcStruct[subjects.Count];
            foreach (var subject in subjects)
            {
                subjectStructs[subjects.IndexOf(subject)] = SubjectToXmlRpcStruct(subject);
            }

            return subjectStructs;
        }

        public static XmlRpcStruct[] GradesToXmlRpcStructs(IList<Grade> grades)
        {
            var gradeStructs = new XmlRpcStruct[grades.Count];
            foreach (var grade in grades)
            {
                gradeStructs[grades.IndexOf(grade)] = GradeToXmlRpcStruct(grade);
            }

            return gradeStructs;
        }
    }
}