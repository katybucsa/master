using System.Collections.Generic;
using System.Linq;
using CSharpServer.config;
using CSharpServer.domain;

namespace CSharpServer.repository
{
    public class SubjectRepository
    {
        public SubjectRepository()
        {
        }

        public void Add(Subject subject)
        {
            using var context = new SchoolContext();
            context.Subjects.Add(subject);
            context.SaveChanges();
        }

        public void Delete(string subjectId)
        {
            using var context = new SchoolContext();
            var subject = context.Subjects
                .First(s => s.SubjectId.Equals(subjectId));
            context.Remove(subject);
            context.SaveChanges();
        }

        public void Update(string subjectId, string name)
        {
            using var context = new SchoolContext();
            var subject = context.Subjects
                .First(s => s.SubjectId == subjectId);
            subject.Name = name;
            context.SaveChanges();
        }

        public IList<Subject> GetAll()
        {
            using var context = new SchoolContext();
            return context.Subjects.ToList();
        }
    }
}