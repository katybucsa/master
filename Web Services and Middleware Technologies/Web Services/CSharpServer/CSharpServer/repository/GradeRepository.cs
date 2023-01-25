using System.Collections.Generic;
using System.Linq;
using CSharpServer.config;
using CSharpServer.domain;

namespace CSharpServer.repository
{
    public class GradeRepository
    {
        public GradeRepository()
        {
        }

        public void Add(Grade grade)
        {
            using var context = new SchoolContext();
            context.Grades.Add(grade);
            context.SaveChanges();
        }

        public void Delete(int gradeId)
        {
            using var context = new SchoolContext();
            var grade = context.Grades
                .First(s => s.GradeId == gradeId);
            context.Remove(grade);
            context.SaveChanges();
        }

        public void Update(int gradeId, float value)
        {
            using var context = new SchoolContext();
            var grade = context.Grades
                .First(s => s.GradeId == gradeId);
            grade.Value = value;
            context.SaveChanges();
        }

        public IList<Grade> GetAll()
        {
            using var context = new SchoolContext();
            return context.Grades.ToList();
        }
    }
}