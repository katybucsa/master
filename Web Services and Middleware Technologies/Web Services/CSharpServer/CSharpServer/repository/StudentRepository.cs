using System.Collections;
using System.Collections.Generic;
using System.Linq;
using CSharpServer.config;
using CSharpServer.domain;
using static System.String;

namespace CSharpServer.repository
{
    public class StudentRepository
    {
        public void Add(Student student)
        {
            using var context = new SchoolContext();
            context.Students.Add(student);
            context.SaveChanges();
        }

        public void Delete(int studentId)
        {
            using var context = new SchoolContext();
            var student = context.Students
                .First(s => s.StudentId == studentId);
            context.Remove(student);
            context.SaveChanges();
        }

        public void Update(int studentId, string firstName, string lastName, string email)
        {
            using var context = new SchoolContext();
            var student = context.Students
                .First(s => s.StudentId == studentId);
            if (!IsNullOrEmpty(firstName) && !IsNullOrWhiteSpace(firstName))
            {
                student.FirstName = firstName;
            }

            if (!IsNullOrEmpty(lastName) && !IsNullOrWhiteSpace(lastName))
            {
                student.LastName = lastName;
            }

            if (!IsNullOrEmpty(email) && !IsNullOrWhiteSpace(email))
            {
                student.Email = email;
            }

            context.SaveChanges();
        }

        public IList<Student> GetAll()
        {
            using var context = new SchoolContext();
            return context.Students.ToList();
        }
    }
}